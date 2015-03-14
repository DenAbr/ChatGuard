package ru.Den_Abr.ChatGuard.Types;

import java.util.Collection;
import java.util.HashMap;

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

	public boolean contains(String sub) {
		return subCommands.containsKey(sub.toLowerCase());
	}

}