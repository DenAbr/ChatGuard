package ru.Den_Abr.ChatGuard.ChatFilters;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class FloodFilter extends AbstractFilter {
	private boolean informAdmins;
	private int floodTime;
	private int levels;

	@Override
	public Violation checkMessage(String message, CGPlayer player) {
		Violation v = null;
		if (player.hasPermission("chatguard.ignore.flood"))
			return v;

		// player object just created
		if (player.getLastMessages().getFixedSize() != levels) {
			player.getLastMessages().setFixedSize(levels);
			return v;
		}
		if (player.getLastMessages().isEmpty() || player.getLastMessageTime() != -1
				|| player.getLastMessageTime() + TimeUnit.SECONDS.toMillis(floodTime) < System.currentTimeMillis())
			return v;
		String wws = message.replaceAll("\\s+", " ").toLowerCase();
		for (String lm : player.getLastMessages()) {
			lm = lm.replaceAll("\\s+", " ").toLowerCase();
			if (lm.equalsIgnoreCase(wws) || (lm.startsWith(wws) && wws.length() - lm.length() < 4)) {
				v = Violation.FLOOD;
			}
		}
		if (v != null && informAdmins) {
			informAdmins(player, message);
		}
		return v;
	}

	private void informAdmins(CGPlayer player, String message) {
		Bukkit.broadcast(Message.INFORM_FLOOD.get().replace("{PLAYER}", player.getName()).replace("{MESSAGE}", message),
				"chatguard.inform.flood");
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		return message;
	}

	@Override
	public void register() {
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("flood settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		floodTime = cs.getInt("flood time");
		levels = cs.getInt("flood levels");
		getActiveFilters().add(this);
	}

}
