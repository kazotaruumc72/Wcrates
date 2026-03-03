package com.wcrates.plugin.gui;

import com.wcrates.plugin.WcratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GUI for selecting block types across Minecraft versions 1.16.5 to 1.21.11
 */
public class BlockSelectorGUI {

    private final WcratesPlugin plugin;
    private final Player player;
    private Inventory inventory;
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 45; // 5 rows for blocks
    private static final int INVENTORY_SIZE = 54; // 6 rows total

    // Comprehensive list of vanilla blocks from 1.16.5 to 1.21.11
    private static final List<String> ALL_BLOCKS = Arrays.asList(
        // 1.16.5 blocks - Common building blocks
        "STONE", "GRANITE", "POLISHED_GRANITE", "DIORITE", "POLISHED_DIORITE",
        "ANDESITE", "POLISHED_ANDESITE", "GRASS_BLOCK", "DIRT", "COARSE_DIRT",
        "PODZOL", "COBBLESTONE", "OAK_PLANKS", "SPRUCE_PLANKS", "BIRCH_PLANKS",
        "JUNGLE_PLANKS", "ACACIA_PLANKS", "DARK_OAK_PLANKS", "OAK_LOG", "SPRUCE_LOG",
        "BIRCH_LOG", "JUNGLE_LOG", "ACACIA_LOG", "DARK_OAK_LOG", "STRIPPED_OAK_LOG",
        "STRIPPED_SPRUCE_LOG", "STRIPPED_BIRCH_LOG", "STRIPPED_JUNGLE_LOG", "STRIPPED_ACACIA_LOG", "STRIPPED_DARK_OAK_LOG",
        "STRIPPED_OAK_WOOD", "STRIPPED_SPRUCE_WOOD", "STRIPPED_BIRCH_WOOD", "STRIPPED_JUNGLE_WOOD", "STRIPPED_ACACIA_WOOD",
        "STRIPPED_DARK_OAK_WOOD", "OAK_WOOD", "SPRUCE_WOOD", "BIRCH_WOOD", "JUNGLE_WOOD",
        "ACACIA_WOOD", "DARK_OAK_WOOD", "SAND", "RED_SAND", "GRAVEL",
        "GOLD_ORE", "IRON_ORE", "COAL_ORE", "NETHER_GOLD_ORE", "OAK_LEAVES",
        "SPRUCE_LEAVES", "BIRCH_LEAVES", "JUNGLE_LEAVES", "ACACIA_LEAVES", "DARK_OAK_LEAVES",
        "GLASS", "LAPIS_ORE", "LAPIS_BLOCK", "SANDSTONE", "CHISELED_SANDSTONE",
        "CUT_SANDSTONE", "WHITE_WOOL", "ORANGE_WOOL", "MAGENTA_WOOL", "LIGHT_BLUE_WOOL",
        "YELLOW_WOOL", "LIME_WOOL", "PINK_WOOL", "GRAY_WOOL", "LIGHT_GRAY_WOOL",
        "CYAN_WOOL", "PURPLE_WOOL", "BLUE_WOOL", "BROWN_WOOL", "GREEN_WOOL",
        "RED_WOOL", "BLACK_WOOL", "GOLD_BLOCK", "IRON_BLOCK", "BRICKS",
        "TNT", "BOOKSHELF", "MOSSY_COBBLESTONE", "OBSIDIAN", "TORCH",
        "SPAWNER", "OAK_STAIRS", "CHEST", "DIAMOND_ORE", "DIAMOND_BLOCK",
        "CRAFTING_TABLE", "FURNACE", "LADDER", "COBBLESTONE_STAIRS", "SNOW",
        "ICE", "SNOW_BLOCK", "CLAY", "JUKEBOX", "OAK_FENCE",
        "PUMPKIN", "NETHERRACK", "SOUL_SAND", "SOUL_SOIL", "BASALT",
        "POLISHED_BASALT", "GLOWSTONE", "CARVED_PUMPKIN", "JACK_O_LANTERN", "WHITE_STAINED_GLASS",
        "ORANGE_STAINED_GLASS", "MAGENTA_STAINED_GLASS", "LIGHT_BLUE_STAINED_GLASS", "YELLOW_STAINED_GLASS", "LIME_STAINED_GLASS",
        "PINK_STAINED_GLASS", "GRAY_STAINED_GLASS", "LIGHT_GRAY_STAINED_GLASS", "CYAN_STAINED_GLASS", "PURPLE_STAINED_GLASS",
        "BLUE_STAINED_GLASS", "BROWN_STAINED_GLASS", "GREEN_STAINED_GLASS", "RED_STAINED_GLASS", "BLACK_STAINED_GLASS",
        "STONE_BRICKS", "MOSSY_STONE_BRICKS", "CRACKED_STONE_BRICKS", "CHISELED_STONE_BRICKS", "MELON",
        "MYCELIUM", "NETHER_BRICKS", "NETHER_BRICK_FENCE", "NETHER_BRICK_STAIRS", "END_STONE",
        "END_STONE_BRICKS", "REDSTONE_LAMP", "EMERALD_ORE", "EMERALD_BLOCK", "SPRUCE_STAIRS",
        "BIRCH_STAIRS", "JUNGLE_STAIRS", "QUARTZ_BLOCK", "CHISELED_QUARTZ_BLOCK", "QUARTZ_PILLAR",
        "QUARTZ_STAIRS", "WHITE_TERRACOTTA", "ORANGE_TERRACOTTA", "MAGENTA_TERRACOTTA", "LIGHT_BLUE_TERRACOTTA",
        "YELLOW_TERRACOTTA", "LIME_TERRACOTTA", "PINK_TERRACOTTA", "GRAY_TERRACOTTA", "LIGHT_GRAY_TERRACOTTA",
        "CYAN_TERRACOTTA", "PURPLE_TERRACOTTA", "BLUE_TERRACOTTA", "BROWN_TERRACOTTA", "GREEN_TERRACOTTA",
        "RED_TERRACOTTA", "BLACK_TERRACOTTA", "SLIME_BLOCK", "PRISMARINE", "PRISMARINE_BRICKS",
        "DARK_PRISMARINE", "PRISMARINE_STAIRS", "PRISMARINE_BRICK_STAIRS", "DARK_PRISMARINE_STAIRS", "SEA_LANTERN",
        "HAY_BLOCK", "WHITE_CARPET", "ORANGE_CARPET", "MAGENTA_CARPET", "LIGHT_BLUE_CARPET",
        "YELLOW_CARPET", "LIME_CARPET", "PINK_CARPET", "GRAY_CARPET", "LIGHT_GRAY_CARPET",
        "CYAN_CARPET", "PURPLE_CARPET", "BLUE_CARPET", "BROWN_CARPET", "GREEN_CARPET",
        "RED_CARPET", "BLACK_CARPET", "TERRACOTTA", "COAL_BLOCK", "PACKED_ICE",
        "RED_SANDSTONE", "CHISELED_RED_SANDSTONE", "CUT_RED_SANDSTONE", "RED_SANDSTONE_STAIRS", "PURPUR_BLOCK",
        "PURPUR_PILLAR", "PURPUR_STAIRS", "END_ROD", "END_STONE_BRICK_STAIRS", "MAGMA_BLOCK",
        "NETHER_WART_BLOCK", "RED_NETHER_BRICKS", "BONE_BLOCK", "WHITE_GLAZED_TERRACOTTA", "ORANGE_GLAZED_TERRACOTTA",
        "MAGENTA_GLAZED_TERRACOTTA", "LIGHT_BLUE_GLAZED_TERRACOTTA", "YELLOW_GLAZED_TERRACOTTA", "LIME_GLAZED_TERRACOTTA", "PINK_GLAZED_TERRACOTTA",
        "GRAY_GLAZED_TERRACOTTA", "LIGHT_GRAY_GLAZED_TERRACOTTA", "CYAN_GLAZED_TERRACOTTA", "PURPLE_GLAZED_TERRACOTTA", "BLUE_GLAZED_TERRACOTTA",
        "BROWN_GLAZED_TERRACOTTA", "GREEN_GLAZED_TERRACOTTA", "RED_GLAZED_TERRACOTTA", "BLACK_GLAZED_TERRACOTTA", "WHITE_CONCRETE",
        "ORANGE_CONCRETE", "MAGENTA_CONCRETE", "LIGHT_BLUE_CONCRETE", "YELLOW_CONCRETE", "LIME_CONCRETE",
        "PINK_CONCRETE", "GRAY_CONCRETE", "LIGHT_GRAY_CONCRETE", "CYAN_CONCRETE", "PURPLE_CONCRETE",
        "BLUE_CONCRETE", "BROWN_CONCRETE", "GREEN_CONCRETE", "RED_CONCRETE", "BLACK_CONCRETE",

        // 1.16 Nether Update
        "CRIMSON_PLANKS", "WARPED_PLANKS", "CRIMSON_STEM", "WARPED_STEM", "STRIPPED_CRIMSON_STEM",
        "STRIPPED_WARPED_STEM", "CRIMSON_HYPHAE", "WARPED_HYPHAE", "STRIPPED_CRIMSON_HYPHAE", "STRIPPED_WARPED_HYPHAE",
        "CRIMSON_NYLIUM", "WARPED_NYLIUM", "CRIMSON_FUNGUS", "WARPED_FUNGUS", "SHROOMLIGHT",
        "WARPED_WART_BLOCK", "CRIMSON_ROOTS", "WARPED_ROOTS", "NETHER_SPROUTS", "CRIMSON_STAIRS",
        "WARPED_STAIRS", "BLACKSTONE", "POLISHED_BLACKSTONE", "CHISELED_POLISHED_BLACKSTONE", "POLISHED_BLACKSTONE_BRICKS",
        "CRACKED_POLISHED_BLACKSTONE_BRICKS", "BLACKSTONE_STAIRS", "POLISHED_BLACKSTONE_STAIRS", "POLISHED_BLACKSTONE_BRICK_STAIRS", "GILDED_BLACKSTONE",
        "CRYING_OBSIDIAN", "TARGET", "ANCIENT_DEBRIS", "NETHERITE_BLOCK", "LODESTONE",
        "CHAIN", "SOUL_TORCH", "SOUL_CAMPFIRE", "RESPAWN_ANCHOR", "QUARTZ_BRICKS",

        // 1.17 Caves & Cliffs Part 1
        "COPPER_ORE", "DEEPSLATE_COPPER_ORE", "COPPER_BLOCK", "EXPOSED_COPPER", "WEATHERED_COPPER",
        "OXIDIZED_COPPER", "CUT_COPPER", "EXPOSED_CUT_COPPER", "WEATHERED_CUT_COPPER", "OXIDIZED_CUT_COPPER",
        "CUT_COPPER_STAIRS", "EXPOSED_CUT_COPPER_STAIRS", "WEATHERED_CUT_COPPER_STAIRS", "OXIDIZED_CUT_COPPER_STAIRS", "WAXED_COPPER_BLOCK",
        "WAXED_EXPOSED_COPPER", "WAXED_WEATHERED_COPPER", "WAXED_OXIDIZED_COPPER", "WAXED_CUT_COPPER", "WAXED_EXPOSED_CUT_COPPER",
        "WAXED_WEATHERED_CUT_COPPER", "WAXED_OXIDIZED_CUT_COPPER", "AMETHYST_BLOCK", "BUDDING_AMETHYST", "CALCITE",
        "TUFF", "DRIPSTONE_BLOCK", "ROOTED_DIRT", "MOSS_BLOCK", "MOSS_CARPET",
        "AZALEA_LEAVES", "FLOWERING_AZALEA_LEAVES", "SPORE_BLOSSOM", "CAVE_VINES", "GLOW_LICHEN",
        "DEEPSLATE", "COBBLED_DEEPSLATE", "POLISHED_DEEPSLATE", "DEEPSLATE_BRICKS", "CRACKED_DEEPSLATE_BRICKS",
        "DEEPSLATE_TILES", "CRACKED_DEEPSLATE_TILES", "CHISELED_DEEPSLATE", "DEEPSLATE_COAL_ORE", "DEEPSLATE_IRON_ORE",
        "DEEPSLATE_GOLD_ORE", "DEEPSLATE_DIAMOND_ORE", "DEEPSLATE_LAPIS_ORE", "DEEPSLATE_REDSTONE_ORE", "DEEPSLATE_EMERALD_ORE",
        "SMOOTH_BASALT", "RAW_IRON_BLOCK", "RAW_COPPER_BLOCK", "RAW_GOLD_BLOCK", "TINTED_GLASS",

        // 1.18 Caves & Cliffs Part 2
        "POWDER_SNOW", "SCULK_SENSOR",

        // 1.19 Wild Update
        "SCULK", "SCULK_VEIN", "SCULK_CATALYST", "SCULK_SHRIEKER", "REINFORCED_DEEPSLATE",
        "MUD", "PACKED_MUD", "MUD_BRICKS", "MANGROVE_PLANKS", "MANGROVE_LOG",
        "STRIPPED_MANGROVE_LOG", "MANGROVE_WOOD", "STRIPPED_MANGROVE_WOOD", "MANGROVE_LEAVES", "MANGROVE_ROOTS",
        "MUDDY_MANGROVE_ROOTS", "MANGROVE_STAIRS", "OCHRE_FROGLIGHT", "VERDANT_FROGLIGHT", "PEARLESCENT_FROGLIGHT",

        // 1.20 Trails & Tales
        "BAMBOO_PLANKS", "BAMBOO_MOSAIC", "BAMBOO_BLOCK", "STRIPPED_BAMBOO_BLOCK", "CHERRY_PLANKS",
        "CHERRY_LOG", "STRIPPED_CHERRY_LOG", "CHERRY_WOOD", "STRIPPED_CHERRY_WOOD", "CHERRY_LEAVES",
        "CHERRY_STAIRS", "PINK_PETALS", "TORCHFLOWER", "SUSPICIOUS_SAND", "SUSPICIOUS_GRAVEL",
        "CALIBRATED_SCULK_SENSOR", "CHISELED_BOOKSHELF",

        // 1.21 Tricky Trials
        "CRAFTER", "COPPER_GRATE", "EXPOSED_COPPER_GRATE", "WEATHERED_COPPER_GRATE", "OXIDIZED_COPPER_GRATE",
        "WAXED_COPPER_GRATE", "WAXED_EXPOSED_COPPER_GRATE", "WAXED_WEATHERED_COPPER_GRATE", "WAXED_OXIDIZED_COPPER_GRATE", "COPPER_BULB",
        "EXPOSED_COPPER_BULB", "WEATHERED_COPPER_BULB", "OXIDIZED_COPPER_BULB", "WAXED_COPPER_BULB", "WAXED_EXPOSED_COPPER_BULB",
        "WAXED_WEATHERED_COPPER_BULB", "WAXED_OXIDIZED_COPPER_BULB", "COPPER_DOOR", "EXPOSED_COPPER_DOOR", "WEATHERED_COPPER_DOOR",
        "OXIDIZED_COPPER_DOOR", "WAXED_COPPER_DOOR", "WAXED_EXPOSED_COPPER_DOOR", "WAXED_WEATHERED_COPPER_DOOR", "WAXED_OXIDIZED_COPPER_DOOR",
        "CHISELED_COPPER", "EXPOSED_CHISELED_COPPER", "WEATHERED_CHISELED_COPPER", "OXIDIZED_CHISELED_COPPER", "WAXED_CHISELED_COPPER",
        "WAXED_EXPOSED_CHISELED_COPPER", "WAXED_WEATHERED_CHISELED_COPPER", "WAXED_OXIDIZED_CHISELED_COPPER", "TRIAL_SPAWNER", "VAULT",
        "HEAVY_CORE", "TUFF_BRICKS", "CHISELED_TUFF", "POLISHED_TUFF", "TUFF_STAIRS",
        "CHISELED_TUFF_BRICKS", "POLISHED_TUFF_STAIRS"
    );

