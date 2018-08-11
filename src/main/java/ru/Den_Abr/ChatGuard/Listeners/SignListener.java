package ru.Den_Abr.ChatGuard.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import ru.Den_Abr.ChatGuard.MessageInfo;
import ru.Den_Abr.ChatGuard.ChatFilters.AbstractFilter;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Integration.AbstractIntegration;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class SignListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e) {
		if (AbstractIntegration.shouldSkip(e.getPlayer()) /* lol? */ || !Settings.isSignsEnabled())
			return;
		CGPlayer player = CGPlayer.get(e.getPlayer());
		for (int i = 0; i < e.getLines().length; i++) {
			String line = e.getLine(i);
			if (line.isEmpty())
				continue;
			MessageInfo info = AbstractFilter.handleMessage(line, player, false);
			e.setLine(i, info.getClearMessage());

			if (!info.getViolations().isEmpty()) {
				if (Settings.isCancellingEnabled()) {
					e.setCancelled(true);
				}
			}
		}
	}
}
