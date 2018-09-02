package ru.Den_Abr.ChatGuard;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.EventExecutor;
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
import ru.Den_Abr.ChatGuard.Integration.AuthMe;
import ru.Den_Abr.ChatGuard.Integration.NoCheatPlus;
import ru.Den_Abr.ChatGuard.Listeners.FallbackCommandsListener;
import ru.Den_Abr.ChatGuard.Listeners.ItemListener;
import ru.Den_Abr.ChatGuard.Listeners.PacketsListener;
import ru.Den_Abr.ChatGuard.Listeners.PlayerListener;
import ru.Den_Abr.ChatGuard.Listeners.SignListener;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.Utils;
import org.mcstats.Metrics;

public class ChatGuardPlugin extends JavaPlugin {
    private static ChatGuardPlugin instance;
    public static Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("cg").setExecutor(new CommandManager(this));

        Settings.load(this);
        Messages.load(this);
        Whitelist.load(this);
        ItemListener.scheduleChecks();

        initMetrics();
        if (!setupProtocol()) {
            final PlayerListener listener = new PlayerListener();
            EventExecutor exec = new EventExecutor() {

                @Override
                public void execute(Listener paramListener, Event e) throws EventException {
                    if (e instanceof AsyncPlayerChatEvent) {
                        listener.onPlayerChat((AsyncPlayerChatEvent) e);
                    }
                    if (e instanceof PlayerCommandPreprocessEvent) {
                        listener.onPlayerCommand((PlayerCommandPreprocessEvent) e);
                    }
                }
            };
            getServer().getPluginManager().registerEvent(AsyncPlayerChatEvent.class, listener, Settings.getPriority(),
                    exec, this, true);
            getServer().getPluginManager().registerEvent(PlayerCommandPreprocessEvent.class, listener,
                    Settings.getPriority(), exec, this, true);
            getServer().getPluginManager().registerEvents(new FallbackCommandsListener(), this);
        }
        getServer().getPluginManager().registerEvents(new SignListener(), this);
        registerIntegratedPlugins();
        registerFilters();
        loadOnlinePlayers();

        startMetrics();

        getLogger().info("ChatGuard enabled!");
    }

    private void loadOnlinePlayers() {
        for (Player p : Utils.getOnlinePlayers()) {
            CGPlayer.get(p);
        }
    }

    private void registerIntegratedPlugins() {
        AbstractIntegration.getIntegratedPlugins().clear();

        // you can do it from your's plugins
        new AuthMe().register();
        new NoCheatPlus().register();
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

    public static ChatGuardPlugin getInstance() {
        return instance;
    }

    private boolean setupProtocol() {
        if (!Settings.usePackets()) {
            return false;
        }
        Plugin plpl = getServer().getPluginManager().getPlugin("ProtocolLib");
        if (null != plpl && plpl.isEnabled()) {
            getLogger().info("ProtocolLib found!");
            PacketsListener.startListening();
            return true;
        } else
            getLogger().info("Install ProtocolLib before activating 'use packets' setting");
        return false;
    }

    @Override
    public void onDisable() {
        if (Settings.usePackets())
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
