package ru.Den_Abr.ChatGuard.Common.Integration;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import ru.Den_Abr.ChatGuard.Bukkit.ChatGuardPlugin;

public abstract class AbstractIntegration implements IntegratedPlugin {
	private static Set<IntegratedPlugin> plugins = new HashSet<>();

	public static Set<IntegratedPlugin> getIntegratedPlugins() {
		return plugins;
	}

	public static boolean shouldSkip(Player player) {
		
		for (IntegratedPlugin pl : getIntegratedPlugins()) {
			if (pl.skipPlayer(player)) {
				ChatGuardPlugin.debug(1,
						pl.getPlugin().getName() + " thinks we should skip " + player.getName());
				return true;
			}
		}
		return false;
	}
}
