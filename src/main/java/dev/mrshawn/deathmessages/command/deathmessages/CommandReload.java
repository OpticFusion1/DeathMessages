package dev.mrshawn.deathmessages.command.deathmessages;

import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import optic_fusion1.deathmessages.config.ConfigManager;
import org.bukkit.command.CommandSender;

public class CommandReload extends DeathMessagesCommand {

    private ConfigManager configManager;

    public CommandReload(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public String command() {
        return "reload";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_RELOAD.getValue())) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        configManager.reload();
        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Reload.Reloaded"));
    }
}
