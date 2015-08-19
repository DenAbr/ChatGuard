package ru.Den_Abr.ChatGuard.ChatFilters;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.io.Files;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Settings;
import ru.Den_Abr.ChatGuard.ViolationType;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class CharacterFilter extends AbstractFilter {
	private String charSet;

	@Override
	public ViolationType checkMessage(String message, CGPlayer player) {
		for (char c : message.toCharArray()) {
			if (isNotAllowed(c)) {
				return ViolationType.BLACKCHAR;
			}
		}
		return null;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		for (char c : message.toCharArray()) {
			if (isNotAllowed(c)) {
				message = message.replace(Character.valueOf(c).toString(), "");
			}
		}
		return message;
	}

	private boolean isNotAllowed(char ch) {
		return !charSet.contains(ch + "");
	}

	@Override
	public void register() {
		if (!Settings.getConfig().getBoolean("Messages.enable character whitelist")) {
			return;
		}
		try {
			File fCh = new File(ChatGuardPlugin.getInstance().getDataFolder(), "characterwhitelist.txt");
			File oldFCh = new File(ChatGuardPlugin.getInstance().getDataFolder(), "allowedsymbols.txt");
			if (!fCh.exists()) {
				if (oldFCh.exists()) {
					Files.move(oldFCh, fCh);
				} else {
					ChatGuardPlugin.getInstance().saveResource("characterwhitelist.txt", false);
				}
			}
			charSet = Files.readFirstLine(fCh, Charset.forName("UTF-8"));
			getActiveFilters().add(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
