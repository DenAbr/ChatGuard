package ru.Den_Abr.ChatGuard.Integration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import uk.org.whoami.authme.cache.auth.PlayerCache;

public class AuthMeLegacy extends AbstractIntegration {
	private JavaPlugin plugin;

	@Override
	public boolean skipPlayer(Player p) {
		return !PlayerCache.getInstance().isAuthenticated(p.getName());
	}

	@Override
	public JavaPlugin getPlugin() {
		return plugin;
	}

	@Override
	public boolean load() {
		Plugin pl = Bukkit.getPluginManager().getPlugin("AuthMe");
		if (null == pl)
			return false;
		if (pl.getDescription().getAuthors().contains("whoami")) {
			plugin = (JavaPlugin) pl;
		}
		return plugin != null && pl.isEnabled();
	}
}
