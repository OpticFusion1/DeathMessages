package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import optic_fusion1.deathmessages.util.PluginMessagingUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class OnJoin implements Listener {

    private DeathMessages deathMessages;

    public OnJoin(DeathMessages deathMessages) {
        this.deathMessages = deathMessages;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (PlayerManager.getPlayer(p) == null) {
                    new PlayerManager(p);
                }
            }
        }.runTaskAsynchronously(deathMessages);

        if (!deathMessages.isBungeeInit()) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (deathMessages.isBungeeServerNameRequest()) {
                    PluginMessagingUtils.sendServerNameRequest(deathMessages, deathMessages.getFileStore(), p);
                }
            }
        }.runTaskLater(deathMessages, 5);
    }
}
