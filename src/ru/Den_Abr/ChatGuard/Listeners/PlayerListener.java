package ru.Den_Abr.ChatGuard.Listeners;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.MessageInfo;
import ru.Den_Abr.ChatGuard.ChatFilters.AbstractFilter;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Integration.AbstractIntegration;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.Utils;

public class PlayerListener implements Listener {
	private static PlayerListener instance;
	public static boolean globalMute = false;

	public PlayerListener() {
		instance = this;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		MessageInfo info = handleMessage(e.getMessage(), CGPlayer.get(e.getPlayer()));
		if (info == null)
			return;
		if (info.isCancelled())
			e.setCancelled(true);
		e.setMessage(info.getClearMessage());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		MessageInfo info = handleCommand(e.getMessage(), CGPlayer.get(e.getPlayer()));
		if (info == null)
			return;
		if (info.isCancelled())
			e.setCancelled(true);
		e.setMessage(info.getClearMessage());
	}

	public static MessageInfo handleMessage(String message, CGPlayer e) {
		if (AbstractIntegration.shouldSkip(e.getPlayer()))
			return null;
		MessageInfo info = new MessageInfo();
		if (globalMute && !e.hasPermission("chatguard.ignore.globalmute")) {
			info.cancel(true);
			e.getPlayer().sendMessage(Message.GLOBAL_MUTE.get());
			return info;
		}

		if (e.isMuted()) {
			e.getPlayer().sendMessage(Message.UR_MUTED.get().replace("{REASON}", e.getMuteReason()).replace("{TIME}",
					Utils.getTimeInMaxUnit(e.getMuteTime() - System.currentTimeMillis())));
			info.cancel(true);
			return info;
		}

		if (!e.hasPermission("chatguard.ignore.cooldown")) {
			int cdtime = isCooldownOver(e);
			ChatGuardPlugin.debug(1, e.getName() + "'s CD " + cdtime);
			if (cdtime > 0) {
				info.cancel(true);
				e.getPlayer().sendMessage(Message.WAIT_COOLDOWN.get().replace("{TIME}", cdtime + Message.SEC.get()));
				return info;
			}
		}

		info = AbstractFilter.handleMessage(message, e, false);

		if (!info.getViolations().isEmpty()) {
			if (Settings.isCancellingEnabled()) {
				info.cancel(true);
			} else {
				e.setLastMessageTime(System.currentTimeMillis());
				e.getLastMessages().add(message);
			}
			return info;
		}
		e.setLastMessageTime(System.currentTimeMillis());
		e.getLastMessages().add(message);
		return info;
	}

	public static MessageInfo handleCommand(String message, CGPlayer player) {
		if (AbstractIntegration.shouldSkip(player.getPlayer()) || Settings.getCheckCommands().isEmpty())
			return null;
		String comand = message.split(" ")[0].toLowerCase();
		ChatGuardPlugin.debug(2, "Command: " + comand);
		ChatGuardPlugin.debug(2, "Commands list: " + Settings.getCheckCommands());
		if (!Settings.getCheckCommands().containsKey(comand))
			return null;

		String[] words = message.split(" ");
		int offset = Settings.getCheckCommands().get(comand) + 1;

		String skipped = "";
		if (offset > 1) {
			skipped = StringUtils.join(Arrays.copyOfRange(words, 1, offset), ' ') + " ";
		}
		String fixedMessage = "";
		if (offset <= words.length) {
			fixedMessage = StringUtils.join(Arrays.copyOfRange(words, offset, words.length), ' ');
		} else {
			ChatGuardPlugin.debug(0,
					"Something wrong with '" + message + "'. Offset: " + offset + ", array lenght: " + words.length);
		}
		ChatGuardPlugin.debug(2, "Fixed part: " + fixedMessage);
		ChatGuardPlugin.debug(2, "Skipped part: " + skipped);

		comand += " " + skipped;

		if (fixedMessage.isEmpty())
			return null;

		if (player.isMuted()) {
			player.getPlayer().sendMessage(Message.UR_MUTED.get().replace("{REASON}", player.getMuteReason())
					.replace("{TIME}", Utils.getTimeInMaxUnit(player.getMuteTime() - System.currentTimeMillis())));
			MessageInfo info = new MessageInfo();
			info.cancel(true);
			return info;
		}

		MessageInfo info = AbstractFilter.handleMessage(fixedMessage, player, false);
		info.setClearMessage(comand + info.getClearMessage());

		if (!info.getViolations().isEmpty()) {
			if (Settings.isCancellingEnabled()) {
				info.cancel(true);
			} else {
				player.setLastMessageTime(System.currentTimeMillis());
				player.getLastMessages().add(message);
			}
			return info;
		}
		player.setLastMessageTime(System.currentTimeMillis());
		player.getLastMessages().add(message);
		return info;
	}

	public static int isCooldownOver(CGPlayer pl) {
		if (!Settings.isCooldownEnabled()) {
			return 0;
		}
		if (pl.getLastMessageTime() != -1) {
			long overtime = pl.getLastMessageTime() + TimeUnit.SECONDS.toMillis(Settings.getCooldown());
			double offset = Math.ceil((double) (overtime - System.currentTimeMillis()) / 1000);
			if (offset > 0) {
				return (int) offset;
			}
		}
		return 0;
	}

	public static PlayerListener getInstance() {
		return instance;
	}
}
