package ru.Den_Abr.ChatGuard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import ru.Den_Abr.ChatGuard.AuthPlugins.Integrator;
import ru.Den_Abr.ChatGuard.Configs.Config;
import ru.Den_Abr.ChatGuard.Configs.Messages;
import ru.Den_Abr.ChatGuard.Configs.PlayerData;
import ru.Den_Abr.ChatGuard.Handlers.ChatHandler;
import ru.Den_Abr.ChatGuard.Handlers.CmdHandler;
import ru.Den_Abr.ChatGuard.Handlers.SignListener;
import ru.Den_Abr.ChatGuard.Packets.BKPackets;
import ru.Den_Abr.ChatGuard.Packets.PLPackets;
import ru.Den_Abr.ChatGuard.Workers.Checkers;
import ru.Den_Abr.ChatGuard.Workers.Updater;
import ru.Den_Abr.ChatGuard.Workers.Updater.UpdateType;

public class ChatGuardPlugin extends JavaPlugin {
	static String[] help = new String[14];
	public static ChatGuardPlugin plugin;
	public boolean pl = false;
	public boolean bk = false;
	public Config config;
	public static File fileConf;
	public List<String> li = new ArrayList<String>();
	public Integrator in;
	public Utils ut;
	public ChatHandler ch;
	public CmdHandler cmh;

	@Override
	public void onEnable() {
		plugin = this;
		ch = new ChatHandler();
		cmh = new CmdHandler();
		if (!setupProtocol()) {
			getLogger()
					.info("ProtocolLib or BKCommonLib not found! Using Events System. It may cause incompatibility with other chat plugins!!!");
			getServer().getPluginManager().registerEvents(ch, this);
			getServer().getPluginManager().registerEvents(cmh, this);
		}
		setupAuth();
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (Exception e) {
			getLogger().info("Failed to connect with Metrics");
		}
		if (getConfig().getBoolean("CheckUpdates", false))
			checkUpdates();
		config = new Config(this);
		ut = new Utils(this);
		fileConf = Config.fileConf;
		getLogger().info(
				"Loaded " + Config.stopwords.split(Pattern.quote("|")).length
						+ " words");
		getServer().getPluginManager().registerEvents(new SignListener(this),
				this);
		installTabCompleter();
		getLogger().info("ChatGuard enabled!");
	}

	private void installTabCompleter() {
		getCommand("cg").setTabCompleter(new TabCompleter() {

			@Override
			public List<String> onTabComplete(CommandSender arg0, Command arg1,
					String arg2, String[] arg3) {
				if (arg3.length == 1) {
					return (List<String>) StringUtil.copyPartialMatches(
							arg3[0], subComs, new ArrayList<String>());
				}
				return null;
			}
		});
	}

	private void setupAuth() {
		if (null != getServer().getPluginManager().getPlugin("AuthMe")) {
			Plugin pl = getServer().getPluginManager().getPlugin("AuthMe");
			Integrator.isAuthMeAncient = pl.getDescription().getAuthors()
					.contains("whoami");
			Integrator.isAuthMeReloaded = !pl.getDescription().getAuthors().contains("whoami");
		} else if (null != getServer().getPluginManager().getPlugin("xAuth")) {
			Integrator.isXAuth = true;
		} else if (null != getServer().getPluginManager().getPlugin(
				"LoginSecurity")) {
			Integrator.isLoginSecurity = true;
		}

		in = Integrator.getIntegrator();
		getLogger().info(
				in.getName()
						+ " is hooked. Players will be checked after login.");
	}

	public static ChatGuardPlugin getInstance() {
		return plugin;
	}

