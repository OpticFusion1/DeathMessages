package dev.mrshawn.deathMessages.listeners.customlisteners;

import com.sk89q.worldguard.protection.flags.StateFlag;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.Assets;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.List;
import java.util.regex.Matcher;
import optic_fusion1.deathmessages.config.ConfigFile;
import optic_fusion1.deathmessages.util.FileStore;
import optic_fusion1.deathmessages.util.PluginMessagingUtils;

public class BroadcastPlayerDeathListener implements Listener {

    private FileStore fileStore;
    private boolean discordSent;
    private DeathMessages deathMessages;
    private ConfigFile messagesConfigFile;

    public BroadcastPlayerDeathListener(DeathMessages deathMessages) {
        this.deathMessages = deathMessages;
        fileStore = deathMessages.getFileStore();
        messagesConfigFile = deathMessages.getConfigManager().getMessagesConfig();
    }

    @EventHandler
    public void broadcastListener(BroadcastDeathMessageEvent e) {

        if (!e.isCancelled()) {
            if (messagesConfigFile.getConfig().getBoolean("Console.Enabled")) {
                String message = Assets.playerDeathPlaceholders(messagesConfigFile.getConfig().getString("Console.Message"), PlayerManager.getPlayer(e.getPlayer()), e.getLivingEntity());
                message = message.replaceAll("%message%", Matcher.quoteReplacement(e.getTextComponent().toLegacyText()));
                Bukkit.getConsoleSender().sendMessage(message);
            }

            PlayerManager pm = PlayerManager.getPlayer(e.getPlayer());
            if (pm.isInCooldown()) {
                return;
            } else {
                pm.setCooldown();
            }

            boolean privatePlayer = fileStore.getConfig().getBoolean(Config.PRIVATE_MESSAGES_PLAYER);
            boolean privateMobs = fileStore.getConfig().getBoolean(Config.PRIVATE_MESSAGES_MOBS);
            boolean privateNatural = fileStore.getConfig().getBoolean(Config.PRIVATE_MESSAGES_NATURAL);

            //To reset for each death message
            discordSent = false;

            for (World w : e.getBroadcastedWorlds()) {
                if (fileStore.getConfig().getStringList(Config.DISABLED_WORLDS).contains(w.getName())) {
                    continue;
                }
                for (Player pls : w.getPlayers()) {
                    PlayerManager pms = PlayerManager.getPlayer(pls);
                    if (pms == null) {
                        pms = new PlayerManager(deathMessages, pls);
                    }
                    if (e.getMessageType().equals(MessageType.PLAYER)) {
                        if (privatePlayer && (e.getPlayer().getUniqueId().equals(pms.getUUID())
                                || e.getLivingEntity().getUniqueId().equals(pms.getUUID()))) {
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        } else if (!privatePlayer) {
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        }
                    } else if (e.getMessageType().equals(MessageType.MOB)) {
                        if (privateMobs && e.getPlayer().getUniqueId().equals(pms.getUUID())) {
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        } else if (!privateMobs) {
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        }
                    } else if (e.getMessageType().equals(MessageType.NATURAL)) {
                        if (privateNatural && e.getPlayer().getUniqueId().equals(pms.getUUID())) {
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        } else if (!privateNatural) {
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        }
                    }
                }
            }
            PluginMessagingUtils.sendMessage(deathMessages, fileStore, e.getPlayer(), ComponentSerializer.toString(e.getTextComponent()));
        }
    }

    private void normal(BroadcastDeathMessageEvent e, PlayerManager pms, Player pls, List<World> worlds) {
        if (deathMessages.getWorldGuardExtension() != null) {
            if (deathMessages.getWorldGuardExtension().getRegionState(pls, e.getMessageType().getValue()).equals(StateFlag.State.DENY)
                    || deathMessages.getWorldGuardExtension().getRegionState(e.getPlayer(), e.getMessageType().getValue()).equals(StateFlag.State.DENY)) {
                return;
            }
        }
        try {
            if (pms.getMessagesEnabled()) {
                pls.spigot().sendMessage(e.getTextComponent());
            }
            if (fileStore.getConfig().getBoolean(Config.HOOKS_DISCORD_WORLD_WHITELIST_ENABLED)) {
                List<String> discordWorldWhitelist = fileStore.getConfig().getStringList(Config.HOOKS_DISCORD_WORLD_WHITELIST_WORLDS);
                boolean broadcastToDiscord = false;
                for (World world : worlds) {
                    if (discordWorldWhitelist.contains(world.getName())) {
                        broadcastToDiscord = true;
                    }
                }
                if (!broadcastToDiscord) {
                    //Wont reach the discord broadcast
                    return;
                }
                //Will reach the discord broadcast
            }
            if (deathMessages.getDiscordBotAPIExtension() != null && !discordSent) {
                deathMessages.getDiscordBotAPIExtension().sendDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), ChatColor.stripColor(e.getTextComponent().toLegacyText()));
                discordSent = true;
            }
            if (deathMessages.getDiscordSRVExtension() != null && !discordSent) {
                deathMessages.getDiscordSRVExtension().sendDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), ChatColor.stripColor(e.getTextComponent().toLegacyText()));
                discordSent = true;
            }
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }
    }
}
