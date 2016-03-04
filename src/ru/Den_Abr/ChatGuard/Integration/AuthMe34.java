package ru.Den_Abr.ChatGuard.Integration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.xephi.authme.api.API;
import ru.Den_Abr.ChatGuard.Utils.Utils;

public class AuthMe34 extends AbstractIntegration {
	private JavaPlugin plugin;

	@Override
	public boolean skipPlayer(Player p) {
		return !API.isAuthenticated(p);
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
		if (pl.getDescription().getAuthors().get(0).equals("Xephi59")
				&& Utils.isInt(pl.getDescription().getVersion().split("\\.")[0])
				&& Integer
						.parseInt(pl.getDescription().getVersion().split("\\.")[0]) < 5) {
			plugin = (JavaPlugin) pl;
		}
		return plugin != null && pl.isEnabled();
	}

	@Override
	public void register() {
		if (load()) {
			getIntegratedPlugins().add(this);
		}
	}

}
