package ru.Den_Abr.ChatGuard.ChatFilters;

import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;

import ru.Den_Abr.ChatGuard.Settings;
import ru.Den_Abr.ChatGuard.ViolationType;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.FixedSizeList;

public class FloodFilter extends AbstractFilter {
	private HashMap<CGPlayer, FixedSizeList<String>> lastMessages = new HashMap<>();
	private boolean informAdmins;
	private int floodTime;
	private int cooldown;
	private int levels;

	@Override
	public ViolationType checkMessage(String message, CGPlayer player) {

		return null;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		return message;
	}

	@Override
	public void register() {
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("Flood settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		floodTime = cs.getInt("flood time");
		cooldown = cs.getInt("message cooldown");
		levels = cs.getInt("flood levels");
		getActiveFilters().add(this);
	}

}
