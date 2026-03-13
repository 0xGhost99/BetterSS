package it.betterss.listeners;

import it.betterss.BetterSS;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;

/**
 * Blocca l'uso dell'inventario e degli item durante la SS.
 * Configurabile tramite config.yml (block-inventory).
 */
public class InventoryListener implements Listener {
    private final BetterSS plugin;

    public InventoryListener(BetterSS plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!plugin.getConfig().getBoolean("settings.block-inventory", true)) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (player.hasPermission("betterss.bypass")) return;
        if (plugin.getSSManager().isInSession(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (!plugin.getConfig().getBoolean("settings.block-inventory", true)) return;
        Player player = e.getPlayer();
        if (player.hasPermission("betterss.bypass")) return;
        if (!plugin.getSSManager().isInSession(player.getUniqueId())) return;

        // Blocca uso di Enderpearl specificamente
        if (e.getItem() != null && e.getItem().getType() == Material.ENDER_PEARL) {
            e.setCancelled(true);
        }
    }
}