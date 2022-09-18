package dev.mrshawn.deathmessages.command.deathmessages;

import dev.mrshawn.deathMessages.command.deathmessages.CommandDiscordLog;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.joshb.discordbotapi.bungee.config.Messages;
import optic_fusion1.deathmessages.config.ConfigFile;
import optic_fusion1.deathmessages.config.ConfigManager;

public class CommandManager implements CommandExecutor {

    private final List<DeathMessagesCommand> commands = new ArrayList<>();
    private DeathMessages deathMessages;
    private ConfigManager configManager;
    private ConfigFile messagesConfigFile;

    public CommandManager(DeathMessages deathMessages, ConfigManager configManager) {
        this.deathMessages = deathMessages;
        this.configManager = configManager;
        messagesConfigFile = configManager.getMessagesConfig();
    }

    public void initializeSubCommands() {
        commands.add(new CommandBackup(configManager));
        commands.add(new CommandBlacklist(configManager.getUserDataConfig()));
        commands.add(new CommandDiscordLog(deathMessages));
        commands.add(new CommandEdit(deathMessages.getConfigManager().getPlayerDeathMessagesConfig(),
                deathMessages.getConfigManager().getEntityDeathMessagesConfig()));
        commands.add(new CommandReload(deathMessages.getConfigManager()));
        commands.add(new CommandRestore(deathMessages.getConfigManager()));
        commands.add(new CommandToggle());
        commands.add(new CommandVersion(deathMessages));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmdLabel, String[] args) {
        if (sender instanceof Player && !sender.hasPermission(Permission.DEATHMESSAGES_COMMAND.getValue())) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return false;
        }
        if (args.length == 0) {
            for (String s : Assets.formatMessage(Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Help"))) {
                sender.sendMessage(s);
            }
        } else {
            DeathMessagesCommand cmd = get(args[0]);
            if (!(cmd == null)) {
                ArrayList<String> a = new ArrayList<>(Arrays.asList(args));
                a.remove(0);
                args = a.toArray(new String[a.size()]);
                cmd.onCommand(sender, args);
                return false;
            }
            for (String s : Assets.formatMessage(messagesConfigFile.getConfig().getStringList("Commands.DeathMessages.Help"))) {
                sender.sendMessage(s);
            }
        }
        return false;
    }

    private DeathMessagesCommand get(String name) {
        for (DeathMessagesCommand cmd : commands) {
            if (cmd.command().equalsIgnoreCase(name)) {
                return cmd;
            }
        }
        return null;
    }
}
