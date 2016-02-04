package ru.Den_Abr.ChatGuard.Common;

import ru.Den_Abr.ChatGuard.Common.Player.CGPlayer;

public interface ChatGuardCommon {

	public CGPlayer wrapPlayer(Object handle);

	public String getName(Object handle);

}
