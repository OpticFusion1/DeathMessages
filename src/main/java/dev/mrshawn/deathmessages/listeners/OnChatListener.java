package dev.mrshawn.deathmessages.listeners;

//import dev.mrshawn.deathmessages.config.EntityDeathMessages;
//import dev.mrshawn.deathmessages.config.PlayerDeathMessages;
import dev.mrshawn.deathmessages.utils.Assets;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.List;
import optic_fusion1.deathmessages.config.ConfigFile;

// TODO: Clean this class up more
public class OnChatListener implements Listener {

    private ConfigFile playerDeathMessagesConfig;
    private ConfigFile entityDeathMessagesConfig;

    public OnChatListener(ConfigFile playerDeathMessagesConfig, ConfigFile entityDeathMessagesConfig) {
        this.playerDeathMessagesConfig = playerDeathMessagesConfig;
        this.entityDeathMessagesConfig = entityDeathMessagesConfig;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (Assets.addingMessage.containsKey(p.getName())) {
            e.setCancelled(true);
            String args = Assets.addingMessage.get(p.getName());
            Assets.addingMessage.remove(p.getName());
            String[] spl = args.split(":");
            if (spl[0].equalsIgnoreCase("Gang") || spl[0].equalsIgnoreCase("Solo")) {
                add(spl[0], spl[1], spl[2], e, p, true);
                return;
            } else {
                add("", spl[0], spl[1], e, p, false);
            }
        }
    }

    private void add(String mode, String mobName, String damageType, AsyncPlayerChatEvent event, Player player, boolean gangOrSolo) {
        String path = gangOrSolo ? "Mobs." + mobName + "." + mode + "." + damageType : "Entities." + mobName + "." + damageType;
        List<String> list = gangOrSolo ? playerDeathMessagesConfig.getConfig().getStringList(path) : entityDeathMessagesConfig.getConfig().getStringList(path);
        list.add(event.getMessage());

        if (gangOrSolo) {
            playerDeathMessagesConfig.getConfig().set(path, list);
            playerDeathMessagesConfig.save();
            playerDeathMessagesConfig.reload();
        } else {
            entityDeathMessagesConfig.getConfig().set(path, list);
            entityDeathMessagesConfig.save();
            entityDeathMessagesConfig.reload();
        }
        player.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Added-Message")
                .replaceAll("%message%", event.getMessage())
                .replaceAll("%mob_name%", mobName)
                .replaceAll("%mode%", mode)
                .replaceAll("%damage_type%", damageType));
    }

}
