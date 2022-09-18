package dev.mrshawn.deathmessages.api;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.files.Config;
import static github.scarsz.discordsrv.DiscordSRV.config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import optic_fusion1.deathmessages.config.ConfigFile;
import optic_fusion1.deathmessages.util.FileStore;

public class PlayerManager {

    private FileStore fileStore;
    private final UUID uuid;
    private final String name;
    private boolean messagesEnabled;
    private boolean isBlacklisted;
    private DamageCause damageCause;
    private Entity lastEntityDamager;
    private Entity lastExplosiveEntity;
    private Projectile lastProjectileEntity;
    private Material climbing;
    private Location explosionCauser;
    private Location location;
    private int cooldown = 0;
    private BukkitTask cooldownTask;
    private Inventory cachedInventory;

    private BukkitTask lastEntityTask;

    private static final List<PlayerManager> players = new ArrayList<>();
    private boolean saveUserData;
    private ConfigFile userDataConfigFile;
    
//    public boolean saveUserData = fileStore.getConfig().getInt(Config.SAVED_USER_DATA);
    private DeathMessages deathMessages;

    public PlayerManager(DeathMessages deathMessages, Player p) {
        fileStore = deathMessages.getFileStore();
        saveUserData = fileStore.getConfig().getBoolean(Config.SAVED_USER_DATA);
        userDataConfigFile = deathMessages.getConfigManager().getUserDataConfig();
        this.uuid = p.getUniqueId();
        this.name = p.getName();

        if (saveUserData && !userDataConfigFile.getConfig().contains(p.getUniqueId().toString())) {
            userDataConfigFile.getConfig().set(p.getUniqueId() + ".username", p.getName());
            userDataConfigFile.getConfig().set(p.getUniqueId() + ".messages-enabled", true);
            userDataConfigFile.getConfig().set(p.getUniqueId() + ".is-blacklisted", false);
            userDataConfigFile.save();
        }
        if (saveUserData) {
            messagesEnabled = userDataConfigFile.getConfig().getBoolean(p.getUniqueId() + ".messages-enabled");
            isBlacklisted = userDataConfigFile.getConfig().getBoolean(p.getUniqueId() + ".is-blacklisted");
        } else {
            messagesEnabled = true;
            isBlacklisted = false;
        }
        this.damageCause = DamageCause.CUSTOM;
        players.add(this);
    }

    public Player getPlayer() {
        return Bukkit.getServer().getPlayer(uuid);
    }

    public UUID getUUID() {
        return Objects.requireNonNull(uuid);
    }

    public String getName() {
        return Objects.requireNonNull(name);
    }

    public boolean getMessagesEnabled() {
        return messagesEnabled;
    }

    public void setMessagesEnabled(boolean b) {
        this.messagesEnabled = b;
        if (saveUserData) {
            userDataConfigFile.getConfig().set(uuid.toString() + ".messages-enabled", b);
            userDataConfigFile.save();
        }
    }

    public boolean isBlacklisted() {
        return isBlacklisted;
    }

    public void setBlacklisted(boolean b) {
        this.isBlacklisted = b;
        if (saveUserData) {
            userDataConfigFile.getConfig().set(uuid.toString() + ".is-blacklisted", b);
            userDataConfigFile.save();
        }
    }

    public void setLastDamageCause(DamageCause dc) {
        this.damageCause = dc;
    }

    public DamageCause getLastDamage() {
        return damageCause;
    }

    public void setLastEntityDamager(Entity e) {
        setLastExplosiveEntity(null);
        setLastProjectileEntity(null);
        this.lastEntityDamager = e;
        if (e == null) {
            return;
        }
        if (lastEntityTask != null) {
            lastEntityTask.cancel();
        }
        lastEntityTask = new BukkitRunnable() {
            @Override
            public void run() {
                setLastEntityDamager(null);
            }
        }.runTaskLater(deathMessages, fileStore.getConfig().getInt(Config.EXPIRE_LAST_DAMAGE_EXPIRE_PLAYER) * 20L);
    }

    public Entity getLastEntityDamager() {
        return lastEntityDamager;
    }

    public void setLastExplosiveEntity(Entity e) {
        this.lastExplosiveEntity = e;
    }

    public Entity getLastExplosiveEntity() {
        return lastExplosiveEntity;
    }

    public Projectile getLastProjectileEntity() {
        return lastProjectileEntity;
    }

    public void setLastProjectileEntity(Projectile lastProjectileEntity) {
        this.lastProjectileEntity = lastProjectileEntity;
    }

    public Material getLastClimbing() {
        return climbing;
    }

    public void setLastClimbing(Material climbing) {
        this.climbing = climbing;
    }

    public void setExplosionCauser(Location location) {
        this.explosionCauser = location;
    }

    public Location getExplosionCauser() {
        return explosionCauser;
    }

    public Location getLastLocation() {
        return getPlayer().getLocation();
    }

    public boolean isInCooldown() {
        return cooldown > 0;
    }

    public void setCooldown() {
        cooldown = fileStore.getConfig().getInt(Config.COOLDOWN);
        cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldown <= 0) {
                    this.cancel();
                }
                cooldown--;
            }
        }.runTaskTimer(deathMessages, 0, 20);
    }

    public void setCachedInventory(Inventory inventory) {
        cachedInventory = inventory;
    }

    public Inventory getCachedInventory() {
        return cachedInventory;
    }

    public static PlayerManager getPlayer(Player p) {
        for (PlayerManager pm : players) {
            if (pm.getUUID().equals(p.getUniqueId())) {
                return pm;
            }
        }
        return null;
    }

    public static PlayerManager getPlayer(UUID uuid) {
        for (PlayerManager pm : players) {
            if (pm.getUUID().equals(uuid)) {
                return pm;
            }
        }
        return null;
    }

    public void removePlayer() {
        players.remove(this);
    }
}
