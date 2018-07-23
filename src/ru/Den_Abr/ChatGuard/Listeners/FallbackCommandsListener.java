package ru.Den_Abr.ChatGuard.Listeners;

import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.Den_Abr.ChatGuard.Configuration.Settings;

public class FallbackCommandsListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        e.setMessage(getCommandWithoutFallback(e.getMessage()));
    }

    public static String getCommandWithoutFallback(String cmd) {
        if (!Settings.doReplaceCommandsWithFallbackPrefix())
            return cmd;

        String[] splitted = cmd.split(" ");
        String cmdName = splitted[0];
        if (!cmdName.contains(":"))
            return cmd;

        StringBuilder slashes = new StringBuilder();
        for (char slash : cmdName.toCharArray()) {
            if (slash != '/')
                break;
            slashes.append(slash);
        }
        String origCommand = cmdName.split(":")[1];
        
        splitted[0] = slashes.append(origCommand).toString();
        return StringUtils.join(splitted, ' ');
    }
}
