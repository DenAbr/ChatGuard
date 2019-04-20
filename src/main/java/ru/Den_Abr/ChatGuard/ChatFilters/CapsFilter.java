package ru.Den_Abr.ChatGuard.ChatFilters;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class CapsFilter extends AbstractFilter {
	private boolean informAdmins;
	private int maxCapsPercent;
	private int minLenght;

	@Override
	public Violation checkMessage(String message, CGPlayer player, boolean justCheck) {
		if (message.length() < minLenght)
			return null;
		if (player.hasPermission("chatguard.ignore.caps"))
			return null;

		String ws = message.replaceAll(" ", "").replaceAll("[^A-Za-zА-Яа-яà-ÿÀ-ß]", "");
		if (ws.length() == 0 || ws.length() < minLenght)
			return null;
		int capsCount = getCapsCount(ws);
		int capspercent = capsCount * 100 / ws.length();
		Violation v = (capspercent > maxCapsPercent) ? Violation.CAPS : null;
		if (!justCheck && v != null && informAdmins) {
			informAdmins(player, message);
		}
		return v;
	}

	@Override
	public Violation checkMessage(String message, CGPlayer player) {
		return checkMessage(message, player, false);
	}

	private void informAdmins(CGPlayer player, String message) {
		String complete = Message.INFORM_CAPS.get().replace("{PLAYER}", player.getName()).replace("{MESSAGE}", message);
		Bukkit.getConsoleSender().sendMessage(complete);
		Bukkit.broadcast(complete, "chatguard.inform.caps");
	}

	private int getCapsCount(String message) {
		int count = 0;
		for (char ch : message.toCharArray()) {
			if (Character.isUpperCase(ch)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		if (player.hasPermission("chatguard.ignore.caps"))
			return message;
		return message.toLowerCase();
	}

	@Override
	public void register() {
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("caps settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		maxCapsPercent = cs.getInt("max caps percent");
		minLenght = cs.getInt("min message lenght");
		addMetricsGraph();
		getActiveFilters().add(this);
	}

}
