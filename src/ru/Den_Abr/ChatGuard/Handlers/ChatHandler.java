package ru.Den_Abr.ChatGuard.Handlers;

import java.nio.charset.Charset;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Configs.Config;
import ru.Den_Abr.ChatGuard.Configs.Messages;
import ru.Den_Abr.ChatGuard.Configs.PlayerData;
import ru.Den_Abr.ChatGuard.Workers.Checkers;

@SuppressWarnings("deprecation")
public class ChatHandler implements Listener {
	public static ChatGuardPlugin pl = ChatGuardPlugin.plugin;

	public void onChat(PlayerChatEvent e) {
		if (pl.pl || pl.bk) {
			pl.getServer().getPluginManager().callEvent(e);
		}
		if (e.isCancelled()) {
			return;
		}
		Player p = e.getPlayer();
		if (pl.ut.hasMute(p)) {
			p.sendMessage(Config.getMessage(Messages.MUTED));
			e.setCancelled(true);
			pl.getLogger().info(p.getName() + " tried to speak, but is muted.");
			return;
		} else {
			String message = new String(e.getMessage().replaceAll("\\s+", " ")
					.trim().getBytes(Charset.forName("UTF-8")),
					Charset.forName("UTF-8"));
			message = pl.ut.removeDisallowedSymbols(message);
			e.setMessage(message);
			String message2 = message.replaceAll(" ", "").replaceAll(
					"[^A-Za-zА-Яа-яà-ÿÀ-ß]", "");
			if (Config.floodenabled) {
				Checkers.checkFlood(p, message);
			}
			if (Config.capsenabled) {
				message = Checkers.checkCaps(p, message2, message);
			}
			if (Config.swearenabled) {
				message = Checkers.checkSwear(p, message);
			}
			if (Config.advenabled) {
				message = Checkers.checkAdvert(p, message);
			}
			if (Checkers.hasWarnings(p)) {
				Checkers.inform(e.getMessage(), p);
			}
			if (Checkers.checkWarnings(p) && Config.stopit) {
				e.setCancelled(true);
			}
			PlayerData.get(p).clear();
			e.setMessage(message);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerChat(PlayerChatEvent e) {
		onChat(e);
	}
}
