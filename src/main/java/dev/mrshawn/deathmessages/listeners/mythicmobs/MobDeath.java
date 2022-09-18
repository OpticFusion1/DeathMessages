package dev.mrshawn.deathmessages.listeners.mythicmobs;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.Assets;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.ArrayList;
import java.util.List;
import optic_fusion1.deathmessages.config.ConfigFile;
import optic_fusion1.deathmessages.util.FileStore;

public class MobDeath implements Listener {

    private ConfigFile settingsConfigFile;
    private FileStore fileStore;
    private ConfigFile entityDeathMessagesConfigFile;

    public MobDeath(DeathMessages deathMessages) {
        fileStore = deathMessages.getFileStore();
        settingsConfigFile = deathMessages.getConfigManager().getSettingsConfig();
        entityDeathMessagesConfigFile = deathMessages.getConfigManager().getEntityDeathMessagesConfig();
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        if (entityDeathMessagesConfigFile.getConfig().getConfigurationSection("Mythic-Mobs-Entities").getKeys(false).isEmpty()) {
            return;
        }
        for (String customMobs : entityDeathMessagesConfigFile.getConfig().getConfigurationSection("Mythic-Mobs-Entities").getKeys(false)) {
            if (e.getMob().getType().getInternalName().equals(customMobs)) {
                EntityManager em = EntityManager.getEntity(e.getEntity().getUniqueId());

                if (em == null || em.getLastPlayerDamager() == null) {
                    return;
                }

                PlayerManager damager = em.getLastPlayerDamager();
                TextComponent tx = Assets.entityDeathMessage(em, MobType.MYTHIC_MOB);
                if (tx == null) {
                    return;
                }
                BroadcastEntityDeathMessageEvent event = new BroadcastEntityDeathMessageEvent(damager, e.getEntity(), MessageType.ENTITY, tx, getWorlds(e.getEntity()));
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }

    // TODO: Move this to Utils
    public List<World> getWorlds(Entity e) {
        List<World> broadcastWorlds = new ArrayList<>();
        if (fileStore.getConfig().getStringList(Config.DISABLED_WORLDS).contains(e.getWorld().getName())) {
            return broadcastWorlds;
        }
        if (fileStore.getConfig().getBoolean(Config.PER_WORLD_MESSAGES)) {
            for (String groups : settingsConfigFile.getConfig().getConfigurationSection("World-Groups").getKeys(false)) {
                List<String> worlds = settingsConfigFile.getConfig().getStringList("World-Groups." + groups);
                if (worlds.contains(e.getWorld().getName())) {
                    for (String single : worlds) {
                        broadcastWorlds.add(Bukkit.getWorld(single));
                    }
                }
            }
            if (broadcastWorlds.isEmpty()) {
                broadcastWorlds.add(e.getWorld());
            }
        } else {
            return Bukkit.getWorlds();
        }
        return broadcastWorlds;
    }

}
