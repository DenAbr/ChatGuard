package ru.Den_Abr.ChatGuard.Common.ChatFilters;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import ru.Den_Abr.ChatGuard.Bukkit.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Common.Violation;
import ru.Den_Abr.ChatGuard.Common.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Common.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Common.Player.CGPlayer;
import thirdparty.org.mcstats.Metrics.Graph;
import thirdparty.org.mcstats.Metrics.Plotter;

public class CapsFilter extends AbstractFilter {
	private boolean informAdmins;
	private int maxCapsPercent;
	private int minLenght;

	@Override
	public Violation checkMessage(String message, CGPlayer player) {
		if (message.length() < minLenght)
			return null;
		if (player.hasPermission("chatguard.ignore.caps"))
			return null;
		ChatGuardPlugin.debug(2, getClass().getSimpleName() + ": Hello!");

		String ws = message.replaceAll(" ", "").replaceAll("[^A-Za-zА-Яа-яà-ÿÀ-ß]", "");
		if (ws.length() == 0 || ws.length() < minLenght)
			return null;
		int capsCount = getCapsCount(ws);
		int capspercent = capsCount * 100 / ws.length();
		Violation v = (capspercent > maxCapsPercent) ? Violation.CAPS : null;
		if (v != null && informAdmins) {
			informAdmins(player, message);
		}
		return v;
	}

	private void informAdmins(CGPlayer player, String message) {
		String complete = Message.INFORM_CAPS.get().replace("{PLAYER}", player.getName()).replace("{MESSAGE}", message);
		Bukkit.getConsoleSender().sendMessage(complete);
		Bukkit.broadcast(complete,
				"chatguard.inform.caps");
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
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("caps settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		maxCapsPercent = cs.getInt("max caps percent");
		minLenght = cs.getInt("min message lenght");
		addMetricsGraph();
		getActiveFilters().add(this);
	}

	@Override
	public void addMetricsGraph() {
		Graph g = ChatGuardPlugin.metrics.getOrCreateGraph("Filters used");
		g.addPlotter(new Plotter("Caps filter") {
			
			@Override
			public int getValue() {
				return 1;
			}
		});
	}

}
