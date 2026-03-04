package com.wcrates.plugin.command;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.crate.CrateConfiguration;
import com.wcrates.plugin.gui.CrateMenuGUI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.Bukkit;
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
        // Handle sub-commands
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("key")) {
                return handleKeyCommand(player, args);
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
     * Handle the /wcrate key sub-command
     */
    private boolean handleKeyCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getLanguageManager().getMessage("messages.key.usage"));
            return true;
        }

        String keySubCommand = args[1].toLowerCase();

        if (keySubCommand.equals("give")) {
            return handleKeyGiveCommand(player, args);
        }

        player.sendMessage(plugin.getLanguageManager().getMessage("messages.key.usage"));
        return true;
    }

    /**
     * Handle the /wcrate key give sub-command
     */
    private boolean handleKeyGiveCommand(Player player, String[] args) {
        // Usage: /wcrate key give <player> <crate_id> [amount]
        if (args.length < 4) {
            player.sendMessage(plugin.getLanguageManager().getMessage("messages.key.give_usage"));
            return true;
        }

        String targetPlayerName = args[2];
        String crateId = args[3];
        int amount = 1;

        // Parse amount if provided
        if (args.length >= 5) {
            try {
                amount = Integer.parseInt(args[4]);
                if (amount <= 0) {
                    player.sendMessage(plugin.getLanguageManager().getMessage("messages.key.invalid_amount"));
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getLanguageManager().getMessage("messages.key.invalid_amount"));
                return true;
            }
        }

        // Get target player
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage(plugin.getLanguageManager().getMessage("messages.key.player_not_found")
                    .replace("{player}", targetPlayerName));
            return true;
        }

        // Check if crate exists
        if (plugin.getCrateManager().getCrate(crateId) == null) {
            player.sendMessage(plugin.getLanguageManager().getMessage("messages.key.crate_not_found")
                    .replace("{crate}", crateId));
            return true;
        }

        // Give keys
        plugin.getKeyManager().giveKeys(targetPlayer, crateId, amount);

        // Send success messages
        player.sendMessage(plugin.getLanguageManager().getMessage("messages.key.give_success")
                .replace("{amount}", String.valueOf(amount))
                .replace("{crate}", crateId)
                .replace("{player}", targetPlayer.getName()));

        targetPlayer.sendMessage(plugin.getLanguageManager().getMessage("messages.key.received")
                .replace("{amount}", String.valueOf(amount))
                .replace("{crate}", crateId));

        return true;
    }
}
