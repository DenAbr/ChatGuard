package ru.Den_Abr.ChatGuard.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventPriority;

import com.google.common.io.Files;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Utils.Utils;

public class Settings {
    private static final int CONFIG_VERSION = 7;
    private static YamlConfiguration config;

    private static boolean usePackets;
    private static boolean separateWarnings;
    private static boolean hardmode;
    private static boolean cancelEnabled;
    private static boolean warnsEnabled;
    private static boolean punishmentsEnabled;
    private static boolean signsEnabled;
    private static boolean itemsEnabled;
    private static boolean replaceCommandsWithFallbackPrefix;

    private static int maxWarnings;
    private static int debugLevel;
    private static int cooldown;

    private static long maxMuteTime;

    private static String replacement;
    private static EventPriority prior;

    private static Map<String, Integer> commands = new HashMap<>();
    private static Map<String, String> reasons = new HashMap<>();
    private static Map<String, String> substitutions = new HashMap<>();

    private static List<String> disabledIntegrations = new ArrayList<>();

    public static void load(ChatGuardPlugin pl) {
        File fconfig = new File(pl.getDataFolder(), "config.yml");
        if (!fconfig.exists())
            pl.saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(fconfig);

        if (!config.isSet("Version")) {
            try {
                new File(pl.getDataFolder(), "old").mkdirs();
                Files.move(fconfig, new File(new File(pl.getDataFolder(), "old"), "old_config.yml"));
                Files.move(new File(pl.getDataFolder(), "warnings.yml"),
                        new File(new File(pl.getDataFolder(), "old"), "warnings.yml"));
                Files.move(new File(pl.getDataFolder(), "messages.yml"),
                        new File(new File(pl.getDataFolder(), "old"), "messages.yml"));
                ChatGuardPlugin.getLog().info("Old configuration was moved to old dirrectory");
            } catch (IOException e) {
                e.printStackTrace();
            }
            fconfig.delete();
            pl.saveResource("config.yml", false);
            config = YamlConfiguration.loadConfiguration(fconfig);
        }

        if (config.getInt("Version") != CONFIG_VERSION) {
            migrateFrom(config.getInt("Version"));
        }

        usePackets = config.getBoolean("Other settings.use packets");
        separateWarnings = config.getBoolean("Warnings settings.separate");
        hardmode = config.getBoolean("Hard mode");
        cancelEnabled = config.getBoolean("Messages.cancel if violation");
        warnsEnabled = config.getBoolean("Warnings settings.enabled");
        punishmentsEnabled = config.getBoolean("Punishment settings.enabled");
        signsEnabled = config.getBoolean("Other settings.check signs");
        itemsEnabled = config.getBoolean("Other settings.check items");
        replaceCommandsWithFallbackPrefix = config.getBoolean("Other settings.remove command fallback prefix");

        maxMuteTime = Utils.parseTime(config.getString("Punishment settings.max mute time"));

        replacement = config.getString("Messages.replacement");
        prior = EventPriority.valueOf(config.getString("Other settings.event priority").toUpperCase());
        if (prior == null) {
            ChatGuardPlugin.getLog().warning("Wrong priority "
                    + config.getString("Other settings.event priority").toUpperCase() + "! Using HIGHEST");
            prior = EventPriority.HIGHEST;
        }

        maxWarnings = config.getInt("Warnings settings.max count");
        debugLevel = config.getInt("Other settings.debug level");
        cooldown = config.getInt("flood settings.message cooldown");

        commands.clear();
        for (String command : config.getStringList("Other settings.check commands")) {
            String[] cmd = command.split(Pattern.quote(":"));
            if (cmd.length != 2 || !Utils.isInt(cmd[1]) || Integer.parseInt(cmd[1]) < 0)
                continue;
            commands.put(cmd[0].toLowerCase(), Integer.parseInt(cmd[1]));
        }
        reasons.clear();
        for (String key : config.getConfigurationSection("Punishment settings.reasons").getKeys(false)) {
            reasons.put(key, ChatColor.translateAlternateColorCodes('&',
                    config.getString("Punishment settings.reasons." + key)));
        }
        substitutions.clear();
        for (String s : config.getStringList("Substitutions")) {
            String[] els = s.split(Pattern.quote("|"), 2);
            if (els.length != 2)
                continue;
            substitutions.put(els[0], els[1]);
        }

        disabledIntegrations.clear();
        disabledIntegrations.addAll(config.getStringList("Other settings.disabled integrations"));

        if (debugLevel > 0) {
            ChatGuardPlugin.debug(1, "Debug level: " + getDebugLevel());
        }
    }

