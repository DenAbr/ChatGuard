package ru.Den_Abr.ChatGuard.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.base.Objects;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Utils.FixedSizeList;

public abstract class CGPlayer {
	private static List<CGPlayer> cache = new LinkedList<>();

	private FixedSizeList<String> lastMessages = new FixedSizeList<>(1);
	private List<Violation> violations = new ArrayList<>();
	protected long lastMessage = -1L;

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

	public static CGPlayer get(Player p) {
		CGPlayer temp = new LegacyChatPlayer(p);
		if (cache.contains(new LegacyChatPlayer(p))) {
			return cache.get(cache.indexOf(temp));
		}
		ChatGuardPlugin.debug(2, cache);
		cache.add(temp);
		return temp;
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
		violations.add(v);
		int violCount = getViolationCount(v, false);
		warn(v, violCount, maxWarn);
		if (violCount >= maxWarn && Settings.isPunishmentsEnabled() && Settings.isWarnsEnabled()) {
			punish(v);
			clearWarnings(v, Settings.isSeparatedWarnings());
		}
	}

	private void clearWarnings(Violation v, boolean separatedWarnings) {
		if (!separatedWarnings) {
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
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
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
			break;
		}
	}
}
