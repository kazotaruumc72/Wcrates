package com.wcrates.plugin.listener;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.crate.CrateAnimation;
import com.wcrates.plugin.crate.CrateConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

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
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        // Check if the block has crate metadata
        if (!block.hasMetadata("wcrates_id")) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        String crateId = block.getMetadata("wcrates_id").get(0).asString();

        // Get the crate configuration
        CrateConfiguration crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) {
            player.sendMessage(ChatColor.RED + "This crate is not configured properly!");
            return;
        }

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
