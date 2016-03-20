package ru.Den_Abr.ChatGuard.Listeners;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.ChatFilters.AbstractFilter;
import ru.Den_Abr.ChatGuard.ChatFilters.CharacterFilter;
import ru.Den_Abr.ChatGuard.ChatFilters.Filter;
import ru.Den_Abr.ChatGuard.ChatFilters.SpamFilter;
import ru.Den_Abr.ChatGuard.ChatFilters.SwearFilter;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.Utils;

public class ItemListener implements Listener {
	private static BukkitTask task;
	private static final List<Class<? extends AbstractFilter>> checkFilters = Arrays.asList(CharacterFilter.class,
			SpamFilter.class, SwearFilter.class);

	public static void scheduleChecks() {
		task = new BukkitRunnable() {

			@Override
			public void run() {
				if (Settings.isItemsEnabled())
					for (Player p : Utils.getOnlinePlayers()) {
						CGPlayer cp = CGPlayer.get(p);
						for (int i = 0; i < p.getInventory().getSize(); i++) {
							ItemStack item = p.getInventory().getItem(i);
							if (item == null || item.getType() == Material.AIR || !item.hasItemMeta())
								continue;
							boolean changed = false;
							ItemMeta im = item.getItemMeta();
							if (im.hasDisplayName()) {
								String dname = im.getDisplayName();
								if (check(cp, dname)) {
									if (Settings.isCancellingEnabled()) {
										im.setDisplayName(null);
									} else {
										im.setDisplayName(getClearMessage(cp, dname));
									}
									changed = true;
								}
							}
							if (im.hasLore()) {
								String jlore = StringUtils.join(im.getLore(), ' ');
								if (check(cp, jlore)) {
									im.setLore(null);
									changed = true;
								}
							}
							if (changed) {
								ChatGuardPlugin.debug(1, "Found item " + item + " in " + p.getName() + "' inventory");
								item.setItemMeta(im);
								setSlotSync(p, i, item);
							}
						}
					}
			}
		}.runTaskTimerAsynchronously(ChatGuardPlugin.getInstance(), 0, 30);
	}

	public static void setSlotSync(final Player p, final int i, final ItemStack item) {
		new BukkitRunnable() {

			@Override
			public void run() {
				p.getInventory().setItem(i, item);
			}
		}.runTask(ChatGuardPlugin.getInstance());
	}

	public static String getClearMessage(CGPlayer cp, String mes) {
		if (mes == null)
			return null;
		for (Filter filter : AbstractFilter.getActiveFilters()) {
			if (!checkFilters.contains(filter.getClass()))
				continue;

			mes = filter.getClearMessage(mes, cp);
		}
		return mes;
	}

	public static boolean check(CGPlayer p, String mes) {
		if (mes == null)
			return false;
		for (Filter filter : AbstractFilter.getActiveFilters()) {
			if (!checkFilters.contains(filter.getClass()))
				continue;

			if (!mes.equals(filter.getClearMessage(new String(mes), p))) {
				return true;
			}
		}
		return false;
	}

	public static void stop() {
		if (task != null)
			task.cancel();
	}
}
