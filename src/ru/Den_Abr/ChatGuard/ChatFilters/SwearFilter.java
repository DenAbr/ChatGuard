package ru.Den_Abr.ChatGuard.ChatFilters;

import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.ViolationType;

public class SwearFilter implements Filter {

	@Override
	public ViolationType checkMessage(String message, CGPlayer player) {
		return null;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		return null;
	}

}
