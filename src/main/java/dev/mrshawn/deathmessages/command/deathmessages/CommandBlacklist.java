package dev.mrshawn.deathmessages.command.deathmessages;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import java.util.Map;
import java.util.UUID;
import optic_fusion1.deathmessages.config.ConfigFile;

public class CommandBlacklist extends DeathMessagesCommand {

    private ConfigFile userData;

    public CommandBlacklist(ConfigFile userData) {
        this.userData = userData;
    }

    @Override
    public String command() {
        return "blacklist";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_BLACKLIST.getValue())) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Help"));
        } else {
            for (Map.Entry<String, Object> entry : userData.getConfig().getValues(false).entrySet()) {
                String username = userData.getConfig().getString(entry.getKey() + ".username");
                if (username.equalsIgnoreCase(args[0])) {
                    boolean blacklisted = userData.getConfig().getBoolean(entry.getKey() + ".is-blacklisted");
                    if (blacklisted) {
                        if (Bukkit.getPlayer(UUID.fromString(entry.getKey())) != null) {
                            PlayerManager pm = PlayerManager.getPlayer(UUID.fromString(entry.getKey()));
                            if (pm != null) {
                                pm.setBlacklisted(false);
                            }
                        }
                        userData.getConfig().set(entry.getKey() + ".is-blacklisted", false);
                        userData.save();
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Remove")
                                .replaceAll("%player%", args[0]));
                    } else {
                        if (Bukkit.getPlayer(UUID.fromString(entry.getKey())) != null) {
                            PlayerManager pm = PlayerManager.getPlayer(UUID.fromString(entry.getKey()));
                            if (pm != null) {
                                pm.setBlacklisted(true);
                            }
                        }
                        userData.getConfig().set(entry.getKey() + ".is-blacklisted", true);
                        userData.save();
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Add")
                                .replaceAll("%player%", args[0]));
                    }
                    return;
                }
            }
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Username-None-Existent")
                    .replaceAll("%player%", args[0]));
        }

    }
}
