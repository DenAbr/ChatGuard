package ru.Den_Abr.ChatGuard.Bukkit.Command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import ru.Den_Abr.ChatGuard.Bukkit.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Common.Commands.Cmd;
import ru.Den_Abr.ChatGuard.Common.Commands.CommandManager;
import ru.Den_Abr.ChatGuard.Common.Commands.SubCommand;
import ru.Den_Abr.ChatGuard.Common.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Common.Utils.SubCommandSet;

public class BukkitCommandManager extends CommandManager implements CommandExecutor {
	private ChatGuardPlugin plugin;

	public BukkitCommandManager(ChatGuardPlugin pl) {
		super();
		this.plugin = pl;
	}

	public void registerCommands() {
		subComs = new SubCommandSet();
		for (Method method : handler.getClass().getDeclaredMethods()) {
			if (!method.isAnnotationPresent(Cmd.class)) {
				continue;
			}
			ChatGuardPlugin.debug(2, method, Arrays.asList(method.getAnnotations()));
			SubCommand subCommand = new SubCommand(method.getAnnotation(Cmd.class), method);
			subComs.add(subCommand);
		}
		setTabCompleter();
	}

	public void setTabCompleter() {
		ChatGuardPlugin.getInstance().getCommand("cg").setTabCompleter(new TabCompleter() {

			@Override
			public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
				if (arg3.length == 1) {
					return (List<String>) StringUtil.copyPartialMatches(arg3[0], subComs.getNames(),
							new ArrayList<String>());
				}
				return null;
			}
		});
	}

	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] arg3) {
		if (arg3.length == 0) {
			cs.sendMessage(ChatColor.GOLD + plugin.getDescription().getName() + " v" + ChatColor.GREEN
					+ plugin.getDescription().getVersion() + ChatColor.GOLD + " by " + ChatColor.DARK_PURPLE
					+ plugin.getDescription().getAuthors().get(0));
			return true;
		}

		CGPlayer arg0 = CGPlayer.get(cs);

		String sub = arg3[0].toLowerCase();
		String[] args = Arrays.copyOfRange(arg3, 1, arg3.length);
		if (!subComs.contains(sub)) {
			arg0.sendMessage("Unknown command. Type '/" + arg2 + " help' for help");
			return true;
		}
		SubCommand sc = subComs.getCommand(sub);
		if (!sc.isPermitted(arg0)) {
			arg0.sendMessage(ChatColor.RED + "You don't have permissions for this command! " + ChatColor.GRAY + "("
					+ sc.getPermission() + ")");
			return true;
		}
		if (!sc.isArgsValid(args)) {
			sc.printHelp(arg0, arg2);
			return true;
		}
		sc.execute(arg0, args);
		return true;
	}
}
