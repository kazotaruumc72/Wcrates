package com.wcrates.plugin.crate;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        this.minValue = config.getInt("crate.placeholders.min", 1);
        this.maxValue = config.getInt("crate.placeholders.max", 200);
        this.animationDuration = config.getInt("crate.placeholders.animation.duration", 100);
        this.animationSpeed = config.getInt("crate.placeholders.animation.speed", 2);

        this.rewards = new ArrayList<>();

        if (config.contains("crate.rewards")) {
            List<?> rewardsList = config.getList("crate.rewards");
            if (rewardsList != null) {
                for (Object obj : rewardsList) {
                    if (obj instanceof ConfigurationSection) {
                        ConfigurationSection section = (ConfigurationSection) obj;
                        rewards.add(new RewardRange(section));
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
        private final List<String> commands;
        private final List<String> messages;

        public RewardRange(ConfigurationSection section) {
            this.min = section.getInt("range.min", 0);
            this.max = section.getInt("range.max", 100);
            this.name = section.getString("name", "&7Reward");
            this.commands = section.getStringList("commands");
            this.messages = section.getStringList("messages");
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

        public List<String> getCommands() {
            return commands;
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}
