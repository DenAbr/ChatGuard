package ru.Den_Abr.ChatGuard.Configs;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerData {
	public boolean warned = false;
	public boolean warn = false;
	public boolean swear = false;
	public boolean adv = false;
	public boolean caps = false;
	public boolean flood = false;
	public int i = 0;
	public int ch = 0;
	public int seconds = -1;
	public String messageText = null;
	public int taskId = 0;
	public static HashMap<Player, PlayerData> map = new HashMap<Player, PlayerData>();

	public static PlayerData get(Player player) {
		PlayerData vars = map.get(player);
		if (vars != null)
			return vars;
		else {
			PlayerData nvars = new PlayerData();
			map.put(player, nvars);
			return nvars;
		}
	}

	public void clear() {
		adv = false;
		flood = false;
		swear = false;
		caps = false;
	}
}
