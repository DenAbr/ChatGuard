package ru.Den_Abr.ChatGuard.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Listeners.PlayerListener;
import ru.Den_Abr.ChatGuard.Utils.Utils;

public class SubCommandHandler {

	/**
	 * TODO Command handling
	 */

	@Cmd(desc = "Show your warnings or (Player)'s", name = "warnings", perm = "chatguard.warns", args = "(Player)")
	public void warnings(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Add new swear [WORD]", name = "ban", perm = "chatguard.addword", args = "[WORD]", min = 1)
	public void add(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Do not mark [WORD] as advertising or swearing", name = "whitelist", perm = "chatguard.whitelistadd", args = "[WORD]", min = 1)
	public void whitelist(CommandSender cs, String[] args) {

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
		if (cs.hasPermission("chatguard.clearchat.all") && args[0].equalsIgnoreCase("ALL")) {
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

	@Cmd(desc = "Show your warnings or (Player)'s", name = "globalmute", perm = "chatguard.globalmute", args = "", max = 0)
	public void globalmute(CommandSender cs, String[] args) {
		// ya
		PlayerListener.globalMute = !PlayerListener.globalMute;

		Bukkit.broadcastMessage(
				PlayerListener.globalMute ? Message.GLOBAL_MUTE_ENABLED.get() : Message.GLOBAL_MUTE_DISABLED.get());
	}

	@Cmd(desc = "Clear some warnings", name = "clear", perm = "chatguard.clear", args = "(Type) (Player)", max = 2)
	public void clear(CommandSender cs, String[] args) {
		
	}

	@Cmd(desc = "Warn [Player]", name = "warn", perm = "chatguard.addwarn", args = "[Player] (Type)", min = 1, max = 2)
	public void warn(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Reload plugin configurations", name = "reload", perm = "chatguard.reload", args = "", max = 0)
	public void reload(CommandSender cs, String[] args) {
		Settings.load(ChatGuardPlugin.getInstance());
		ChatGuardPlugin.getInstance().registerFilters();
		cs.sendMessage(ChatColor.GRAY + "Reload complete.");
	}

	@Cmd(desc = "Show this page", name = "help", perm = "", args = "")
	public void help(CommandSender cs, String[] args) {
		cs.sendMessage(ChatColor.GOLD + ChatGuardPlugin.getInstance().getDescription().getName() + " v"
				+ ChatColor.GREEN + ChatGuardPlugin.getInstance().getDescription().getVersion() + ChatColor.GOLD
				+ " by " + ChatColor.DARK_PURPLE + ChatGuardPlugin.getInstance().getDescription().getAuthors().get(0));
		for (SubCommand sc : CommandManager.instance.subComs.getCommands()) {
			sc.printHelp(cs, "chatguard");
		}
	}

}
