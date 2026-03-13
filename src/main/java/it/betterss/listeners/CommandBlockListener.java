package it.betterss.listeners;

import it.betterss.BetterSS;
import it.betterss.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Blocca tutti i comandi non autorizzati durante la SS.
 * Lista bianca: solo comandi dello staff o del plugin stesso.
 */
public class CommandBlockListener implements Listener {
    private final BetterSS plugin;
    private final MessageUtil msg;

    // Comandi SEMPRE bloccati durante SS (anche per staff senza bypass)
    private static final List<String> BLOCKED_COMMANDS = Arrays.asList(
        "/spawn", "/hub", "/tp", "/teleport", "/warp", "/home",
        "/back", "/rtp", "/wild", "/tpa", "/tpaccept", "/tpdeny",
        "/enderpearl", "/ender", "/ec"
    );

    public CommandBlockListener(BetterSS plugin) {
        this.plugin = plugin;
        this.msg = new MessageUtil(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("betterss.bypass")) return;
        if (!plugin.getSSManager().isInSession(player.getUniqueId())) return;

        String command = e.getMessage().toLowerCase().split(" ")[0];

        // Controlla se il comando è nella lista bloccati
        boolean blocked = BLOCKED_COMMANDS.stream()
            .anyMatch(bc -> command.equalsIgnoreCase(bc) || command.startsWith(bc + " "));

        // Blocca tutti i comandi per il player (eccetto /ss-related che arrivano dallo staff)
        // I player in SS possono usare SOLO: nessun comando
        if (blocked || !command.startsWith("/ss")) {
            e.setCancelled(true);
            player.sendMessage(msg.get("player-command-blocked"));
        }
    }
}