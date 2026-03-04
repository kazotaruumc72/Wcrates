package com.wcrates.plugin.gui;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * GUI for the crate creation menu (3 rows)
 */
public class CrateMenuGUI {

    private final WcratesPlugin plugin;
    private final Player player;
    private final Inventory inventory;
    private final LanguageManager lang;

    // Storage for player's crate configuration
    private static final Map<UUID, CrateConfig> playerConfigs = new HashMap<>();

    public CrateMenuGUI(WcratesPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.lang = plugin.getLanguageManager();

        // Create 3-row inventory (27 slots)
        this.inventory = Bukkit.createInventory(null, 27, lang.getMessage("menu.title"));

        setupMenu();
    }

    /**
     * Set up the menu items in their respective slots
     */
    private void setupMenu() {
        // Get player's current config
        CrateConfig config = getPlayerConfig(player.getUniqueId());

        // Slot 4: Crate result display
        inventory.setItem(4, createMenuItem(Material.CHEST,
            lang.getMessage("menu.result.name"),
            lang.getMessageList("menu.result.lore")));

        // Slot 10: Block type selector - show the currently selected block
        Material selectedBlock = config.getBlockType();
        inventory.setItem(10, createMenuItem(selectedBlock,
            lang.getMessage("menu.block_selector.name"),
            lang.getMessageList("menu.block_selector.lore")));

        // Slot 12: Crate name selector
        inventory.setItem(12, createMenuItem(Material.NAME_TAG,
            lang.getMessage("menu.crate_name.name"),
            lang.getMessageList("menu.crate_name.lore")));

        // Slot 13: Display duration selector
        inventory.setItem(13, createMenuItem(Material.CLOCK,
            lang.getMessage("menu.display_duration.name"),
            lang.getMessageList("menu.display_duration.lore")));

        // Slot 14: Crate ID selector
        inventory.setItem(14, createMenuItem(Material.NAME_TAG,
            lang.getMessage("menu.crate_id.name"),
            lang.getMessageList("menu.crate_id.lore")));

        // Slot 16: Block placement mode
        inventory.setItem(16, createMenuItem(Material.DIAMOND_PICKAXE,
            lang.getMessage("menu.block_placement.name"),
            lang.getMessageList("menu.block_placement.lore")));

        // Slot 21: Key selector (placeholder for later)
        inventory.setItem(21, createMenuItem(Material.TRIPWIRE_HOOK,
            lang.getMessage("menu.key_selector.name"),
            lang.getMessageList("menu.key_selector.lore")));

        // Slot 22: Create crate button
        inventory.setItem(22, createMenuItem(Material.EMERALD,
            lang.getMessage("menu.create_crate.name"),
            lang.getMessageList("menu.create_crate.lore")));

        // Slot 23: Close button
        inventory.setItem(23, createMenuItem(Material.BARRIER,
            lang.getMessage("menu.close.name"),
            lang.getMessageList("menu.close.lore")));
    }

    /**
     * Create a menu item with name and lore
     */
    private ItemStack createMenuItem(Material material, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Open the menu for the player
     */
    public void open() {
        player.openInventory(inventory);
    }

    /**
     * Get the inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get or create crate config for a player
     */
    public static CrateConfig getPlayerConfig(UUID playerId) {
        return playerConfigs.computeIfAbsent(playerId, k -> new CrateConfig());
    }

    /**
     * Remove player config
     */
    public static void removePlayerConfig(UUID playerId) {
        playerConfigs.remove(playerId);
    }

    /**
     * Class to store crate configuration for a player
     */
    public static class CrateConfig {
        private Material blockType = Material.STONE;
        private String crateName = "Default Crate";
        private String crateId = "default_crate";
        private boolean placementMode = false;
        private int displayDuration = 3; // Default 3 seconds

        public Material getBlockType() {
            return blockType;
        }

        public void setBlockType(Material blockType) {
            this.blockType = blockType;
        }

        public String getCrateName() {
            return crateName;
        }

        public void setCrateName(String crateName) {
            this.crateName = crateName;
        }

        public String getCrateId() {
            return crateId;
        }

        public void setCrateId(String crateId) {
            this.crateId = crateId;
        }

        public boolean isPlacementMode() {
            return placementMode;
        }

        public void setPlacementMode(boolean placementMode) {
            this.placementMode = placementMode;
        }

        public int getDisplayDuration() {
            return displayDuration;
        }

        public void setDisplayDuration(int displayDuration) {
            this.displayDuration = displayDuration;
        }
    }
}
