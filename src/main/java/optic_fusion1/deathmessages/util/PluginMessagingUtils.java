package optic_fusion1.deathmessages.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import java.util.List;
import org.bukkit.entity.Player;

public final class PluginMessagingUtils {

    private PluginMessagingUtils() {
    }

    public static void sendServerNameRequest(DeathMessages deathMessages, FileStore fileStore, Player p) {
        FileSettings<Config> config = fileStore.getConfig();
        if (!config.getBoolean(Config.HOOKS_BUNGEE_ENABLED)) {
            return;
        }
        deathMessages.getLogger().info("Attempting to initialize server-name variable from Bungee...");
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        p.sendPluginMessage(deathMessages, "BungeeCord", out.toByteArray());
    }

    public static void sendMessage(DeathMessages deathMessages, FileStore fileStore, Player p, String msg) {
        FileSettings<Config> config = fileStore.getConfig();
        if (!config.getBoolean(Config.HOOKS_BUNGEE_ENABLED)) {
            return;
        }
        if (config.getBoolean(Config.HOOKS_BUNGEE_SERVER_GROUPS_ENABLED)) {
            List<String> serverList = config.getStringList(Config.HOOKS_BUNGEE_SERVER_GROUPS_SERVERS);
            for (String server : serverList) {
                PluginMessagingUtils.sendMessage(deathMessages, server, msg, ByteStreams.newDataOutput(), p);
            }
        } else {
            PluginMessagingUtils.sendMessage(deathMessages, "ONLINE", msg, ByteStreams.newDataOutput(), p);
        }
    }

    private static void sendMessage(DeathMessages deathMessages, String s, String msg, ByteArrayDataOutput out, Player player) {
        out.writeUTF("Forward");
        out.writeUTF(s);
        out.writeUTF("DeathMessages");
        out.writeUTF(deathMessages.getBungeeServerName() + "######" + msg);
        player.sendPluginMessage(deathMessages, "BungeeCord", out.toByteArray());
    }

}
