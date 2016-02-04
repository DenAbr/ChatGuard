package ru.Den_Abr.ChatGuard.Bukkit.Player;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import ru.Den_Abr.ChatGuard.Bukkit.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Common.Violation;
import ru.Den_Abr.ChatGuard.Common.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Common.Player.CGPlayer;

/** This implementation is needed to develop for Bukkit and BungeeCord **/

public class BukkitPlayer extends CGPlayer {
	private String p;

	public BukkitPlayer(String string) {
		this.p = string;
	}

	@Override
	public boolean hasPermission(String perm) {
		return getPlayer().hasPermission(perm);
	}

	public Player getPlayer() {
		return Bukkit.getPlayerExact(p);
	}

	@Override
	public String getName() {
		return getPlayer().getName();
	}

	@Override
	public void punish(Violation v) {
		for (String command : Settings.getPunishCommands(v.getPunishmentSection())) {
			for (Entry<String, String> reasonEntry : Settings.getPunishReasons().entrySet()) {
				command = command.replace("{Reason_" + reasonEntry.getKey() + "}", reasonEntry.getValue());
			}
			command = command.replace("{Player}", getName());
			final StringBuilder sb = new StringBuilder(command);
			ChatGuardPlugin.debug(2, "Punish command: " + command);
			new BukkitRunnable() {
				// chat events is async and we need to sync command execution
				@Override
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sb.toString());
				}
			}.runTask(ChatGuardPlugin.getInstance());

		}
	}

	@Override
	public void sendMessage(String string) {
		getPlayer().sendMessage(string);
	}

	@Override
	public Object getHandle() {
		return getPlayer();
	}

}
