package ru.Den_Abr.ChatGuard.Player;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;

public abstract class CGPlayer {
	private static List<CGPlayer> cache = new LinkedList<>();

	public abstract boolean hasPermission(String perm);

	public abstract String getName();

	public static CGPlayer get(Player p) {
		CGPlayer temp = new LegacyChatPlayer(p);
		if (cache.contains(new LegacyChatPlayer(p))) {
			return cache.get(cache.indexOf(temp));
		}
		ChatGuardPlugin.debug(2, cache);
		cache.add(temp);
		return temp;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return getName().equalsIgnoreCase(obj.toString());
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		return getName().equalsIgnoreCase(((CGPlayer) obj).getName());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + getName() + "}";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
