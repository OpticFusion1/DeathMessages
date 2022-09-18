package dev.mrshawn.deathmessages.command.deathmessages;

import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import optic_fusion1.deathmessages.config.ConfigManager;
import org.bukkit.command.CommandSender;

public class CommandBackup extends DeathMessagesCommand {

    private ConfigManager configManager;

    public CommandBackup(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public String command() {
        return "backup";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_BACKUP.getValue())) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Backup.Usage"));
        } else {
            boolean b = Boolean.parseBoolean(args[0]);
            String code = configManager.backup(b);
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Backup.Backed-Up")
                    .replaceAll("%backup-code%", code));
        }
    }
}