	private boolean setupProtocol() {
		if (null != getServer().getPluginManager().getPlugin("BKCommonLib")) {
			if (getServer().getPluginManager().getPlugin("BKCommonLib")
					.isEnabled()) {
				getLogger().info("BKCommonLib found! Hooking...");
				bk = true;
				new BKPackets(this);
				return true;
			}
		}
		if (null != getServer().getPluginManager().getPlugin("ProtocolLib")) {
			if (getServer().getPluginManager().getPlugin("ProtocolLib")
					.isEnabled()) {
				getLogger().info("ProtocolLib found! Hooking...");
				pl = true;
				new PLPackets(this);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onDisable() {
		if (pl)
			PLPackets.removeListeners();
		else if (bk)
			BKPackets.removeListeners();

		getServer().getScheduler().cancelTasks(this);
	}

	@SuppressWarnings("deprecation")
	public void checkUpdates() {
		final Updater up = new Updater(this, 50092, null,
				UpdateType.NO_DOWNLOAD, true);
		getServer().getScheduler().scheduleAsyncDelayedTask(this,
				new Runnable() {

					@Override
					public void run() {
						if (up.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
							getLogger()
									.info("Found a new version "
											+ up.getLatestName()
													.replace("ChatGuard", "")
													.trim()

											+ "! Download it on http://dev.bukkit.org/bukkit-plugins/chat-guard");
						} else if (up.getResult() == Updater.UpdateResult.NO_UPDATE) {
							getLogger()
									.info("You are using a latest version of ChatGuard!");
						} else {
							getLogger().info("Can't check for updates!");
						}
					}
				});

	}

	/* -------------------------------------------------------- */
	public static boolean hasPermission(Player player, String permission) {
		if (player.isOp()) {
			return true;
		} else {
			return player.hasPermission(permission);
		}
	}

	private final List<String> subComs = Arrays.asList(new String[] { "warns",
			"add", "remove", "addwarn", "whitelist", "unmute", "mute", "cc",
			"globalmute", "clear", "list", "reload", "removewarn", "help" });

	/* -------------------------------------------------------- */
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2,
			String[] a) {
		help[0] = ("ChatGuard v" + getDescription().getVersion() + " commands help:");
		help[1] = (ChatColor.GRAY + "/" + arg2 + " help" + ChatColor.GOLD + " - This page.");
		help[2] = (ChatColor.GRAY + "/" + arg2 + " warns {Player}"
				+ ChatColor.GOLD + " - Show {Player}'s warnings or your own.");
		help[3] = (ChatColor.GRAY + "/" + arg2 + " add [WORD]" + ChatColor.GOLD + " - Add new swearword [WORD]");
		help[4] = (ChatColor.GRAY + "/" + arg2 + " remove [WORD]"
				+ ChatColor.GOLD + " - Remove swearword [WORD]");
		help[5] = (ChatColor.GRAY + "/" + arg2 + " addwarn [PLAYER]"
				+ ChatColor.GOLD + " - Add a warning to [PLAYER]");
		help[6] = (ChatColor.GRAY + "/" + arg2 + " whitelist [WORD]"
				+ ChatColor.GOLD + " - Add word to Whitelist");
		help[7] = (ChatColor.GRAY + "/" + arg2 + " removewarn [PLAYER]"
				+ ChatColor.GOLD + " - Remove a warning to [PLAYER]");
		help[8] = (ChatColor.GRAY + "/" + arg2 + " unmute [PLAYER]"
				+ ChatColor.GOLD + " - unmute [PLAYER]");
		help[9] = (ChatColor.GRAY + "/" + arg2 + " list" + ChatColor.GOLD + " - The list of players with warnings.");
		help[10] = (ChatColor.GRAY + "/" + arg2 + " cc {Player|all}"
				+ ChatColor.GOLD + " - Clears chat for you/Player/all.");
		help[11] = (ChatColor.GRAY + "/" + arg2 + " globalmute"
				+ ChatColor.GOLD + " - Toggle global muting.");
		help[12] = (ChatColor.GRAY + "/" + arg2 + " clear" + ChatColor.GOLD + " - clear warnings.");
		help[13] = (ChatColor.GRAY + "/" + arg2 + " reload" + ChatColor.GOLD + " - reload ChatGuard.");
		if (a.length == 0 || !subComs.contains(a[0].toLowerCase())
				|| a[0].equalsIgnoreCase("help")) {
			s.sendMessage(help);
			return true;
		}
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("reload")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.reload")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			new Config(this);
			s.sendMessage("[" + this.getName() + "] Loaded "
					+ Config.stopwords.split(Pattern.quote("|")).length
					+ " words");
			s.sendMessage("[" + this.getName() + "] ChatGuard reloaded!");
			return true;
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("list")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.list")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			File flang = new File(getDataFolder(), "warnings.yml");
			YamlConfiguration config = YamlConfiguration
					.loadConfiguration(flang);
			if (!config.get("Players").equals("")) {
				Set<String> list = config.getConfigurationSection("Players")
						.getKeys(false);
				s.sendMessage("[ChatGuard] List of warned players:");
				for (String st : list) {
					if (ut.getWarn(st) != 0 && !Config.custom) {
						s.sendMessage(ChatColor.GRAY + st + ChatColor.RESET
								+ " - " + ChatColor.GOLD + ut.getWarn(st)
								+ " warnings");
					}
					if (Config.custom) {
						s.sendMessage(ChatColor.GRAY + st + ": "
								+ ChatColor.GOLD + "Flood - "
								+ ut.getCustomWarn(st, "flood") + ". Swear - "
								+ ut.getCustomWarn(st, "swear") + ". Caps - "
								+ ut.getCustomWarn(st, "caps") + ". Advert - "
								+ ut.getCustomWarn(st, "adv") + ".");
					}
				}
				return true;
			} else {
				s.sendMessage("[ChatGuard] List is empty");
				return true;
			}
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("warns")) {
			if (a.length == 2) {
				if (s instanceof Player) {
					if (!hasPermission((Player) s, "chatguard.warns.others")) {
						s.sendMessage(ChatColor.RED
								+ "You don't have permissions for this command!");
						return true;
					}
				}
				Player p = Bukkit.getPlayer(a[1]);
				if (p == null) {
					s.sendMessage(Config.getMessage(Messages.PLNOTFOUND));
					return true;
				}
				if (Config.custom) {
					s.sendMessage(ChatColor.GRAY + p.getName() + ": "
							+ ChatColor.GOLD + "Flood - "
							+ ut.getCustomWarn(p.getName(), "flood")
							+ ". Swear - "
							+ ut.getCustomWarn(p.getName(), "swear")
							+ ". Caps - "
							+ ut.getCustomWarn(p.getName(), "caps")
							+ ". Advert - "
							+ ut.getCustomWarn(p.getName(), "adv") + ".");
				} else {
					s.sendMessage(ChatColor.GRAY + p.getName() + ": "
							+ ut.getWarn(p.getName()) + " warnings");
				}
				return true;
			}
			if (s instanceof ConsoleCommandSender) {
				s.sendMessage("You don't have warnings");
				return true;
			}
			if (!Config.custom)
				s.sendMessage(Config.getMessage(Messages.YOURWARNS)
						+ ut.getWarn(s.getName()));
			else
				s.sendMessage(Config.getMessage(Messages.YOURWARNS)
						+ ChatColor.GOLD + "Flood - "
						+ ut.getCustomWarn(s.getName(), "flood") + ". Swear - "
						+ ut.getCustomWarn(s.getName(), "swear") + ". Caps - "
						+ ut.getCustomWarn(s.getName(), "caps") + ". Advert - "
						+ ut.getCustomWarn(s.getName(), "adv") + ".");

		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("cc")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.clearchat")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			if (a.length == 2) {
				if (s instanceof Player) {
					if (!hasPermission((Player) s, "chatguard.clearchat.others")) {
						s.sendMessage(ChatColor.RED
								+ "You don't have permissions for this command!");
						return true;
					}
				}
				if (!a[1].equalsIgnoreCase("all")) {
					Player cp = getServer().getPlayer(a[1]);
					if (cp == null) {
						s.sendMessage(Config.getMessage(Messages.PLNOTFOUND));
						return true;
					}
					if (cp.hasPermission("chatguard.clearchat.ignore")) {
						s.sendMessage(ChatColor.RED
								+ "You can't clear chat for this player.");
						return true;
					}
					for (int i = 0; i < 100; i++) {
						cp.sendMessage("");
					}
				} else {
					for (Player fp : getServer().getOnlinePlayers()) {
						for (int i = 0; i < 100; i++) {
							if (!fp.hasPermission("chatguard.clearchat.ignore"))
								fp.sendMessage("");
						}
					}
				}
			} else {
				for (int i = 0; i < 100; i++) {
					s.sendMessage("");
				}
			}
			return true;
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("globalmute")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.globalmute")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			if (!ut.globalmute) {
				ut.globalmute = true;
				getServer().broadcastMessage(
						Config.getMessage(Messages.GLOBALMUTE)
								+ ChatColor.GREEN + "ON");
			} else {
				ut.globalmute = false;
				getServer().broadcastMessage(
						Config.getMessage(Messages.GLOBALMUTE) + ChatColor.RED
								+ "OFF");
			}
			return true;
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("add")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.addword")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			if (a.length < 2) {
				s.sendMessage("[" + this.getName() + "]" + ChatColor.DARK_RED
						+ " Enter word!");
				return true;
			}
			a[1] = a[1].replaceAll("\\s+", "");
			if (ut.containWord(a[1].toLowerCase())) {
				s.sendMessage("[" + this.getName() + "] "
						+ Config.getMessage(Messages.WORDEXIST));
				return true;
			} else {
				Config.addWord(a[1].toLowerCase());
				s.sendMessage("[" + this.getName() + "] "
						+ Config.getMessage(Messages.NEWWORD) + " "
						+ a[1].toLowerCase().trim());
				return true;
			}
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("whitelist")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.whitelistadd")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			if (a.length < 2) {
				s.sendMessage("[" + this.getName() + "]" + ChatColor.DARK_RED
						+ " Enter word!");
				return true;
			}
			a[1] = a[1].replaceAll("\\s+", "");
			if (Config.whitelist.contains(a[1].toLowerCase())) {
				s.sendMessage("[" + this.getName() + "] "
						+ Config.getMessage(Messages.WORDEXIST));
				return true;
			} else {
				Config.whitelist.add(a[1].toLowerCase());
				Config.config.set("Whitelist", Config.whitelist);
				try {
					Config.config.save(fileConf);
				} catch (IOException e) {
					e.printStackTrace();
				}
				new Config(this);
				s.sendMessage("[" + this.getName() + "] "
						+ Config.getMessage(Messages.NEWWORD) + " "
						+ a[1].toLowerCase().trim());
				return true;
			}
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("remove")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.removeword")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			if (a.length < 2) {
				s.sendMessage("[" + this.getName() + "]" + ChatColor.DARK_RED
						+ " Enter word!");
				return true;
			}
			a[1] = a[1].replaceAll("\\s+", "");
			if (!ut.containWord(a[1].toLowerCase())) {
				s.sendMessage("[" + this.getName() + "] "
						+ Config.getMessage(Messages.WORDNOTFOUND));
				return true;
			} else {
				String[] sw = Config.stopwords.split(Pattern.quote("|"));
				for (String st : sw) {
					li.add(st);
					li.remove(a[1]);
				}
				Config.stopwords = "";
				for (int i = 0; i < li.size(); i++) {
					Config.stopwords = Config.stopwords + "|" + li.get(i);
				}
				Config.stopwords = Config.stopwords.replaceFirst(
						Pattern.quote("|"), "");
				Config.removeWord(Config.stopwords);
				li.clear();
				s.sendMessage("[" + this.getName() + "] "
						+ Config.getMessage(Messages.REMWORD) + " "
						+ a[1].toLowerCase().trim());
				return true;
			}
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("help")) {
			s.sendMessage(help);
			return true;
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("clear")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.clear")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			File flang = new File(getDataFolder(), "warnings.yml");
			YamlConfiguration config = YamlConfiguration
					.loadConfiguration(flang);
			if (!(a.length > 1)) {
				config.set("Players", "");
				try {
					config.save(flang);
				} catch (IOException e) {
					e.printStackTrace();
					s.sendMessage("Error!");
					return true;
				}
				s.sendMessage("[" + this.getName() + "] Warnings cleared!");
				return true;
			} else {
				String pl = Bukkit.getPlayer(a[1]).getName();
				config.set("Players." + pl, null);
				try {
					config.save(flang);
				} catch (IOException e) {
					e.printStackTrace();
					s.sendMessage("Error!");
					return true;
				}
				s.sendMessage("[" + this.getName() + "] Warnings cleared for "
						+ pl);
				return true;
			}
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("addwarn")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.addwarn")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			if (a.length < 2) {
				s.sendMessage("[" + this.getName() + "]" + ChatColor.DARK_RED
						+ " Enter nick!");
				return true;
			}
			a[1] = a[1].replaceAll("\\s+", "");
			Player player = Bukkit.getServer().getPlayer(a[1]);
			if (player != null) {
				if (Config.custom) {
					if (a.length != 3) {
						s.sendMessage("[" + this.getName() + "]"
								+ ChatColor.DARK_RED + " Enter type!");
						return true;
					}
					String type = a[2];
					if ((!type.equalsIgnoreCase("swear")
							&& !type.equalsIgnoreCase("adv")
							&& !type.equalsIgnoreCase("caps") && !type
								.equalsIgnoreCase("flood"))) {
						s.sendMessage("[ChatGuard] Types: swear, adv, caps, flood");
						return true;
					} else {
						ut.changeWarns(player, true, type);
					}
				} else {
					PlayerData.get(player).warned = true;
					ut.changeWarns(player, true, "nichego");
					PlayerData.get(player).warned = false;
				}
				Checkers.checkForPunishment(player);
				s.sendMessage("[ChatGuard] "
						+ Config.getMessage(Messages.ADDWARNS) + " "
						+ player.getDisplayName());
				return true;
			} else {
				s.sendMessage("[ChatGuard] "
						+ Config.getMessage(Messages.PLNOTFOUND));
				return true;
			}
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("unmute")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.unmute")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			if (a.length < 2) {
				s.sendMessage("[" + this.getName() + "]" + ChatColor.DARK_RED
						+ " Enter nick!");
				return true;
			}
			a[1] = a[1].replaceAll("\\s+", "");
			Player player = Bukkit.getServer().getPlayer(a[1]);
			if (player != null) {
				ut.unMute(player);
				player.sendMessage(Config.getMessage(Messages.UNMUTED));
				s.sendMessage("[ChatGuard] "
						+ Config.getMessage(Messages.ADMINUNMUTE).replace(
								"$player$", player.getName()));
				return true;
			} else {
				s.sendMessage("[ChatGuard] "
						+ Config.getMessage(Messages.PLNOTFOUND));
				return true;
			}
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("mute")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.mute")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			if (a.length < 2) {
				s.sendMessage("[" + this.getName() + "]" + ChatColor.DARK_RED
						+ " Enter nick!");
				return true;
			}
			a[1] = a[1].trim();
			String reason = "";
			if (a.length < 3) {
				s.sendMessage("[" + this.getName() + "]" + ChatColor.DARK_RED
						+ " Enter reason!");
				return true;
			}
			StringBuilder sb = new StringBuilder();
			boolean silent = false;
			for (int i = 2; i < a.length; ++i) {
				if (i + 1 == a.length && a[i].equals("silent")) {
					silent = true;
				} else
					sb.append(a[i]).append(" ");
			}
			reason = sb.toString().trim();
			Player player = Bukkit.getServer().getPlayer(a[1]);
			if (player != null) {
				ut.mute(player);
				player.sendMessage(Config.getMessage(Messages.MUTED));
				s.sendMessage("[ChatGuard] "
						+ Config.getMessage(Messages.ADMINMUTE)
								.replace("$player$", player.getName())
								.replace("$reason$", reason));
				if (!silent) {
					for (String c : Config.command) {
						Bukkit.getServer().dispatchCommand(
								Bukkit.getConsoleSender(),
								c.replace("{player}", player.getName())
										.replace("{reason}", Config.reason));
					}
				}
				return true;
			}
			s.sendMessage("[ChatGuard] "
					+ Config.getMessage(Messages.PLNOTFOUND));
			return true;
		} else
		/* -------------------------------------------------------- */
		if (a[0].equalsIgnoreCase("removewarn")) {
			if (s instanceof Player) {
				if (!hasPermission((Player) s, "chatguard.removewarn")) {
					s.sendMessage(ChatColor.RED
							+ "You don't have permissions for this command!");
					return true;
				}
			}
			if (a.length < 2) {
				s.sendMessage("[" + this.getName() + "]" + ChatColor.DARK_RED
						+ " Enter nick!");
				return true;
			}
			a[1] = a[1].replaceAll("\\s+", "");
			Player player = Bukkit.getServer().getPlayer(a[1]);
			if (player != null) {
				if ((ut.getWarn(player.getName()) == 0 && !Config.custom)) {
					s.sendMessage("[ChatGuard] "
							+ Config.getMessage(Messages.WARNSZERO));
				} else {
					if (Config.custom) {
						if (a.length != 3) {
							s.sendMessage("[" + this.getName() + "]"
									+ ChatColor.DARK_RED + " Enter type!");
							return true;
						}
						String type = a[2];
						if (!type.equalsIgnoreCase("swear")
								&& !type.equalsIgnoreCase("adv")
								&& !type.equalsIgnoreCase("caps")
								&& !type.equalsIgnoreCase("flood")) {
							s.sendMessage("[" + this.getName()
									+ "] Types: swear, adv, caps, flood");
							return true;
						} else {
							ut.changeWarns(player, false, type);
						}
					} else {
						ut.changeWarns(player, false, "no");
					}
					s.sendMessage("[" + this.getName() + "]"
							+ Config.getMessage(Messages.REMWARNS) + " "
							+ player.getDisplayName());
					return true;
				}
			} else {
				s.sendMessage("[" + this.getName() + "]"
						+ Config.getMessage(Messages.PLNOTFOUND));
				return true;
			}
		}
		return true;
	}

}
