package optic_fusion1.deathmessages.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Utils {

    private Utils() {

    }

    public static boolean isPlayer(String name) {
        return Bukkit.getPlayer(name) != null;
    }

    public static Player getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }

}
