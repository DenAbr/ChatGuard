package ru.Den_Abr.ChatGuard.ChatFilters;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.io.Files;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Configuration.Whitelist;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.Utils;
import thirdparty.org.mcstats.Metrics.Graph;
import thirdparty.org.mcstats.Metrics.Plotter;

public class SwearFilter extends AbstractFilter {
	private static Pattern swearPattern;
	private String replacement;
	private boolean informAdmins;

	@Override
	public Violation checkMessage(String message, CGPlayer player) {
		if (player.hasPermission("chatguard.ignore.swear"))
			return null;
		String checkMessage = message;
		if (Settings.isHardMode()) {
			checkMessage = checkMessage.replaceAll(" ", "").replaceAll("[^A-Za-zА-Яа-яà-ÿÀ-ß]", "");
		}
		Violation v = null;
		Matcher swearMatcher = swearPattern.matcher(checkMessage.toLowerCase());
		while (swearMatcher.find()) {
			String found = Utils.getWord(message, swearMatcher.start(), swearMatcher.end());
			if (Whitelist.isWhitelisted(found.toLowerCase()))
				continue;
			v = Violation.SWEAR;
		}

		if (v != null && informAdmins) {
			informAdmins(player, message);
		}
		return v;
	}

	private void informAdmins(CGPlayer player, String message) {
		String complete = Message.INFORM_SWEAR.get().replace("{PLAYER}", player.getName()).replace("{MESSAGE}",
				message);
		Bukkit.getConsoleSender().sendMessage(complete);
		Bukkit.broadcast(complete, "chatguard.inform.swear");
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		Matcher swearMatcher = swearPattern.matcher(message.toLowerCase());
		List<String> toReplace = new ArrayList<>();
		while (swearMatcher.find()) {
			String found = Utils.getWord(message, swearMatcher.start(), swearMatcher.end());
			if (Whitelist.isWhitelisted(found.toLowerCase()))
				continue;

			toReplace.add(found);
		}
		for (String s : toReplace)
			message = message.replace(s, Settings.isSeparatedWarnings() ? replacement : Settings.getReplacement());

		return message;
	}

	@Override
	public void register() {
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("swear settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");
		replacement = ChatColor.translateAlternateColorCodes('&', cs.getString("custom replacement"));

		try {
			File oldFileSwear = new File(ChatGuardPlugin.getInstance().getDataFolder(), "swearlist.txt");
			File newFileSwear = new File(ChatGuardPlugin.getInstance().getDataFolder(), "swearwords.txt");
			if (!newFileSwear.exists()) {
				if (oldFileSwear.exists()) {
					String oldLine = Files.readFirstLine(oldFileSwear, Charset.forName("UTF-8")).replace('|', '\n');
					Files.write(oldLine.getBytes(Charset.forName("UTF-8")), newFileSwear);
					new File(ChatGuardPlugin.getInstance().getDataFolder(), "old").mkdirs();
					Files.move(oldFileSwear, new File(ChatGuardPlugin.getInstance().getDataFolder(),
							"old" + File.separator + "swearlist.txt"));
					ChatGuardPlugin.getLog().info("Moved old swearlist file");
				} else {
					ChatGuardPlugin.getInstance().saveResource("swearwords.txt", false);
					ChatGuardPlugin.getInstance().getLogger().warning("Check your swearwords.txt file!");
				}
			}
			String pat = "";
			for (String word : new ArrayList<>(Files.readLines(newFileSwear, Charset.forName("UTF-8")))) {
				if (word.isEmpty())
					continue;
				if (pat.isEmpty()) {
					pat = word;
				} else {
					pat += "|" + word;
				}
			}
			swearPattern = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
			addMetricsGraph();
			getActiveFilters().add(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void addWord(String w) {
		Pattern p = Pattern.compile(w);
		if (swearPattern.pattern().contains(p.pattern()))
			return;
		swearPattern = Pattern.compile(swearPattern.pattern() + "|" + p.pattern());
		File swearFile = new File(ChatGuardPlugin.getInstance().getDataFolder(), "swearwords.txt");
		try {
			Files.append("\n" + w, swearFile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addMetricsGraph() {
		Graph g = ChatGuardPlugin.metrics.getOrCreateGraph("Filters used");
		g.addPlotter(new Plotter("Swear filter") {

			@Override
			public int getValue() {
				return 1;
			}
		});
	}
}
