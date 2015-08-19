package ru.Den_Abr.ChatGuard.Integration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import uk.org.whoami.authme.cache.auth.PlayerCache;

public class AuthMe extends AbstractPlugin {
	private uk.org.whoami.authme.AuthMe plugin;

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
			plugin = (uk.org.whoami.authme.AuthMe) pl;
		}
		return plugin != null && pl.isEnabled();
	}

	@Override
	public void register() {
		if (load()) {
			getIntegratedPlugins().add(this);
			// Anything else?
		}
	}
}
