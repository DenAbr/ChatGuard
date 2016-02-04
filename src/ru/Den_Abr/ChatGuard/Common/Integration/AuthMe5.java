package ru.Den_Abr.ChatGuard.Common.Integration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.xephi.authme.api.NewAPI;
import ru.Den_Abr.ChatGuard.Common.Utils.Utils;

public class AuthMe5 extends AbstractIntegration {
	private JavaPlugin plugin;

	@Override
	public boolean skipPlayer(Player p) {
		return !NewAPI.getInstance().isAuthenticated(p);
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
		if (Utils.isInt(pl.getDescription().getVersion().split("\\.")[0])
				&& Integer
						.parseInt(pl.getDescription().getVersion().split("\\.")[0]) >= 5) {
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
