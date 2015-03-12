package ru.Den_Abr.ChatGuard;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ru.Den_Abr.ChatGuard.Configs.Config;
import ru.Den_Abr.ChatGuard.Configs.Messages;
import ru.Den_Abr.ChatGuard.Configs.PlayerData;

public class Utils {
	public File dataFolder;
	public ChatGuardPlugin pl;
	public YamlConfiguration config;
	public boolean globalmute = false;

	public Utils(ChatGuardPlugin pl) {
		this.pl = pl;
		dataFolder = pl.getDataFolder();
		config = Config.config;
	}

	/* -------------------------------------------------------- */
	public void punish(Player player) {
		if (!Config.custom)
			writeWarn(player, 0, "no");
		else {
			writeWarn(player, 0, "swear");
			writeWarn(player, 0, "caps");
			writeWarn(player, 0, "flood");
			writeWarn(player, 0, "adv");
		}
		if (Config.kill) {
			player.setHealth(0);
			player.sendMessage(Config.reason);
		}
		if (Config.mute) {
			mute(player);
			player.sendMessage(Config.getMessage(Messages.MUTED));
		}
		if (Config.kick)
			kickPlayer(player, Config.reason);
		if (Config.ban) {
			player.setBanned(true);
			kickPlayer(player, Config.reason);
		}
		for (String c : Config.command) {
			Bukkit.getServer().dispatchCommand(
					Bukkit.getConsoleSender(),
					c.replace("{player}", player.getName()).replace("{reason}",
							Config.reason));
		}
	}

	/* -------------------------------------------------------- */

	public boolean hasMute(Player p) {
		int mute = (int) (System.currentTimeMillis() / 1000 / 60);
		if (mute < getMute(p)) {
			return true;
		}
		if (globalmute && !p.hasPermission("chatguard.globalmute.ignore")) {
			return true;
		}
		return false;
	}

