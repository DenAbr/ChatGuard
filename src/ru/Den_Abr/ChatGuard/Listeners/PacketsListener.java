package ru.Den_Abr.ChatGuard.Listeners;

import java.lang.reflect.InvocationTargetException;

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

            if (!message.startsWith("/")) {
                if (PlayerListener.getPMCommand(message) != null) {
                    message = PlayerListener.substitute(message);
                    packet.getStrings().write(0, message);
                    e.setPacket(packet);
                }
            } else {
                message = FallbackCommandsListener.getCommandWithoutFallback(message);
            }

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
                cp.getAllMessages().add(ChatColor.stripColor(info.getClearMessage().toLowerCase()));
            packet.getStrings().write(0, info.getClearMessage());
            e.setPacket(packet);
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
