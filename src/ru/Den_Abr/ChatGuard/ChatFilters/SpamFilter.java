package ru.Den_Abr.ChatGuard.ChatFilters;

import java.util.regex.Matcher;
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

		if (player.hasPermission("chatguard.ignore.adv"))
			return null;

		Matcher ipMatcher = ipPattern.matcher(message.replaceAll("[^A-Za-zА-Яа-яà-ÿÀ-ß]", ""));
		Matcher domMatcher = domainPattern.matcher(message.replaceAll("[^A-Za-zА-Яа-яà-ÿÀ-ß]", ""));

		if (ipMatcher.find() || domMatcher.find()) {
			return ViolationType.ADVERT;
		}
		return null;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		return null;
	}

	@Override
	public void register() {
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("Spam settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		ipPattern = Pattern.compile(cs.getString("ip regexp"), Pattern.CASE_INSENSITIVE);
		domainPattern = Pattern.compile(cs.getString("domain regexp"), Pattern.CASE_INSENSITIVE);

		maxNums = cs.getInt("max numbers");
		replacement = ChatColor.translateAlternateColorCodes('&', cs.getString("custom replacement"));
		getActiveFilters().add(this);
		return;
	}

}
