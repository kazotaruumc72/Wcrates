package com.wcrates.plugin.listener;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.gui.BlockSelectorGUI;
import com.wcrates.plugin.gui.CrateMenuGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Listener for menu click events
 */
public class MenuClickListener implements Listener {

    private final WcratesPlugin plugin;

    public MenuClickListener(WcratesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the clicked inventory is our menu
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String menuTitle = plugin.getLanguageManager().getMessage("menu.title");

        // Check if this is our menu
        if (event.getView().getTitle().equals(menuTitle)) {
            event.setCancelled(true); // Prevent taking items

            int slot = event.getRawSlot();

            // Handle different slot clicks
            switch (slot) {
                case 10:
                    // Block type selector - for now, just cycle through common blocks
                    // In a full implementation, this could open a block selection sub-menu
                    handleBlockTypeSelection(player);
                    break;

                case 12:
                    // Crate name selector
                    // In a full implementation, this could open a chat-based input
                    handleCrateNameSelection(player);
                    break;

                case 13:
                    // Display duration selector
                    handleDisplayDurationSelection(player);
                    break;

                case 14:
                    // Crate ID selector
                    // In a full implementation, this could open a chat-based input
                    handleCrateIdSelection(player);
                    break;

                case 16:
                    // Block placement mode
                    handleBlockPlacementMode(player);
                    break;

                case 21:
                    // Key selector - placeholder for later
                    player.sendMessage("§7Key selector - Coming soon!");
                    break;

                case 22:
                    // Create crate button
                    handleCreateCrate(player);
                    break;

                case 23:
                    // Close menu
                    player.closeInventory();
                    CrateMenuGUI.removePlayerConfig(player.getUniqueId());
                    break;
            }
        }
    }

    /**
     * Handle block type selection (slot 10)
     */
    private void handleBlockTypeSelection(Player player) {
        // Open the block selector GUI
        BlockSelectorGUI blockSelector = new BlockSelectorGUI(plugin, player);

        // Store the GUI instance in player metadata so the listener can access it
        player.setMetadata("wcrates_block_selector", new FixedMetadataValue(plugin, blockSelector));

        // Close current menu and open block selector
        player.closeInventory();
        blockSelector.open();
    }

    /**
     * Handle crate name selection (slot 12)
     */
    private void handleCrateNameSelection(Player player) {
        player.sendMessage("§aCrate name selection - Enter name in chat");
        player.sendMessage("§7This feature allows you to set the crate name");
    }

    /**
     * Handle display duration selection (slot 13)
     */
    private void handleDisplayDurationSelection(Player player) {
        CrateMenuGUI.CrateConfig config = CrateMenuGUI.getPlayerConfig(player.getUniqueId());

        // Cycle through common durations: 1, 2, 3, 5, 10 seconds
        int currentDuration = config.getDisplayDuration();
        int newDuration;

        switch (currentDuration) {
            case 1:
                newDuration = 2;
                break;
            case 2:
                newDuration = 3;
                break;
            case 3:
                newDuration = 5;
                break;
            case 5:
                newDuration = 10;
                break;
            default:
                newDuration = 1;
                break;
        }

        config.setDisplayDuration(newDuration);
        player.sendMessage("§6Display duration set to: §e" + newDuration + " seconds");
        player.sendMessage("§7This controls how long placeholder values are displayed");

        // Reopen the menu to update the display
        player.closeInventory();
        CrateMenuGUI gui = new CrateMenuGUI(plugin, player);
        gui.open();
    }

    /**
     * Handle crate ID selection (slot 14)
     */
    private void handleCrateIdSelection(Player player) {
        CrateMenuGUI.CrateConfig config = CrateMenuGUI.getPlayerConfig(player.getUniqueId());

        // For now, use the example crate ID
        // In a full implementation, this could open a selection menu
        config.setCrateId("example_crate");

        player.sendMessage("§bCrate ID set to: example_crate");
        player.sendMessage("§7This crate will use the configuration from crates/example_crate.yml");
    }

    /**
     * Handle block placement mode (slot 16)
     */
    private void handleBlockPlacementMode(Player player) {
        CrateMenuGUI.CrateConfig config = CrateMenuGUI.getPlayerConfig(player.getUniqueId());

        // Toggle placement mode
        config.setPlacementMode(true);

        // Close the menu temporarily
        player.closeInventory();

        // Send title and subtitle
        String title = plugin.getLanguageManager().getMessage("block_selection.title");
        String subtitle = plugin.getLanguageManager().getMessage("block_selection.subtitle");

        player.sendTitle(title, subtitle, 10, 70, 20);
        player.sendMessage(plugin.getLanguageManager().getMessage("block_selection.activated"));
    }

    /**
     * Handle create crate button (slot 22)
     */
    private void handleCreateCrate(Player player) {
        CrateMenuGUI.CrateConfig config = CrateMenuGUI.getPlayerConfig(player.getUniqueId());

        String crateId = config.getCrateId();
        String crateName = config.getCrateName();
        org.bukkit.Material blockType = config.getBlockType();
        int displayDuration = config.getDisplayDuration();

        // Create the crate files
        boolean success = plugin.getCrateFileManager().createCrateFiles(crateId, crateName, blockType, displayDuration);

        if (success) {
            player.sendMessage(plugin.getLanguageManager().getMessage("crate.created_success"));
            player.sendMessage("§7Created files for crate: §b" + crateId);
            player.sendMessage("§7- crates/" + crateId + ".yml");
            player.sendMessage("§7- placeholders/" + crateId + ".yml");

            // Reload crates to load the newly created crate
            plugin.getCrateManager().reload();
        } else {
            player.sendMessage(plugin.getLanguageManager().getMessage("crate.created_error"));
        }
    }
}
