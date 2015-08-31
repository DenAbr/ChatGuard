package ru.Den_Abr.ChatGuard.ChatFilters;

import ru.Den_Abr.ChatGuard.Violation;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public interface Filter {

	public Violation checkMessage(String message, CGPlayer player);
	public String getClearMessage(String message, CGPlayer player);
	public int getMaxWarnings();
	public void register();
	
}
