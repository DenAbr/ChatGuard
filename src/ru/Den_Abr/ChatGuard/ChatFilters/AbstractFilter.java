package ru.Den_Abr.ChatGuard.ChatFilters;

import java.util.HashSet;
import java.util.Set;

import ru.Den_Abr.ChatGuard.MessageInfo;
import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public abstract class AbstractFilter implements Filter {
	private static Set<Filter> activeFilters = new HashSet<>();
	protected int maxWarns;

	public static Set<Filter> getActiveFilters() {
		return activeFilters;
	}

	public static MessageInfo handleMessage(String mes, CGPlayer player) {
		MessageInfo info = new MessageInfo();
		info.setPlayer(player);
		info.setOriginalMessage(mes);
		info.setClearMessage(mes);
		for (Filter f : getActiveFilters()) {
			Violation v = f.checkMessage(mes, player);
			info.setClearMessage(f.getClearMessage(mes, player));
			if (v != null && v != Violation.BLACKCHAR) {
				player.handleViolation(v, f.getMaxWarnings());
			}
		}
		return info;
	}

	@Override
	public int getMaxWarnings() {
		return Settings.isSeparatedWarnings() ? maxWarns : Settings
				.getMaxWarns();
	}
}
