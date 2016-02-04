package ru.Den_Abr.ChatGuard.Bukkit.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.Den_Abr.ChatGuard.Common.MessageInfo;
import ru.Den_Abr.ChatGuard.Common.Integration.AbstractIntegration;
import ru.Den_Abr.ChatGuard.Common.Listeners.CommonListener;
import ru.Den_Abr.ChatGuard.Common.Player.CGPlayer;

public class PlayerListener extends CommonListener implements Listener {
	private static PlayerListener instance;

	public PlayerListener() {
		instance = this;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (AbstractIntegration.shouldSkip(e.getPlayer()))
			return;
		MessageInfo info = handleMessage(e.getMessage(), CGPlayer.get(e.getPlayer()));
		if (info == null)
			return;
		if (info.isCancelled())
			e.setCancelled(true);
		e.setMessage(info.getClearMessage());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		if (AbstractIntegration.shouldSkip(e.getPlayer()))
			return;
		MessageInfo info = handleCommand(e.getMessage(), CGPlayer.get(e.getPlayer()));
		if (info == null)
			return;
		if (info.isCancelled())
			e.setCancelled(true);
		e.setMessage(info.getClearMessage());
	}

	public static PlayerListener getInstance() {
		return instance;
	}
}
