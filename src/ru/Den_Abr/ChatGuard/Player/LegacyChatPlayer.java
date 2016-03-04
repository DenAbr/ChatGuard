package ru.Den_Abr.ChatGuard.Player;

import org.bukkit.entity.Player;

/** This implementation is needed for intagrating UUID system in the future **/

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

	@Override 
	public Player getPlayer() {
		return p;
	}

	@Override
	public void updatePlayer(Player p) {
		this.p = p;
	}

}
