package ru.Den_Abr.ChatGuard.Common.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;

import ru.Den_Abr.ChatGuard.Bukkit.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Common.ChatGuard;
import ru.Den_Abr.ChatGuard.Common.Violation;
import ru.Den_Abr.ChatGuard.Common.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Common.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Common.Utils.FixedSizeList;
import ru.Den_Abr.ChatGuard.Common.Utils.MessagePair;
import ru.Den_Abr.ChatGuard.Common.Utils.Utils;

public abstract class CGPlayer {
	private static Map<String, CGPlayer> cache = new HashMap<>();

	private FixedSizeList<String> lastMessages = new FixedSizeList<>(1);
	private FixedSizeList<MessagePair> sentMessages = new FixedSizeList<>(100);
	private FixedSizeList<String> allMessages = new FixedSizeList<>(100);
	private List<Violation> violations = new ArrayList<>();
	protected long lastMessage = -1L;
	protected long muteTime = -1L;
	private String muteReason;

	public abstract boolean hasPermission(String perm);

	public abstract String getName();
	
	public abstract Object getHandle();

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
		sendMessage(Message.UR_MUTED.get().replace("{REASON}", reason).replace("{TIME}", Utils.getTimeInMaxUnit(time)));
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
		return Objects.toStringHelper(this).addValue(getName()).toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public static CGPlayer get(Object p) {
		String name;
		if (p instanceof String) {
			name = ((String) p).toLowerCase();
		} else {
			name = ChatGuard.INSTANCE.getName(p).toLowerCase();
		}

		if (cache.containsKey(name)) {
			return cache.get(name);
		} else {
			CGPlayer cp = ChatGuard.INSTANCE.wrapPlayer(p);
			cache.put(name, cp);
			return cp;
		}
	}

	public static Collection<CGPlayer> getCache() {
		return cache.values();
	}

	public static void clearAllWarnings(Violation v, boolean separate) {
		for (CGPlayer player : cache.values()) {
			player.clearWarnings(v, separate);
		}
	}

	public FixedSizeList<MessagePair> getSentMessages() {
		return sentMessages;
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

	public abstract void punish(Violation v);

	public void warn(Violation v, Integer violCount, Integer max) {
		String warnFormat = Message.WARN_FORMAT.get().replace("{MAX}", max.toString()).replace("{CURRENT}",
				violCount.toString());
		if (!Settings.isWarnsEnabled())
			warnFormat = "";
		// *_*
		switch (v) {
		case SWEAR:
			sendMessage(Message.SWEARING.get().replace("{WARNS}", warnFormat));
			break;
		case CAPS:
			sendMessage(Message.CAPSING.get().replace("{WARNS}", warnFormat));
			break;
		case SPAM:
			sendMessage(Message.SPAMMING.get().replace("{WARNS}", warnFormat));
			break;
		case FLOOD:
			sendMessage(Message.FLOODING.get().replace("{WARNS}", warnFormat));
			break;
		default:
			ChatGuardPlugin.debug(0, "Magic Violation type " + v);
			break;
		}
	}

	public abstract void sendMessage(String string);

}
