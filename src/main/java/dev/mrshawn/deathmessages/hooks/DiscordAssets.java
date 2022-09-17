package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.files.Config;

import java.util.List;
import optic_fusion1.deathmessages.util.FileStore;

public class DiscordAssets {

    private FileStore fileStore;
    
    public DiscordAssets(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    public List<String> getIDs(MessageType messageType) {
        return switch (messageType) {
            case PLAYER ->
                fileStore.getConfig().getStringList(Config.HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS);
            case MOB ->
                fileStore.getConfig().getStringList(Config.HOOKS_DISCORD_CHANNELS_MOB_CHANNELS);
            case NATURAL ->
                fileStore.getConfig().getStringList(Config.HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS);
            case ENTITY ->
                fileStore.getConfig().getStringList(Config.HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS);
        };
    }

}
