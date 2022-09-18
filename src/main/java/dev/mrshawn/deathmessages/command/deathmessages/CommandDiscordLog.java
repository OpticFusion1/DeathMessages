package dev.mrshawn.deathMessages.command.deathmessages;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.command.deathmessages.DeathMessagesCommand;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.Assets;
import github.scarsz.discordsrv.DiscordSRV;
import me.joshb.discordbotapi.server.DiscordBotAPI;
import org.bukkit.command.CommandSender;
import java.util.List;
import optic_fusion1.deathmessages.config.ConfigFile;
import optic_fusion1.deathmessages.util.FileStore;

public class CommandDiscordLog extends DeathMessagesCommand {

    private DeathMessages deathMessages;
    private FileStore fileStore;
    private ConfigFile messagesConfigFile;

    public CommandDiscordLog(DeathMessages deathMessages) {
        this.deathMessages = deathMessages;
        fileStore = deathMessages.getFileStore();
        messagesConfigFile = deathMessages.getConfigManager().getMessagesConfig();
    }

    @Override
    public String command() {
        return "discordlog";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_DISCORDLOG.getValue())) {
            sender.sendMessage(Assets.formatMessage("Commands.deathMessages.No-Permission"));
            return;
        }
        List<String> discordLog = messagesConfigFile.getConfig().getStringList("Commands.deathMessages.Sub-Commands.DiscordLog");
        String discordJar;
        if (deathMessages.getDiscordBotAPIExtension() != null) {
            discordJar = "DiscordBotAPI";
        } else if (deathMessages.getDiscordSRVExtension() != null) {
            discordJar = "DiscordSRV";
        } else {
            discordJar = "Discord Jar Not Installed";
        }
        String discordToken;
        if (discordJar.equals("DiscordBotAPI")) {
            discordToken = DiscordBotAPI.getJDA().getToken().length() > 40 ? DiscordBotAPI.getJDA().getToken().substring(40) : "Token Not Set";
        } else if (deathMessages.getDiscordSRVExtension() != null) {
            discordToken = DiscordSRV.getPlugin().getJda().getToken().length() > 40 ? DiscordSRV.getPlugin().getJda().getToken().substring(40) : "Token Not Set";
        } else {
            discordToken = "Discord Jar Not Installed";
        }
        for (String log : discordLog) {
            if (log.equals("%discordConfig%")) {
                sender.sendMessage(Assets.colorize("  &aEnabled: &c" + fileStore.getConfig().getBoolean(Config.HOOKS_DISCORD_ENABLED)));
                sender.sendMessage(Assets.colorize("  &aChannels:"));
                //Player
                sender.sendMessage(Assets.colorize("    &aPlayer-Enabled: &c" + fileStore.getConfig().getBoolean(Config.HOOKS_DISCORD_CHANNELS_PLAYER_ENABLED)));
                sender.sendMessage(Assets.colorize("    &aPlayer-Channels:"));
                for (String channels : fileStore.getConfig().getStringList(Config.HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS)) {
                    sender.sendMessage("      - " + channels);
                }
                //Mob
                sender.sendMessage(Assets.colorize("    &aMob-Enabled: &c" + fileStore.getConfig().getBoolean(Config.HOOKS_DISCORD_CHANNELS_MOB_ENABLED)));
                sender.sendMessage(Assets.colorize("    &aMob-Channels:"));
                for (String channels : fileStore.getConfig().getStringList(Config.HOOKS_DISCORD_CHANNELS_MOB_CHANNELS)) {
                    sender.sendMessage("      - " + channels);
                }
                //Player
                sender.sendMessage(Assets.colorize("    &aNatural-Enabled: &c" + fileStore.getConfig().getBoolean(Config.HOOKS_DISCORD_CHANNELS_NATURAL_ENABLED)));
                sender.sendMessage(Assets.colorize("    &aNatural-Channels:"));
                for (String channels : fileStore.getConfig().getStringList(Config.HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS)) {
                    sender.sendMessage("      - " + channels);
                }
                //Player
                sender.sendMessage(Assets.colorize("    &aEntity-Enabled: &c" + fileStore.getConfig().getBoolean(Config.HOOKS_DISCORD_CHANNELS_ENTITY_ENABLED)));
                sender.sendMessage(Assets.colorize("    &aEntity-Channels:"));
                for (String channels : fileStore.getConfig().getStringList(Config.HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS)) {
                    sender.sendMessage("      - " + channels);
                }
                continue;
            }
            sender.sendMessage(Assets.colorize(log
                    .replaceAll("%discordJar%", discordJar)
                    .replaceAll("%discordToken%", discordToken)
                    .replace("%prefix%", messagesConfigFile.getConfig().getString("Prefix"))));
        }
    }
}
