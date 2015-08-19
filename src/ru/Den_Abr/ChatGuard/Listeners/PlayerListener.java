package ru.Den_Abr.ChatGuard.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.Den_Abr.ChatGuard.ViolationInfo;
import ru.Den_Abr.ChatGuard.ChatFilters.AbstractFilter;
import ru.Den_Abr.ChatGuard.Integration.AbstractPlugin;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (AbstractPlugin.shouldSkip(e.getPlayer()))
			return;
		// Testing...
		ViolationInfo info = AbstractFilter.handleMessage(e.getMessage(), CGPlayer.get(e.getPlayer()));
		e.setMessage(info.getClearMessage());

	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		if (AbstractPlugin.shouldSkip(e.getPlayer()))
			return;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e) {
		if (AbstractPlugin.shouldSkip(e.getPlayer()))
			return;
	}
}
