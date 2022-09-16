package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.Assets;
import static github.scarsz.discordsrv.DiscordSRV.config;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import me.joshb.discordbotapi.bungee.config.Messages;
import optic_fusion1.deathmessages.util.FileStore;

public class PluginMessaging implements PluginMessageListener {

    private FileStore fileStore;
    private DeathMessages deathMessages;

    public PluginMessaging(DeathMessages deathMessages) {
        fileStore = deathMessages.getFileStore();
        this.deathMessages = deathMessages;
    }

    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] messageBytes) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(messageBytes));
        try {
            String subChannel = in.readUTF();

            if (subChannel.equals("GetServer")) {
                String serverName = in.readUTF();
                deathMessages.getLogger().info("Server-Name successfully initialized from Bungee! (" + serverName + ")");
                deathMessages.set
//                deathMessages.setBungeeServerName(serverName);
                fileStore.getConfig().set(Config.HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME, Config.HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME, serverName);
                fileStore.getConfig().save();
                deathMessages.setBungeeServerNameRequest(false);
            } else if (subChannel.equals("DeathMessages")) {
                String[] data = in.readUTF().split("######");
                String serverName = data[0];
                String rawMsg = data[1];
                TextComponent prefix = new TextComponent(Assets.colorize(Messages.getInstance().getConfig().getString("Bungee.Message").replaceAll("%server_name%", serverName)));
                TextComponent message = new TextComponent(ComponentSerializer.parse(rawMsg));
                for (Player pls : Bukkit.getOnlinePlayers()) {
                    PlayerManager pms = PlayerManager.getPlayer(pls);
                    if (pms.getMessagesEnabled()) {
                        pls.spigot().sendMessage(prefix, message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
