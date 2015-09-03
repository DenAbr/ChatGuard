package ru.Den_Abr.ChatGuard.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.ChatFilters.SwearFilter;
import ru.Den_Abr.ChatGuard.Configuration.Messages;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Configuration.Whitelist;
import ru.Den_Abr.ChatGuard.Listeners.PlayerListener;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.Utils;

public class SubCommandHandler {

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
			cs.sendMessage(Message.valueOf(v.toString().toUpperCase()).get()
					+ ": " + ChatColor.GRAY + player.getViolationCount(v, true));
		}
	}

	@Cmd(desc = "Add new banned [WORD]", name = "ban", perm = "chatguard.banword", args = "[WORD]", min = 1)
	public void add(CommandSender cs, String[] args) {
		String word = args[0].toLowerCase();
		SwearFilter.addWord(word);
		cs.sendMessage(Message.SUCCESSFULLY.get());
	}

	@Cmd(desc = "Do not mark [WORD] as advertising or swearing", name = "whitelist", perm = "chatguard.whitelistadd", args = "[WORD]", min = 1)
	public void whitelist(CommandSender cs, String[] args) {
		String word = args[0].toLowerCase();
		Whitelist.add(word);
		cs.sendMessage(Message.SUCCESSFULLY.get());
	}

	@Cmd(desc = "Clear your (or everyone's/Player's) chat", name = "cc", perm = "chatguard.clearchat", args = "(ALL|Player)", max = 1)
	public void cc(CommandSender cs, String[] args) {
		if (args.length == 0) {
			if (cs instanceof Player) {
				Utils.clearChat((Player) cs);
			} else {
				// Console or command block doesnt have chat
				cs.sendMessage("No.");
			}
			return;
		}
		if (cs.hasPermission("chatguard.clearchat.all")
				&& args[0].equalsIgnoreCase("ALL")) {
			for (Player p : Utils.getOnlinePlayers()) {
				Utils.clearChat(p);
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
			}
		} else {
			cs.sendMessage(Message.NO_PERMS.get());
		}
	}

	@Cmd(desc = "Toggle global mute", name = "globalmute", perm = "chatguard.globalmute", max = 0)
	public void globalmute(CommandSender cs, String[] args) {
		// ya
		PlayerListener.globalMute = !PlayerListener.globalMute;

		Bukkit.broadcastMessage(PlayerListener.globalMute ? Message.GLOBAL_MUTE_ENABLED
				.get() : Message.GLOBAL_MUTE_DISABLED.get());
	}

	@Cmd(desc = "Clear some warnings", name = "clear", perm = "chatguard.clearwarnings", args = "(Type) (Player)", max = 2)
	public void clear(CommandSender cs, String[] args) {
		if (args.length > 0) {
			Violation v = Violation.get(args[0].toUpperCase());
			if (v == null || v == Violation.BLACKCHAR) {
				StringBuilder sb = new StringBuilder();
				for (Violation allV : Violation.values()) {
					if (allV.getPunishmentSection().equals("none"))
						continue;
					sb.append(allV).append(" ");// looks fine
				}
				cs.sendMessage(ChatColor.GOLD + "Available types: "
						+ ChatColor.GREEN + sb.toString().trim());
				return;
			}
			if (args.length == 1) {
				CGPlayer.clearAllWarnings(v, true);
			} else if (args.length == 2) {
				Player p = Bukkit.getPlayer(args[0]);
				if (p == null || !p.isOnline() /* hello cauldron */) {
					cs.sendMessage(Message.PLAYER_NOT_FOUND.get());
					return;
				}
				CGPlayer.get(p).clearWarnings(v, true);
			}
		} else
			CGPlayer.clearAllWarnings(null, false);
		cs.sendMessage(Message.SUCCESSFULLY.get());
		
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
			cs.sendMessage(ChatColor.GOLD + "Available types: "
					+ ChatColor.GREEN + sb.toString().trim());
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
		cs.sendMessage(ChatColor.GOLD
				+ ChatGuardPlugin.getInstance().getDescription().getName()
				+ " v"
				+ ChatColor.GREEN
				+ ChatGuardPlugin.getInstance().getDescription().getVersion()
				+ ChatColor.GOLD
				+ " by "
				+ ChatColor.DARK_PURPLE
				+ ChatGuardPlugin.getInstance().getDescription().getAuthors()
						.get(0));
		for (SubCommand sc : CommandManager.instance.subComs.getCommands()) {
			sc.printHelp(cs, "chatguard");
		}
	}

}
