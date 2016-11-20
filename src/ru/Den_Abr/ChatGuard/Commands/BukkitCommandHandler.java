package ru.Den_Abr.ChatGuard.Commands;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.ChatFilters.SwearFilter;
import ru.Den_Abr.ChatGuard.Configuration.Messages;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Configuration.Whitelist;
import ru.Den_Abr.ChatGuard.Listeners.PacketsListener;
import ru.Den_Abr.ChatGuard.Listeners.PlayerListener;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.MessagePair;
import ru.Den_Abr.ChatGuard.Utils.Utils;

public class BukkitCommandHandler {

	@Cmd(desc = "Show your warnings or (Player)'s", name = "info", perm = "chatguard.info", args = "(Player)")
	public void warnings(CommandSender cs, String[] args) {
		CGPlayer player;
		if (args.length == 0) {
			if (!(cs instanceof Player)) {
				cs.sendMessage("Only players can break the rules!");
				return;
			}
			player = CGPlayer.get((Player) cs);
		} else {
			if (cs.hasPermission("chatguard.info.others"))
				player = CGPlayer.get(Bukkit.getPlayer(args[0]));
			else {
				cs.sendMessage(Message.NO_PERMS.get());
				return;
			}
		}
		if (null == player) {
			cs.sendMessage(Message.PLAYER_NOT_FOUND.get());
			return;
		}
		cs.sendMessage(ChatColor.GOLD + player.getName() + ":");
		for (Violation v : Violation.values()) {
			if (v == Violation.BLACKCHAR)
				continue;
			cs.sendMessage(Message.valueOf(v.toString().toUpperCase()).get() + ": " + ChatColor.GRAY
					+ player.getViolationCount(v, true));
		}
		cs.sendMessage(Message.MUTED.get() + ": " + (player.isMuted() ? Message.YES : Message.NO));
		if (player.isMuted()) {
			cs.sendMessage(Message.EXPIRE_TIME.get() + ": " + ChatColor.RED
					+ Utils.getTimeInMaxUnit(player.getMuteTime() - System.currentTimeMillis()));
		}
	}

	@Cmd(desc = "Add new banned [WORD]", name = "ban", perm = "chatguard.banword", args = "[WORD]", min = 1)
	public void ban(CommandSender cs, String[] args) {
		String word = StringUtils.join(args, ' ').toLowerCase().trim();

		if (SwearFilter.addWord(word))
			cs.sendMessage(Message.SUCCESSFULLY.get());
		else
			cs.sendMessage(ChatColor.RED + word + " is already banned");
	}

	@Cmd(desc = "Remove banned [WORD]", name = "unban", perm = "chatguard.unbanword", args = "[WORD]", min = 1)
	public void unban(CommandSender cs, String[] args) {
		String word = StringUtils.join(args, ' ').toLowerCase().trim();
		if (SwearFilter.removeWord(word))
			cs.sendMessage(Message.SUCCESSFULLY.get());
		else
			cs.sendMessage(ChatColor.RED + word + " is not banned");
	}

	@Cmd(desc = "Do not mark [WORD] as advertising or swearing", name = "whitelist", perm = "chatguard.whitelistadd", args = "[WORD]", min = 1)
	public void whitelist(CommandSender cs, String[] args) {
		String word = StringUtils.join(args, ' ').toLowerCase().trim();
		Whitelist.add(word);
		cs.sendMessage(Message.SUCCESSFULLY.get());
	}

