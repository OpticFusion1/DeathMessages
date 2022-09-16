package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.files.Config;
import optic_fusion1.deathmessages.util.FileStore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    private FileStore fileStore;

    public PlayerDeath(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (fileStore.getConfig().getBoolean(Config.DISABLE_DEFAULT_MESSAGES)) {
            e.setDeathMessage(null);
        }
    }
}
