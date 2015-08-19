package ru.Den_Abr.ChatGuard.Integration;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractPlugin implements IntegratedPlugin {
	private Set<IntegratedPlugin> plugins = new HashSet<>();

	public Set<IntegratedPlugin> getIntegratedPlugins() {
		return plugins;
	}

}
