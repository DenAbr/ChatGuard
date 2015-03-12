package ru.Den_Abr.ChatGuard.Workers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Configs.Config;
import ru.Den_Abr.ChatGuard.Configs.Messages;
import ru.Den_Abr.ChatGuard.Configs.PlayerData;

public class Checkers {
	public static Pattern ipPattern;
	public static Pattern domenPattern;
	public static Pattern swearPattern;
	public static ChatGuardPlugin pl = ChatGuardPlugin.plugin;

	public static PlayerData getVars(Player player) {
		return PlayerData.get(player);
	}

	public static String word(String t) {
		for (int i = 0; i < Config.replacements.size(); i++) {
			t = t.replace("(", "").replace(")", "");
			t = t.replaceAll(
					"[("
							+ ((String) Config.replacements.get(i))
									.split(Pattern.quote("|"))[0] + ")]",
					((String) Config.replacements.get(i)).split(Pattern
							.quote("|"))[1]);
		}
		return t;
	}

	public static void checkFlood(Player p, String m) {
		PlayerData vars = getVars(p);
		if (null != vars.messageText) {
			if ((vars.messageText.toLowerCase().equalsIgnoreCase(
					m.toLowerCase()) || m.toLowerCase().startsWith(
					vars.messageText.toLowerCase()))
					&& vars.seconds != -1
					&& ((int) (System.currentTimeMillis() / 1000L) < vars.seconds)
					&& (m.length() - vars.messageText.length() < 4)
					&& (!ChatGuardPlugin
							.hasPermission(p, "chatguard.ignoreall"))) {
				if (!ChatGuardPlugin.hasPermission(p, "chatguard.ignoreflood")) {
					p.sendMessage(Config.getMessage(Messages.FLOOD));
					vars.flood = true;
				}
			}
		}
		vars.messageText = m.toLowerCase();
		vars.seconds = ((int) (System.currentTimeMillis() / 1000L) + Config.floodtime);
	}

	public static String checkSwear(Player p, String m) {
		for (String t : m.split(" ")) {
			String s = word(t);
			swearPattern = Pattern.compile("(" + Config.stopwords + ")");
			Matcher swearMatcher = swearPattern.matcher(s.toLowerCase()
					.replaceAll("[^A-Za-zА-Яа-яà-ÿÀ-ß]", ""));
			if ((swearMatcher.find())
					&& (!ChatGuardPlugin
							.hasPermission(p, "chatguard.ignoreall"))) {
				if ((!ChatGuardPlugin.hasPermission(p, "chatguard.ignoreswear"))
						&& (!isWhitelisted(t.toLowerCase()))) {
					getVars(p).swear = true;
					p.sendMessage(Config.getMessage(Messages.SWEAR));
					m = m.replace(t, Config.custom ? Config.swearalt
							: Config.altword);
				}
			}

		}

		return m;
	}

	public static boolean isWhitelisted(String t) {
		String s = "";
		for (String w : Config.whitelist) {
			if (s.length() > 0)
				s = s + "|" + w;
			else
				s = w;
		}
		Pattern wp = Pattern.compile("(" + s.toLowerCase() + ")");
		Matcher wm = wp.matcher(t);
		return wm.find();
	}

	public static String checkAdvert(Player p, String m) {
		for (String t : m.split("\\s+")) {
			ipPattern = Pattern.compile(Config.ipPattern);
			domenPattern = Pattern.compile(Config.domainPattern);
			Matcher ipMatcher = ipPattern.matcher(t.toLowerCase());
			Matcher domenMatcher = domenPattern.matcher(t.toLowerCase());
			if ((ipMatcher.find())
					&& (!ChatGuardPlugin
							.hasPermission(p, "chatguard.ignoreall"))) {
				if (!ChatGuardPlugin.hasPermission(p, "chatguard.ignoreadv")
						&& !isWhitelisted(t.toLowerCase())) {
					getVars(p).adv = true;
					p.sendMessage(Config.getMessage(Messages.ADV));
					m = m.replace(t, Config.custom ? Config.advalt
							: Config.altword);
				}

			}

			if ((domenMatcher.find())
					&& (!ChatGuardPlugin
							.hasPermission(p, "chatguard.ignoreall"))) {
				if (!ChatGuardPlugin.hasPermission(p, "chatguard.ignoreadv")
						&& !isWhitelisted(t.toLowerCase())) {
					p.sendMessage(Config.getMessage(Messages.ADV));
					getVars(p).adv = true;
					m = m.replace(t, Config.custom ? Config.advalt
							: Config.altword);
				}
			}

		}

		return m;
	}

