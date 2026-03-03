package com.wcrates.plugin.command;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.gui.CrateMenuGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        // Open the crate creation menu
        CrateMenuGUI menu = new CrateMenuGUI(plugin, player);
        menu.open();

        return true;
    }
}
