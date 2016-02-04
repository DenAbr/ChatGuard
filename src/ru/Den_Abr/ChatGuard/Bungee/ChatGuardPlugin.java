package ru.Den_Abr.ChatGuard.Bungee;

import net.md_5.bungee.api.plugin.Plugin;
import ru.Den_Abr.ChatGuard.Common.ChatGuardCommon;
import ru.Den_Abr.ChatGuard.Common.Player.CGPlayer;

public class ChatGuardPlugin extends Plugin implements ChatGuardCommon {

	@Override
	public CGPlayer wrapPlayer(Object handle) {
		return null;
	}

	@Override
	public String getName(Object handle) {
		return null;
	}

}
