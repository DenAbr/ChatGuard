package ru.Den_Abr.ChatGuard.Packets;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Configs.Config;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

@SuppressWarnings("deprecation")
public class BKPackets {

	public BKPackets(final ChatGuardPlugin pl) {
		PacketUtil.addPacketListener(ChatGuardPlugin.plugin,
				new PacketListener() {
					@Override
					public void onPacketReceive(PacketReceiveEvent e) {
						Player p = e.getPlayer();
						if (null != pl.in) {
							if (!pl.in.isLogged(p)) {
								return;
							}
						}
						String m = e.getPacket()
								.read(PacketType.IN_CHAT.message).toString();
						if (m.startsWith("/")) {
							if (!Config.listencmds)
								return;
							PlayerCommandPreprocessEvent pcre = new PlayerCommandPreprocessEvent(
									p, m);
							pl.cmh.onCommand(pcre);
							if (pcre.isCancelled()) {
								e.setCancelled(true);
								return;
							}
							e.getPacket().write("message", pcre.getMessage());
						} else {
							PlayerChatEvent pce = new PlayerChatEvent(p, m);
							pl.ch.onChat(pce);
							if (pce.isCancelled()) {
								e.setCancelled(true);
								return;
							}
							e.getPacket().write("message", pce.getMessage());
						}
					}

					@Override
					public void onPacketSend(PacketSendEvent e) {
					}
				}, PacketType.IN_CHAT);
	}

	public static void removeListeners() {
		PacketUtil.removePacketListeners(ChatGuardPlugin.plugin);
	}
}
