package ru.Den_Abr.ChatGuard;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
	private static YamlConfiguration config;

	private static boolean checkUpdates;
	private static boolean usePackets;
	private static boolean separateWarnings;

	private static int maxWarnings;
	private static int debugLevel;

	public static void load(ChatGuardPlugin pl) {
		File fconfig = new File(pl.getDataFolder(), "config.yml");
		if (!fconfig.exists())
			pl.saveResource("config.yml", false);
		config = YamlConfiguration.loadConfiguration(fconfig);

		checkUpdates = config.getBoolean("Check for updates");
		usePackets = config.getBoolean("Other settings.use packets");
		separateWarnings = config.getBoolean("Warnings settings.separate");

		maxWarnings = config.getInt("Warnings settings.max warnings");
		debugLevel = config.getInt("Other settings.debug level");

		if (debugLevel != 0) {
			ChatGuardPlugin.getInstance().getLogger().info("Debugging level: " + getDebugLevel());
		}
	}

	public static boolean canCheckUpdates() {
		return checkUpdates;
	}

	public static boolean useProtocol() {
		return usePackets;
	}

	public static YamlConfiguration getConfig() {
		return config;
	}

	public static boolean isSeparatedWarnings() {
		return separateWarnings;
	}

	public static int getMaxWarns() {
		return maxWarnings;
	}

	public static int getDebugLevel() {
		return debugLevel;
	}

}
