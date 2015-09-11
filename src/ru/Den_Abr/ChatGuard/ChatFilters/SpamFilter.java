package ru.Den_Abr.ChatGuard.ChatFilters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Configuration.Whitelist;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import thirdparty.org.mcstats.Metrics.Graph;
import thirdparty.org.mcstats.Metrics.Plotter;

public class SpamFilter extends AbstractFilter {
	private Pattern ipPattern;
	private Pattern domainPattern;

	private String replacement;
	private boolean informAdmins;
	private int maxNums;

	@Override
	public Violation checkMessage(String message, CGPlayer player) {
		if (player.hasPermission("chatguard.ignore.spam"))
			return null;
		String checkMessage = message;
		if (Settings.isHardMode()) {
			checkMessage = checkMessage.replace(" ", "");
		}
		Matcher ipMatcher = ipPattern.matcher(checkMessage);
		Matcher domMatcher = domainPattern.matcher(checkMessage);
		Violation v = null;
		while (ipMatcher.find()) {
			if (Whitelist.isWhitelisted(ipMatcher.group()))
				continue;
			v = Violation.SPAM;
		}
		while (domMatcher.find()) {
			if (Whitelist.isWhitelisted(domMatcher.group()))
				continue;
			v = Violation.SPAM;
		}
		if (maxNums > 0) {
			int numCount = message.replaceAll("[^0-9]", "").length();
			ChatGuardPlugin.debug(1, "Numerics count: " + numCount);
			if (numCount > maxNums)
				v = Violation.SPAM;
		}
		if (v != null && informAdmins) {
			informAdmins(player, message);
		}
		return v;
	}

	private void informAdmins(CGPlayer player, String message) {
		String complete = Message.INFORM_SPAM.get()
				.replace("{PLAYER}", player.getName())
				.replace("{MESSAGE}", message);
		Bukkit.getConsoleSender().sendMessage(complete);
		Bukkit.broadcast(complete, "chatguard.inform.spam");
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		Matcher ipMatcher = ipPattern.matcher(message);
		Matcher domMatcher = domainPattern.matcher(message);
		while (ipMatcher.find()) {
			String group = ipMatcher.group();
			if (Whitelist.isWhitelisted(group)) {
				continue;
			}
			message = message.replaceAll(
					group,
					Settings.isSeparatedWarnings() ? replacement : Settings
							.getReplacement());
		}
		while (domMatcher.find()) {
			String group = domMatcher.group();
			if (Whitelist.isWhitelisted(group)) {
				continue;
			}
			message = message.replaceAll(
					group,
					Settings.isSeparatedWarnings() ? replacement : Settings
							.getReplacement());
		}
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
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection(
				"spam settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		ipPattern = Pattern.compile(cs.getString("ip regexp"),
				Pattern.CASE_INSENSITIVE);
		domainPattern = Pattern
				.compile(
						cs.getString("domain regexp"),
						Pattern.CASE_INSENSITIVE);

		maxNums = cs.getInt("max numbers");
		replacement = ChatColor.translateAlternateColorCodes('&',
				cs.getString("custom replacement"));
		
		
		getActiveFilters().add(this);
		return;
	}
	@Override
	public void addMetricsGraph() {
		Graph g = ChatGuardPlugin.metrics.getOrCreateGraph("Filters used");
		g.addPlotter(new Plotter("Spam filter") {
			
			@Override
			public int getValue() {
				return 1;
			}
		});
	}
}
