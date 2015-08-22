package ru.Den_Abr.ChatGuard.Commands;

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

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Utils.SubCommandSet;

public class CommandManager implements CommandExecutor {
	protected static CommandManager instance;
	protected static SubCommandHandler handler;
	private ChatGuardPlugin plugin;
	protected SubCommandSet subComs;

	public CommandManager(ChatGuardPlugin pl) {
		handler = new SubCommandHandler();
		plugin = pl;
		registerCommands();
		instance = this;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (arg3.length == 0) {
			arg0.sendMessage(ChatColor.GOLD + plugin.getDescription().getName() + " v" + ChatColor.GREEN
					+ plugin.getDescription().getVersion() + ChatColor.GOLD + " by " + ChatColor.DARK_PURPLE
					+ plugin.getDescription().getAuthors().get(0));
			return true;
		}

		String sub = arg3[0].toLowerCase();
		String[] args = Arrays.copyOfRange(arg3, 1, arg3.length);
		if (!subComs.contains(sub)) {
			arg0.sendMessage("Unknown command. Type '/" + arg2 + " help' for help");
			return true;
		}
		SubCommand sc = subComs.getCommand(sub);
		if (!sc.isPermitted(arg0)) {
			arg0.sendMessage(ChatColor.RED + "You don't have permissions for this command! " + ChatColor.GRAY + "("
					+ sc.perm + ")");
			return true;
		}
		if (!sc.isArgsValid(args)) {
			sc.printHelp(arg0, arg2);
			return true;
		}
		sc.execute(arg0, args);
		return true;
	}

	private void registerCommands() {
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

	private void setTabCompleter() {
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

}
