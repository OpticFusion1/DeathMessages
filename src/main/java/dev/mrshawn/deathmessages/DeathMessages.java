package dev.mrshawn.deathmessages;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.command.deathmessages.CommandManager;
import dev.mrshawn.deathmessages.command.deathmessages.TabCompleter;
import dev.mrshawn.deathmessages.command.deathmessages.alias.CommandDeathMessagesToggle;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.hooks.DiscordAssets;
import dev.mrshawn.deathmessages.hooks.DiscordBotAPIExtension;
import dev.mrshawn.deathmessages.hooks.DiscordSRVExtension;
import dev.mrshawn.deathmessages.hooks.PlaceholderAPIExtension;
import dev.mrshawn.deathmessages.listeners.EntityDamage;
import dev.mrshawn.deathmessages.listeners.EntityDamageByBlock;
import dev.mrshawn.deathmessages.listeners.EntityDamageByEntity;
import dev.mrshawn.deathmessages.listeners.EntityDeath;
import dev.mrshawn.deathmessages.listeners.InteractEvent;
import dev.mrshawn.deathmessages.listeners.OnChatListener;
import dev.mrshawn.deathmessages.listeners.OnJoin;
import dev.mrshawn.deathmessages.listeners.OnMove;
import dev.mrshawn.deathmessages.listeners.PlayerDeath;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.listeners.customlisteners.BlockExplosion;
import dev.mrshawn.deathmessages.listeners.customlisteners.BroadcastEntityDeathListener;
import dev.mrshawn.deathmessages.listeners.customlisteners.BroadcastPlayerDeathListener;
import dev.mrshawn.deathmessages.listeners.mythicmobs.MobDeath;
import dev.mrshawn.deathmessages.worldguard.WorldGuard7Extension;
import dev.mrshawn.deathmessages.worldguard.WorldGuardExtension;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import optic_fusion1.deathmessages.config.ConfigManager;
import optic_fusion1.deathmessages.util.FileStore;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class DeathMessages extends JavaPlugin {

    private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    private static DeathMessages instance;

    public boolean placeholderAPIEnabled = false;
    public boolean combatLogXAPIEnabled = false;

    public MythicBukkit mythicMobs = null;
    public boolean mythicmobsEnabled = false;

    public String bungeeServerName;
    public boolean bungeeServerNameRequest = true;
    public boolean bungeeInit = false;

    public WorldGuardExtension worldGuardExtension;
    public boolean worldGuardEnabled;

    public DiscordBotAPIExtension discordBotAPIExtension;
    public DiscordSRVExtension discordSRVExtension;

    private EventPriority eventPriority = EventPriority.HIGH;

    private FileStore fileStore;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        initializeListeners();
        initializeCommands();
        initializeHooks();
        initializeOnlinePlayers();
        checkGameRules();
    }

    @Override
    public void onLoad() {
        instance = this;
        initializeConfigs();
        initializeHooksOnLoad();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void initializeConfigs() {
        configManager = new ConfigManager(this);
        configManager.initialize();
        fileStore = new FileStore(this);
//        DeathMessages.eventPriority = EventPriority.valueOf(
//                config.getString(Config.DEATH_LISTENER_PRIORITY).toUpperCase()
//        );
    }

    private void initializeListeners() {
        registerListeners(new BroadcastPlayerDeathListener(), new BroadcastEntityDeathListener(configManager.getMessagesConfig(), fileStore),
                new BlockExplosion(), new EntityDamage(), new EntityDamageByBlock(), new EntityDamageByEntity(),
                new EntityDeath(this), new InteractEvent(), new OnJoin(this), new OnMove(), new PlayerDeath(fileStore),
                new OnChatListener(configManager.getPlayerDeathMessagesConfig(), configManager.getEntityDeathMessagesConfig()));
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            PLUGIN_MANAGER.registerEvents(listener, this);
        }
    }

    private void initializeCommands() {
        CommandManager cm = new CommandManager();
        cm.initializeSubCommands();
        getCommand("deathmessages").setExecutor(cm);
        getCommand("deathmessages").setTabCompleter(new TabCompleter());
        getCommand("deathmessagestoggle").setExecutor(new CommandDeathMessagesToggle());
    }

    private void initializeHooks() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIExtension(this).register();
            placeholderAPIEnabled = true;
            getLogger().info("PlaceholderAPI Hook Enabled!");
        }

        if (worldGuardEnabled) {
            getLogger().info("WorldGuard Hook Enabled!");
        }

        if (Bukkit.getPluginManager().getPlugin("DiscordBotAPI") != null && fileStore.getConfig().getBoolean(Config.HOOKS_DISCORD_ENABLED)) {
            discordBotAPIExtension = new DiscordBotAPIExtension(fileStore, new DiscordAssets(fileStore), this);
            getLogger().info("DiscordBotAPI Hook Enabled!");
        }

        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null && fileStore.getConfig().getBoolean(Config.HOOKS_DISCORD_ENABLED)) {
            discordSRVExtension = new DiscordSRVExtension(fileStore, new DiscordAssets(fileStore), this);
            getLogger().info("DiscordSRV Hook Enabled!");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlugMan") && worldGuardExtension != null) {
            Plugin plugMan = Bukkit.getPluginManager().getPlugin("PlugMan");
            getLogger().info("PlugMan found. Adding this plugin to its ignored plugins list due to WorldGuard hook being enabled!");
            try {
                List<String> ignoredPlugins = (List<String>) plugMan.getClass().getMethod("getIgnoredPlugins").invoke(plugMan);
                if (!ignoredPlugins.contains("DeathMessages")) {
                    ignoredPlugins.add("DeathMessages");
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
                getLogger().log(Level.SEVERE, "Error adding plugin to ignored plugins list: {0}", exception.getMessage());
            }
        }

//
//        if (Bukkit.getPluginManager().getPlugin("CombatLogX") != null) {
//            combatLogXAPIEnabled = true;
//            Bukkit.getPluginManager().registerEvents(new PlayerUntag(), this);
//            getLogger().info("CombatLogX Hook Enabled!");
//        }
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null && fileStore.getConfig().getBoolean(Config.HOOKS_MYTHICMOBS_ENABLED)) {
            mythicMobs = MythicBukkit.inst();
            mythicmobsEnabled = true;
            getLogger().info("MythicMobs Hook Enabled!");
            Bukkit.getPluginManager().registerEvents(new MobDeath(), this);
        }

        if (fileStore.getConfig().getBoolean(Config.HOOKS_BUNGEE_ENABLED)) {
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessaging(this));
            getLogger().info("Bungee Hook enabled!");
            if (fileStore.getConfig().getBoolean(Config.HOOKS_BUNGEE_SERVER_NAME_GET_FROM_BUNGEE)) {
                bungeeInit = true;
            } else {
                bungeeInit = false;
                bungeeServerName = fileStore.getConfig().getString(Config.HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME);
            }
        }
    }

    private void initializeHooksOnLoad() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null && fileStore.getConfig().getBoolean(Config.HOOKS_WORLDGUARD_ENABLED)) {
            try {
                final WorldGuardPlugin worldGuardPlugin = WorldGuardPlugin.inst();
                if (worldGuardPlugin == null) {
                    throw new Exception();
                }
                final String version = worldGuardPlugin.getDescription().getVersion();
                if (version.startsWith("7")) {
                    worldGuardExtension = new WorldGuard7Extension();
                    worldGuardExtension.registerFlags();
                } else if (version.startsWith("6")) {
                    //worldGuardExtension = new WorldGuard6Extension();
                    worldGuardExtension.registerFlags();
                } else {
                    throw new Exception();
                }
                worldGuardEnabled = true;
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Error loading WorldGuardHook. Error: {0}", e.getMessage());
                worldGuardEnabled = false;
            }
        }
    }

    private void initializeOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(PlayerManager::new);
    }

    private void checkGameRules() {
        if (fileStore.getConfig().getBoolean(Config.DISABLE_DEFAULT_MESSAGES)) {
            for (World world : Bukkit.getWorlds()) {
                if (world.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES).equals(true)) {
                    world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
                }
            }
        }
    }

    public EventPriority getEventPriority() {
        return eventPriority;
    }

    public DeathMessages getInstance() {
        return instance;
    }

    public void setBungeeServerNameRequest(boolean bungeeServerNameRequest) {
        this.bungeeServerNameRequest = bungeeServerNameRequest;
    }

    public String getBungeeServerName() {
        return bungeeServerName;
    }

    public FileStore getFileStore() {
        return fileStore;
    }

    public boolean isBungeeServerNameRequest() {
        return bungeeServerNameRequest;
    }

    public boolean isBungeeInit() {
        return bungeeInit;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

}
