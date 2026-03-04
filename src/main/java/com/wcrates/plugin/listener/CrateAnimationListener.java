package com.wcrates.plugin.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Listener for the crate animation GUI
 * Prevents clicking during the animation
 */
public class CrateAnimationListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the inventory title contains a color code (crate animations use colored titles)
        String title = event.getView().getTitle();

        // If the title is colored, it's likely a crate animation GUI
        // We prevent all clicks in these GUIs
        if (title != null && (title.contains("§") || ChatColor.stripColor(title).length() < title.length())) {
            // Additional check: animation GUIs are always 27 slots (3 rows)
            if (event.getInventory().getSize() == 27) {
                event.setCancelled(true);
            }
        }
    }
}
