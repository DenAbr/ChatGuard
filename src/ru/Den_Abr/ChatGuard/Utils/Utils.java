package ru.Den_Abr.ChatGuard.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Utils {

	public static void clearChat(Player p) {
		for (int i = 0; i < 150; i++) {
			p.sendMessage("");
		}
	}

	// for compability with 1.7.10< versions
	@SuppressWarnings("unchecked")
	public static Collection<Player> getOnlinePlayers() {
		Collection<Player> playersOnline = new ArrayList<>();
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class)
				playersOnline = ((Collection<Player>) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0])
						.invoke(null, new Object[0]));
			else
				playersOnline = Arrays.asList(((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0])
						.invoke(null, new Object[0])));
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return playersOnline;

	}

	public static String getWord(String message, int start, int end) {
		int wordStart = 0;
		int wordEnd = message.length();

		char[] chars = message.toCharArray();
		for (int i = start; i >= 0; i--) {
			if (chars[i] == ' ') {
				wordStart = i;
				break;
			}
		}
		for (int i = end; i < message.length(); i++) {
			if (chars[i] == ' ') {
				wordEnd = i;
				break;
			}
		}

		String word = message.substring(wordStart, wordEnd);
		return word.trim();
	}
	
	public static boolean isInt(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
}