	@Cmd(desc = "Clear your (or everyone's/Player's) chat", name = "cc", perm = "chatguard.clearchat", args = "(ALL|Player)", max = 1)
	public void cc(CommandSender cs, String[] args) {
		if (args.length == 0) {
			if (cs instanceof Player) {
				Utils.clearChat((Player) cs);
				CGPlayer.get((Player) cs).getSentMessages().clear();
			} else {
				// Console or command block doesnt have chat
				cs.sendMessage("No.");
			}
			return;
		}
		if (cs.hasPermission("chatguard.clearchat.all") && args[0].equalsIgnoreCase("ALL")) {
			for (Player p : Utils.getOnlinePlayers()) {
				if (!p.hasPermission("chatguard.ignore.cc")) {
					Utils.clearChat(p);
					CGPlayer.get(p).getSentMessages().clear();
				}
			}
			return;
		}
		if (cs.hasPermission("chatguard.clearchar.others")) {
			Player p = Bukkit.getPlayer(args[0]);
			if (p == null || !p.isOnline()) {
				cs.sendMessage(Message.PLAYER_NOT_FOUND.get());
				return;
			} else {
				Utils.clearChat(p);
				CGPlayer.get(p).getSentMessages().clear();
			}
		} else {
			cs.sendMessage(Message.NO_PERMS.get());
		}
	}

	@Cmd(desc = "Toggle global mute", name = "globalmute", perm = "chatguard.globalmute", max = 0)
	public void globalmute(CommandSender cs, String[] args) {
		// ya
		PlayerListener.globalMute = !PlayerListener.globalMute;

		Bukkit.broadcastMessage(
				PlayerListener.globalMute ? Message.GLOBAL_MUTE_ENABLED.get() : Message.GLOBAL_MUTE_DISABLED.get());
	}

	@Cmd(desc = "Mute player for some reason", name = "mute", perm = "chatguard.mute", args = "[Player] ([Time] (Reason))", min = 1, max = 99)
	public void mute(CommandSender cs, String[] args) {
		Player pl = Bukkit.getPlayer(args[0]);
		if (pl == null) {
			cs.sendMessage(Message.PLAYER_NOT_FOUND.get());
			return;
		}
		CGPlayer cp = CGPlayer.get(pl);
		if (cp.isMuted()) {
			cs.sendMessage(Message.ALREADY_MUTED.get());
			return;
		}
		long time = Settings.getMaxMuteTime();
		if (args.length > 1) {
			long parsed = Utils.parseTime(args[1]);
			if (parsed > 0 && parsed <= Settings.getMaxMuteTime())
				time = parsed;
		}
		String reason = Message.DEFAULT_REASON.get();
		if (args.length > 2) {
			reason = ChatColor
					.translateAlternateColorCodes('&', StringUtils.join(Arrays.copyOfRange(args, 2, args.length), ' '))
					.trim();
		}
		cp.mute(time, reason);
		cs.sendMessage(
				Message.PLAYER_MUTED.get().replace("{REASON}", reason).replace("{TIME}", Utils.getTimeInMaxUnit(time)));
	}

	@Cmd(desc = "Unmute muted player", name = "unmute", perm = "chatguard.unmute", args = "[Player]", min = 1, max = 1)
	public void unMute(CommandSender cs, String[] args) {
		Player pl = Bukkit.getPlayer(args[0]);
		if (pl == null) {
			cs.sendMessage(Message.PLAYER_NOT_FOUND.get());
			return;
		}
		CGPlayer cp = CGPlayer.get(pl);
		if (!cp.isMuted()) {
			cs.sendMessage(Message.IS_NOT_MUTED.get());
			return;
		}
		cp.unMute();
		cs.sendMessage(Message.SUCCESSFULLY.get());
	}

	@Cmd(desc = "Clear some warnings", name = "clear", perm = "chatguard.clearwarnings", args = "(Type) (Player)", max = 2)
	public void clear(CommandSender cs, String[] args) {
		if (args.length > 0) {
			Violation v = Violation.get(args[0].toUpperCase());
			if ((v == null || v == Violation.BLACKCHAR) && !args[0].equalsIgnoreCase("ALL")) {
				StringBuilder sb = new StringBuilder();
				for (Violation allV : Violation.values()) {
					if (allV.getPunishmentSection().equals("none"))
						continue;
					sb.append(allV).append(" ");// looks fine
				}
				cs.sendMessage(ChatColor.GOLD + "Available types: " + ChatColor.GREEN + sb.toString() + "ALL");
				return;
			}
			if (args.length == 1) {
				CGPlayer.clearAllWarnings(v, true);
			} else if (args.length == 2) {
				CGPlayer p = CGPlayer.get(args[1]);
				if (p == null) {
					cs.sendMessage(Message.PLAYER_NOT_FOUND.get());
					return;
				}
				p.clearWarnings(args[0].equalsIgnoreCase("ALL") ? null : v, true);
			}
		} else
			CGPlayer.clearAllWarnings(null, false);
		cs.sendMessage(Message.SUCCESSFULLY.get());

	}

