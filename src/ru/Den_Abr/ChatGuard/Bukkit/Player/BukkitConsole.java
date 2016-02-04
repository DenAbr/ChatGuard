package ru.Den_Abr.ChatGuard.Bukkit.Player;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import ru.Den_Abr.ChatGuard.Common.Violation;
import ru.Den_Abr.ChatGuard.Common.Player.CGPlayer;

public class BukkitConsole extends CGPlayer {
	public static final CGPlayer INSTANCE = new BukkitConsole();
	private ConsoleCommandSender console = Bukkit.getConsoleSender();

	@Override
	public boolean hasPermission(String perm) {
		return true;
	}

	@Override
	public String getName() {
		return console.getName();
	}

	@Override
	public void punish(Violation v) {
	}

	@Override
	public void sendMessage(String string) {
		console.sendMessage(string);
	}

	@Override
	public Object getHandle() {
		return console;
	}

}
