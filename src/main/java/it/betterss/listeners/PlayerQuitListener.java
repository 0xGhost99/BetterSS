package it.betterss.listeners;

import it.betterss.BetterSS;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Gestisce il logout del player durante una SS.
 * Attiva il ban automatico se configurato.
 */
public class PlayerQuitListener implements Listener {
    private final BetterSS plugin;

    public PlayerQuitListener(BetterSS plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if (plugin.getSSManager().isInSession(e.getPlayer().getUniqueId())) {
            plugin.getSSManager().handleQuit(e.getPlayer());
        }
    }
}