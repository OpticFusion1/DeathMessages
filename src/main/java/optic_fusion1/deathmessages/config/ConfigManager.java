package optic_fusion1.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import github.scarsz.discordsrv.dependencies.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.RandomStringUtils;

public class ConfigManager {

    private File backupDirectory;
    private ConfigFile entityDeathMessagesConfig;
    private ConfigFile gangsConfig;
    private ConfigFile messagesConfig;
    private ConfigFile playerDeathMessagesConfig;
    private ConfigFile settingsConfig;
    private ConfigFile userDataConfig;
    private DeathMessages deathMessages;
    private File dataFolder;

    public ConfigManager(DeathMessages deathMessages) {
        this.deathMessages = deathMessages;
        dataFolder = deathMessages.getDataFolder();
        this.backupDirectory = new File(dataFolder, "Backups");
    }

    public void initialize() {
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        entityDeathMessagesConfig = new ConfigFile("EntityDeathMessages", deathMessages, "Entities", "Mythic-Mobs-Entities");
        gangsConfig = new ConfigFile("Gangs", deathMessages, "none");
        messagesConfig = new ConfigFile("Messages", deathMessages, "none");
        playerDeathMessagesConfig = new ConfigFile("PlayerDeathMessages", deathMessages, "Mobs");
        settingsConfig = new ConfigFile("Settings", deathMessages, "none");
        // TODO: Might have to handle UserData differently
        userDataConfig = new UserDataConfig(deathMessages);
    }

    public void reload() {
        entityDeathMessagesConfig.reload();
        gangsConfig.reload();
        messagesConfig.reload();
        playerDeathMessagesConfig.reload();
        settingsConfig.reload();
    }

    public String backup(boolean excludeUserData) {
        if (!backupDirectory.exists()) {
            backupDirectory.mkdir();
        }
        String randomCode = RandomStringUtils.randomNumeric(4);
        File backupDir = new File(backupDirectory, randomCode);
        backupDir.mkdir();
        backupFile(entityDeathMessagesConfig.getFile());
        backupFile(gangsConfig.getFile());
        backupFile(messagesConfig.getFile());
        backupFile(playerDeathMessagesConfig.getFile());
        backupFile(settingsConfig.getFile());
        if (!excludeUserData) {
            backupFile(userDataConfig.getFile());
        }
        return randomCode;
    }

    private void backupFile(File file) {
        try {
            FileUtils.copyFileToDirectory(file, backupDirectory);
        } catch (IOException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    * Returns true if the operation was successful.
    * Returns false if the operation was not successful.
     */
    public boolean restore(String code, boolean excludeUserData) {
        File backupDir = new File(backupDirectory, code);
        if (!backupDir.exists()) {
            return false;
        }
        restore(entityDeathMessagesConfig);
        restore(gangsConfig);
        restore(messagesConfig);
        restore(playerDeathMessagesConfig);
        restore(settingsConfig);
        if (!excludeUserData) {
            restore(userDataConfig);
        }
        reload();
        return true;
    }

    private void restore(ConfigFile config) {
        String fileName = config.getFileName();
        File file = new File(backupDirectory, fileName + ".yml");
        if (config.getFile().delete()) {
            try {
                FileUtils.copyFileToDirectory(file, dataFolder);
            } catch (IOException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            deathMessages.getLogger().log(Level.SEVERE, "COULD NOT RESTORE {0}", fileName);
        }
    }

    public ConfigFile getEntityDeathMessagesConfig() {
        return entityDeathMessagesConfig;
    }

    public ConfigFile getGangsConfig() {
        return gangsConfig;
    }

    public ConfigFile getMessagesConfig() {
        return messagesConfig;
    }

    public ConfigFile getPlayerDeathMessagesConfig() {
        return playerDeathMessagesConfig;
    }

    public ConfigFile getSettingsConfig() {
        return settingsConfig;
    }

    public ConfigFile getUserDataConfig() {
        return userDataConfig;
    }

}
