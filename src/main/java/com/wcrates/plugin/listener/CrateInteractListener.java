package com.wcrates.plugin.listener;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.crate.CrateAnimation;
import com.wcrates.plugin.crate.CrateConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listener for player interactions with crate blocks
 */
public class CrateInteractListener implements Listener {

    private final WcratesPlugin plugin;

    public CrateInteractListener(WcratesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        // Check if the block has crate metadata
        if (!block.hasMetadata("wcrates_id")) {
            return;
        }

        Player player = event.getPlayer();
        String crateId = block.getMetadata("wcrates_id").get(0).asString();

        // Get the crate configuration
        CrateConfiguration crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) {
            player.sendMessage(ChatColor.RED + "This crate is not configured properly!");
            return;
        }

        // Handle sneak+left-click for admin removal in creative mode
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.isSneaking() && player.getGameMode() == GameMode.CREATIVE) {
                // Check if player has admin permission
                if (player.hasPermission("wcrates.admin") || player.isOp()) {
                    event.setCancelled(true);

                    // Remove the coordinate from the crate configuration
                    crate.removeCoordinate(block.getLocation());

                    // Remove metadata
                    block.removeMetadata("wcrates_id", plugin);

                    // Reset the block to air
                    block.setType(Material.AIR);

                    // Send success message
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getLanguageManager().getMessage("crate.removed")));
                    return;
                }
            }
            // For non-sneaking left-clicks, don't do anything
            return;
        }

        // Handle right-click to open crate
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);

            // Check if player has permission (optional)
            // if (!player.hasPermission("wcrates.use." + crateId)) {
            //     player.sendMessage(ChatColor.RED + "You don't have permission to use this crate!");
            //     return;
            // }

            // Start the crate animation
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getLanguageManager().getMessage("crate.opening")));

            CrateAnimation animation = new CrateAnimation(plugin, player, crate);
            animation.start();
        }
    }
}
