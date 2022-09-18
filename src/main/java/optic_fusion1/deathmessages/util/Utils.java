package optic_fusion1.deathmessages.util;

import static dev.mrshawn.deathmessages.utils.Assets.itemMaterialIsWeapon;
import static dev.mrshawn.deathmessages.utils.Assets.itemNameIsWeapon;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.EntityDamageEvent;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;
import org.bukkit.inventory.ItemStack;

public final class Utils {

    private Utils() {

    }

    public static boolean isNumeric(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isClimbable(Block block) {
        return isClimbable(block.getType());
    }

    public static boolean isClimbable(Material material) {
        final String name = material.name();
        return name.contains("LADDER")
                || name.contains("VINE")
                || name.contains("SCAFFOLDING")
                || name.contains("TRAPDOOR");
    }

    public static boolean isPlayer(String name) {
        return Bukkit.getPlayer(name) != null;
    }

    public static Player getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }

    public static boolean isWeapon(ItemStack itemStack) {
        return isWeapon(itemStack.getType())
                && !itemNameIsWeapon(itemStack)
                && !itemMaterialIsWeapon(itemStack);
    }

    public static boolean isWeapon(Material material) {
        String materialName = material.toString();
        return !materialName.contains("SHOVEL")
                && !materialName.contains("PICKAXE")
                && !materialName.contains("AXE")
                && !materialName.contains("HOE")
                && !materialName.contains("SWORD")
                && !materialName.contains("BOW");
    }

    public static String getColorOfString(String input) {
        StringBuilder result = new StringBuilder();
        int length = input.length();
        // Search backwards from the end as it is faster
        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == ChatColor.COLOR_CHAR && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = ChatColor.getByChar(c);
                if (color != null) {
                    result.insert(0, color);
                    // Once we find a color or reset we can stop searching
                    if (isChatColorAColor(color) || color.equals(ChatColor.RESET)) {
                        break;
                    }
                }
            }
        }
        return result.toString();
    }

    public static boolean isChatColorAColor(ChatColor chatColor) {
        return chatColor != ChatColor.MAGIC && chatColor != ChatColor.BOLD
                && chatColor != ChatColor.STRIKETHROUGH && chatColor != ChatColor.UNDERLINE
                && chatColor != ChatColor.ITALIC;
    }

    public static String getSimpleProjectile(Projectile projectile) {
        if (projectile instanceof Arrow) {
            return "Projectile-Arrow";
        } else if (projectile instanceof DragonFireball) {
            return "Projectile-Dragon-Fireball";
        } else if (projectile instanceof Egg) {
            return "Projectile-Egg";
        } else if (projectile instanceof EnderPearl) {
            return "Projectile-EnderPearl";
        } else if (projectile instanceof WitherSkull) {
            return "Projectile-Fireball";
        } else if (projectile instanceof Fireball) {
            return "Projectile-Fireball";
        } else if (projectile instanceof FishHook) {
            return "Projectile-FishHook";
        } else if (projectile instanceof LlamaSpit) {
            return "Projectile-LlamaSpit";
        } else if (projectile instanceof Snowball) {
            return "Projectile-Snowball";
        } else if (projectile instanceof Trident) {
            return "Projectile-Trident";
        } else if (projectile instanceof ShulkerBullet) {
            return "Projectile-ShulkerBullet";
        } else {
            return "Projectile-Arrow";
        }
    }

    public static String getSimpleCause(EntityDamageEvent.DamageCause damageCause) {
        return switch (damageCause) {
            case CONTACT ->
                "Contact";
            case ENTITY_ATTACK ->
                "Melee";
            case PROJECTILE ->
                "Projectile";
            case SUFFOCATION ->
                "Suffocation";
            case FALL ->
                "Fall";
            case FIRE ->
                "Fire";
            case FIRE_TICK ->
                "Fire-Tick";
            case MELTING ->
                "Melting";
            case LAVA ->
                "Lava";
            case DROWNING ->
                "Drowning";
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION ->
                "Explosion";
            case VOID ->
                "Void";
            case LIGHTNING ->
                "Lightning";
            case SUICIDE ->
                "Suicide";
            case STARVATION ->
                "Starvation";
            case POISON ->
                "Poison";
            case MAGIC ->
                "Magic";
            case WITHER ->
                "Wither";
            case FALLING_BLOCK ->
                "Falling-Block";
            case THORNS ->
                "Thorns";
            case DRAGON_BREATH ->
                "Dragon-Breath";
            case CUSTOM ->
                "Custom";
            case FLY_INTO_WALL ->
                "Fly-Into-Wall";
            case HOT_FLOOR ->
                "Hot-Floor";
            case CRAMMING ->
                "Cramming";
            case DRYOUT ->
                "Dryout";
            case FREEZE ->
                "Freeze";
            case SONIC_BOOM ->
                "Sonic-Boom";
            default ->
                "Unknown";
        };
    }

    public static String convertString(String string) {
        string = string.replaceAll("_", " ").toLowerCase();
        String[] spl = string.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spl.length; i++) {
            if (i == spl.length - 1) {
                sb.append(StringUtils.capitalize(spl[i]));
            } else {
                sb.append(StringUtils.capitalize(spl[i])).append(" ");
            }
        }
        return sb.toString();
    }

    public static String formatting(BaseComponent tx) {
        String returning = "";
        if (tx.isBold()) {
            returning = returning + "&l";
        }
        if (tx.isItalic()) {
            returning = returning + "&o";
        }
        if (tx.isObfuscated()) {
            returning = returning + "&k";
        }
        if (tx.isStrikethrough()) {
            returning = returning + "&m";
        }
        if (tx.isUnderlined()) {
            returning = returning + "&n";
        }
        return returning;
    }

}
