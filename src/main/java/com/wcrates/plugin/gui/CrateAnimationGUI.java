package com.wcrates.plugin.gui;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.crate.CrateConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * GUI for displaying the crate animation with a scrolling menu effect
 */
public class CrateAnimationGUI {

    private final WcratesPlugin plugin;
    private final Player player;
    private final CrateConfiguration crate;
    private final Inventory inventory;
    private final Random random;
    private final List<Integer> shuffledValues;
    private int currentIndex;
    private BukkitRunnable animationTask;

    public CrateAnimationGUI(WcratesPlugin plugin, Player player, CrateConfiguration crate) {
        this.plugin = plugin;
        this.player = player;
        this.crate = crate;
        this.random = new Random();

        // Create inventory with crate name as title
        String title = ChatColor.translateAlternateColorCodes('&', crate.getName());
        this.inventory = Bukkit.createInventory(null, 27, title);

        // Generate shuffled list of values from min to max
        this.shuffledValues = new ArrayList<>();
        for (int i = crate.getMinValue(); i <= crate.getMaxValue(); i++) {
            shuffledValues.add(i);
        }
        Collections.shuffle(shuffledValues);

        this.currentIndex = 0;
    }

    /**
     * Open the GUI and start the animation
     */
    public void open() {
        player.openInventory(inventory);
        startAnimation();
    }

    /**
     * Start the scrolling animation
     */
    private void startAnimation() {
        // Calculate the final value where the animation will stop
        final int finalValue = random.nextInt(crate.getMaxValue() - crate.getMinValue() + 1) + crate.getMinValue();
        final int totalTicks = crate.getAnimationDuration();

        animationTask = new BukkitRunnable() {
            private int currentTick = 0;
            private long lastUpdateSpeed = crate.getAnimationSpeed();

            @Override
            public void run() {
                // Check if animation is complete
                if (currentTick >= totalTicks) {
                    // Show final result for 3 seconds (60 ticks)
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            giveReward(finalValue);
                            player.closeInventory();
                        }
                    }.runTaskLater(plugin, 60L); // 3 seconds pause
                    cancel();
                    return;
                }

                // Calculate progress (0.0 to 1.0)
                double progress = (double) currentTick / totalTicks;

                // Determine current value based on progress
                int displayValue;
                if (progress < 0.7) {
                    // Fast scrolling phase - use shuffled values
                    displayValue = shuffledValues.get(currentIndex % shuffledValues.size());
                    currentIndex++;
                } else {
                    // Slow down and approach final value
                    double slowdownProgress = (progress - 0.7) / 0.3;
                    int range = (int) ((1 - slowdownProgress) * (crate.getMaxValue() - crate.getMinValue()));
                    if (range < 5) range = 5;

                    int deviation = random.nextInt(range) - range / 2;
                    displayValue = Math.max(crate.getMinValue(), Math.min(crate.getMaxValue(), finalValue + deviation));
                }

                // Update the GUI with current value and rewards
                updateGUI(displayValue, finalValue, progress);

                // Progressive slowdown - increase delay between updates
                if (progress >= 0.7) {
                    double slowdownFactor = (progress - 0.7) / 0.3;
                    lastUpdateSpeed = (long) (crate.getAnimationSpeed() * (1 + slowdownFactor * 4));

                    // Cancel current task and reschedule with new speed
                    currentTick += crate.getAnimationSpeed();
                    cancel();
                    animationTask = this;
                    runTaskLater(plugin, lastUpdateSpeed);
                } else {
                    currentTick += crate.getAnimationSpeed();
                }
            }
        };

        animationTask.runTaskTimer(plugin, 0L, crate.getAnimationSpeed());
    }

    /**
     * Update the GUI to display current scrolling state
     */
    private void updateGUI(int currentValue, int finalValue, double progress) {
        inventory.clear();

        // Find the reward for current value
        CrateConfiguration.RewardRange currentReward = crate.findRewardForValue(currentValue);

        // Display current value in center slot (slot 13)
        if (currentReward != null) {
            ItemStack centerItem = createRewardDisplay(currentReward, currentValue, true);
            inventory.setItem(13, centerItem);
        }

        // Display previous and next values for scrolling effect
        // Slot 4: Previous reward
        int prevValue = Math.max(crate.getMinValue(), currentValue - random.nextInt(20) - 1);
        CrateConfiguration.RewardRange prevReward = crate.findRewardForValue(prevValue);
        if (prevReward != null) {
            inventory.setItem(4, createRewardDisplay(prevReward, prevValue, false));
        }

        // Slot 22: Next reward
        int nextValue = Math.min(crate.getMaxValue(), currentValue + random.nextInt(20) + 1);
        CrateConfiguration.RewardRange nextReward = crate.findRewardForValue(nextValue);
        if (nextReward != null) {
            inventory.setItem(22, createRewardDisplay(nextReward, nextValue, false));
        }

        // Add glass panes for decoration
        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glassPane.setItemMeta(glassMeta);
        }

        // Fill border slots
        int[] borderSlots = {0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 23, 24, 25, 26};
        for (int slot : borderSlots) {
            inventory.setItem(slot, glassPane);
        }

        // Add progress indicator in slot 26
        if (progress >= 0.7) {
            ItemStack indicator = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta indicatorMeta = indicator.getItemMeta();
            if (indicatorMeta != null) {
                indicatorMeta.setDisplayName(ChatColor.GREEN + "Slowing down...");
                indicator.setItemMeta(indicatorMeta);
            }
            inventory.setItem(26, indicator);
        }
    }

    /**
     * Create an ItemStack to display a reward
     */
    private ItemStack createRewardDisplay(CrateConfiguration.RewardRange reward, int value, boolean highlight) {
        Material displayMaterial = highlight ? Material.CHEST : Material.BARREL;
        ItemStack item = new ItemStack(displayMaterial);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Display reward name
            String displayName = ChatColor.translateAlternateColorCodes('&', reward.getName());
            if (highlight) {
                displayName = ChatColor.BOLD + "" + displayName;
            }
            meta.setDisplayName(displayName);

            // Add lore with value, placeholder, and Nexo glyph
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Value: " + ChatColor.YELLOW + value);
            lore.add(ChatColor.GRAY + "Range: " + ChatColor.AQUA + reward.getMin() + "-" + reward.getMax());

            // Add Nexo glyph placeholder
            String nexoGlyph = reward.getNexoGlyph();
            if (!nexoGlyph.isEmpty()) {
                lore.add(ChatColor.GRAY + "Glyph: " + ChatColor.LIGHT_PURPLE + nexoGlyph);
            }

            if (highlight) {
                lore.add("");
                lore.add(ChatColor.GOLD + ">>> Current <<<");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
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

        // Display final value with title
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

    /**
     * Get the inventory for this GUI (used by listener to identify this GUI)
     */
    public Inventory getInventory() {
        return inventory;
    }
}
