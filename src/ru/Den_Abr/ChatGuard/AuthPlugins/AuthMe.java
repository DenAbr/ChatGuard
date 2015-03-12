package ru.Den_Abr.ChatGuard.AuthPlugins;

import org.bukkit.entity.Player;

import uk.org.whoami.authme.cache.auth.PlayerCache;

public class AuthMe extends Integrator {

	public boolean isLogged(Player p) {
		return PlayerCache.getInstance().isAuthenticated(p.getName());
	}
}
