package optic_fusion1.deathmessages.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public final class Colorize {

    private static final Pattern PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    private Colorize() {
    }

    public static String colorize(String string) {
        for (Matcher matcher = PATTERN.matcher(string); matcher.find(); matcher = PATTERN.matcher(string)) {
            String color = string.substring(matcher.start(), matcher.end());
            string = string.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
