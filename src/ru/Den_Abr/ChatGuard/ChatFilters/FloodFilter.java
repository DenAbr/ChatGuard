package ru.Den_Abr.ChatGuard.ChatFilters;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.primitives.Chars;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import thirdparty.org.mcstats.Metrics.Graph;
import thirdparty.org.mcstats.Metrics.Plotter;

public class FloodFilter extends AbstractFilter {
	private boolean informAdmins;
	private int floodTime;
	private int levels;
	private int floodPercent;

	@Override
	public Violation checkMessage(String message, CGPlayer player) {
		Violation v = null;
		if (player.hasPermission("chatguard.ignore.flood"))
			return v;
		ChatGuardPlugin.debug(2, getClass().getSimpleName() + ": Hello!");

		// player object just created
		if (player.getLastMessages().getFixedSize() != levels) {
			player.getLastMessages().setFixedSize(levels);
			return v;
		}
		if (player.getLastMessages().isEmpty() || player.getLastMessageTime() == -1
				|| player.getLastMessageTime() + TimeUnit.SECONDS.toMillis(floodTime) < System.currentTimeMillis())
			return v;

		for (String lm : player.getLastMessages()) {
			int percent = getEqualCount(message.replace(" ", ""), lm.replace(" ", "")) * 100 / message.replace(" ", "").length();
			ChatGuardPlugin.debug(2, "Similarity of '" + message + "' with '" + lm + "': " + percent + "%");
			if (percent > floodPercent) {
				v = Violation.FLOOD;
				break;
			}
		}
		if (v != null && informAdmins) {
			informAdmins(player, message);
		}
		return v;
	}

	private int getEqualCount(String message, String lm) {
		int equal = 0;
		// get message that has more characters
		String first = message.length() < lm.length() ? message : lm;
		String second = message.length() > lm.length() ? message : lm;

		int offset = second.length() - first.length();

		Iterator<Character> fit = Chars.asList(first.toCharArray()).iterator();
		Iterator<Character> sit = Chars.asList(second.toCharArray()).iterator();

		// omg
		fw: while (fit.hasNext()) {
			char fc = fit.next();
			while (sit.hasNext()) {
				char sc = sit.next();
				if (fc == sc) {
					equal++;
					continue fw;
				} else {
					while (offset > 0 && sit.hasNext()) {
						sc = sit.next();
						offset--;
						if (fc == sc) {
							equal++;
							continue fw;
						}
					}
				}
			}
		}
		return equal;
	}

	private void informAdmins(CGPlayer player, String message) {
		String complete = Message.INFORM_FLOOD.get().replace("{PLAYER}", player.getName()).replace("{MESSAGE}",
				message);
		Bukkit.getConsoleSender().sendMessage(complete);
		Bukkit.broadcast(complete, "chatguard.inform.flood");
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		return message;
	}

	@Override
	public void register() {
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("flood settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		floodTime = cs.getInt("flood time");
		levels = cs.getInt("flood levels");
		floodPercent = cs.getInt("flood percent");
		addMetricsGraph();
		getActiveFilters().add(this);
	}

	@Override
	public void addMetricsGraph() {
		Graph g = ChatGuardPlugin.metrics.getOrCreateGraph("Filters used");
		g.addPlotter(new Plotter("Flood filter") {

			@Override
			public int getValue() {
				return 1;
			}
		});
	}
}
