package ru.Den_Abr.ChatGuard.ChatFilters;

import org.bukkit.configuration.ConfigurationSection;

import ru.Den_Abr.ChatGuard.Settings;
import ru.Den_Abr.ChatGuard.ViolationType;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class CapsFilter extends AbstractFilter {
	private boolean informAdmins;
	private int maxCapsPercent;
	private int minLenght;

	@Override
	public ViolationType checkMessage(String message, CGPlayer player) {
		if (message.length() < minLenght)
			return null;
		if (player.hasPermission("chatguard.ignore.caps"))
			return null;
		String ws = message.replaceAll(" ", "").replaceAll("[^A-Za-zА-Яа-яà-ÿÀ-ß]", "");
		if (ws.length() == 0)
			return null;
		int capsCount = getCapsCount(ws);
		int capspercent = capsCount * 100 / ws.length();
		return (capspercent > maxCapsPercent) ? ViolationType.CAPS : null;
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
		return message.toLowerCase();
	}

	@Override
	public void register() {
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("Caps settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		maxCapsPercent = cs.getInt("max caps percent");
		minLenght = cs.getInt("min message lenght");
		getActiveFilters().add(this);
	}

}
