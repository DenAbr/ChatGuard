package ru.Den_Abr.ChatGuard.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Settings;

public class SubCommandHandler {

	/**
	 * TODO Command handling
	 */

	@Cmd(desc = "Show your warnings or (Player)'s", name = "warnings", perm = "chatguard.warns", args = "(Player)")
	public void warnings(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Add new swear [WORD]", name = "add", perm = "chatguard.addword", args = "[WORD]", min = 1)
	public void add(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Remove swear [WORD] from file", name = "remove", perm = "chatguard.removeword", args = "[WORD]", min = 1)
	public void remove(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Do not mark [WORD] as advertising or swearing", name = "whitelist", perm = "chatguard.whitelistadd", args = "[WORD]", min = 1)
	public void whitelist(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Clear your (or everyone's/Player's) chat", name = "cc", perm = "chatguard.clearchat", args = "(ALL|Player)", max = 1)
	public void cc(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Show your warnings or (Player)'s", name = "globalmute", perm = "chatguard.globalmute", args = "", max = 0)
	public void globalmute(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Clear all warnings", name = "clear", perm = "chatguard.clear", args = "", max = 0)
	public void clear(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Give a warning for [Player]", name = "warn", perm = "chatguard.addwarn", args = "[Player] (Type)", min = 1, max = 2)
	public void warn(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Lower [Player's] warnings", name = "unwarn", perm = "chatguard.removewarn", args = "[Player] (Type)", min = 1, max = 2)
	public void unwarn(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Mute [Players] for some [Reason]", name = "mute", perm = "chatguard.mute", args = "[Player] [Reason]", min = 2, max = 30)
	public void mute(CommandSender cs, String[] args) {

	}

	@Cmd(desc = "Allow [Player] to send messages", name = "unmute", perm = "chatguard.unmute", args = "[Player]", min = 1)
	public void unmute(CommandSender cs, String[] args) {
		
	}

	@Cmd(desc = "Reload plugin configurations", name = "reload", perm = "chatguard.reload", args = "", max = 0)
	public void reload(CommandSender cs, String[] args) {
		Settings.load(ChatGuardPlugin.getInstance());
		ChatGuardPlugin.getInstance().registerFilters();
		cs.sendMessage(ChatColor.GRAY + "Reload complete.");
	}

	@Cmd(desc = "Show this page", name = "help", perm = "", args = "")
	public void help(CommandSender cs, String[] args) {
		cs.sendMessage(ChatColor.GOLD + ChatGuardPlugin.getInstance().getDescription().getName() + " v" + ChatColor.GREEN
				+ ChatGuardPlugin.getInstance().getDescription().getVersion() + ChatColor.GOLD + " by " + ChatColor.DARK_PURPLE
				+ ChatGuardPlugin.getInstance().getDescription().getAuthors().get(0));
		for (SubCommand sc : CommandManager.instance.subComs.getCommands()) {
			sc.printHelp(cs, "chatguard");
		}
	}

}
