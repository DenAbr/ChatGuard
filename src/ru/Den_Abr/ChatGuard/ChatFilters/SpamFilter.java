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

		Matcher ipMatcher = ipPattern.matcher(message);
		Matcher domMatcher = domainPattern.matcher(message);

		if (ipMatcher.find() || domMatcher.find()) {
			return ViolationType.ADVERT;
		}
		if (maxNums < 1)
			return null;
		int charCount = 0;
		for (char c : message.replaceAll(" ", "").toCharArray()) {
			if (Character.isDigit(c)) {
				charCount++;
			}
		}
		return charCount > maxNums ? ViolationType.MANYNUMS : null;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		message = message
				.replaceAll(ipPattern.pattern(),
						Settings.isSeparatedWarnings() ? replacement : Settings.getReplacement())
				.replaceAll(domainPattern.pattern(),
						Settings.isSeparatedWarnings() ? replacement : Settings.getReplacement());
		if (maxNums > 0) {
			StringBuffer sb = new StringBuffer();

			int charCount = 0;
			for (int i = 0; i < message.length(); i++) {
				if (Character.isDigit(message.charAt(i))) {
					charCount++;
					if (charCount > maxNums) {
						continue;
					}
				}
				sb.append(message.charAt(i));
			}
			return sb.toString().trim();
		}
		return message;
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
