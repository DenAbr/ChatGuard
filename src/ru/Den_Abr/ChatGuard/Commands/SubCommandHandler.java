package ru.Den_Abr.ChatGuard.Commands;

import org.bukkit.command.CommandSender;

public class SubCommandHandler {

	/**
	 * TODO Command handling
	 */

	@Cmd(desc = "Show your warnings or (Player)'s", name = "warnings", perm = "chatguard.warns", vals = "(Player)")
	public void warnings(CommandSender cs, String args) {

	}

	@Cmd(desc = "Add new swear [WORD]", name = "add", perm = "chatguard.addword", vals = "[WORD]", min = 1)
	public void add(CommandSender cs, String args) {

	}

	@Cmd(desc = "Remove swear [WORD] from file", name = "remove", perm = "chatguard.removeword", vals = "[WORD]", min = 1)
	public void remove(CommandSender cs, String args) {

	}

	@Cmd(desc = "Do not mark [WORD] as advertising or swearing", name = "whitelist", perm = "chatguard.whitelistadd", vals = "[WORD]", min = 1)
	public void whitelist(CommandSender cs, String args) {

	}

	@Cmd(desc = "Clear your (or everyone's/Player's) chat", name = "cc", perm = "chatguard.clearchat", vals = "(ALL|Player)", max = 1)
	public void cc(CommandSender cs, String args) {

	}

	@Cmd(desc = "Show your warnings or (Player)'s", name = "globalmute", perm = "chatguard.globalmute", vals = "", max = 0)
	public void globalmute(CommandSender cs, String args) {

	}

	@Cmd(desc = "Clear all warnings", name = "clear", perm = "chatguard.clear", vals = "", max = 0)
	public void clear(CommandSender cs, String args) {

	}

	@Cmd(desc = "Reload plugin configurations", name = "reload", perm = "chatguard.reload", vals = "", max = 0)
	public void reload(CommandSender cs, String args) {

	}

	@Cmd(desc = "Give a warning for [Player]", name = "warn", perm = "chatguard.addwarn", vals = "[Player] (Type)", min = 1, max = 2)
	public void warn(CommandSender cs, String args) {

	}

	@Cmd(desc = "Lower [Player's] warnings", name = "unwarn", perm = "chatguard.removewarn", vals = "[Player] (Type)", min = 1, max = 2)
	public void unwarn(CommandSender cs, String args) {

	}

	@Cmd(desc = "Mute [Players] for some [Reason]", name = "mute", perm = "chatguard.mute", vals = "[Player] [Reason]",min = 2, max = 30)
	public void mute(CommandSender cs, String args) {

	}

	@Cmd(desc = "Allow [Player] to send messages", name = "unmute", perm = "chatguard.unmute", vals = "[Player]", min = 1)
	public void unmute(CommandSender cs, String args) {

	}

	@Cmd(desc = "Show help for you", name = "help", perm = "", vals = "")
	public void help(CommandSender cs, String args) {

	}
}
