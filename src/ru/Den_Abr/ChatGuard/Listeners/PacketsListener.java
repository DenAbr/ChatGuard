package ru.Den_Abr.ChatGuard.Listeners;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.MessageInfo;
import ru.Den_Abr.ChatGuard.Integration.AbstractIntegration;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class PacketsListener {

	public static void stopListening() {
		if (null != ChatPacketListner.instance)
			ProtocolLibrary.getProtocolManager().removePacketListener(ChatPacketListner.instance);
	}

	public static void startListening() {
		ProtocolLibrary.getProtocolManager()
				.addPacketListener(new ChatPacketListner(ChatGuardPlugin.getInstance(), PacketType.Play.Client.CHAT));
	}

	static class ChatPacketListner extends PacketAdapter {
		static ChatPacketListner instance;

		public ChatPacketListner(Plugin plugin, PacketType... types) {
			super(plugin, types);
			instance = this;
		}

		@Override
		public void onPacketReceiving(PacketEvent e) {
			if (AbstractIntegration.shouldSkip(e.getPlayer()))
				return;
			PacketContainer packet = e.getPacket();
			String message = packet.getStrings().read(0);
			MessageInfo info = null;
			if (!message.startsWith("/")) {
				info = PlayerListener.handleMessage(message, CGPlayer.get(e.getPlayer()));
			} else {
				info = PlayerListener.handleCommand(message, CGPlayer.get(e.getPlayer()));
			}
			if (info == null)
				return;
			if (info.isCancelled())
				e.setCancelled(true);
			packet.getStrings().write(0, info.getClearMessage());
			e.setPacket(packet);
		}

	}
}