	public static String checkCaps(Player p, String m, String om) {
		if (m.length() >= Config.minlength) {
			int caps = 0;
			for (int i = 0; i < m.length(); i++) {
				if (Character.isUpperCase(m.charAt(i)))
					caps++;
			}
			int capspercent = caps * 100 / m.length();
			if ((capspercent >= Config.capsper)) {
				if (!ChatGuardPlugin.hasPermission(p, "chatguard.ignoreall")) {
					if (!ChatGuardPlugin.hasPermission(p,
							"chatguard.ignorecaps")) {
						getVars(p).caps = true;
						p.sendMessage(Config.getMessage(Messages.CAPS));
						om = om.toLowerCase();
					}
				}
			}
		}
		return om;
	}

	public static boolean hasWarnings(Player p) {
		return getVars(p).swear || getVars(p).caps || getVars(p).adv
				|| getVars(p).flood || getVars(p).warned;
	}

	public static boolean checkWarnings(Player p) {
		if (hasWarnings(p)) {
			if (Config.custom) {
				if (getVars(p).swear) {
					pl.ut.changeWarns(p, true, "swear");
				}
				if (getVars(p).adv) {
					pl.ut.changeWarns(p, true, "adv");
				}
				if (getVars(p).flood) {
					pl.ut.changeWarns(p, true, "flood");
				}
				if (getVars(p).caps) {
					pl.ut.changeWarns(p, true, "caps");
				}
			} else {
				pl.ut.changeWarns(p, true, "no");
			}
			checkForPunishment(p);
			return true;
		}
		return false;
	}

	public static void checkForPunishment(Player p) {
		if (Config.custom) {
			if (getVars(p).swear) {
				if (pl.ut
						.getCustomWarn(p.getName(), "swear") >= Config.warnings) {
					pl.ut.punish(p);
				}
			}
			if (getVars(p).adv) {
				if (pl.ut.getCustomWarn(p.getName(), "adv") >= Config.warnings) {
					pl.ut.punish(p);
				}
			}
			if (getVars(p).flood) {
				if (pl.ut
						.getCustomWarn(p.getName(), "flood") >= Config.warnings) {
					pl.ut.punish(p);
				}
			}
			if (getVars(p).caps) {
				if (pl.ut.getCustomWarn(p.getName(), "caps") >= Config.warnings) {
					pl.ut.punish(p);
				}
			}
		} else {
			if (pl.ut.getWarn(p.getName()) >= Config.warnings) {
				pl.ut.punish(p);
			}
		}
	}

	public static void inform(String m, Player p) {
		if (!Config.informmods)
			return;
		String inf = "";
		if (PlayerData.get(p).caps && Config.informmodscaps) {
			inf = Config.getMessage(Messages.TRYTOCAPS)
					.replace("$player$", p.getName()).replace("$message$", m);
			sendMessages(getModerList(), inf);
		}
		if (PlayerData.get(p).flood && Config.informmodsflood) {
			inf = Config.getMessage(Messages.TRYTOFLOOD)
					.replace("$player$", p.getName()).replace("$message$", m);
			sendMessages(getModerList(), inf);
		}
		if (PlayerData.get(p).swear && Config.informmodsswear) {
			inf = Config.getMessage(Messages.TRYTOSWEAR)
					.replace("$player$", p.getName()).replace("$message$", m);
			sendMessages(getModerList(), inf);
		}
		if (PlayerData.get(p).adv && Config.informmodsadv) {
			inf = Config.getMessage(Messages.TRYTOADV)
					.replace("$player$", p.getName()).replace("$message$", m);
			sendMessages(getModerList(), inf);
		}
	}

	private static void sendMessages(List<Player> l, String m) {
		if (!Config.informmods)
			return;
		Bukkit.getConsoleSender().sendMessage("[ChatGuard] " + m);
		if (getModerList().isEmpty())
			return;
		for (Player p : l) {
			p.sendMessage(m);
		}
	}

	private static List<Player> getModerList() {
		List<Player> list = new ArrayList<Player>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission("chatguard.inform")) {
				list.add(p);
			}
		}
		return list;
	}
}
