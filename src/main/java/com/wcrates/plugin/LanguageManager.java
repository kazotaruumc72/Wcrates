package com.wcrates.plugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages language/translation strings from language.yml
 */
public class LanguageManager {

    private final JavaPlugin plugin;
    private FileConfiguration languageConfig;
    private File languageFile;

    public LanguageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadLanguageFile();
    }

    /**
     * Load or reload the language.yml file
     */
    public void loadLanguageFile() {
        languageFile = new File(plugin.getDataFolder(), "language.yml");

        // Create the file if it doesn't exist
        if (!languageFile.exists()) {
            plugin.saveResource("language.yml", false);
        }

        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    }

    /**
     * Get a translated string from language.yml
     * @param path The path in the YAML file (e.g., "menu.title")
     * @return The translated string with color codes
     */
    public String getMessage(String path) {
        String message = languageConfig.getString(path);
        if (message == null) {
            return ChatColor.RED + "Missing translation: " + path;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Get a list of translated strings from language.yml
     * @param path The path in the YAML file
     * @return List of translated strings with color codes
     */
    public List<String> getMessageList(String path) {
        List<String> messages = languageConfig.getStringList(path);
        return messages.stream()
                .map(msg -> ChatColor.translateAlternateColorCodes('&', msg))
                .collect(Collectors.toList());
    }

    /**
     * Reload the language configuration
     */
    public void reload() {
        loadLanguageFile();
    }
}
