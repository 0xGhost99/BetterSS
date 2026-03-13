package it.betterss.listeners;

import it.betterss.BetterSS;
import it.betterss.SSSession;
import it.betterss.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Blocca il movimento del player freezato.
 * Usa PlayerMoveEvent per respingere il player alla posizione precedente.
 */
public class FreezeListener implements Listener {
    private final BetterSS plugin;
    private final MessageUtil msg;

    public FreezeListener(BetterSS plugin) {
        this.plugin = plugin;
        this.msg = new MessageUtil(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("betterss.bypass")) return;

        SSSession session = plugin.getSSManager().getSession(player.getUniqueId());
        if (session == null || !session.isFrozen()) return;

        // Permetti rotazione della testa, blocca spostamento fisico
        if (e.getFrom().getX() != e.getTo().getX()
         || e.getFrom().getY() != e.getTo().getY()
         || e.getFrom().getZ() != e.getTo().getZ()) {
            e.setTo(e.getFrom());
            player.sendActionBar(net.kyori.adventure.text.Component.text(
                MessageUtil.color(
                    plugin.getConfig().getString("messages.player-movement-blocked",
                    "&cSei freezato! Non puoi muoverti."))
            ));
        }
    }

    // Blocca drop di items durante SS
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (plugin.getSSManager().isInSession(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    // Blocca raccolta di items
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (plugin.getSSManager().isInSession(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }
}