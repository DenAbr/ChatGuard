package ru.Den_Abr.ChatGuard.Commands;

import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Types.SubCommand;
import ru.Den_Abr.ChatGuard.Types.SubCommandSet;

public class CommandManager implements CommandExecutor {
	private SubCommandHandler handler;
	private ChatGuardPlugin plugin;
	private SubCommandSet subComs;

	public CommandManager(ChatGuardPlugin pl) {
		handler = new SubCommandHandler();
		plugin = pl;
		registerCommands();
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg3.length == 0) {
			printHelp(arg0, arg2);
			return true;
		}

		String sub = arg3[0].toLowerCase();
		if (!subComs.contains(sub)) {
			arg0.sendMessage("Команды нет такой");
			return true;
		}
		SubCommand sc = subComs.getCommand(sub);
		if (!sc.isPermitted(arg0)) {
			arg0.sendMessage("Нет прав");
			return true;
		}
		if (!sc.isArgsValid(arg3)) {
			sc.printHelp(arg0, arg2);
			return true;
		}
		sc.execute(arg0, arg3);
		return true;
	}

	private void registerCommands() {
		subComs = new SubCommandSet();
		for (Method method : handler.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(Cmd.class)) {
				continue;
			}
			SubCommand subCommand = new SubCommand(
					method.getAnnotation(Cmd.class));
			subComs.add(subCommand);
		}
	}

	private void printHelp(CommandSender cs, String l) {
		cs.sendMessage(ChatColor.GOLD + plugin.getDescription().getName()
				+ " v" + ChatColor.GREEN + plugin.getDescription().getVersion()
				+ " by " + ChatColor.DARK_PURPLE
				+ plugin.getDescription().getAuthors().get(0));
		for (SubCommand sc : subComs.getCommands()) {
			sc.printHelp(cs, l);
		}
	}

}
