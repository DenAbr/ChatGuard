package ru.Den_Abr.ChatGuard.ChatFilters;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.io.Files;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Settings;
import ru.Den_Abr.ChatGuard.ViolationType;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class SwearFilter extends AbstractFilter {
	private List<Pattern> swearPatterns;
	private String replacement;
	private boolean informAdmins;

	@Override
	public ViolationType checkMessage(String message, CGPlayer player) {
		if (player.hasPermission("chatguard.ignore.swear"))
			return null;
		for (Pattern word : swearPatterns) {
			Matcher swearMatcher = word.matcher(message);
			if (swearMatcher.find()) {
				return ViolationType.SWEAR;
			}
		}
		return null;
	}

	@Override
	public String getClearMessage(String message, CGPlayer player) {
		return null;
	}

	@Override
	public void register() {
		ConfigurationSection cs = Settings.getConfig().getConfigurationSection("Swear settings");
		if (!cs.getBoolean("enabled"))
			return;
		informAdmins = cs.getBoolean("inform admins");
		maxWarns = cs.getInt("max warnings");

		replacement = ChatColor.translateAlternateColorCodes('&', cs.getString("custom replacement"));

		try {
			File oldFileSwear = new File(ChatGuardPlugin.getInstance().getDataFolder(), "swearlist.txt");
			File newFileSwear = new File(ChatGuardPlugin.getInstance().getDataFolder(), "swearwords.txt");
			if (!newFileSwear.exists()) {
				if (oldFileSwear.exists()) {
					String oldLine = Files.readFirstLine(oldFileSwear, Charset.forName("UTF-8")).replace('|', '\n');
					Files.write(oldLine.getBytes(Charset.forName("UTF-8")), newFileSwear);
					oldFileSwear.renameTo(new File(ChatGuardPlugin.getInstance().getDataFolder(), "swearlistOLD.txt"));
					ChatGuardPlugin.getLog().info("Moved old swearlist file");
				} else {
					ChatGuardPlugin.getInstance().saveResource("swearwords.txt", false);
					ChatGuardPlugin.getInstance().getLogger().warning("Check your swearwords.txt file!");
				}
			}
			swearPatterns = new ArrayList<>();

			for (String word : new ArrayList<>(Files.readLines(newFileSwear, Charset.forName("UTF-8")))) {
				swearPatterns.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
			}
			getActiveFilters().add(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

}
