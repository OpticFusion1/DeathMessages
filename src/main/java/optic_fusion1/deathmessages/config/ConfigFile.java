package optic_fusion1.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.utils.CommentedConfiguration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigFile {

    private String fileName;
    private CommentedConfiguration config;
    private File file;
    private Logger logger;
    private File dataFolder;
    private DeathMessages deathMessages;
    private String[] sync;

    public ConfigFile(String fileName, DeathMessages deathMessages, String... sync) {
        this.sync = sync;
        this.fileName = fileName;
        this.deathMessages = deathMessages;
        logger = deathMessages.getLogger();
        dataFolder = deathMessages.getDataFolder();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            File brokenFile = new File(dataFolder, fileName + ".broken." + new Date().getTime());
            logger.log(Level.SEVERE, "Could not save: {0}.yml", fileName);
            logger.log(Level.SEVERE, "Regenerating file and renaming the current file to {0}", brokenFile.getName());
            logger.log(Level.SEVERE, "You can try fixing the file with a yaml parser online!");
            file.renameTo(brokenFile);
            initialize();
        }
    }

    public void reload() {
        try {
            config = CommentedConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            File brokenFile = new File(dataFolder, fileName + ".broken." + new Date().getTime());
            logger.log(Level.SEVERE, "Could not reload: {0}.yml", fileName);
            logger.log(Level.SEVERE, "Regenerating file and renaming the current file to: {0}.yml", brokenFile.getName());
            logger.log(Level.SEVERE, "You can try fixing the file with a yaml parser online!");
            file.renameTo(brokenFile);
            initialize();
        }
    }

    public void initialize() {
        file = new File(dataFolder, fileName + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        copy(deathMessages.getResource(fileName + ".yml"), file);
        try {
            config.syncWithConfig(file, deathMessages.getResource(fileName + ".yml"), sync);
        } catch (IOException ignored) {

        }
    }

    private void copy(InputStream in, File file) {
        try {
            try (in; OutputStream out = new FileOutputStream(file)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CommentedConfiguration getConfig() {
        return config;
    }

    public void setConfig(YamlConfiguration config) {
        this.config = (CommentedConfiguration) config;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public Logger getLogger() {
        return logger;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public DeathMessages getDeathMessages() {
        return deathMessages;
    }
    
    

}
