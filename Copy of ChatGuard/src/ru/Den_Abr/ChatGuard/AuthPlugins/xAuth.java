package ru.Den_Abr.ChatGuard.AuthPlugins;

import org.bukkit.entity.Player;

public class xAuth extends Integrator {

	public boolean isLogged(Player p) {
		return de.luricos.bukkit.xAuth.xAuth.getPlugin().getPlayerManager()
				.getPlayer(p).isAuthenticated();
	}
}
