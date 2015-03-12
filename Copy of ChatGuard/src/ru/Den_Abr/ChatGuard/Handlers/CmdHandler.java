package ru.Den_Abr.ChatGuard.Handlers;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Configs.Config;
import ru.Den_Abr.ChatGuard.Configs.Messages;
import ru.Den_Abr.ChatGuard.Configs.PlayerData;
import ru.Den_Abr.ChatGuard.Workers.Checkers;

public class CmdHandler implements Listener {
	public static ChatHandler cl = new ChatHandler();
	public static HashMap<Player, PlayerData> map = new HashMap<Player, PlayerData>();
	public static Pattern ipPattern;
	public static Pattern domenPattern;
	public static Pattern swearPattern;
	public static ChatGuardPlugin pl = ChatGuardPlugin.plugin;

	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (!Config.listencmds)
			return;

		Player p = e.getPlayer();
		String c = e.getMessage();
		if (Config.blockcmds.contains(c.split(" ")[0].toLowerCase())) {
			if (pl.ut.hasMute(p)) {
				p.sendMessage(Config.getMessage(Messages.MUTED));
				e.setCancelled(true);
				pl.getLogger().info(
						p.getName() + " tried to speak, but is muted.");
				return;
			}
			String message = e.getMessage().substring(c.split(" ")[0].length())
					.replaceAll("\\s+", " ");
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
				Checkers.inform(c, p);
			}
			if (Checkers.checkWarnings(p) && Config.stopit) {
				e.setCancelled(true);
			}
			PlayerData.get(p).clear();
			e.setMessage(c.split(" ")[0] + message);
		}
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		onCommand(e);
	}
}
