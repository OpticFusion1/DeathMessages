package dev.mrshawn.deathmessages.files;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSettings<C extends Enum<C>> {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private YamlConfiguration yamlConfig;
    private final Map<Enum<C>, Object> values = new HashMap<>();

    public FileSettings(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        loadFile();
    }

    private void loadFile() {
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
    }

    public void save() {
        try {
            yamlConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileSettings<C> loadSettings(Class<C> enumClass) {
        yamlConfig = YamlConfiguration.loadConfiguration(file);

        EnumSet<C> enumSet = EnumSet.allOf(enumClass);
        for (C value : enumSet) {
            if (!(value instanceof ConfigEnum configEnum)) {
                throw new IllegalArgumentException("Enum " + enumClass.getName() + " must implement ConfigEnum");
            }

            String configPath = configEnum.getPath();
            if (!yamlConfig.contains(configPath)) {
                Object defaultValue = configEnum.getDefault();
                if (defaultValue != null) {
                    yamlConfig.set(configPath, defaultValue);
                    values.put(value, defaultValue);
                }
                continue;
            }
            values.put(value, yamlConfig.get(configPath));
        }
        return this;
    }

    public boolean getBoolean(Enum<?> value) {
        return get(value, Boolean.class);
    }

    public String getString(Enum<?> value) {
        return get(value, String.class);
    }

    public int getInt(Enum<?> value) {
        return get(value, Integer.class);
    }

    public long getLong(Enum<?> value) {
        return get(value, Long.class);
    }

    public List<String> getStringList(Enum<?> value) {
        List<String> tempList = new ArrayList<>();
        for (Object val : get(value, List.class)) {
            tempList.add((String) val);
        }
        return tempList;
    }

    public <T> T get(Enum<?> value, Class<T> clazz) {
        return clazz.cast(values.get(value));
    }

    public void set(Enum<C> enumValue, ConfigEnum configEnum, Object setValue) {
        values.put(enumValue, setValue);
        yamlConfig.set(configEnum.getPath(), setValue);
    }
}
