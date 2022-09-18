package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.Assets;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import java.util.ArrayList;
import java.util.List;
import optic_fusion1.deathmessages.config.ConfigFile;
import optic_fusion1.deathmessages.util.FileStore;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

public class EntityDeath implements Listener {

    private DeathMessages deathMessages;
    private FileStore fileStore;
    private ConfigFile gangConfig;
    private ConfigFile settingsConfig;

    public EntityDeath(DeathMessages deathMessages) {
        this.deathMessages = deathMessages;
        fileStore = deathMessages.getFileStore();
        gangConfig = deathMessages.getConfigManager().getGangsConfig();
        settingsConfig = deathMessages.getConfigManager().getSettingsConfig();
    }

    synchronized void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player && Bukkit.getOnlinePlayers().contains(e.getEntity())) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            if (pm == null) {
                pm = new PlayerManager(deathMessages, p);
            }

            if (e.getEntity().getLastDamageCause() == null) {
                pm.setLastDamageCause(EntityDamageEvent.DamageCause.CUSTOM);
            } else {
                pm.setLastDamageCause(e.getEntity().getLastDamageCause().getCause());
            }
            if (pm.isBlacklisted()) {
                return;
            }

            if (!(pm.getLastEntityDamager() instanceof LivingEntity) || pm.getLastEntityDamager() == e.getEntity()) {
                //Natural Death
                if (pm.getLastExplosiveEntity() instanceof EnderCrystal) {
                    callEvent(Assets.getNaturalDeath(pm, "End-Crystal"), p);
                } else if (pm.getLastExplosiveEntity() instanceof TNTPrimed) {
                    callEvent(Assets.getNaturalDeath(pm, "TNT"), p);
                } else if (pm.getLastExplosiveEntity() instanceof Firework) {
                    callEvent(Assets.getNaturalDeath(pm, "Firework"), p);
                } else if (pm.getLastClimbing() != null && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.FALL)) {
                    callEvent(Assets.getNaturalDeath(pm, "Climbable"), p);
                } else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                    ExplosionManager explosionManager = ExplosionManager.getManagerIfEffected(p.getUniqueId());
                    if (explosionManager == null) {
                        return;
                    }
                    TextComponent tx = explosionManager.getMaterial().name().contains("BED") ? Assets.getNaturalDeath(pm, "Bed")
                            : explosionManager.getMaterial() == Material.RESPAWN_ANCHOR ? Assets.getNaturalDeath(pm, "Respawn-Anchor") : null;
                    if (tx == null) {
                        return;
                    }
                    callEvent(tx, p);
                } else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                    callEvent(Assets.getNaturalDeath(pm, Assets.getSimpleProjectile(pm.getLastProjectileEntity())), p);
                } else {
                    callEvent(Assets.getNaturalDeath(pm, Assets.getSimpleCause(pm.getLastDamage())), p);
                }
            } else {
                //Killed by mob
                Entity ent = pm.getLastEntityDamager();
                String mobName = ent.getType().getEntityClass().getSimpleName().toLowerCase();
                int radius = gangConfig.getConfig().getInt("Gang.Mobs." + mobName + ".Radius");
                int amount = gangConfig.getConfig().getInt("Gang.Mobs." + mobName + ".Amount");

                boolean gangKill = false;

                if (gangConfig.getConfig().getBoolean("Gang.Enabled")) {
                    int totalMobEntities = 0;
                    for (Entity entities : p.getNearbyEntities(radius, radius, radius)) {
                        if (entities.getType().equals(ent.getType())) {
                            totalMobEntities++;
                        }
                    }
                    if (totalMobEntities >= amount) {
                        gangKill = true;
                    }
                }
                TextComponent tx = Assets.playerDeathMessage(pm, gangKill);
                if (tx == null) {
                    return;
                }
                if (ent instanceof Player) {
                    callEvent(tx, p, (LivingEntity) pm.getLastEntityDamager(), MessageType.PLAYER, gangKill);
                    return;
                }
                callEvent(tx, p, (LivingEntity) pm.getLastEntityDamager(), MessageType.PLAYER, gangKill);
            }
        } else {
            //Player killing mob
            MobType mobType = MobType.VANILLA;
            if (deathMessages.isMythicMobsEnabled()) {
                if (deathMessages.getMythicMobs().getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
                    mobType = MobType.MYTHIC_MOB;
                }
            }
            if (EntityManager.getEntity(e.getEntity().getUniqueId()) == null) {
                return;
            }
            EntityManager em = EntityManager.getEntity(e.getEntity().getUniqueId());

            if (em == null || em.getLastPlayerDamager() == null) {
                return;
            }

            PlayerManager damager = em.getLastPlayerDamager();

            TextComponent tx = Assets.entityDeathMessage(em, mobType);
            if (tx == null) {
                return;
            }
            BroadcastEntityDeathMessageEvent event = new BroadcastEntityDeathMessageEvent(damager, e.getEntity(), MessageType.ENTITY, tx, getWorlds(e.getEntity()));
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public List<World> getWorlds(Entity e) {
        List<World> broadcastWorlds = new ArrayList<>();
        if (fileStore.getConfig().getStringList(Config.DISABLED_WORLDS).contains(e.getWorld().getName())) {
            return broadcastWorlds;
        }
        if (fileStore.getConfig().getBoolean(Config.PER_WORLD_MESSAGES)) {
            // TODO: Add support for Map in FileSettings
            for (String groups : settingsConfig.getConfig().getConfigurationSection("World-Groups").getKeys(false)) {
                List<String> worlds = settingsConfig.getConfig().getStringList("World-Groups." + groups);
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath_LOWEST(EntityDeathEvent e) {
        if (deathMessages.getEventPriority() == EventPriority.LOWEST) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath_LOW(EntityDeathEvent e) {
        if (deathMessages.getEventPriority() == EventPriority.LOW) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath_NORMAL(EntityDeathEvent e) {
        if (deathMessages.getEventPriority() == EventPriority.NORMAL) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath_HIGH(EntityDeathEvent e) {
        if (deathMessages.getEventPriority() == EventPriority.HIGH) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath_HIGHEST(EntityDeathEvent e) {
        if (deathMessages.getEventPriority() == EventPriority.HIGHEST) {
            onEntityDeath(e);
        }
    }

    private void callEvent(TextComponent tx, Player p) {
        if (tx == null) {
            return;
        }
        BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, null, MessageType.NATURAL, tx, getWorlds(p), false);
        Bukkit.getPluginManager().callEvent(event);
    }

    private void callEvent(TextComponent tx, Player p, LivingEntity damager, MessageType messageType, boolean gangKill) {
        if (tx == null) {
            return;
        }
        BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, damager, messageType, tx, getWorlds(p), gangKill);
        Bukkit.getPluginManager().callEvent(event);
    }

}
