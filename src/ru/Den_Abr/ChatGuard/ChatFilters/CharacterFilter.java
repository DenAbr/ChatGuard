package ru.Den_Abr.ChatGuard.ChatFilters;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import thirdparty.org.mcstats.Metrics.Graph;
import thirdparty.org.mcstats.Metrics.Plotter;

import com.google.common.io.Files;

public class CharacterFilter extends AbstractFilter {
	private String charSet;

	@Override
	public Violation checkMessage(String message, CGPlayer player) {
		if (player.hasPermission("chatguard.ignore.characters"))
			return null;
		for (char c : message.toCharArray()) {
			if (isNotAllowed(c)) {
				return Violation.BLACKCHAR;
			}
		}
		return null;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		for (char c : message.toCharArray()) {
			if (isNotAllowed(c)) {
				message = message.replace(Character.valueOf(c).toString(), "");
			}
		}
		return message;
	}

	private boolean isNotAllowed(char ch) {
		return !charSet.contains(ch + "");
	}

	@Override
	public void register() {
		if (!Settings.getConfig().getBoolean("Messages.enable character whitelist")) {
			return;
		}
		try {
			File fCh = new File(ChatGuardPlugin.getInstance().getDataFolder(), "characterwhitelist.txt");
			File oldFCh = new File(ChatGuardPlugin.getInstance().getDataFolder(), "allowedsymbols.txt");
			if (!fCh.exists()) {
				if (oldFCh.exists()) {
					Files.move(oldFCh, fCh);
				} else {
					ChatGuardPlugin.getInstance().saveResource("characterwhitelist.txt", false);
				}
			}
			charSet = Files.readFirstLine(fCh, Charset.forName("UTF-8"));
			addMetricsGraph();

			getActiveFilters().add(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void addMetricsGraph() {
		Graph g = ChatGuardPlugin.metrics.getOrCreateGraph("Filters used");
		g.addPlotter(new Plotter("Character filter") {
			
			@Override
			public int getValue() {
				return 1;
			}
		});
	}
}
