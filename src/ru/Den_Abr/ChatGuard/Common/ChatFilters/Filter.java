package ru.Den_Abr.ChatGuard.Common.ChatFilters;

import ru.Den_Abr.ChatGuard.Common.Violation;
import ru.Den_Abr.ChatGuard.Common.Player.CGPlayer;

public interface Filter {

	public Violation checkMessage(String message, CGPlayer player);
	public String getClearMessage(String message, CGPlayer player);
	public int getMaxWarnings();
	public void addMetricsGraph();
	public void register();
	
}
