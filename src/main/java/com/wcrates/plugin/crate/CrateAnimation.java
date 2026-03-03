package com.wcrates.plugin.crate;

import com.wcrates.plugin.WcratesPlugin;
import com.wcrates.plugin.gui.CrateAnimationGUI;
import org.bukkit.entity.Player;

/**
 * Handles the animation of scrolling through placeholder values
 * Now uses a GUI-based animation instead of action bar
 */
public class CrateAnimation {

    private final WcratesPlugin plugin;
    private final Player player;
    private final CrateConfiguration crate;

    public CrateAnimation(WcratesPlugin plugin, Player player, CrateConfiguration crate) {
        this.plugin = plugin;
        this.player = player;
        this.crate = crate;
    }

    /**
     * Start the crate animation by opening the GUI
     */
    public void start() {
        CrateAnimationGUI gui = new CrateAnimationGUI(plugin, player, crate);
        gui.open();
    }
}
