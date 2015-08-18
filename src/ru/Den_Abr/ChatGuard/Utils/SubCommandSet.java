package ru.Den_Abr.ChatGuard.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import ru.Den_Abr.ChatGuard.Commands.SubCommand;

public class SubCommandSet {
	private HashMap<String, SubCommand> subCommands = new HashMap<String, SubCommand>();

	public SubCommandSet() {
	}

	public void add(SubCommand sc) {
		subCommands.put(sc.getName(), sc);
	}

	public SubCommand getCommand(String sub) {
		if (contains(sub.toLowerCase())) {
			return subCommands.get(sub);
		}
		return null;
	}

	public Collection<SubCommand> getCommands() {
		return subCommands.values();
	}

	public Set<String> getNames() {
		return subCommands.keySet();
	}

	public boolean contains(String sub) {
		return subCommands.containsKey(sub.toLowerCase());
	}

}