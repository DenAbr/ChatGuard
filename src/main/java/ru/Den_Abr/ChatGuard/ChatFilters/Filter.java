package ru.Den_Abr.ChatGuard.ChatFilters;

import org.bstats.bukkit.Metrics;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public interface Filter {

    @Deprecated
    public Violation checkMessage(String message, CGPlayer player);

    public Violation checkMessage(String message, CGPlayer player, boolean justCheck);

    public String getClearMessage(String message, CGPlayer player);

    public int getMaxWarnings();

    public void register();

    default void addMetricsGraph() {
        ChatGuardPlugin.metrics.addCustomChart(
                new Metrics.SimplePie("filters_used", () -> getClass().getSimpleName().replace("Filter", "")));
    }

}
