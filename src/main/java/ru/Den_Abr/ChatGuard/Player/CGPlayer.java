package ru.Den_Abr.ChatGuard.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.MoreObjects;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Utils.FixedSizeList;
import ru.Den_Abr.ChatGuard.Utils.Utils;

public abstract class CGPlayer {
    private static List<CGPlayer> cache = new LinkedList<>();

    private FixedSizeList<String> lastMessages = new FixedSizeList<>(1);
    private FixedSizeList<String> allMessages = new FixedSizeList<>(100);
    private List<Violation> violations = new ArrayList<>();
    protected long lastMessage = -1L;
    protected long muteTime = -1L;
    private String muteReason;

    public abstract boolean hasPermission(String perm);

    public abstract String getName();

    public abstract Player getPlayer();

    public long getLastMessageTime() {
        return lastMessage;
    }

    public void setLastMessageTime(long time) {
        lastMessage = time;
    }

    public FixedSizeList<String> getLastMessages() {
        return lastMessages;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public boolean isMuted() {
        if (muteTime < 0)
            return false;
        if (muteTime > System.currentTimeMillis())
            return true;
        muteTime = -1;
        return false;
    }

    public String getMuteReason() {
        return muteReason;
    }

    public void mute(long time, String reason) {
        muteTime = System.currentTimeMillis() + time;
        muteReason = reason;
        getPlayer().sendMessage(
                Message.UR_MUTED.get().replace("{REASON}", reason).replace("{TIME}", Utils.getTimeInMaxUnit(time)));
    }

    public void unMute() {
        muteTime = -1;
        muteReason = null;
    }

    public long getMuteTime() {
        return muteTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return getName().equalsIgnoreCase(obj.toString());
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        return getName().equalsIgnoreCase(((CGPlayer) obj).getName());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(getName()).toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public static CGPlayer get(Player p) {
        if (null == p)
            return null;
        Optional<CGPlayer> optional = cache.stream()
                .filter(cgp -> cgp.getName().equals(p.getName()))
                .findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        CGPlayer player = new LegacyChatPlayer(p);
        ChatGuardPlugin.debug(2, cache);
        cache.add(player);
        return player;
    }

    public static CGPlayer get(String n) {
        for (CGPlayer cp : cache) {
            if (cp.getName().equalsIgnoreCase(n)) {
                return cp;
            }
        }
        return null;
    }

    public static List<CGPlayer> getCache() {
        return cache;
    }

    public static void clearAllWarnings(Violation v, boolean separate) {
        for (CGPlayer player : cache) {
            player.clearWarnings(v, separate);
        }
    }

    public FixedSizeList<String> getAllMessages() {
        return allMessages;
    }

    public int getViolationCount(Violation v, boolean separately) {
        if (!Settings.isSeparatedWarnings() && !separately) {
            return violations.size();
        }
        int c = 0;
        for (Violation pv : violations) {
            if (v == pv) {
                c++;
            }
        }
        return c;
    }

    public void handleViolation(Violation v, int maxWarn) {
        if (maxWarn == -1) {
            maxWarn = Settings.getMaxWarnCount(v.getPunishmentSection());
        }
        violations.add(v);
        int violCount = getViolationCount(v, false);
        warn(v, violCount, maxWarn);
        if (violCount >= maxWarn && Settings.isPunishmentsEnabled() && Settings.isWarnsEnabled()) {
            punish(v);
            clearWarnings(v, Settings.isSeparatedWarnings());
        }
    }

    public void clearWarnings(Violation v, boolean separatedWarnings) {
        if (!separatedWarnings || v == null) {
            violations.clear();
            return;
        }
        Iterator<Violation> iterator = violations.iterator();
        while (iterator.hasNext()) {
            if (v == iterator.next())
                iterator.remove();
        }
    }

    public void punish(Violation v) {
        for (String command : Settings.getPunishCommands(v.getPunishmentSection())) {
            for (Entry<String, String> reasonEntry : Settings.getPunishReasons().entrySet()) {
                command = command.replace("{Reason_" + reasonEntry.getKey() + "}", reasonEntry.getValue());
            }
            command = command.replace("{Player}", getName());
            final StringBuilder sb = new StringBuilder(command);
            ChatGuardPlugin.debug(2, "Punish command: " + command);
            new BukkitRunnable() {
                // chat events is async and we need to sync command execution
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sb.toString());
                }
            }.runTask(ChatGuardPlugin.getInstance());

        }
    }

    public void warn(Violation v, Integer violCount, Integer max) {
        String warnFormat = Message.WARN_FORMAT.get().replace("{MAX}", max.toString()).replace("{CURRENT}",
                violCount.toString());
        if (!Settings.isWarnsEnabled())
            warnFormat = "";
        // *_*
        switch (v) {
        case SWEAR:
            getPlayer().sendMessage(Message.SWEARING.get().replace("{WARNS}", warnFormat));
            break;
        case CAPS:
            getPlayer().sendMessage(Message.CAPSING.get().replace("{WARNS}", warnFormat));
            break;
        case SPAM:
            getPlayer().sendMessage(Message.SPAMMING.get().replace("{WARNS}", warnFormat));
            break;
        case FLOOD:
            getPlayer().sendMessage(Message.FLOODING.get().replace("{WARNS}", warnFormat));
            break;
        default:
            ChatGuardPlugin.debug(0, "Magic Violation type " + v);
            break;
        }
    }

}
