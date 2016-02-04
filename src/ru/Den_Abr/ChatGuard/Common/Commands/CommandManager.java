package ru.Den_Abr.ChatGuard.Common.Commands;

import java.util.Collection;

import ru.Den_Abr.ChatGuard.Common.Utils.SubCommandSet;

public abstract class CommandManager {
	protected static CommandManager instance;
	protected SubCommandSet subComs;
	protected CommandHandler handler;

	public CommandManager() {
		instance = this;
		registerCommands();
	}

	public abstract void registerCommands();

	public abstract void setTabCompleter();

	public static CommandManager getInstance() {
		return instance;
	}

	public Collection<SubCommand> getSubCommands() {
		return subComs.getCommands();
	}

	public CommandHandler getHandler() {
		return handler;
	}
}
