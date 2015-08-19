package ru.Den_Abr.ChatGuard.ChatFilters;

import java.io.File;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.io.Files;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Settings;
import ru.Den_Abr.ChatGuard.ViolationType;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class SwearFilter extends AbstractFilter {
	private Pattern swearPattern;
	private String replacement;
	private boolean informAdmins;

	@Override
	public ViolationType checkMessage(String message, CGPlayer player) {
		return null;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		return null;
	}

	@Override
	public void register() {
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("Spam settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		replacement = ChatColor.translateAlternateColorCodes('&', cs.getString("custom replacement"));

		try {
			File oldFileSwear = new File(ChatGuardPlugin.getInstance().getDataFolder(), "swearlist.txt");
			File newFileSwear = new File(ChatGuardPlugin.getInstance().getDataFolder(), "swearpattern.txt");
			if (!newFileSwear.exists()) {
				if (oldFileSwear.exists()) {
					String oldLine = "(" + Files.readFirstLine(oldFileSwear, Charset.forName("UTF-8")) + ")";
					Files.write(oldLine.getBytes(Charset.forName("UTF-8")), oldFileSwear);
					oldFileSwear.delete();
				} else {
					Files.write("(your|very|bad|words|here|now|with|more|regexp|support)".getBytes(), newFileSwear);
					ChatGuardPlugin.getInstance().getLogger().warning("Check your swearpattern.txt file!");
				}
			}
			String line = Files.readFirstLine(newFileSwear, Charset.forName("UTF-8"));
			swearPattern = Pattern.compile(line, Pattern.CASE_INSENSITIVE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

}
