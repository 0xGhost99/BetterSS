package it.betterss.listeners;

import it.betterss.BetterSS;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Blocca i teletrasporti non autorizzati durante la SS.
 * Permette solo i TP gestiti dal plugin stesso (causa PLUGIN).
 */
public class TeleportListener implements Listener {
    private final BetterSS plugin;

    public TeleportListener(BetterSS plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getPlayer().hasPermission("betterss.bypass")) return;
        if (!plugin.getSSManager().isInSession(e.getPlayer().getUniqueId())) return;

        // Permetti solo TP gestiti dal plugin (causa PLUGIN o COMMAND interno)
        PlayerTeleportEvent.TeleportCause cause = e.getCause();
        if (cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL
         || cause == PlayerTeleportEvent.TeleportCause.COMMAND
         || cause == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            e.setCancelled(true);
        }
        // PLUGIN cause è permessa (usata dal nostro SSManager)
    }
}