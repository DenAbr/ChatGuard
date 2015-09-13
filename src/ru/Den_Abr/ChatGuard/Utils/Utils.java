package ru.Den_Abr.ChatGuard.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;

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

	public static long parseTime(String time) {
		String unit;
		String number;
		if (time.length() > 1) {
			unit = time.substring(time.length() - 1).toLowerCase();
			number = time.substring(0, time.length() - 1);
		} else {
			unit = "s";
			number = time;
		}

		if (!isInt(number)) {
			return Long.MIN_VALUE;
		}

		int ch = Integer.parseInt(number);
		long result = Long.MIN_VALUE;

		switch (unit) {
		case "s":
			result = TimeUnit.SECONDS.toMillis(ch);
			break;
		case "m":
			result = TimeUnit.MINUTES.toMillis(ch);
			break;
		case "h":
			result = TimeUnit.HOURS.toMillis(ch);
			break;
		case "d":
			result = TimeUnit.DAYS.toMillis(ch);
			break;
		default:
			result = TimeUnit.SECONDS.toMillis(isInt(time) ? Integer.parseInt(time) : ch);
			break;
		}
		return result;
	}

	public static String translateTime(String time) {
		String unit;
		String number;
		if (time.length() > 1) {
			unit = time.substring(time.length() - 1).toLowerCase();
			number = time.substring(0, time.length() - 1);
		} else {
			unit = "s";
			number = time;
		}
		int ch = Integer.parseInt(number);
		return translateByLastDigit(ch, unit);
	}

	public static String translateByLastDigit(int ch, String unit2) {
		String unit;
		switch (unit2) {
		case "s":
			unit = Message.SEC.get();
			break;
		case "m":
			unit = Message.MINUTES.get();
			break;
		case "h":
			unit = Message.HOURS.get();
			break;
		case "d":
			unit = Message.DAYS.get();
			break;
		default:
			unit = "ms.";
			break;
		}
		return ch + " " + unit;
	}

	public static String getTimeInMaxUnit(long time) {
		int ch = 0;
		if (TimeUnit.MILLISECONDS.toDays(time) > 0) {
			ch = (int) TimeUnit.MILLISECONDS.toDays(time);
			return translateByLastDigit(ch, "d");
		} else if (TimeUnit.MILLISECONDS.toHours(time) > 0) {
			ch = (int) TimeUnit.MILLISECONDS.toHours(time);
			return translateByLastDigit(ch, "h");
		} else if (TimeUnit.MILLISECONDS.toMinutes(time) > 0) {
			ch = (int) TimeUnit.MILLISECONDS.toMinutes(time);
			return translateByLastDigit(ch, "m");
		} else if (TimeUnit.MILLISECONDS.toSeconds(time) > 0) {
			ch = (int) TimeUnit.MILLISECONDS.toSeconds(time);
			return translateByLastDigit(ch, "s");
		} else {
			ch = (int) time;
			return ch + "ms.";
		}
	}
}
