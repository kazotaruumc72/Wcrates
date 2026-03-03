package com.wcrates.plugin.crate;

import com.wcrates.plugin.WcratesPlugin;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading and accessing crate configurations
 */
public class CrateManager {

    private final WcratesPlugin plugin;
    private final Map<String, CrateConfiguration> crates;

    public CrateManager(WcratesPlugin plugin) {
        this.plugin = plugin;
        this.crates = new HashMap<>();
        loadCrates();
    }

    /**
     * Load all crate configurations from the crates folder
     */
    public void loadCrates() {
        crates.clear();

        File cratesFolder = new File(plugin.getDataFolder(), "crates");
        File placeholdersFolder = new File(plugin.getDataFolder(), "placeholders");

        // Create the crates folder if it doesn't exist
        if (!cratesFolder.exists()) {
            cratesFolder.mkdirs();

            // Copy example crate
            File exampleCrate = new File(cratesFolder, "example_crate.yml");
            if (!exampleCrate.exists()) {
                plugin.saveResource("crates/example_crate.yml", false);
            }
        }

        // Create the placeholders folder if it doesn't exist
        if (!placeholdersFolder.exists()) {
            placeholdersFolder.mkdirs();

            // Copy example placeholder file
            File examplePlaceholder = new File(placeholdersFolder, "example_crate.yml");
            if (!examplePlaceholder.exists()) {
                plugin.saveResource("placeholders/example_crate.yml", false);
            }
        }

        // Load all YAML files in the crates folder
        File[] files = cratesFolder.listFiles((dir, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));

        if (files != null) {
            for (File file : files) {
                try {
                    CrateConfiguration crate = new CrateConfiguration(file);
                    crates.put(crate.getId(), crate);
                    plugin.getLogger().info("Loaded crate: " + crate.getId());
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load crate from " + file.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        plugin.getLogger().info("Loaded " + crates.size() + " crate(s)");
    }

    /**
     * Get a crate by its ID
     */
    public CrateConfiguration getCrate(String id) {
        return crates.get(id);
    }

    /**
     * Get all loaded crates
     */
    public Map<String, CrateConfiguration> getAllCrates() {
        return new HashMap<>(crates);
    }

    /**
     * Reload all crate configurations
     */
    public void reload() {
        loadCrates();
    }
}
