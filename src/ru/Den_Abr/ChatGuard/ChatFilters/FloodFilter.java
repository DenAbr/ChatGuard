package ru.Den_Abr.ChatGuard.ChatFilters;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.ConfigurationSection;

import ru.Den_Abr.ChatGuard.ViolationType;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.FixedSizeList;

public class FloodFilter extends AbstractFilter {
	private ConcurrentHashMap<CGPlayer, FixedSizeList<String>> lastMessages = new ConcurrentHashMap<>();
	private ConcurrentHashMap<CGPlayer, Long> cooldowns = new ConcurrentHashMap<>();
	private boolean informAdmins;
	private int floodTime;
	private int cooldown;
	private int levels;

	@Override
	public ViolationType checkMessage(String message, CGPlayer player) {
		ViolationType v = null;
		if (player.hasPermission("chatguard.ignore.flood"))
			return v;
		if (!lastMessages.containsKey(player) || cooldowns.containsKey(player)) {
			lastMessages.put(player, new FixedSizeList<String>(levels));
			cooldowns.put(player, System.currentTimeMillis());
			return v;
		}
		
		return v;
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
