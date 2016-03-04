package ru.Den_Abr.ChatGuard.Listeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.MessageInfo;
import ru.Den_Abr.ChatGuard.Integration.AbstractIntegration;
import ru.Den_Abr.ChatGuard.Player.CGPlayer;
import ru.Den_Abr.ChatGuard.Utils.MessagePair;

public class PacketsListener {

	public static void stopListening() {
		if (null != ChatPacketListner.instance)
			ProtocolLibrary.getProtocolManager().removePacketListener(ChatPacketListner.instance);
	}

	public static void startListening() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new ChatPacketListner(ChatGuardPlugin.getInstance(),
				PacketType.Play.Client.CHAT, PacketType.Play.Server.CHAT));
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
			CGPlayer cp = CGPlayer.get(e.getPlayer());
			if (!message.startsWith("/")) {
				info = PlayerListener.handleMessage(message, cp);
			} else {
				info = PlayerListener.handleCommand(message, cp);
			}
			if (info == null)
				return;
			if (info.isCancelled())
				e.setCancelled(true);
			else
				cp.getAllMessages().add(ChatColor.stripColor(String.valueOf(info.getClearMessage()).toLowerCase()));
			packet.getStrings().write(0, info.getClearMessage());
			e.setPacket(packet);
		}

		@Override
		public void onPacketSending(PacketEvent e) {
			if (AbstractIntegration.shouldSkip(e.getPlayer()) || e.getPlayer().hasMetadata("clearing"))
				return;
			CGPlayer cp = CGPlayer.get(e.getPlayer());
			PacketContainer pc = e.getPacket();

			Object pos = pc.getBytes().readSafely(0);
			String text = null;
			if (pos != null) {
				if ((Byte) pos == 2)
					return;
				WrappedChatComponent wcc = pc.getChatComponents().readSafely(0);
				if (wcc != null) {
					Object handle = wcc.getHandle();
					try {
						Method m = handle.getClass().getMethod("c", new Class[0]);
						m.setAccessible(true);
						text = String.valueOf(m.invoke(handle, new Object[0]));
						cp.getSentMessages().add(new MessagePair(text, wcc.getJson(), false));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e1) {
						e1.printStackTrace();
					}
				}
			} else { // old version
				text = pc.getStrings().read(0);
				cp.getSentMessages().add(new MessagePair(text, text, true));
			}

		}
	}

	public static void sendComponent(Player p, WrappedChatComponent wcc) {
		PacketContainer pc = new PacketContainer(PacketType.Play.Server.CHAT);
		pc.getBytes().writeDefaults();
		pc.getChatComponents().write(0, wcc);
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, pc);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
