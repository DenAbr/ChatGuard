package ru.Den_Abr.ChatGuard.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerListener implements Listener {
	private static PlayerListener instance;

	public PlayerListener() {
		instance = this;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {

	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {

	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e) {

	}
}
