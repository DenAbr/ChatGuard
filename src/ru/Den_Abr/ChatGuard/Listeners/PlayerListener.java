package ru.Den_Abr.ChatGuard.Listeners;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.Den_Abr.ChatGuard.MessageInfo;
import ru.Den_Abr.ChatGuard.ChatFilters.AbstractFilter;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Integration.AbstractIntegration;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class PlayerListener implements Listener {
	public static boolean globalMute = false;

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (AbstractIntegration.shouldSkip(e.getPlayer()))
			return;
		CGPlayer player = CGPlayer.get(e.getPlayer());
		if (globalMute && !player.hasPermission("chatguard.ignore.globalmute")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(Message.GLOBAL_MUTE.get());
			return;
		}

		if (!player.hasPermission("chatguard.ignore.cooldown")) {
			int cdtime = isCooldownOver(player);
			if (cdtime > 0) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(Message.WAIT_COOLDOWN.get().replace("{TIME}", cdtime + Message.SEC.get()));
				return;
			}
		}

		MessageInfo info = AbstractFilter.handleMessage(e.getMessage(), player);
		e.setMessage(info.getClearMessage());

		if (!info.getViolations().isEmpty()) { // nothing happened
			if (Settings.isCancellingEnabled()) {
				e.setCancelled(true);
			} else {
				player.setLastMessageTime(System.currentTimeMillis());
				player.getLastMessages().add(e.getMessage());
			}
			return;
		}
		player.setLastMessageTime(System.currentTimeMillis());
		player.getLastMessages().add(e.getMessage());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		if (AbstractIntegration.shouldSkip(e.getPlayer()))
			return;
		String comand = e.getMessage().split(" ")[0].toLowerCase();
		if (!Settings.getCheckCommands().containsKey(comand))
			return;
		String[] words = e.getMessage().split(" ");
		int offset = Settings.getCheckCommands().get(comand) + 1;
		words = (String[]) Arrays.copyOfRange(words, offset, words.length);
		String message = String.join(" ", words);
		if (message.isEmpty())
			return;

		CGPlayer player = CGPlayer.get(e.getPlayer());
		MessageInfo info = AbstractFilter.handleMessage(message, player);
		e.setMessage(comand + " " + info.getClearMessage());

		if (!info.getViolations().isEmpty()) {
			if (Settings.isCancellingEnabled()) {
				e.setCancelled(true);
			} else {
				player.setLastMessageTime(System.currentTimeMillis());
				player.getLastMessages().add(e.getMessage());
			}
			return;
		}
		player.setLastMessageTime(System.currentTimeMillis());
		player.getLastMessages().add(e.getMessage());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e) {
		if (AbstractIntegration.shouldSkip(e.getPlayer()))
			return;

	}

	private int isCooldownOver(CGPlayer pl) {
		if (!Settings.isCooldownEnabled())
			return 0;
		if (pl.getLastMessageTime() != -1) {
			long overtime = pl.getLastMessageTime() + TimeUnit.SECONDS.toMillis(Settings.getCooldown());
			int offset = (int) TimeUnit.MILLISECONDS.toSeconds(overtime - System.currentTimeMillis());
			if (offset > 0) {
				return offset;
			}
		}
		return 0;
	}
}
