package ru.Den_Abr.ChatGuard.ChatFilters;

import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.ViolationType;

public interface Filter {

	public ViolationType checkMessage(String message, CGPlayer player);
	public String getClearMessage(String message, CGPlayer player);
	
}
