package com.wcrates.plugin.listener;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.gui.BlockSelectorGUI;
import com.wcrates.plugin.gui.CrateMenuGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Listener for block selector GUI clicks
 */
public class BlockSelectorListener implements Listener {

    private final WcratesPlugin plugin;

    public BlockSelectorListener(WcratesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the clicked inventory is a block selector
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String viewTitle = event.getView().getTitle();

        // Check if this is a block selector menu
        if (viewTitle.startsWith("§6Block Selector")) {
            event.setCancelled(true); // Prevent taking items

            int slot = event.getRawSlot();

            // Get the BlockSelectorGUI instance (stored in metadata)
            if (!player.hasMetadata("wcrates_block_selector")) {
                return;
            }

            BlockSelectorGUI gui = (BlockSelectorGUI) player.getMetadata("wcrates_block_selector").get(0).value();

            // Handle navigation buttons in bottom row
            if (slot == 48) {
                // Previous page
                gui.previousPage();
                return;
            } else if (slot == 50) {
                // Next page
                gui.nextPage();
                return;
            } else if (slot == 49) {
                // Close and return to crate menu
                player.removeMetadata("wcrates_block_selector", plugin);
                player.closeInventory();
                new CrateMenuGUI(plugin, player).open();
                return;
            }

            // Check if a block was clicked (slots 0-44)
            if (slot >= 0 && slot < 45) {
                Material selectedBlock = gui.getBlockAtSlot(slot);
                if (selectedBlock != null) {
                    // Update the player's crate config
                    CrateMenuGUI.CrateConfig config = CrateMenuGUI.getPlayerConfig(player.getUniqueId());
                    config.setBlockType(selectedBlock);

                    // Notify the player
                    player.sendMessage("§aBlock type set to: §e" + formatMaterialName(selectedBlock.name()));

                    // Clean up metadata
                    player.removeMetadata("wcrates_block_selector", plugin);

                    // Close the block selector and reopen the crate menu
                    player.closeInventory();
                    new CrateMenuGUI(plugin, player).open();
                }
            }
        }
    }

    /**
     * Format material name to be more readable
     */
    private String formatMaterialName(String name) {
        String[] parts = name.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(part.charAt(0)));
            result.append(part.substring(1));
        }
        return result.toString();
    }
}
