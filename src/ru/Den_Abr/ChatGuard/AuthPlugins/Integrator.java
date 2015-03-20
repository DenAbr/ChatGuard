package ru.Den_Abr.ChatGuard.AuthPlugins;

import org.bukkit.entity.Player;

public abstract class Integrator {

	public static boolean isAuthMeReloaded = false;
	public static boolean isXAuth = false;
	public static boolean isLoginSecurity = false;
	public static boolean isAuthMeAncient = false;

	public static Integrator getIntegrator() {
		if (isAuthMeReloaded)
			return new AuthMeReloaded();

		if (isAuthMeAncient)
			return new AuthMe();

		if (isXAuth)
			return new xAuth();

		if (isLoginSecurity)
			return new LoginSecurity();

		return null;
	}

	public abstract boolean isLogged(Player p);
	public abstract String getName() ;


}
