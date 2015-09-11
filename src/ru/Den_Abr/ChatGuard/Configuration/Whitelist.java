package ru.Den_Abr.ChatGuard.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;

public class Whitelist {
	private static List<Pattern> whitelisted = new ArrayList<>();
	private static File wlFile;

	public static void load(ChatGuardPlugin pl) {
		wlFile = new File(pl.getDataFolder(), "whitelist.txt");
		if (!wlFile.exists()) {
			pl.saveResource("whitelist.txt", false);
		}
		try {
			for (String line : Files.readLines(wlFile, StandardCharsets.UTF_8)) {
				if (line.isEmpty())
					continue;
				whitelisted.add(Pattern.compile(line, Pattern.CASE_INSENSITIVE));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void add(String s) {
		Pattern p = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
		whitelisted.add(p);
		try {
			Files.append("\n" + s, wlFile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isWhitelisted(String found) {
		ChatGuardPlugin.debug(2, found);
		for (Pattern p : whitelisted) {
			if (p.matcher(found).find())
				return true;
		}
		return false;
	}
}
