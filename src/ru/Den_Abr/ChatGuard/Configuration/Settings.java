package ru.Den_Abr.ChatGuard.Configuration;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;

public class Settings {
	private static YamlConfiguration config;

	private static boolean checkUpdates;
	private static boolean usePackets;
	private static boolean separateWarnings;
	private static boolean hardmode;


	private static int maxWarnings;
	private static int debugLevel;

	private static String replacement;


	public static void load(ChatGuardPlugin pl) {
		File fconfig = new File(pl.getDataFolder(), "config.yml");
		if (!fconfig.exists())
			pl.saveResource("config.yml", false);
		config = YamlConfiguration.loadConfiguration(fconfig);

		checkUpdates = config.getBoolean("Check for updates");
		usePackets = config.getBoolean("Other settings.use packets");
		separateWarnings = config.getBoolean("Warnings settings.separate");
		hardmode = config.getBoolean("Hard mode");
		
		replacement = config.getString("Messages.replacement");

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

	public static String getReplacement() {
		return replacement;
	}

	public static boolean isHardMode() {
		return hardmode;
	}
}
