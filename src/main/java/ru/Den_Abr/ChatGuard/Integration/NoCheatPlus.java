package ru.Den_Abr.ChatGuard.Integration;

import fr.neatmonster.nocheatplus.players.DataManager;
import fr.neatmonster.nocheatplus.players.IPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.neatmonster.nocheatplus.checks.chat.Captcha;
import fr.neatmonster.nocheatplus.checks.chat.ChatConfig;
import fr.neatmonster.nocheatplus.checks.chat.ChatData;

public class NoCheatPlus extends AbstractIntegration {
	private Captcha captcha;
	private JavaPlugin plugin;

	@Override
	public boolean skipPlayer(Player p) {
		IPlayerData pData = DataManager.getPlayerData(p);
		ChatData data = (ChatData)pData.getGenericInstance(ChatData.class);
		ChatConfig cc = (ChatConfig)pData.getGenericInstance(ChatConfig.class);
		return captcha.shouldCheckCaptcha(p, cc, data, pData);
	}

	@Override
	public boolean load() {
		plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("NoCheatPlus");
		if (plugin == null)
			return false;
		captcha = new Captcha();
		return true;
	}

	@Override
	public JavaPlugin getPlugin() {
		return plugin;
	}
}
