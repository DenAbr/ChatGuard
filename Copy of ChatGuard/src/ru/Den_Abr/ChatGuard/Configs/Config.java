package ru.Den_Abr.ChatGuard.Configs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;

import com.google.common.io.Files;

public class Config {
	public static YamlConfiguration config;
	public static String stopwords;
	public static List<String> whitelist;
	public static List<String> replacements;
	public static List<String> blockcmds = new ArrayList<String>();;
	public static boolean stopit;
	public static boolean ban;
	public static boolean kick;
	public static boolean mute;
	public static boolean kill;
	public static boolean custom;
	public static boolean cu;
	public static boolean capsenabled;
	public static boolean floodenabled;
	public static boolean advenabled;
	public static boolean swearenabled;
	public static boolean listencmds;
	public static boolean informmods;
	public static boolean informmodsadv;
	public static boolean informmodsswear;
	public static boolean informmodscaps;
	public static boolean informmodsflood;
	public static boolean warnenable;
	public static boolean checksign;
	public static int capsper;
	public static int floodtime;
	public static int mutetime;
	public static int minlength;
	public static String reason;
	public static String swearalt;
	public static String advalt;
	public static String admindalmut;
	public static List<String> command;
	public static List<Integer> simba = new ArrayList<Integer>();
	public static String altword;
	public static int warnings;
	public static String lang;
	public static String ipPattern;
	public static String domainPattern;
	protected static Pattern chatColorPattern = Pattern
			.compile("(?i)&([0-9A-F])");
	protected static Pattern chatMagicPattern = Pattern.compile("(?i)&([K])");
	protected static Pattern chatBoldPattern = Pattern.compile("(?i)&([L])");
	protected static Pattern chatStrikethroughPattern = Pattern
			.compile("(?i)&([M])");
	protected static Pattern chatUnderlinePattern = Pattern
			.compile("(?i)&([N])");
	protected static Pattern chatItalicPattern = Pattern.compile("(?i)&([O])");
	protected static Pattern chatResetPattern = Pattern.compile("(?i)&([R])");
	public static ChatGuardPlugin pl;
	public static File fileConf;
	public static File fileSwear;
	public static File fileSimba;
	public static YamlConfiguration clang;

	public Config(ChatGuardPlugin plug) {
		pl = plug;
		if (!pl.getDataFolder().isDirectory())
			pl.getDataFolder().mkdirs();
		fileConf = new File(pl.getDataFolder(), "config.yml");
		fileSwear = new File(pl.getDataFolder(), "swearlist.txt");
		fileSimba = new File(pl.getDataFolder(), "allowedsymbols.txt");
		loadConfig();
		loadMessages();
		createWarnings();
		loadSwearWords();
		loadAllowedSymbols();
	}

