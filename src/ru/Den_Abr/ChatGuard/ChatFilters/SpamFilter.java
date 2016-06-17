package ru.Den_Abr.ChatGuard.ChatFilters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import ru.Den_Abr.ChatGuard.Utils.Utils;
import thirdparty.org.mcstats.Metrics.Graph;
import thirdparty.org.mcstats.Metrics.Plotter;

public class SpamFilter extends AbstractFilter {
	private Pattern ipPattern;
	private Pattern domainPattern;

	private String replacement;
	private boolean informAdmins;
	private int maxNums;

	@Override
	public Violation checkMessage(String message, CGPlayer player, boolean justCheck) {
		if (player.hasPermission("chatguard.ignore.spam"))
			return null;
		ChatGuardPlugin.debug(2, getClass().getSimpleName() + ": Hello!");
		String checkMessage = message;
		if (Settings.isHardMode()) {
			checkMessage = checkMessage.replace(" ", "");
		}
		Matcher ipMatcher = ipPattern.matcher(checkMessage);
		Matcher domMatcher = domainPattern.matcher(checkMessage);
		Violation v = null;
		List<String> matches = new ArrayList<>();
		while (ipMatcher.find()) {
			String found = ipMatcher.group();
			if (Whitelist.checkWhitelisted(found) != null)
				continue;
			matches.add(found);
			v = Violation.SPAM;
		}
		while (domMatcher.find()) {
			String found = domMatcher.group();
			if (Whitelist.checkWhitelisted(found) != null)
				continue;
			matches.add(found);
			v = Violation.SPAM;
		}
		if (maxNums > 0) {
			int numCount = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message))
					.replaceAll("[^0-9]", "").length();
			ChatGuardPlugin.debug(1, "Numbers count: " + numCount);
			if (numCount > maxNums)
				v = Violation.SPAM;
		}
		if (Settings.isHardMode())
			matches.clear();
		if (!justCheck && v != null && informAdmins) {
			informAdmins(player, message, matches);
		}
		return v;
	}
	
	@Override
	public Violation checkMessage(String message, CGPlayer player) {
		return checkMessage(message, player, false);
	}

	private void informAdmins(CGPlayer player, String message, List<String> matches) {
		String complete = Message.INFORM_SPAM.get().replace("{PLAYER}", player.getName()).replace("{MESSAGE}",
				ChatColor.stripColor(message));
		for (String s : matches) {
			complete = Utils.replaceFirstSafely(complete, s, ChatColor.UNDERLINE + s + ChatColor.RESET);
		}
		Bukkit.getConsoleSender().sendMessage(complete);
		Bukkit.broadcast(complete, "chatguard.inform.spam");
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		if (player.hasPermission("chatguard.ignore.spam"))
			return message;
		Matcher ipMatcher = ipPattern.matcher(message);
		Matcher domMatcher = domainPattern.matcher(message);
		while (ipMatcher.find()) {
			String group = ipMatcher.group();
			if (Whitelist.checkWhitelisted(group) != null) {
				continue;
			}
			message = message.replace(group, Settings.isSeparatedWarnings() ? replacement : Settings.getReplacement());
		}
		while (domMatcher.find()) {

			String found = Utils.getWord(message, domMatcher.start(), domMatcher.end());

			if (Whitelist.checkWhitelisted(found) != null) {
				continue;
			}

			message = message.replace(found, Settings.isSeparatedWarnings() ? replacement : Settings.getReplacement());
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
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("spam settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		File ipFile = new File(ChatGuardPlugin.getInstance().getDataFolder(), "ipRegexp.txt");
		File domFile = new File(ChatGuardPlugin.getInstance().getDataFolder(), "domainRegexp.txt");

		if (!ipFile.exists()) {
			ChatGuardPlugin.getInstance().saveResource(ipFile.getName(), false);
		}
		if (!domFile.exists()) {
			ChatGuardPlugin.getInstance().saveResource(domFile.getName(), false);
		}

		ipPattern = Pattern.compile(Utils.readLine(ipFile), Pattern.CASE_INSENSITIVE);
		domainPattern = Pattern.compile(Utils.readLine(domFile), Pattern.CASE_INSENSITIVE);

		maxNums = cs.getInt("max numbers");
		replacement = ChatColor.translateAlternateColorCodes('&', cs.getString("custom replacement"));

		addMetricsGraph();

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
