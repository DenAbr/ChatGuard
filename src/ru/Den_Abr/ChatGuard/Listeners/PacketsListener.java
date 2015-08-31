package ru.Den_Abr.ChatGuard.Listeners;

import org.bukkit.plugin.Plugin;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class PacketsListener extends PacketAdapter {
	private static PacketsListener instance;

	public PacketsListener(Plugin plugin, PacketType... types) {
		super(plugin, types);
		instance = this;
	}

	@Override
	public void onPacketReceiving(PacketEvent arg0) {

	}

	public static void stopListening() {
		if (null != instance)
			ProtocolLibrary.getProtocolManager().removePacketListener(instance);
	}

	public static void startListening() {
		ProtocolLibrary.getProtocolManager()
				.addPacketListener(new PacketsListener(ChatGuardPlugin.getInstance(), PacketType.Play.Client.CHAT));
	}
}
