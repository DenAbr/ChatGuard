package ru.Den_Abr.ChatGuard.Packets;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.Configs.Config;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;

@SuppressWarnings("deprecation")
public class PLPackets {

	public PLPackets(final ChatGuardPlugin pl) {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(pl, ConnectionSide.CLIENT_SIDE,
						GamePhase.PLAYING, Packets.Client.CHAT) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						String m = e.getPacket().getStrings().read(0);
						Player p = e.getPlayer();
						if (null != pl.in) {
							if (!pl.in.isLogged(p))
								return;
						}
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
							e.getPacket().getStrings()
									.write(0, pcre.getMessage());
						} else {
							PlayerChatEvent pce = new PlayerChatEvent(p, m);
							pl.ch.onChat(pce);
							if (pce.isCancelled()) {
								e.setCancelled(true);
								return;
							}
							e.getPacket().getStrings()
									.write(0, pce.getMessage());
						}
					}
				});
	}

	public static void removeListeners() {
		ProtocolLibrary.getProtocolManager().removePacketListeners(
				ChatGuardPlugin.plugin);
	}
}
