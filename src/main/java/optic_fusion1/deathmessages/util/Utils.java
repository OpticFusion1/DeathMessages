package optic_fusion1.deathmessages.util;

import static dev.mrshawn.deathmessages.utils.Assets.itemMaterialIsWeapon;
import static dev.mrshawn.deathmessages.utils.Assets.itemNameIsWeapon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// TODO: Move material based stuff to MaterialUtils
public final class Utils {

    private Utils() {

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

}
