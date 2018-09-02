package ru.Den_Abr.ChatGuard.Player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LegacyChatPlayer extends CGPlayer {
    private String p;

    public LegacyChatPlayer(Player p) {
        this.p = p.getName();
    }

    @Override
    public boolean hasPermission(String perm) {
        return getPlayer() != null && getPlayer().hasPermission(perm);
    }

    @Override
    public String getName() {
        return p;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayerExact(getName());
    }

}
