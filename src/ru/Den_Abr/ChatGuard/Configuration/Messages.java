package ru.Den_Abr.ChatGuard.Configuration;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;

public class Messages {

	public enum Message {
		PLAYER_NOT_FOUND("player not found"), INFORM_CAPS("inform caps"), INFORM_SPAM("inform spam"), INFORM_SWEAR(
				"inform swear"), WAIT_COOLDOWN("wait cooldown"), SEC("sec"), GLOBAL_MUTE("global mute"), NO_PERMS(
						"no permissions"), GLOBAL_MUTE_ENABLED("global mute enabled"), GLOBAL_MUTE_DISABLED(
								"global mute disabled"), INFORM_FLOOD("inform flood"), SWEARING(
										"warn swearing"), FLOODING("warn flooding"), SPAMMING("warn spamming"), CAPSING(
												"warn capsing"), WARN_FORMAT("warn format");

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
	}
}
