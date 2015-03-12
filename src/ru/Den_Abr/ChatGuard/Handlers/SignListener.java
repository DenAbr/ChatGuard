package ru.Den_Abr.ChatGuard.Handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Configs.Config;
import ru.Den_Abr.ChatGuard.Configs.PlayerData;
import ru.Den_Abr.ChatGuard.Workers.Checkers;

public class SignListener implements Listener {
	public ChatGuardPlugin pl;

	public SignListener(ChatGuardPlugin pl) {
		this.pl = pl;
		if (!Config.checksign)
			HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent e) {
		Player p = e.getPlayer();
		StringBuilder sb = new StringBuilder();
		if (Config.swearenabled) {
			for (int i = 0; i < e.getLines().length; ++i) {
				String s = e.getLine(i);
				if (null != s) {
					sb.append("|").append(s);
					e.setLine(i, Checkers.checkSwear(p, s));
				} else {
					sb.append("|").append("null");
				}
			}
		}
		String mes = sb.toString().replaceFirst("|", "");
		if (Checkers.hasWarnings(p)) {
			Checkers.inform(mes, p);
		}
		if (Checkers.checkWarnings(p) && Config.stopit) {
			e.setCancelled(true);
		}
		PlayerData.get(p).clear();

	}
}
