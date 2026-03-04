package com.wcrates.plugin.crate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a crate configuration loaded from a YAML file
 */
public class CrateConfiguration {

    private final String id;
    private final String name;
    private final Material block;
    private final int minValue;
    private final int maxValue;
    private final int animationDuration;
    private final int animationSpeed;
    private final List<RewardRange> rewards;
    private final List<String> coordinates;
    private final File configFile;

    public CrateConfiguration(File file) {
        this.configFile = file;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        this.id = config.getString("crate.id", "unknown");
        this.name = config.getString("crate.name", "&6Crate");

        String blockName = config.getString("crate.block", "CHEST");
        Material tempBlock;
        try {
            tempBlock = Material.valueOf(blockName.toUpperCase());
        } catch (IllegalArgumentException e) {
            tempBlock = Material.CHEST;
        }
        this.block = tempBlock;

        // Animation settings are now under crate.animation
        this.minValue = config.getInt("crate.animation.min", 1);
        this.maxValue = config.getInt("crate.animation.max", 200);
        this.animationDuration = config.getInt("crate.animation.duration", 100);
        this.animationSpeed = config.getInt("crate.animation.speed", 2);

        // Load coordinates
        this.coordinates = new ArrayList<>();
        if (config.contains("crate.coordinates")) {
            List<String> coordList = config.getStringList("crate.coordinates");
            if (coordList != null) {
                this.coordinates.addAll(coordList);
            }
        }

        this.rewards = new ArrayList<>();

        // Parse the new rewards.placeholders structure
        if (config.contains("rewards.placeholders")) {
            ConfigurationSection placeholdersSection = config.getConfigurationSection("rewards.placeholders");
            if (placeholdersSection != null) {
                Set<String> keys = placeholdersSection.getKeys(false);
                for (String key : keys) {
                    ConfigurationSection rewardSection = placeholdersSection.getConfigurationSection(key);
                    if (rewardSection != null) {
                        rewards.add(new RewardRange(rewardSection, key));
                    }
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Material getBlock() {
        return block;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public List<RewardRange> getRewards() {
        return rewards;
    }

    public List<String> getCoordinates() {
        return new ArrayList<>(coordinates);
    }

    /**
     * Add a coordinate to the list and save to file
     */
    public void addCoordinate(Location location) {
        String coordString = locationToString(location);
        if (!coordinates.contains(coordString)) {
            coordinates.add(coordString);
            saveCoordinates();
        }
    }

    /**
     * Remove a coordinate from the list and save to file
     */
    public void removeCoordinate(Location location) {
        String coordString = locationToString(location);
        if (coordinates.remove(coordString)) {
            saveCoordinates();
        }
    }

    /**
     * Check if a location is in the coordinates list
     */
    public boolean hasCoordinate(Location location) {
        String coordString = locationToString(location);
        return coordinates.contains(coordString);
    }

    /**
     * Convert a Location to a string format: "world:x:y:z"
     */
    private String locationToString(Location location) {
        return String.format("%s:%d:%d:%d",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    /**
     * Convert a string to a Location
     */
    public static Location stringToLocation(String coordString) {
        String[] parts = coordString.split(":");
        if (parts.length != 4) {
            return null;
        }

        World world = Bukkit.getWorld(parts[0]);
        if (world == null) {
            return null;
        }

        try {
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Save coordinates to the YAML file
     */
    private void saveCoordinates() {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            config.set("crate.coordinates", coordinates);
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find the reward that matches the given value
     */
    public RewardRange findRewardForValue(int value) {
        for (RewardRange reward : rewards) {
            if (reward.isInRange(value)) {
                return reward;
            }
        }
        return null;
    }

    /**
     * Represents a reward range with commands and messages
     */
    public static class RewardRange {
        private final int min;
        private final int max;
        private final String name;
        private final String placeholder;
        private final String crateId;
        private final String rewardNumber;
        private final List<String> commands;
        private final List<String> messages;

        public RewardRange(ConfigurationSection section, String rewardNumber) {
            this.rewardNumber = rewardNumber;
            this.placeholder = section.getString("between", "");
            this.name = section.getString("name", "&7Reward");
            this.commands = section.getStringList("commands");
            this.messages = section.getStringList("messages");

            // Parse the 'between' placeholder to extract crate ID, min and max values
            // Format: %wcrates_CRATEID_MIN-MAX%
            int tempMin = 0;
            int tempMax = 100;
            String tempCrateId = "";

            if (!placeholder.isEmpty()) {
                // Pattern to match: %wcrates_example_crate_140-168%
                Pattern pattern = Pattern.compile("%wcrates_([a-zA-Z0-9_]+)_(\\d+)-(\\d+)%");
                Matcher matcher = pattern.matcher(placeholder);

                if (matcher.find()) {
                    try {
                        tempCrateId = matcher.group(1);
                        tempMin = Integer.parseInt(matcher.group(2));
                        tempMax = Integer.parseInt(matcher.group(3));
                    } catch (NumberFormatException e) {
                        // Use defaults if parsing fails
                    }
                }
            }

            this.crateId = tempCrateId;
            this.min = tempMin;
            this.max = tempMax;
        }

        public boolean isInRange(int value) {
            return value >= min && value <= max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public String getName() {
            return name;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public String getCrateId() {
            return crateId;
        }

        public String getRewardNumber() {
            return rewardNumber;
        }

        /**
         * Get the Nexo glyph placeholder in the format %nexo_CRATEID-CRATEID-NUMBER%
         * Example: %nexo_example_crate-example_crate-1%
         */
        public String getNexoGlyph() {
            if (crateId.isEmpty() || rewardNumber.isEmpty()) {
                return "";
            }
            return "%nexo_" + crateId + "-" + crateId + "-" + rewardNumber + "%";
        }

        public List<String> getCommands() {
            return commands;
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}
