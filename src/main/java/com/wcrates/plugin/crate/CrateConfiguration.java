package com.wcrates.plugin.crate;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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

    public CrateConfiguration(File file) {
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

        this.rewards = new ArrayList<>();

        // Parse the new rewards.placeholders structure
        if (config.contains("rewards.placeholders")) {
            ConfigurationSection placeholdersSection = config.getConfigurationSection("rewards.placeholders");
            if (placeholdersSection != null) {
                Set<String> keys = placeholdersSection.getKeys(false);
                for (String key : keys) {
                    ConfigurationSection rewardSection = placeholdersSection.getConfigurationSection(key);
                    if (rewardSection != null) {
                        rewards.add(new RewardRange(rewardSection));
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
        private final List<String> commands;
        private final List<String> messages;

        public RewardRange(ConfigurationSection section) {
            this.placeholder = section.getString("between", "");
            this.name = section.getString("name", "&7Reward");
            this.commands = section.getStringList("commands");
            this.messages = section.getStringList("messages");

            // Parse the 'between' placeholder to extract min and max values
            // Format: %wcrates_crate_MIN-MAX%
            int tempMin = 0;
            int tempMax = 100;

            if (!placeholder.isEmpty()) {
                // Pattern to match: %wcrates_crate_140-168%
                Pattern pattern = Pattern.compile("%wcrates_crate_(\\d+)-(\\d+)%");
                Matcher matcher = pattern.matcher(placeholder);

                if (matcher.find()) {
                    try {
                        tempMin = Integer.parseInt(matcher.group(1));
                        tempMax = Integer.parseInt(matcher.group(2));
                    } catch (NumberFormatException e) {
                        // Use defaults if parsing fails
                    }
                }
            }

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

        public List<String> getCommands() {
            return commands;
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}
