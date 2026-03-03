package com.wcrates.plugin.listener;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.gui.CrateMenuGUI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listener for block selection (right/left click) when in placement mode
 */
public class BlockSelectionListener implements Listener {

    private final WcratesPlugin plugin;

    public BlockSelectionListener(WcratesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CrateMenuGUI.CrateConfig config = CrateMenuGUI.getPlayerConfig(player.getUniqueId());

        // Check if player is in placement mode
        if (!config.isPlacementMode()) {
            return;
        }

        // Check if player clicked on a block (right or left click)
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true); // Prevent block breaking or placing

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                return;
            }

            // Transform the block to the configured type
            Material selectedType = config.getBlockType();
            clickedBlock.setType(selectedType);

            // Send success message
            player.sendMessage(plugin.getLanguageManager().getMessage("block_selection.success"));

            // Deactivate placement mode
            config.setPlacementMode(false);
            player.sendMessage(plugin.getLanguageManager().getMessage("block_selection.deactivated"));

            // Optionally, reopen the menu
            // CrateMenuGUI menu = new CrateMenuGUI(plugin, player);
            // menu.open();
        }
    }
}
