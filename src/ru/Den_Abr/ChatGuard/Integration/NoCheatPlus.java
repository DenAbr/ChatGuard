package ru.Den_Abr.ChatGuard.Integration;

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
		return captcha.shouldCheckCaptcha(ChatConfig.getConfig(p), ChatData.getData(p));
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
