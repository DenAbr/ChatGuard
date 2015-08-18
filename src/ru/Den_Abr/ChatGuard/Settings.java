package ru.Den_Abr.ChatGuard;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
	private static boolean checkUpdates;
	private static boolean usePackets;

	public static void load(ChatGuardPlugin pl) {
		File fconfig = new File(pl.getDataFolder(), "config.yml");
		if (!fconfig.exists())
			pl.saveResource("config.yml", false);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(fconfig);

		checkUpdates = config.getBoolean("Check for updates");
		usePackets = config.getBoolean("Other settings.use packets");
	}

	public static boolean canCheckUpdates() {
		return checkUpdates;
	}

	public static boolean useProtocol() {
		return usePackets;
	}

}
