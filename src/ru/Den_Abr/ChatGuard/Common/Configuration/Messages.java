package ru.Den_Abr.ChatGuard.Common.Configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import ru.Den_Abr.ChatGuard.Bukkit.ChatGuardPlugin;

public class Messages {
	private static final int VERSION = 3;

	public enum Message {
		PLAYER_NOT_FOUND("player not found"), INFORM_CAPS("inform caps"), INFORM_SPAM("inform spam"), INFORM_SWEAR(
				"inform swear"), WAIT_COOLDOWN("wait cooldown"), SEC("sec"), GLOBAL_MUTE("global mute"), NO_PERMS(
						"no permissions"), GLOBAL_MUTE_ENABLED("global mute enabled"), GLOBAL_MUTE_DISABLED(
								"global mute disabled"), INFORM_FLOOD("inform flood"), SWEARING(
										"warn swearing"), FLOODING("warn flooding"), SPAMMING("warn spamming"), CAPSING(
												"warn capsing"), WARN_FORMAT("warn format"), SUCCESSFULLY(
														"successfully"), SWEAR("swear"), SPAM("spam"), FLOOD(
																"flood"), CAPS("caps"), ALREADY_MUTED(
																		"already muted"), DEFAULT_REASON(
																				"default reason"), UR_MUTED(
																						"you are muted"), PLAYER_MUTED(
																								"player muted"), MINUTES(
																										"minutes"), HOURS(
																												"hours"), DAYS(
																														"days"), IS_NOT_MUTED(
																																"is not muted"), MUTED(
																																		"muted"), YES(
																																				"yes"), NO(
																																						"no"), EXPIRE_TIME(
																																								"expiration time");

		private String key;

		private Message(String k) {
			key = k;
		}

		public String get() {
			return ChatColor.translateAlternateColorCodes('&',
					confMes.getString(key, "*** UNKNOWN MESSAGE " + key + " ***"));
		}
	}

	private static YamlConfiguration confMes;
	private static File fileMes;

	public static void load(ChatGuardPlugin pl) {
		fileMes = new File(pl.getDataFolder(), "locale.yml");
		if (!fileMes.exists()) {
			pl.saveResource("locale.yml", false);
		}
		confMes = YamlConfiguration.loadConfiguration(fileMes);
		if (confMes.getInt("version") != VERSION) {
			migrateFrom(confMes.getInt("version"));
		}
	}

	private static void migrateFrom(int v) {
		if (v == 1) {
			confMes.set("hours", "hrs.");
			confMes.set("days", "days");
			confMes.set("minutes", "mins.");
			confMes.set("you are muted", "You cant send messages because you are muted for {REASON}. Wait {TIME}");
			confMes.set("player muted", "Player muted for {TIME}. Reason: {REASON}");
			confMes.set("already muted", "Player is already muted!");
			confMes.set("default reason", "Without reason");
			confMes.set("version", 2);
			confMes.set("is not muted", "Player is not muted");
			v = 2;
		}
		if (v == 2) {
			confMes.set("muted", "Muted");
			confMes.set("yes", "&4YES");
			confMes.set("no", "&3NO");
			confMes.set("expiration time", "Mute expires");
			confMes.set("version", 3);
			v = 3;
		}
		save();
	}

	public static void save() {
		try {
			confMes.save(fileMes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
