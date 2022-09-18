package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.enums.MobType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;
import optic_fusion1.deathmessages.config.ConfigFile;

public class EntityDamage implements Listener {

    private ConfigFile entityDeathMessagesConfig;
    private DeathMessages deathMessages;

    public EntityDamage(DeathMessages deathMessages) {
        this.entityDeathMessagesConfig = deathMessages.getConfigManager().getEntityDeathMessagesConfig();
        this.deathMessages = deathMessages;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getEntity() instanceof Player && Bukkit.getOnlinePlayers().contains(e.getEntity())) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            pm.setLastDamageCause(e.getCause());
            // for fall large if ppl want it float dist = e.getEntity().getFallDistance();
        } else if (!(e.getEntity() instanceof Player)) {
            if (entityDeathMessagesConfig.getConfig().getConfigurationSection("Entities") == null) {
                return;
            }
            Set<String> listenedMobs = entityDeathMessagesConfig.getConfig().getConfigurationSection("Entities").getKeys(false);
            if (entityDeathMessagesConfig.getConfig().getConfigurationSection("Mythic-Mobs-Entities") != null && deathMessages.isMythicMobsEnabled()) {
                listenedMobs.addAll(entityDeathMessagesConfig.getConfig().getConfigurationSection("Mythic-Mobs-Entities").getKeys(false));
            }
            if (listenedMobs.isEmpty()) {
                return;
            }
            for (String listened : listenedMobs) {
                if (listened.contains(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase())) {
                    EntityManager em;
                    if (EntityManager.getEntity(e.getEntity().getUniqueId()) == null) {
                        MobType mobType = MobType.VANILLA;
                        if (deathMessages.isMythicMobsEnabled() && deathMessages.getMythicMobs().getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
                            mobType = MobType.MYTHIC_MOB;
                        }
                        em = new EntityManager(deathMessages, e.getEntity(), e.getEntity().getUniqueId(), mobType);
                    } else {
                        em = EntityManager.getEntity(e.getEntity().getUniqueId());
                    }
                    em.setLastDamageCause(e.getCause());
                }
            }
        }
    }

}