	public void createWarnings() {
		File fwarn = new File(pl.getDataFolder(), "warnings.yml");
		if (!fwarn.exists()) {
			try {
				fwarn.createNewFile();
			} catch (IOException e) {
				pl.getLogger().severe("Error while creating warnings file");
				e.printStackTrace();
			}
		}
		YamlConfiguration w = YamlConfiguration.loadConfiguration(fwarn);
		if (!w.contains("Players")) {
			w.set("Players", "");
			try {
				w.save(fwarn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void addWord(String w) {
		try {
			FileWriter fw = new FileWriter(fileSwear);
			if (!stopwords.equalsIgnoreCase("null")) {
				Files.append(stopwords + "|" + w, fileSwear,
						Charset.forName("UTF-8"));
			} else {
				Files.append(w, fileSwear, Charset.forName("UTF-8"));
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadSwearWords();
	}

	public static void removeWord(String w) {
		try {
			FileWriter fw = new FileWriter(fileSwear);
			if (w.length() > 1) {
				fw.append(w);
			} else {
				fw.append("null");
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadSwearWords();
	}

	public static void loadSwearWords() {
		if (!fileSwear.exists()) {
			try {
				fileSwear.createNewFile();
				Files.append("fuck|shit|noob|suck", fileSwear,
						Charset.forName("UTF-8"));
				pl.getLogger().info("Created swearlist file.");
			} catch (Exception ex) {
				pl.getLogger().severe("Error while creating swearlist file");
				ex.printStackTrace();
				return;
			}
		}

		try {
			stopwords = Files
					.readFirstLine(fileSwear, Charset.forName("UTF-8")).trim();
		} catch (Exception e) {
			pl.getLogger().severe("Error while reading swearlist file");
			e.printStackTrace();
		}
	}

	public static void loadAllowedSymbols() {
		if (!fileSimba.exists()) {
			pl.saveResource("allowedsymbols.txt", true);
		}
		try {
			String line = Files.readFirstLine(fileSimba,
					Charset.forName("UTF-8")).trim();
			simba.clear();
			for (char c : line.toCharArray()) {
				simba.add(((Character) c).hashCode());
			}
		} catch (Exception e) {
			pl.getLogger().severe("Error while reading symbols file");
			e.printStackTrace();
		}
	}

	public static void loadConfig() {
		if (!fileConf.exists()) {
			try {
				fileConf.createNewFile();
			} catch (IOException e) {
				pl.getLogger().severe("Error while creating config file");
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(fileConf);
		if (config.isSet("Swearwords")) {
			removeWord(config.getString("Swearwords"));
			config.set("Swearwords", null);
		}
		if (!config.isList("Replacements")) {
			List<String> l = Arrays.asList("0|o", "1|l", "@|a");
			config.set("Replacements", l);
		}
		if (!config.isSet("CheckUpdates")) {
			config.set("CheckUpdates", false);
		}
		if (!config.isSet("Caps")) {
			config.set("Caps", true);
		}
		if (!config.isSet("Flood")) {
			config.set("Flood", true);
		}
		if (!config.isSet("Swear")) {
			config.set("Swear", true);
		}
		if (!config.isSet("Advertisment")) {
			config.set("Advertisment", true);
		}
		if (!config.isSet("CapsPercent")) {
			config.set("CapsPercent", 80);
		}
		if (!config.isSet("FloodTime")) {
			config.set("FloodTime", 10);
		}
		if (!config.isSet("Cancel")) {
			config.set("Cancel", false);
		}
		if (!config.isSet("caps-length")) {
			config.set("caps-length", 3);
		}
		if (!config.isSet("Alternative")) {
			config.set("Alternative", "<censored>");
		}
		if (!config.isSet("Warnings.enable")) {
			config.set("Warnings.enable", true);
		}
		if (!config.isSet("Warnings.custom")) {
			config.set("Warnings.custom", true);
		}
		if (!config.isSet("Listen-commands")) {
			config.set("Listen-commands", true);
		}
		if (!config.isSet("Informing.swear")) {
			config.set("Informing.swear", true);
		}
		if (!config.isSet("Informing.caps")) {
			config.set("Informing.caps", true);
		}
		if (!config.isSet("Informing.advert")) {
			config.set("Informing.advert", true);
		}
		if (!config.isSet("Informing.flood")) {
			config.set("Informing.flood", true);
		}
		if (!config.isSet("Check-signs")) {
			config.set("Check-signs", true);
		}
		if (!config.isList("Check-cmds")
				|| config.getStringList("Check-cmds").size() < 1) {
			List<String> l = Arrays.asList("/me", "/msg", "/tell");
			config.set("Check-cmds", l);
		}
		if (!config.isSet("Patterns.ip")) {
			config.set("Patterns.ip",
					"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d*)?");
		}
		if (!config.isSet("Patterns.domain")) {
			config.set(
					"Patterns.domain",
					"([0-9a-z]{2,}\\.)+(ru|com|org|ua|su|tv|net|biz|info|name|mobi|kz|by|lv|eu|tk)(:\\d*)?");
		}
		if (!config.isSet("Warnings.count")) {
			config.set("Warnings.count", 10);
		}
		if (!config.isSet("Warnings.custom-alts.swear")) {
			config.set("Warnings.custom-alts.swear", "<swearing>");
		}
		if (!config.isSet("Warnings.custom-alts.advert")) {
			config.set("Warnings.custom-alts.advert", "<advertisment>");
		}
		if (!config.isSet("Punishment.ban")) {
			config.set("Punishment.ban", false);
		}
		if (!config.isSet("Punishment.kick")) {
			config.set("Punishment.kick", true);
		}
		if (!config.isSet("Punishment.mute.enable")) {
			config.set("Punishment.mute.enable", false);
		}
		if (!config.isSet("Punishment.mute.minutes")) {
			config.set("Punishment.mute.minutes", 10);
		}
		if (config.isList("Punishment.mute.block-cmds")) {
			config.set("Punishment.mute.block-cmds", null);
		}
		if (!config.isSet("Punishment.kill")) {
			config.set("Punishment.kill", false);
		}
		if (config.isSet("Punishment.command")
				&& !config.isList("Punishment.command")) {
			List<String> l = Arrays.asList(config
					.getString("Punishment.command"));
			config.set("Punishment.command", l);
		}
		if (!config.isSet("Punishment.command")) {
			List<String> l = Arrays.asList(
					"say {player} was banned for {reason}",
					"ban {player} {reason}", "give Den_Abr 1");

			config.set("Punishment.command", l);
		}
		if (!config.isSet("Punishment.reason")) {
			config.set("Punishment.reason", "You are bad boy!");
		}
		try {
			config.save(fileConf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		floodenabled = config.getBoolean("Flood");
		capsenabled = config.getBoolean("Caps");
		advenabled = config.getBoolean("Advertisment");
		swearenabled = config.getBoolean("Swear");
		whitelist = config.getStringList("Whitelist");
		stopit = config.getBoolean("Cancel");
		warnenable = config.getBoolean("Warnings.enable");
		altword = config.getString("Alternative");
		warnings = config.getInt("Warnings.count");
		swearalt = config.getString("Warnings.custom-alts.swear");
		advalt = config.getString("Warnings.custom-alts.advert");
		custom = config.getBoolean("Warnings.custom");
		minlength = config.getInt("caps-length");
		blockcmds = config.getStringList("Check-cmds");
		ban = config.getBoolean("Punishment.ban");
		listencmds = config.getBoolean("Listen-commands");
		floodtime = config.getInt("FloodTime");
		informmodsadv = config.getBoolean("Informing.advert");
		informmodscaps = config.getBoolean("Informing.caps");
		informmodsswear = config.getBoolean("Informing.swear");
		informmodsflood = config.getBoolean("Informing.flood");
		informmods = informmodsadv || informmodscaps || informmodsflood
				|| informmodsswear;
		warnenable = config.getBoolean("Warnings.enable");
		kick = config.getBoolean("Punishment.kick");
		replacements = config.getStringList("Replacements");
		mute = config.getBoolean("Punishment.mute.enable");
		mutetime = config.getInt("Punishment.mute.minutes");
		kill = config.getBoolean("Punishment.kill");
		command = config.getStringList("Punishment.command");
		reason = config.getString("Punishment.reason");
		capsper = config.getInt("CapsPercent");
		ipPattern = config.getString("Patterns.ip");
		domainPattern = config.getString("Patterns.domain");
		checksign = config.getBoolean("checksigns");
		if (whitelist.size() < 1) {
			whitelist.add("best-minecraft.ru");
			whitelist.add("127.0.0.1");
			config.set("Whitelist", whitelist);
			try {
				config.save(fileConf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getMessage(Messages path) {
		validate(path);
		String m = clang.getString(path.name().toLowerCase());
		m = ChatColor.translateAlternateColorCodes('&', m);
		return m;
	}

	private static void validate(Messages path) {
		if (clang.getString(path.name().toLowerCase()) == null)
			loadMessages();
	}

	public static void loadMessages() {
		File flang = new File(pl.getDataFolder(), "messages.yml");
		if (!flang.exists()) {
			try {
				flang.createNewFile();
			} catch (Exception e) {
			}
			flang = new File(pl.getDataFolder(), "messages.yml");
		}
		clang = YamlConfiguration.loadConfiguration(flang);
		if (!clang.isSet("wordexist"))
			clang.set("wordexist", "This word already exist!");
		if (!clang.isSet("newword"))
			clang.set("newword", "Added new word");
		if (!clang.isSet("addwarns"))
			clang.set("addwarns", "Warns changed for");
		if (!clang.isSet("remwarns"))
			clang.set("remwarns", "Warns changed for");
		if (!clang.isSet("plnotfound"))
			clang.set("plnotfound", "Player not found!");
		if (!clang.isSet("warnszero"))
			clang.set("warnszero", "Player has no warnings");
		if (!clang.isSet("getwarn"))
			clang.set("getwarn", "You were warned");
		if (!clang.isSet("remwarn"))
			clang.set("remwarn", "You have removed warning");
		if (!clang.isSet("caps"))
			clang.set("caps", "Stop capsing!");
		if (!clang.isSet("flood"))
			clang.set("flood", "Stop flooding!");
		if (!clang.isSet("swear"))
			clang.set("swear", "Stop swearing!");
		if (!clang.isSet("adv"))
			clang.set("adv", "It is bad place for advertisment!");
		if (!clang.isSet("muted"))
			clang.set("muted", "You has been muted!");
		if (!clang.isSet("wordnotfound"))
			clang.set("wordnotfound", "Word not found!");
		if (!clang.isSet("remword"))
			clang.set("remword", "Word removed");
		if (!clang.isSet("givemute"))
			clang.set("givemute", "Player unmuted");
		if (!clang.isSet("unmuted"))
			clang.set("unmuted", "You can talk again");
		if (!clang.isSet("trytoflood"))
			clang.set("trytoflood",
					"Player $player$ tries to flooding: $message$");
		if (!clang.isSet("trytoswear"))
			clang.set("trytoswear",
					"Player $player$ tries to swearing: $message$");
		if (!clang.isSet("trytoadv"))
			clang.set("trytoadv",
					"Player $player$ tries to advertise: $message$");
		if (!clang.isSet("trytocaps"))
			clang.set("trytocaps",
					"Player $player$ tries to capsing: $message$");
		if (!clang.isSet("globalmute"))
			clang.set("globalmute", "Global Mute: ");
		if (!clang.isSet("yourwarns"))
			clang.set("yourwarns", "Your warnings: ");
		if (!clang.isSet("adminmute"))
			clang.set("adminmute", "$player$ is muted for $reason$");
		if (!clang.isSet("adminunmute"))
			clang.set("adminunmute", "$player$ unmuted");
		try {
			clang.save(flang);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
