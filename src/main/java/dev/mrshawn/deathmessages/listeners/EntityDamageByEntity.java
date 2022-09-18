package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.enums.MobType;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import optic_fusion1.deathmessages.config.ConfigFile;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;

public class EntityDamageByEntity implements Listener {

    private static Map<UUID, Entity> explosions = new HashMap<>();
    private DeathMessages deathMessages;
    private ConfigFile entityDeathMessagesConfig;

    public EntityDamageByEntity(DeathMessages deathMessages, ConfigFile entityDeathMessagesConfig) {
        this.deathMessages = deathMessages;
        this.entityDeathMessagesConfig = entityDeathMessagesConfig;
    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && Bukkit.getOnlinePlayers().contains(e.getEntity())) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                if (e.getDamager() instanceof EnderCrystal && explosions.containsKey(e.getDamager().getUniqueId())) {
                    pm.setLastEntityDamager(explosions.get(e.getDamager().getUniqueId()));
                    pm.setLastExplosiveEntity(e.getDamager());
                } else if (e.getDamager() instanceof TNTPrimed) {
                    TNTPrimed tnt = (TNTPrimed) e.getDamager();
                    if (tnt.getSource() instanceof LivingEntity) {
                        pm.setLastEntityDamager(tnt.getSource());
                    }
                    pm.setLastExplosiveEntity(e.getDamager());
                } else if (e.getDamager() instanceof Firework) {
                    Firework firework = (Firework) e.getDamager();
                    try {
                        if (firework.getShooter() instanceof LivingEntity) {
                            pm.setLastEntityDamager((LivingEntity) firework.getShooter());
                        }
                        pm.setLastExplosiveEntity(e.getDamager());
                    } catch (NoSuchMethodError ignored) {
                        //McMMO ability
                    }
                } else {
                    pm.setLastEntityDamager(e.getDamager());
                    pm.setLastExplosiveEntity(e.getDamager());
                }
            } else if (e.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) e.getDamager();
                if (projectile.getShooter() instanceof LivingEntity) {
                    pm.setLastEntityDamager((LivingEntity) projectile.getShooter());
                }
                pm.setLastProjectileEntity(projectile);
            } else if (e.getDamager() instanceof FallingBlock) {
                pm.setLastEntityDamager(e.getDamager());
            } else if (e.getDamager().getType().isAlive()) {
                pm.setLastEntityDamager(e.getDamager());
            } else if (e.getDamager() instanceof EvokerFangs) {
                EvokerFangs evokerFangs = (EvokerFangs) e.getDamager();
                pm.setLastEntityDamager(evokerFangs.getOwner());
            }
        } else if (!(e.getEntity() instanceof Player) && e.getDamager() instanceof Player) {
            if (entityDeathMessagesConfig.getConfig().getConfigurationSection("Entities") == null) {
                return;
            }
            Set<String> listenedMobs = entityDeathMessagesConfig.getConfig().getConfigurationSection("Entities")
                    .getKeys(false);
            if (entityDeathMessagesConfig.getConfig().getConfigurationSection("Mythic-Mobs-Entities") != null
                    && deathMessages.isMythicMobsEnabled()) {
                listenedMobs.addAll(entityDeathMessagesConfig.getConfig().getConfigurationSection("Mythic-Mobs-Entities")
                        .getKeys(false));
            }
            if (listenedMobs.isEmpty()) {
                return;
            }
            for (String listened : listenedMobs) {
                if (listened.contains(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase())
                        || (deathMessages.isMythicMobsEnabled() && deathMessages.getMythicMobs().getAPIHelper().isMythicMob(e.getEntity().getUniqueId()))) {
                    EntityManager em;
                    if (EntityManager.getEntity(e.getEntity().getUniqueId()) == null) {
                        MobType mobType = MobType.VANILLA;
                        if (deathMessages.isMythicMobsEnabled()
                                && deathMessages.getMythicMobs().getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
                            mobType = MobType.MYTHIC_MOB;
                        }
                        em = new EntityManager(deathMessages, e.getEntity(), e.getEntity().getUniqueId(), mobType);
                    } else {
                        em = EntityManager.getEntity(e.getEntity().getUniqueId());
                    }
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                        if (e.getDamager() instanceof EnderCrystal && explosions.containsKey(e.getDamager())) {
                            if (explosions.get(e.getDamager().getUniqueId()) instanceof Player) {
                                em.setLastPlayerDamager(PlayerManager.getPlayer((Player) explosions.get(e.getDamager().getUniqueId())));
                                em.setLastExplosiveEntity(e.getDamager());
                            }
                        } else if (e.getDamager() instanceof TNTPrimed tnt) {
                            if (tnt.getSource() instanceof Player) {
                                em.setLastPlayerDamager(PlayerManager.getPlayer((Player) tnt.getSource()));
                            }
                            em.setLastExplosiveEntity(e.getDamager());
                        } else if (e.getDamager() instanceof Firework) {
                            Firework firework = (Firework) e.getDamager();
                            try {
                                if (firework.getShooter() instanceof Player) {
                                    em.setLastPlayerDamager(PlayerManager.getPlayer((Player) firework.getShooter()));
                                }
                                em.setLastExplosiveEntity(e.getDamager());
                            } catch (NoSuchMethodError ignored) {
                                //McMMO ability
                            }
                        } else {
                            em.setLastPlayerDamager(PlayerManager.getPlayer((Player) e.getDamager()));
                            em.setLastExplosiveEntity(e.getDamager());
                        }
                    } else if (e.getDamager() instanceof Projectile) {
                        Projectile projectile = (Projectile) e.getDamager();
                        if (projectile.getShooter() instanceof Player) {
                            em.setLastPlayerDamager(PlayerManager.getPlayer((Player) projectile.getShooter()));
                        }
                        em.setLastProjectileEntity(projectile);
                    } else if (e.getDamager() instanceof Player) {
                        em.setLastPlayerDamager(PlayerManager.getPlayer((Player) e.getDamager()));
                    }
                }
            }
        }

        if (e.getEntity() instanceof EnderCrystal) {
            if (e.getDamager().getType().isAlive()) {
                explosions.put(e.getEntity().getUniqueId(), e.getDamager());
            } else if (e.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) e.getDamager();
                if (projectile.getShooter() instanceof LivingEntity) {
                    explosions.put(e.getEntity().getUniqueId(), (LivingEntity) projectile.getShooter());
                }
            }

        }
    }
}
