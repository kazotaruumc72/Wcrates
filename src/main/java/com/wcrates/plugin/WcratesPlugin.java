package com.wcrates.plugin;

import com.wcrates.plugin.command.WcratesCommand;
import com.wcrates.plugin.crate.CrateManager;
import com.wcrates.plugin.listener.BlockSelectionListener;
import com.wcrates.plugin.listener.MenuClickListener;
import com.wcrates.plugin.listener.CrateInteractListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for Wcrates
 */
public class WcratesPlugin extends JavaPlugin {

    private static WcratesPlugin instance;
    private LanguageManager languageManager;
    private CrateManager crateManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize language manager
        languageManager = new LanguageManager(this);

        // Initialize crate manager
        crateManager = new CrateManager(this);

        // Register command
        getCommand("wcrates").setExecutor(new WcratesCommand(this));

        // Register event listeners
        getServer().getPluginManager().registerEvents(new MenuClickListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockSelectionListener(this), this);
        getServer().getPluginManager().registerEvents(new CrateInteractListener(this), this);

        getLogger().info("Wcrates plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Wcrates plugin has been disabled!");
    }

    /**
     * Get the plugin instance
     */
    public static WcratesPlugin getInstance() {
        return instance;
    }

    /**
     * Get the language manager
     */
    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    /**
     * Get the crate manager
     */
    public CrateManager getCrateManager() {
        return crateManager;
    }
}
