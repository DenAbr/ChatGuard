package ru.Den_Abr.ChatGuard.Player;

import org.bukkit.entity.Player;

public class LegacyChatPlayer extends CGPlayer {
	private Player p;

	public LegacyChatPlayer(Player p) {
		this.p = p;
	}

	@Override
	public boolean hasPermission(String perm) {
		return p.hasPermission(perm);
	}

	@Override
	public String getName() {
		return p.getName();
	}
	
	
}
