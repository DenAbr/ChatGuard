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
		return null;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		return null;
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
