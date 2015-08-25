package ru.Den_Abr.ChatGuard.ChatFilters;

import java.util.HashSet;
import java.util.Set;

import ru.Den_Abr.ChatGuard.ViolationInfo;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public abstract class AbstractFilter implements Filter {
	private static Set<Filter> activeFilters = new HashSet<>();
	protected int maxWarns;

	public static Set<Filter> getActiveFilters() {
		return activeFilters;
	}

	public static ViolationInfo handleMessage(String mes, CGPlayer player) {
		ViolationInfo info = new ViolationInfo();
		info.setPlayer(player);
		info.setOriginalMessage(mes);
		info.setClearMessage(mes);
		for (Filter f : getActiveFilters()) {
			if (f.checkMessage(mes, player) != null) {
				info.setClearMessage(f.getClearMessage(mes, player));
			}
		}
		return info;
	}

	@Override
	public int getMaxWarnings() {
		return Settings.isSeparatedWarnings() ? maxWarns : Settings.getMaxWarns();
	}
}