	@Cmd(desc = "Clear messages of specified player", name = "of", perm = "chatguard.clearof", min = 1)
	public void of(CommandSender cs, String[] args) {
		if(!Settings.usePackets()) {
			cs.sendMessage(ChatColor.RED + "Enable 'use packets' settings first.");
			return;
		}
		CGPlayer cp = CGPlayer.get(args[0]);
		if (cp == null) {
			cs.sendMessage(Message.PLAYER_NOT_FOUND.get());
			return;
		}
		for (Player p : Utils.getOnlinePlayers()) {
			p.setMetadata("clearing", new FixedMetadataValue(ChatGuardPlugin.getInstance(), true));
			CGPlayer clp = CGPlayer.get(p);
			Utils.clearChat(p);
			pairs: for (MessagePair mp : clp.getSentMessages()) {
				for (String am : cp.getAllMessages()) {
					if (mp.getKey().toLowerCase().contains(am))
						continue pairs;
				}
				if (!mp.isOld())
					PacketsListener.sendComponent(p, WrappedChatComponent.fromJson(mp.getValue()));
				else
					p.sendMessage(mp.getValue());
			}
			p.removeMetadata("clearing", ChatGuardPlugin.getInstance());
		}
	}

	@Cmd(desc = "Warn [Player]", name = "warn", perm = "chatguard.warn", args = "[Player] [Type]", min = 2, max = 2)
	public void warn(CommandSender cs, String[] args) {
		Player p = Bukkit.getPlayer(args[0]);
		if (p == null || !p.isOnline() /* hello cauldron */) {
			cs.sendMessage(Message.PLAYER_NOT_FOUND.get());
			return;
		}
		String type = args[1].toUpperCase();
		Violation v = Violation.get(type);
		if (v == null || v == Violation.BLACKCHAR) {
			StringBuilder sb = new StringBuilder();
			for (Violation allV : Violation.values()) {
				if (allV.getPunishmentSection().equals("none"))
					continue;
				sb.append(allV).append(" ");// looks fine
			}
			cs.sendMessage(ChatColor.GOLD + "Available types: " + ChatColor.GREEN + sb.toString().trim());
			return;
		}
		CGPlayer cgp = CGPlayer.get(p);
		cgp.handleViolation(v, -1);
		cs.sendMessage(Message.SUCCESSFULLY.get());
	}

	@Cmd(desc = "Reload plugin configurations", name = "reload", perm = "chatguard.reload", max = 0)
	public void reload(CommandSender cs, String[] args) {
		Settings.load(ChatGuardPlugin.getInstance());
		Messages.load(ChatGuardPlugin.getInstance());
		Whitelist.load(ChatGuardPlugin.getInstance());
		ChatGuardPlugin.getInstance().registerFilters();

		cs.sendMessage(ChatColor.GRAY + "Reload complete.");
	}

	@Cmd(desc = "Show this page", name = "help", perm = "")
	public void help(CommandSender cs, String[] args) {
		cs.sendMessage(ChatColor.GOLD + ChatGuardPlugin.getInstance().getDescription().getName() + " v"
				+ ChatColor.GREEN + ChatGuardPlugin.getInstance().getDescription().getVersion() + ChatColor.GOLD
				+ " by " + ChatColor.DARK_PURPLE + ChatGuardPlugin.getInstance().getDescription().getAuthors().get(0));
		for (SubCommand sc : CommandManager.instance.subComs.getCommands()) {
			if (sc.isPermitted(cs))
				sc.printHelp(cs, "chatguard");
		}
	}

}
