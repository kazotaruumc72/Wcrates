package com.wcrates.plugin.crate;

import com.wcrates.plugin.WcratesPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player keys for crates
 */
public class KeyManager {

    private final WcratesPlugin plugin;
    private final File keysFile;
    private FileConfiguration keysConfig;

    public KeyManager(WcratesPlugin plugin) {
        this.plugin = plugin;
        this.keysFile = new File(plugin.getDataFolder(), "keys.yml");
        loadKeys();
    }

    /**
     * Load keys from the keys.yml file
     */
    private void loadKeys() {
        if (!keysFile.exists()) {
            try {
                keysFile.getParentFile().mkdirs();
                keysFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create keys.yml file: " + e.getMessage());
            }
        }
        keysConfig = YamlConfiguration.loadConfiguration(keysFile);
    }

    /**
     * Save keys to the keys.yml file
     */
    private void saveKeys() {
        try {
            keysConfig.save(keysFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save keys.yml file: " + e.getMessage());
        }
    }

    /**
     * Give keys to a player
     *
     * @param player The player to give keys to
     * @param crateId The ID of the crate
     * @param amount The number of keys to give
     */
    public void giveKeys(Player player, String crateId, int amount) {
        String path = player.getUniqueId().toString() + "." + crateId;
        int currentAmount = keysConfig.getInt(path, 0);
        keysConfig.set(path, currentAmount + amount);
        saveKeys();
    }

    /**
     * Take keys from a player
     *
     * @param player The player to take keys from
     * @param crateId The ID of the crate
     * @param amount The number of keys to take
     * @return true if the player had enough keys, false otherwise
     */
    public boolean takeKeys(Player player, String crateId, int amount) {
        String path = player.getUniqueId().toString() + "." + crateId;
        int currentAmount = keysConfig.getInt(path, 0);

        if (currentAmount < amount) {
            return false;
        }

        keysConfig.set(path, currentAmount - amount);
        saveKeys();
        return true;
    }

    /**
     * Get the number of keys a player has for a specific crate
     *
     * @param player The player to check
     * @param crateId The ID of the crate
     * @return The number of keys the player has
     */
    public int getKeys(Player player, String crateId) {
        String path = player.getUniqueId().toString() + "." + crateId;
        return keysConfig.getInt(path, 0);
    }

    /**
     * Check if a player has at least one key for a specific crate
     *
     * @param player The player to check
     * @param crateId The ID of the crate
     * @return true if the player has at least one key, false otherwise
     */
    public boolean hasKey(Player player, String crateId) {
        return getKeys(player, crateId) > 0;
    }

    /**
     * Get all keys for a player
     *
     * @param player The player to get keys for
     * @return A map of crate IDs to key amounts
     */
    public Map<String, Integer> getAllKeys(Player player) {
        Map<String, Integer> keys = new HashMap<>();
        String playerPath = player.getUniqueId().toString();

        if (keysConfig.contains(playerPath)) {
            for (String crateId : keysConfig.getConfigurationSection(playerPath).getKeys(false)) {
                keys.put(crateId, keysConfig.getInt(playerPath + "." + crateId));
            }
        }

        return keys;
    }

    /**
     * Reload the keys configuration
     */
    public void reload() {
        loadKeys();
    }
}
