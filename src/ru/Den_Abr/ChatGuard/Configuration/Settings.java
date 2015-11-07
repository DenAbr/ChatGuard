package ru.Den_Abr.ChatGuard.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Utils.Utils;

public class Settings {
	private static final int CONFIG_VERSION = 2;
	private static YamlConfiguration config;

	private static boolean checkUpdates;
	private static boolean usePackets;
	private static boolean separateWarnings;
	private static boolean hardmode;
	private static boolean cancelEnabled;
	private static boolean warnsEnabled;
	private static boolean punishmentsEnabled;
	private static boolean signsEnabled;

	private static int maxWarnings;
	private static int debugLevel;
	private static int cooldown;

	private static long maxMuteTime;

	private static String replacement;

	private static Map<String, Integer> commands = new HashMap<>();
	private static Map<String, String> reasons = new HashMap<>();

	public static void load(ChatGuardPlugin pl) {
		File fconfig = new File(pl.getDataFolder(), "config.yml");
		if (!fconfig.exists())
			pl.saveResource("config.yml", false);
		config = YamlConfiguration.loadConfiguration(fconfig);

		if (!config.isSet("Version")) {
			try {
				new File(pl.getDataFolder(), "old").mkdirs();
				Files.move(fconfig, new File(new File(pl.getDataFolder(), "old"), "old_config.yml"));
				Files.move(new File(pl.getDataFolder(), "warnings.yml"),
						new File(new File(pl.getDataFolder(), "old"), "warnings.yml"));
				Files.move(new File(pl.getDataFolder(), "messages.yml"),
						new File(new File(pl.getDataFolder(), "old"), "messages.yml"));
				ChatGuardPlugin.getLog().info("Old configuration was moved to old dirrectory");
			} catch (IOException e) {
				e.printStackTrace();
			}
			fconfig.delete();
			pl.saveResource("config.yml", false);
			config = YamlConfiguration.loadConfiguration(fconfig);
		}

		if (config.getInt("Version") != CONFIG_VERSION) {
			migrateFrom(config.getInt("Version"));
		}

		checkUpdates = config.getBoolean("Check for updates");
		usePackets = config.getBoolean("Other settings.use packets");
		separateWarnings = config.getBoolean("Warnings settings.separate");
		hardmode = config.getBoolean("Hard mode");
		cancelEnabled = config.getBoolean("Messages.cancel if violation");
		warnsEnabled = config.getBoolean("Warnings settings.enabled");
		punishmentsEnabled = config.getBoolean("Punishment settings.enabled");
		signsEnabled = config.getBoolean("Other settings.check signs");

		maxMuteTime = Utils.parseTime(config.getString("Punishment settings.max mute time"));

		replacement = config.getString("Messages.replacement");

		maxWarnings = config.getInt("Warnings settings.max count");
		debugLevel = config.getInt("Other settings.debug level");
		cooldown = config.getInt("flood settings.message cooldown");

		commands.clear();
		for (String command : config.getStringList("Other settings.check commands")) {
			String[] cmd = command.split(Pattern.quote(":"));
			if (cmd.length != 2 || !Utils.isInt(cmd[1]) || Integer.parseInt(cmd[1]) < 0)
				continue;
			commands.put(cmd[0].toLowerCase(), Integer.parseInt(cmd[1]));
		}
		reasons.clear();
		for (String key : config.getConfigurationSection("Punishment settings.reasons").getKeys(false)) {
			reasons.put(key, ChatColor.translateAlternateColorCodes('&',
					config.getString("Punishment settings.reasons." + key)));
		}

		if (debugLevel > 0) {
			ChatGuardPlugin.debug(1, "Debug level: " + getDebugLevel());
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

	public static void saveConfig() {
		try {
			getConfig().save(new File(ChatGuardPlugin.getInstance().getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static boolean isCancellingEnabled() {
		return cancelEnabled || isHardMode();
	}

	public static int getCooldown() {
		return cooldown;
	}

	public static boolean isCooldownEnabled() {
		return cooldown > 0;
	}

	public static Map<String, Integer> getCheckCommands() {
		return commands;
	}

	public static int getMaxWarnCount(String sec) {
		if (isSeparatedWarnings()) {
			return config.getInt(sec + " settings.max warnings");
		} else {
			return getMaxWarns();
		}
	}

	public static List<String> getPunishCommands(String sec) {
		if (!config.getBoolean("Punishment settings.commands.custom"))
			sec = "common";
		return config.getStringList("Punishment settings.commands." + sec + " commands");
	}

	public static Map<String, String> getPunishReasons() {
		return reasons;
	}

	public static boolean isWarnsEnabled() {
		return warnsEnabled;
	}

	public static boolean isPunishmentsEnabled() {
		return punishmentsEnabled;
	}

	public static boolean isSignsEnabled() {
		return signsEnabled;
	}

	public static long getMaxMuteTime() {
		return maxMuteTime;
	}

	private static void migrateFrom(int v) {
		if (v == 1) {
			getConfig().set("Punishment settings.max mute time", "1h");
			getConfig().set("Version", 2);

			v = 2;
		}
		saveConfig();
	}
}
