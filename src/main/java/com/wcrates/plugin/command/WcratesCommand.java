package com.wcrates.plugin.command;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.crate.CrateConfiguration;
import com.wcrates.plugin.gui.CrateMenuGUI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Command handler for /wcrates
 */
public class WcratesCommand implements CommandExecutor {

    private final WcratesPlugin plugin;

    public WcratesCommand(WcratesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("wcrates.admin")) {
            player.sendMessage(plugin.getLanguageManager().getMessage("messages.no_permission"));
            return true;
        }

        // Handle subcommands
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("delete")) {
                return handleDeleteCommand(player, args);
            }
        }

        // Open the crate creation menu
        CrateMenuGUI menu = new CrateMenuGUI(plugin, player);
        menu.open();

        return true;
    }

    /**
     * Handle the /wcrate delete <crateId> command
     */
    private boolean handleDeleteCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /wcrate delete <crateId>");
            return true;
        }

        String crateId = args[1];
        CrateConfiguration crate = plugin.getCrateManager().getCrate(crateId);

        if (crate == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getLanguageManager().getMessage("crate.delete.not_found")
                    .replace("%crate%", crateId)));
            return true;
        }

        // Remove all crate blocks in the world
        int removedBlocks = 0;
        for (String coordString : crate.getCoordinates()) {
            Location location = CrateConfiguration.stringToLocation(coordString);
            if (location != null && location.getWorld() != null) {
                Block block = location.getBlock();
                // Remove metadata
                block.removeMetadata("wcrates_id", plugin);
                // Reset the block to air
                block.setType(Material.AIR);
                removedBlocks++;
            }
        }

        // Delete the crate configuration file
        File crateFile = new File(plugin.getDataFolder(), "crates/" + crateId + ".yml");
        boolean crateFileDeleted = false;
        if (crateFile.exists()) {
            crateFileDeleted = crateFile.delete();
        }

        // Delete the placeholder file
        File placeholderFile = new File(plugin.getDataFolder(), "placeholders/" + crateId + ".yml");
        boolean placeholderFileDeleted = false;
        if (placeholderFile.exists()) {
            placeholderFileDeleted = placeholderFile.delete();
        }

        // Reload crates to remove from memory
        plugin.getCrateManager().reload();

        // Send success message
        String message = plugin.getLanguageManager().getMessage("crate.delete.success")
            .replace("%crate%", crateId)
            .replace("%blocks%", String.valueOf(removedBlocks));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        return true;
    }
}
