package ru.Den_Abr.ChatGuard.Integration;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface IntegratedPlugin {

	public boolean skipPlayer(Player p);

	public JavaPlugin getPlugin();
	
	public boolean register();
}
