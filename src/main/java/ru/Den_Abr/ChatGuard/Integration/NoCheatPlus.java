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
        ChatData data = ChatData.getData(p);
        ChatConfig cc = ChatConfig.getConfig(p);
        return captcha.shouldCheckCaptcha(cc, data);
    }

    @Override
    public boolean load() {
        plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("NoCheatPlus");
        if (plugin == null)
            return false;
        try {
            Class.forName("fr.neatmonster.nocheatplus.players.IPlayerData");
            // 3.16.1+
            return false;
        } catch (ClassNotFoundException e) {
        }
        captcha = new Captcha();
        return true;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }
}
