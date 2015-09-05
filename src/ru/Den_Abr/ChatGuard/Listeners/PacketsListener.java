package ru.Den_Abr.ChatGuard.Listeners;

import java.util.Arrays;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import ru.Den_Abr.ChatGuard.ChatGuardPlugin;
import ru.Den_Abr.ChatGuard.MessageInfo;
import ru.Den_Abr.ChatGuard.ChatFilters.AbstractFilter;
import ru.Den_Abr.ChatGuard.Configuration.Messages.Message;
import ru.Den_Abr.ChatGuard.Configuration.Settings;
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
			CGPlayer player = CGPlayer.get(e.getPlayer());
			String message = packet.getStrings().read(0);
			String comand = "";
			if (!message.startsWith("/")) {
				if (PlayerListener.globalMute && !player.hasPermission("chatguard.ignore.globalmute")) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(Message.GLOBAL_MUTE.get());
					return;
				}

				if (!player.hasPermission("chatguard.ignore.cooldown")) {
					int cdtime = PlayerListener.isCooldownOver(player);
					ChatGuardPlugin.debug(1, player.getName() + " CD " + cdtime);
					if (cdtime > 0) {
						e.setCancelled(true);
						e.getPlayer()
								.sendMessage(Message.WAIT_COOLDOWN.get().replace("{TIME}", cdtime + Message.SEC.get()));
						return;
					}
				}
			} else {
				if (Settings.getCheckCommands().isEmpty())
					return;
				comand = message.split(" ")[0].toLowerCase();
				ChatGuardPlugin.debug(2, comand);
				ChatGuardPlugin.debug(2, Settings.getCheckCommands());
				if (!Settings.getCheckCommands().containsKey(comand))
					return;
				String[] words = message.split(" ");
				int offset = Settings.getCheckCommands().get(comand) + 1;
				String skipped = "";
				if(offset > 1) {
					skipped = String.join(" ", Arrays.copyOfRange(words, 1, offset)) + " ";
				}
				message = String.join(" ", Arrays.copyOfRange(words, offset, words.length));
				ChatGuardPlugin.debug(2, message);
				ChatGuardPlugin.debug(2, skipped);

				comand += " " + skipped;
			}
			if (message.isEmpty())
				return;
			MessageInfo info = AbstractFilter.handleMessage(message, player);

			packet.getStrings().write(0, comand + info.getClearMessage());
			e.setPacket(packet);

			if (!info.getViolations().isEmpty()) {
				if (Settings.isCancellingEnabled()) {
					e.setCancelled(true);
				} else {
					player.setLastMessageTime(System.currentTimeMillis());
					player.getLastMessages().add(message);
				}
				return;
			}
			player.setLastMessageTime(System.currentTimeMillis());
			player.getLastMessages().add(message);
		}

	}
}
