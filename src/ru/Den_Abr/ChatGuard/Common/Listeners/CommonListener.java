package ru.Den_Abr.ChatGuard.Common.Listeners;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import ru.Den_Abr.ChatGuard.Bukkit.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Common.ChatGuard;
import ru.Den_Abr.ChatGuard.Common.MessageInfo;
import ru.Den_Abr.ChatGuard.Common.ChatFilters.AbstractFilter;
import ru.Den_Abr.ChatGuard.Common.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Common.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Common.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Common.Utils.Utils;

public abstract class CommonListener {

	public static MessageInfo handleMessage(String message, CGPlayer player) {

		MessageInfo info = new MessageInfo();
		if (ChatGuard.GLOBAL_MUTE && !player.hasPermission("chatguard.ignore.globalmute")) {
			info.cancel(true);
			player.sendMessage(Message.GLOBAL_MUTE.get());
			return info;
		}

		if (player.isMuted()) {
			player.sendMessage(Message.UR_MUTED.get().replace("{REASON}", player.getMuteReason()).replace("{TIME}",
					Utils.getTimeInMaxUnit(player.getMuteTime() - System.currentTimeMillis())));
			info.cancel(true);
			return info;
		}

		if (!player.hasPermission("chatguard.ignore.cooldown")) {
			int cdtime = isCooldownOver(player);
			ChatGuardPlugin.debug(1, player.getName() + "'s CD " + cdtime);
			if (cdtime > 0) {
				info.cancel(true);
				player.sendMessage(Message.WAIT_COOLDOWN.get().replace("{TIME}", cdtime + Message.SEC.get()));
				return info;
			}
		}

		info = AbstractFilter.handleMessage(message, player, false);

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

	public static MessageInfo handleCommand(String message, CGPlayer player) {
		if (Settings.getCheckCommands().isEmpty())
			return null;
		String comand = message.split(" ")[0].toLowerCase();
		comand = Utils.getOriginalCommand(comand);
		ChatGuardPlugin.debug(2, "Command: " + comand, "Commands list: " + Settings.getCheckCommands());
		if (!Settings.getCheckCommands().containsKey(comand))
			return null;

		String[] words = message.split(" ");
		int offset = Settings.getCheckCommands().get(comand) + 1;

		String skipped = "";
		if (offset > 1) {
			skipped = StringUtils.join(Arrays.copyOfRange(words, 1, offset), ' ') + " ";
		}
		String cutMessage = "";
		if (offset <= words.length) {
			cutMessage = StringUtils.join(Arrays.copyOfRange(words, offset, words.length), ' ');
		} else {
			ChatGuardPlugin.debug(1,
					"Something wrong with '" + message + "'. Offset: " + offset + ", array lenght: " + words.length);
		}
		ChatGuardPlugin.debug(2, "Fixed part: " + cutMessage, "Skipped part: " + skipped);

		comand += " " + skipped;

		if (cutMessage.isEmpty())
			return null;

		if (player.isMuted()) {
			player.sendMessage(Message.UR_MUTED.get().replace("{REASON}", player.getMuteReason()).replace("{TIME}",
					Utils.getTimeInMaxUnit(player.getMuteTime() - System.currentTimeMillis())));
			MessageInfo info = new MessageInfo();
			info.cancel(true);
			return info;
		}

		MessageInfo info = AbstractFilter.handleMessage(cutMessage, player, false);
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
}
