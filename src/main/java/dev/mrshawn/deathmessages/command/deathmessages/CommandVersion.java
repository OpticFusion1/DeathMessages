package dev.mrshawn.deathmessages.command.deathmessages;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import org.bukkit.command.CommandSender;

public class CommandVersion extends DeathMessagesCommand {

    private DeathMessages deathMessages;

    public CommandVersion(DeathMessages deathMessages) {
        this.deathMessages = deathMessages;
    }

    @Override
    public String command() {
        return "version";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_VERSION.getValue())) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        String message = Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Version");
        message = message.replaceAll("%version%", deathMessages.getDescription().getVersion());
        sender.sendMessage(message);
    }
}
