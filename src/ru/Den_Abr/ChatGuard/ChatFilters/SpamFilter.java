package ru.Den_Abr.ChatGuard.ChatFilters;

import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import ru.Den_Abr.ChatGuard.Settings;
import ru.Den_Abr.ChatGuard.ViolationType;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class SpamFilter extends AbstractFilter {
	private Pattern ipPattern;
	private Pattern domainPattern;

	private String replacement;
	private boolean informAdmins;
	private int maxNums;

	@Override
	public ViolationType checkMessage(String message, CGPlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register() {
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("Spam settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		ipPattern = Pattern.compile("ip regexp", Pattern.CASE_INSENSITIVE);
		domainPattern = Pattern.compile("domain regexp", Pattern.CASE_INSENSITIVE);

		maxNums = cs.getInt("max numbers");
		replacement = ChatColor.translateAlternateColorCodes('&', cs.getString("custom replacement"));
		return;
	}

}
