package ru.Den_Abr.ChatGuard;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ru.Den_Abr.ChatGuard.Listeners.PacketsListener;
import ru.Den_Abr.ChatGuard.Listeners.PlayerListener;
import thirdparty.net.gravitydevelopment.updater.Updater;
import thirdparty.net.gravitydevelopment.updater.Updater.UpdateType;
import thirdparty.org.mcstats.MetricsLite;

public class ChatGuardPlugin extends JavaPlugin {
	private static ChatGuardPlugin instance;

	@Override
	public void onEnable() {
		instance = this;

		Settings.load(this);
		if (Settings.canCheckUpdates()) {
			checkForUpdates();
		}
		if (!setupProtocol()) {
			getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		}
		startMetrics();

		getLogger().info("ChatGuard enabled!");
	}

	private void startMetrics() {
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (Exception e) {
			getLogger().info("Failed to connect with Metrics");
		}
	}

	private void checkForUpdates() {
		Updater up = new Updater(this, 50092, getFile(), UpdateType.NO_DOWNLOAD, true);
		System.out.println(up.getResult());
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
		PacketsListener.stopListening();
		getServer().getScheduler().cancelTasks(this);
	}

}
