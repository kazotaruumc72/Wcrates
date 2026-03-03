package com.wcrates.plugin.crate;

import com.wcrates.plugin.WcratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Handles the animation of scrolling through placeholder values
 */
public class CrateAnimation {

    private final WcratesPlugin plugin;
    private final Player player;
    private final CrateConfiguration crate;
    private final Random random;

    public CrateAnimation(WcratesPlugin plugin, Player player, CrateConfiguration crate) {
        this.plugin = plugin;
        this.player = player;
        this.crate = crate;
        this.random = new Random();
    }

    /**
     * Start the crate animation
     */
    public void start() {
        // Calculate the final value where the animation will stop
        final int finalValue = random.nextInt(crate.getMaxValue() - crate.getMinValue() + 1) + crate.getMinValue();

        new BukkitRunnable() {
            private int currentTick = 0;
            private int currentValue = crate.getMinValue();

            @Override
            public void run() {
                // Check if animation is complete
                if (currentTick >= crate.getAnimationDuration()) {
                    // Animation is complete, give reward
                    giveReward(finalValue);
                    cancel();
                    return;
                }

                // Update value based on progress
                // Gradually slow down as we approach the end
                if (currentTick < crate.getAnimationDuration() * 0.7) {
                    // Fast scrolling phase
                    currentValue = random.nextInt(crate.getMaxValue() - crate.getMinValue() + 1) + crate.getMinValue();
                } else {
                    // Slow down and approach final value
                    double progress = (currentTick - crate.getAnimationDuration() * 0.7) / (crate.getAnimationDuration() * 0.3);
                    int range = (int) ((1 - progress) * (crate.getMaxValue() - crate.getMinValue()));
                    if (range < 5) range = 5;

                    int deviation = random.nextInt(range) - range / 2;
                    currentValue = Math.max(crate.getMinValue(), Math.min(crate.getMaxValue(), finalValue + deviation));
                }

                // Display current value to player
                displayValue(currentValue);

                currentTick += crate.getAnimationSpeed();
            }
        }.runTaskTimer(plugin, 0L, crate.getAnimationSpeed());
    }

    /**
     * Display the current value to the player using action bar or title
     */
    private void displayValue(int value) {
        String displayText = ChatColor.translateAlternateColorCodes('&',
                "&6&lCrate Opening... &e&l" + value);

        // Use action bar (newer Spigot versions)
        try {
            player.sendActionBar(displayText);
        } catch (NoSuchMethodError e) {
            // Fallback for older versions - use title with subtitle
            player.sendTitle("", displayText, 0, 10, 0);
        }
    }

    /**
     * Give the reward based on the final value
     */
    private void giveReward(int finalValue) {
        CrateConfiguration.RewardRange reward = crate.findRewardForValue(finalValue);

        if (reward == null) {
            player.sendMessage(ChatColor.RED + "No reward found for value " + finalValue);
            return;
        }

        // Display final value
        String finalMessage = ChatColor.translateAlternateColorCodes('&',
                "&6&lFinal Value: &e&l" + finalValue);
        player.sendTitle(finalMessage, ChatColor.translateAlternateColorCodes('&', reward.getName()), 10, 60, 20);

        // Send messages to player
        for (String message : reward.getMessages()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        // Execute commands
        for (String command : reward.getCommands()) {
            String processedCommand = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
        }
    }
}
