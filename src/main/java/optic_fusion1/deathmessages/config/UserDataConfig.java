package optic_fusion1.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

// TODO: See if this can be merged into the Config file
public class UserDataConfig extends ConfigFile {

    public UserDataConfig(DeathMessages deathMessages) {
        super("UserData", deathMessages);
    }

    @Override
    public void reload() {
        try {
            getConfig().load(getFile());
        } catch (IOException | InvalidConfigurationException e) {
            File brokenFile = new File(getDataFolder(), getFileName() + ".broken." + new Date().getTime());
            getLogger().log(Level.SEVERE, "Could not reload: {0}.yml", getFileName());
            getLogger().log(Level.SEVERE, "Regenerating file and renaming the current file to: {0}.yml", brokenFile.getName());
            getLogger().log(Level.SEVERE, "You can try fixing the file with a yaml parser online!");
            getFile().renameTo(brokenFile);
            initialize();
        }
    }

    @Override
    public void initialize() {
        File file = new File(getDataFolder(), getFileName() + ".yml");
        setFile(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(UserDataConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        setConfig(YamlConfiguration.loadConfiguration(file));
        save();
        reload();
    }

}
