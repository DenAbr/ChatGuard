package ru.Den_Abr.ChatGuard;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ru.Den_Abr.ChatGuard.ChatFilters.AbstractFilter;
import ru.Den_Abr.ChatGuard.ChatFilters.CapsFilter;
import ru.Den_Abr.ChatGuard.ChatFilters.CharacterFilter;
import ru.Den_Abr.ChatGuard.ChatFilters.FloodFilter;
import ru.Den_Abr.ChatGuard.ChatFilters.SpamFilter;
import ru.Den_Abr.ChatGuard.ChatFilters.SwearFilter;
import ru.Den_Abr.ChatGuard.Commands.CommandManager;
import ru.Den_Abr.ChatGuard.Configuration.Messages;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Configuration.Whitelist;
import ru.Den_Abr.ChatGuard.Integration.AbstractIntegration;
import ru.Den_Abr.ChatGuard.Integration.AuthMe34;
import ru.Den_Abr.ChatGuard.Integration.AuthMe5;
import ru.Den_Abr.ChatGuard.Integration.AuthMeLegacy;
import ru.Den_Abr.ChatGuard.Listeners.PacketsListener;
import ru.Den_Abr.ChatGuard.Listeners.PlayerListener;
import ru.Den_Abr.ChatGuard.Listeners.SignListener;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import thirdparty.net.gravitydevelopment.updater.Updater;
import thirdparty.net.gravitydevelopment.updater.Updater.UpdateType;
import thirdparty.org.mcstats.Metrics;

public class ChatGuardPlugin extends JavaPlugin {
	private static ChatGuardPlugin instance;
	public static Metrics metrics;

	@Override
	public void onEnable() {
		instance = this;

		getCommand("cg").setExecutor(new CommandManager(this));

		Settings.load(this);
		if (Settings.canCheckUpdates()) {
			checkForUpdates();
		}
		Messages.load(this);
		Whitelist.load(this);

		initMetrics();
		if (!setupProtocol()) {
			getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		}
		getServer().getPluginManager().registerEvents(new SignListener(), this);
		registerIntegratedPlugins();
		registerFilters();
		loadOnlinePlayers();

		startMetrics();

		getLogger().info("ChatGuard enabled!");
	}

	private void loadOnlinePlayers() {
		for (Player p : getServer().getOnlinePlayers()) {
			CGPlayer.get(p);
		}
	}

	private void registerIntegratedPlugins() {
		AbstractIntegration.getIntegratedPlugins().clear();

		// you can do it from your's plugins
		new AuthMeLegacy().register();
		new AuthMe34().register();
		new AuthMe5().register();
	}

	// the same as integration
	public void registerFilters() {
		AbstractFilter.getActiveFilters().clear();

		new CharacterFilter().register();
		new FloodFilter().register();
		new CapsFilter().register();
		new SpamFilter().register();
		new SwearFilter().register();

	}

	private void initMetrics() {
		try {
			metrics = new Metrics(this);
		} catch (IOException e) {
			getLogger().warning("Failed to init metrics");
		}
	}

	private void startMetrics() {
		if (null != metrics)
			metrics.start();
	}

	private void checkForUpdates() {
		new Updater(this, 50092, getFile(), UpdateType.DEFAULT, true);
	}

	public static ChatGuardPlugin getInstance() {
		return instance;
	}

	private boolean setupProtocol() {
		if (!Settings.useProtocol()) {
			return false;
		}
		Plugin plpl = getServer().getPluginManager().getPlugin("ProtocolLib");
		if (null != plpl && plpl.isEnabled()) {
			getLogger().info("ProtocolLib found!");
			PacketsListener.startListening();
			return true;
		} else
			getLogger().info("Install ProtocolLib to enable 'use packets' setting");
		return false;
	}

	@Override
	public void onDisable() {
		if (Settings.useProtocol())
			PacketsListener.stopListening();
		getServer().getScheduler().cancelTasks(this);
	}

	public static void debug(int level, Object... o) {
		if (level > Settings.getDebugLevel())
			return;
		for (Object obj : o)
			getInstance().getLogger().info("[DEBUG " + level + "] " + obj);
	}

	public static Logger getLog() {
		return getInstance().getLogger();
	}
	
	
}
