package optic_fusion1.deathmessages.util;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;

public class FileStore {

    private FileSettings<Config> config;

    public FileStore(DeathMessages deathMessages) {
        config = new FileSettings<Config>(deathMessages, "Settings.yml").loadSettings(Config.class);
    }

    public FileSettings<Config> getConfig() {
        return config;
    }

}
