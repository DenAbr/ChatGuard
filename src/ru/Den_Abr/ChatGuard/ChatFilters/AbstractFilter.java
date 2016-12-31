package ru.Den_Abr.ChatGuard.ChatFilters;

import java.util.HashSet;
import java.util.Set;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
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

	public static MessageInfo handleMessage(String mes, CGPlayer player, boolean isChat) {
		MessageInfo info = new MessageInfo();
		info.setPlayer(player);
		info.setOriginalMessage(mes);
		info.setClearMessage(mes);
		String copy = info.getOriginalMessage();
		for (Filter f : getActiveFilters()) {
			if (!isChat && (f.getClass().equals(FloodFilter.class) || f.getClass().equals(CapsFilter.class)))
				continue;

			Violation v = f.checkMessage(info.getOriginalMessage(), player);
			if (v != null) {
				copy = f.getClearMessage(copy, player);
				ChatGuardPlugin.debug(1, "Clear message after " + f.getClass().getSimpleName() + ": " + copy);
				info.setClearMessage(copy);
				if (v != Violation.BLACKCHAR) {
					player.handleViolation(v, f.getMaxWarnings());
					info.getViolations().add(v);
				}
			}
		}
		return info;
	}

	@Override
	public int getMaxWarnings() {
		return Settings.isSeparatedWarnings() ? maxWarns : Settings.getMaxWarns();
	}
}
