package ru.Den_Abr.ChatGuard.AuthPlugins;

import org.bukkit.entity.Player;

import fr.xephi.authme.api.API;

public class AuthMeReloaded extends Integrator {

	public boolean isLogged(Player p) {
		return API.isAuthenticated(p);
	}

	@Override
	public String getName() {
		return "AuthMe Reloaded";
	}


}
