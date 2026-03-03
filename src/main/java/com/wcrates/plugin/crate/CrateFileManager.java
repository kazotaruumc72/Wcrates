package com.wcrates.plugin.crate;

import com.wcrates.plugin.WcratesPlugin;
import org.bukkit.Material;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages creation of crate and placeholder files
 */
public class CrateFileManager {

    private final WcratesPlugin plugin;

    public CrateFileManager(WcratesPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Create both crate configuration and placeholder files for a new crate
     */
    public boolean createCrateFiles(String crateId, String crateName, Material blockType) {
        try {
            // Create crate configuration file
            boolean crateCreated = createCrateConfigFile(crateId, crateName, blockType);

            // Create placeholder configuration file
            boolean placeholderCreated = createPlaceholderFile(crateId);

            if (crateCreated && placeholderCreated) {
                plugin.getLogger().info("Successfully created files for crate: " + crateId);
                return true;
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create files for crate " + crateId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create a crate configuration file
     */
    private boolean createCrateConfigFile(String crateId, String crateName, Material blockType) throws IOException {
        File cratesFolder = new File(plugin.getDataFolder(), "crates");
        if (!cratesFolder.exists()) {
            cratesFolder.mkdirs();
        }

        File crateFile = new File(cratesFolder, crateId + ".yml");
        if (crateFile.exists()) {
            plugin.getLogger().warning("Crate file already exists: " + crateId + ".yml");
            return false;
        }

        StringBuilder content = new StringBuilder();
        content.append("# Crate configuration for ").append(crateId).append("\n");
        content.append("# This crate uses placeholders to determine rewards\n\n");
        content.append("crate:\n");
        content.append("  # The ID of this crate (must be unique)\n");
        content.append("  id: \"").append(crateId).append("\"\n\n");
        content.append("  # Display name of the crate\n");
        content.append("  name: \"").append(crateName).append("\"\n\n");
        content.append("  # Block type for this crate\n");
        content.append("  block: \"").append(blockType.name()).append("\"\n\n");
        content.append("  # Animation settings\n");
        content.append("  animation:\n");
        content.append("    # Duration in ticks (20 ticks = 1 second)\n");
        content.append("    duration: 100\n");
        content.append("    # Speed of scrolling (ticks between updates)\n");
        content.append("    speed: 2\n");
        content.append("    # Min and max values for scrolling\n");
        content.append("    min: 1\n");
        content.append("    max: 200\n\n");
        content.append("# Rewards configuration with placeholder-based ranges\n");
        content.append("rewards:\n");
        content.append("  placeholders:\n");
        content.append("    1:\n");
        content.append("      between: '%wcrates_").append(crateId).append("_1-50%'\n");
        content.append("      name: \"&7&lCommon Reward\"\n");
        content.append("      commands:\n");
        content.append("        - \"give %player% iron_ingot 10\"\n");
        content.append("      messages:\n");
        content.append("        - \"&7You won the Common Reward!\"\n");
        content.append("        - \"&7You received 10 iron ingots!\"\n\n");
        content.append("    2:\n");
        content.append("      between: '%wcrates_").append(crateId).append("_51-100%'\n");
        content.append("      name: \"&e&lUncommon Reward\"\n");
        content.append("      commands:\n");
        content.append("        - \"give %player% gold_ingot 5\"\n");
        content.append("        - \"give %player% iron_ingot 15\"\n");
        content.append("      messages:\n");
        content.append("        - \"&eYou won the Uncommon Reward!\"\n");
        content.append("        - \"&7You received 5 gold ingots and 15 iron ingots!\"\n\n");
        content.append("    3:\n");
        content.append("      between: '%wcrates_").append(crateId).append("_101-139%'\n");
        content.append("      name: \"&b&lRare Reward I\"\n");
        content.append("      commands:\n");
        content.append("        - \"give %player% diamond 3\"\n");
        content.append("      messages:\n");
        content.append("        - \"&bYou won Rare Reward I!\"\n");
        content.append("        - \"&7You received 3 diamonds!\"\n\n");
        content.append("    4:\n");
        content.append("      between: '%wcrates_").append(crateId).append("_140-168%'\n");
        content.append("      name: \"&a&lRare Reward\"\n");
        content.append("      commands:\n");
        content.append("        - \"give %player% diamond 5\"\n");
        content.append("        - \"give %player% emerald 3\"\n");
        content.append("      messages:\n");
        content.append("        - \"&aYou won the Rare Reward!\"\n");
        content.append("        - \"&7You received 5 diamonds and 3 emeralds!\"\n\n");
        content.append("    5:\n");
        content.append("      between: '%wcrates_").append(crateId).append("_169-200%'\n");
        content.append("      name: \"&5&lEpic Reward\"\n");
        content.append("      commands:\n");
        content.append("        - \"give %player% netherite_ingot 1\"\n");
        content.append("        - \"give %player% diamond 10\"\n");
        content.append("      messages:\n");
        content.append("        - \"&5You won the Epic Reward!\"\n");
        content.append("        - \"&7You received 1 netherite ingot and 10 diamonds!\"\n");

        try (FileWriter writer = new FileWriter(crateFile)) {
            writer.write(content.toString());
        }

        return true;
    }

    /**
     * Create a placeholder configuration file
     */
    private boolean createPlaceholderFile(String crateId) throws IOException {
        File placeholdersFolder = new File(plugin.getDataFolder(), "placeholders");
        if (!placeholdersFolder.exists()) {
            placeholdersFolder.mkdirs();
        }

        File placeholderFile = new File(placeholdersFolder, crateId + ".yml");
        if (placeholderFile.exists()) {
            plugin.getLogger().warning("Placeholder file already exists: " + crateId + ".yml");
            return false;
        }

        StringBuilder content = new StringBuilder();
        content.append("# Placeholder configuration for ").append(crateId).append("\n");
        content.append("# This file defines the placeholders used by this crate\n\n");
        content.append("placeholder:\n");
        content.append("  # The crate ID this placeholder belongs to\n");
        content.append("  crate_id: \"").append(crateId).append("\"\n\n");
        content.append("  # Placeholder prefix (will be %wcrates_CRATEID_value%)\n");
        content.append("  prefix: \"wcrates\"\n\n");
        content.append("  # Available placeholders for this crate\n");
        content.append("  placeholders:\n");
        content.append("    # %wcrates_").append(crateId).append("_1-50%\n");
        content.append("    - range: \"1-50\"\n");
        content.append("      description: \"Common Reward range\"\n\n");
        content.append("    # %wcrates_").append(crateId).append("_51-100%\n");
        content.append("    - range: \"51-100\"\n");
        content.append("      description: \"Uncommon Reward range\"\n\n");
        content.append("    # %wcrates_").append(crateId).append("_101-139%\n");
        content.append("    - range: \"101-139\"\n");
        content.append("      description: \"Rare Reward I range\"\n\n");
        content.append("    # %wcrates_").append(crateId).append("_140-168%\n");
        content.append("    - range: \"140-168\"\n");
        content.append("      description: \"Rare Reward range\"\n\n");
        content.append("    # %wcrates_").append(crateId).append("_169-200%\n");
        content.append("    - range: \"169-200\"\n");
        content.append("      description: \"Epic Reward range\"\n");

        try (FileWriter writer = new FileWriter(placeholderFile)) {
            writer.write(content.toString());
        }

        return true;
    }
}