    private List<Material> availableBlocks;

    public BlockSelectorGUI(WcratesPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.availableBlocks = getAvailableBlocks();
        createInventory();
    }

    /**
     * Filter blocks to only include ones available in the current server version
     */
    private List<Material> getAvailableBlocks() {
        List<Material> blocks = new ArrayList<>();

        for (String blockName : ALL_BLOCKS) {
            try {
                Material material = Material.valueOf(blockName);
                // Only include blocks (not items)
                if (material.isBlock()) {
                    blocks.add(material);
                }
            } catch (IllegalArgumentException e) {
                // Material doesn't exist in this version, skip it
            }
        }

        return blocks;
    }

    /**
     * Create the inventory with current page of blocks
     */
    private void createInventory() {
        int totalPages = (int) Math.ceil((double) availableBlocks.size() / ITEMS_PER_PAGE);
        String title = "§6Block Selector §8(Page " + (currentPage + 1) + "/" + totalPages + ")";

        inventory = Bukkit.createInventory(null, INVENTORY_SIZE, title);

        // Calculate start and end indices for current page
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, availableBlocks.size());

        // Add blocks to the inventory
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            Material material = availableBlocks.get(i);
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                // Format the material name nicely
                String name = formatMaterialName(material.name());
                meta.setDisplayName("§e" + name);
                meta.setLore(Arrays.asList("§7Click to select this block"));
                item.setItemMeta(meta);
            }
            inventory.setItem(slot++, item);
        }

        // Add navigation buttons in the bottom row
        if (currentPage > 0) {
            // Previous page button (slot 48)
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName("§aPrevious Page");
                prevButton.setItemMeta(prevMeta);
            }
            inventory.setItem(48, prevButton);
        }

        if (currentPage < totalPages - 1) {
            // Next page button (slot 50)
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName("§aNext Page");
                nextButton.setItemMeta(nextMeta);
            }
            inventory.setItem(50, nextButton);
        }

        // Close button (slot 49)
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName("§cClose");
            closeMeta.setLore(Arrays.asList("§7Return to crate menu"));
            closeButton.setItemMeta(closeMeta);
        }
        inventory.setItem(49, closeButton);
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

    /**
     * Open the inventory for the player
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
     * Navigate to next page
     */
    public void nextPage() {
        int totalPages = (int) Math.ceil((double) availableBlocks.size() / ITEMS_PER_PAGE);
        if (currentPage < totalPages - 1) {
            currentPage++;
            createInventory();
            player.openInventory(inventory);
        }
    }

    /**
     * Navigate to previous page
     */
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            createInventory();
            player.openInventory(inventory);
        }
    }

    /**
     * Get the material at a specific slot
     */
    public Material getBlockAtSlot(int slot) {
        int index = (currentPage * ITEMS_PER_PAGE) + slot;
        if (index >= 0 && index < availableBlocks.size()) {
            return availableBlocks.get(index);
        }
        return null;
    }
}