    public static boolean usePackets() {
        return usePackets;
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static void saveConfig() {
        try {
            getConfig().save(new File(ChatGuardPlugin.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isSeparatedWarnings() {
        return separateWarnings;
    }

    public static int getMaxWarns() {
        return maxWarnings;
    }

    public static boolean doReplaceCommandsWithFallbackPrefix() {
        return replaceCommandsWithFallbackPrefix;
    }

    public static int getDebugLevel() {
        return debugLevel;
    }

    public static String getReplacement() {
        return replacement;
    }

    public static boolean isHardMode() {
        return hardmode;
    }

    public static boolean isCancellingEnabled() {
        return cancelEnabled || isHardMode();
    }

    public static int getCooldown() {
        return cooldown;
    }

    public static boolean isCooldownEnabled() {
        return cooldown > 0;
    }

    public static Map<String, Integer> getCheckCommands() {
        return commands;
    }

    public static int getMaxWarnCount(String sec) {
        if (isSeparatedWarnings()) {
            return config.getInt(sec + " settings.max warnings");
        } else {
            return getMaxWarns();
        }
    }

    public static List<String> getPunishCommands(String sec) {
        if (!config.getBoolean("Punishment settings.commands.custom"))
            sec = "common";
        return config.getStringList("Punishment settings.commands." + sec + " commands");
    }

    public static Map<String, String> getPunishReasons() {
        return reasons;
    }

    public static boolean isWarnsEnabled() {
        return warnsEnabled;
    }

    public static boolean isPunishmentsEnabled() {
        return punishmentsEnabled;
    }

    public static boolean isSignsEnabled() {
        return signsEnabled;
    }

    public static long getMaxMuteTime() {
        return maxMuteTime;
    }

    public static boolean isItemsEnabled() {
        return itemsEnabled;
    }

    private static void migrateFrom(int v) {
        boolean needSave = false;
        if (v == 1) {
            getConfig().set("Punishment settings.max mute time", "1h");
            getConfig().set("Version", 2);

            needSave = true;
            v = 2;
        }
        if (v == 2 || v == 3) {
            getConfig().set("Version", 4);
            getConfig().set("Other settings.check items", true);

            try {
                java.nio.file.Files.write(
                        new File(ChatGuardPlugin.getInstance().getDataFolder(), "ipRegexp.txt").toPath(),
                        getConfig().getString("spam settings.ip regexp").getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                java.nio.file.Files.write(
                        new File(ChatGuardPlugin.getInstance().getDataFolder(), "domainRegexp.txt").toPath(),
                        getConfig().getString("spam settings.domain regexp").getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

                getConfig().set("spam settings.domain regexp", "now in domainRegexp.txt");
                getConfig().set("spam settings.ip regexp", "now in ipRegexp.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

            getConfig().set("Substitutions", Arrays.asList("Red|Green"));
            needSave = true;
            v = 4;
        }
        if (v == 4) {
            getConfig().set("Version", 5);
            getConfig().set("Other settings.event priority", "HIGHEST");
            needSave = true;
            v = 5;
        }
        if (v == 5) {
            getConfig().set("Version", 6);
            getConfig().set("Other settings.disabled integrations", Collections.EMPTY_LIST);
            needSave = true;
            v = 6;
        }
        if (v == 6) {
            getConfig().set("Version", 7);
            getConfig().set("Other settings.remove command fallback prefix", false);
            needSave = true;
            v = 7;
        }
        if (needSave)
            saveConfig();
    }

    public static EventPriority getPriority() {
        return prior;
    }

    public static Map<String, String> getSubstitutions() {
        return substitutions;
    }

    public static List<String> getDisabledIntegrations() {
        return disabledIntegrations;
    }
}