	/* -------------------------------------------------------- */
	public void kickPlayer(final Player p, final String r) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {

			@Override
			public void run() {
				p.kickPlayer(r);
			}
		});
	}

	/* -------------------------------------------------------- */
	public void changeWarns(Player player, boolean increase, String type) {
		if (Config.warnenable) {
			if (increase) {
				if (Config.custom) {
					if (type.equalsIgnoreCase("swear")) {
						int i1 = getCustomWarn(player.getName(), "swear") + 1;
						writeWarn(player, i1, "swear");
						player.sendMessage(Config.getMessage(Messages.GETWARN)
								+ " for " + type + ": "
								+ getCustomWarn(player.getName(), "swear")
								+ "/" + Config.warnings);
						PlayerData.get(player).warn = false;
					} else if (type.equalsIgnoreCase("caps")) {
						int i2 = getCustomWarn(player.getName(), "caps") + 1;
						writeWarn(player, i2, "caps");
						player.sendMessage(Config.getMessage(Messages.GETWARN)
								+ " for " + type + ": "
								+ getCustomWarn(player.getName(), "caps") + "/"
								+ Config.warnings);
						PlayerData.get(player).warn = false;
					} else if (type.equalsIgnoreCase("flood")) {
						int i3 = getCustomWarn(player.getName(), "flood") + 1;
						writeWarn(player, i3, "flood");
						player.sendMessage(Config.getMessage(Messages.GETWARN)
								+ " for " + type + ": "
								+ getCustomWarn(player.getName(), "flood")
								+ "/" + Config.warnings);
						PlayerData.get(player).warn = false;
					} else if (type.equalsIgnoreCase("adv")) {
						int i4 = getCustomWarn(player.getName(), "adv") + 1;
						writeWarn(player, i4, "adv");
						player.sendMessage(Config.getMessage(Messages.GETWARN)
								+ " for advert: "
								+ getCustomWarn(player.getName(), "adv") + "/"
								+ Config.warnings);
						PlayerData.get(player).warn = false;
					}
				} else {
					int i = getWarn(player.getName()) + 1;
					writeWarn(player, i, "no");
					player.sendMessage(Config.getMessage(Messages.GETWARN)
							+ ": " + getWarn(player.getName()) + "/"
							+ Config.warnings);
					PlayerData.get(player).warn = false;
				}
			} else {
				int i = (!Config.custom ? getWarn(player.getName())
						: getCustomWarn(player.getName(), type)) - 1;
				if (i < 0)
					return;
				writeWarn(player, i, type);
				String suffix = Config.custom ? "" : " for " + type;
				player.sendMessage(Config.getMessage(Messages.REMWARN)
						+ suffix
						+ ": "
						+ (!Config.custom ? getWarn(player.getName())
								: getCustomWarn(player.getName(), type)) + "/"
						+ Config.warnings);
			}
		}

	}

	/* -------------------------------------------------------- */
	public boolean containWord(String s) {
		for (int i = 0; i < Config.stopwords.split(Pattern.quote("|")).length; i++) {
			if (Config.stopwords.split(Pattern.quote("|"))[i].equals(s)) {
				return true;
			}
		}
		return false;
	}

	/* -------------------------------------------------------- */
	public int getCustomWarn(String player, String type) {
		File fwarn = new File(dataFolder, "warnings.yml");
		config = YamlConfiguration.loadConfiguration(fwarn);
		return config.getInt("Players." + player + "." + type);
	}

	/* -------------------------------------------------------- */
	public int getWarn(String player) {
		File fwarn = new File(dataFolder, "warnings.yml");
		config = YamlConfiguration.loadConfiguration(fwarn);
		return config.getInt("Players." + player + ".warnings");
	}

	/* -------------------------------------------------------- */
	public void writeWarn(Player player, int i, String type) {
		File fwarn = new File(dataFolder, "warnings.yml");
		if (i == 0) {
			config = YamlConfiguration.loadConfiguration(fwarn);
			config.set(
					"Players."
							+ (!Config.custom ? player.getName() + ".warnings"
									: player.getName() + "." + type), 0);
			try {
				config.save(fwarn);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		if (type.equalsIgnoreCase("no")) {
			config = YamlConfiguration.loadConfiguration(fwarn);
			config.set("Players." + player.getName() + ".warnings",
					Integer.valueOf(i));
			try {
				config.save(fwarn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			config = YamlConfiguration.loadConfiguration(fwarn);
			config.set("Players." + player.getName() + "." + type,
					Integer.valueOf(i));
			try {
				config.save(fwarn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* -------------------------------------------------------- */
	public void mute(Player player) {
		File fwarn = new File(dataFolder, "warnings.yml");
		config = YamlConfiguration.loadConfiguration(fwarn);
		config.set("Players." + player.getName() + ".mute",
				(int) (System.currentTimeMillis() / 1000 / 60)
						+ Config.mutetime);
		try {
			config.save(fwarn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* -------------------------------------------------------- */
	public int getMute(Player player) {
		File fwarn = new File(dataFolder, "warnings.yml");
		config = YamlConfiguration.loadConfiguration(fwarn);
		return config.getInt("Players." + player.getName() + ".mute");
	}

	/* -------------------------------------------------------- */
	public void unMute(Player player) {
		File fwarn = new File(dataFolder, "warnings.yml");
		config = YamlConfiguration.loadConfiguration(fwarn);
		config.set("Players." + player.getName() + ".mute", null);
		try {
			config.save(fwarn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* ------------------------------------------------------- */
	public boolean hasDisallowedSymbols(String s) {
		for (char c : s.toCharArray()) {
			if (isDisallowedChar(c)) {
				return true;
			}
		}
		return false;
	}

	/* ------------------------------------------------------- */
	public boolean isDisallowedChar(char c) {
		return !Config.simba.contains(((Character) c).hashCode());
	}

	/* ------------------------------------------------------- */
	public String removeDisallowedSymbols(String s) {
		for (Character c : s.toCharArray()) {
			if (isDisallowedChar(c)) {
				s = s.replaceAll(c.toString(), "");
			}
		}
		return s.trim();
	}
}
