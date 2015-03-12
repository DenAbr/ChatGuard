package ru.Den_Abr.ChatGuard.AuthPlugins;

import org.bukkit.entity.Player;

public class LoginSecurity extends Integrator {

	public boolean isLogged(Player p) {
		return !com.lenis0012.bukkit.ls.LoginSecurity.instance.authList.containsKey(p.getName());
	}

}
