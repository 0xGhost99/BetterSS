package it.betterss.managers;

import it.betterss.BetterSS;
import it.betterss.SSSession;
import it.betterss.utils.MessageUtil;
import it.betterss.utils.WebhookUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SSManager {
    private final BetterSS plugin;
    private final MessageUtil msg;
    private final WebhookUtil webhook;
    private final ScoreboardManager scoreboardManager;

    // Map principale: UUID del player -> sessione SS
    private final Map<UUID, SSSession> activeSessions = new HashMap<>();

    // Set UUID player bannati per anti-relog (cache runtime)
    private final Set<UUID> pendingBan = new HashSet<>();

    public SSManager(BetterSS plugin) {
        this.plugin = plugin;
        this.msg = new MessageUtil(plugin);
        this.webhook = new WebhookUtil(plugin);
        this.scoreboardManager = new ScoreboardManager(plugin);
    }

    // =============================================
    //         AVVIO SESSIONE SS
    // =============================================

    /**
     * Avvia una sessione SS su un player.
     * @return true se avviata con successo, false se già attiva
     */
    public boolean startSession(Player staff, Player target) {
        if (activeSessions.containsKey(target.getUniqueId())) return false;

        SSSession session = new SSSession(target, staff);
        activeSessions.put(target.getUniqueId(), session);

        // 1. Teletrasporta nel mondo SS
        teleportToSSWorld(target);
        teleportToSSWorld(staff);

        // 2. Freeze il player
        freezePlayer(target);

        // 3. Imposta gamemode e effetti visivi
        target.setGameMode(GameMode.ADVENTURE);

        // 4. Scoreboard
        scoreboardManager.setSSScoreboard(target, session);

        // 5. Messaggi di avvio
        target.sendTitle(
            MessageUtil.color("&c&lSCREENSHARE"),
            MessageUtil.color("&7Coopera con lo staff. Non disconnetterti."),
            10, 80, 20
        );
        target.sendMessage(msg.get("player-frozen"));

        // 6. Task: messaggi automatici
        int interval = plugin.getConfig().getInt("settings.message-interval", 5) * 20;
        List<String> autoMsgs = plugin.getConfig().getStringList("auto-messages");
        final int[] msgIndex = {0};

        int msgTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!target.isOnline()) return;
            if (!autoMsgs.isEmpty()) {
                target.sendMessage(MessageUtil.color(autoMsgs.get(msgIndex[0] % autoMsgs.size())));
                msgIndex[0]++;
            }
        }, interval, interval).getTaskId();
        session.setMessageTaskId(msgTaskId);

        // 7. Task: aggiornamento scoreboard ogni 2 secondi
        int sbTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!target.isOnline()) return;
            scoreboardManager.updateScoreboard(target, session);
        }, 40L, 40L).getTaskId();
        session.setScoreboardTaskId(sbTaskId);

        // 8. Task: timer massimo SS
        int maxTime = plugin.getConfig().getInt("settings.max-ss-time", 0);
        if (maxTime > 0) {
            int timerTaskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (activeSessions.containsKey(target.getUniqueId())) {
                    staff.sendMessage(msg.replace(msg.get("ss-max-time"), "{player}", target.getName()));
                    endSession(target, "Tempo scaduto", false);
                }
            }, maxTime * 20L).getTaskId();
            session.setTimerTaskId(timerTaskId);
        }

        // 9. Log e Webhook
        plugin.getLogManager().logSession(target.getName(), staff.getName(), "SS_START", null);
        webhook.sendSSStart(target.getName(), staff.getName());

        return true;
    }

    // =============================================
    //         FINE SESSIONE SS
    // =============================================

    /**
     * Termina una sessione SS. Teletrasporta i giocatori e rimuove gli effetti.
     */
    public boolean endSession(Player target, String reason, boolean banPlayer) {
        SSSession session = activeSessions.remove(target.getUniqueId());
        if (session == null) return false;

        // Cancella tutti i task schedulati
        cancelTasks(session);

        // Unfreeze
        unfreezePlayer(target);

        // Rimuovi scoreboard
        scoreboardManager.removeSSScoreboard(target);

        // Ripristina gamemode
        target.setGameMode(GameMode.SURVIVAL);

        // Teletrasporta indietro (spawn principale)
        teleportToMain(target);

        Player staff = Bukkit.getPlayer(session.getStaffUUID());
        if (staff != null && staff.isOnline()) {
            teleportToMain(staff);
        }

        // Messaggio di fine
        target.sendMessage(msg.get("player-unfrozen"));

        // Log e Webhook
        long duration = session.getElapsedSeconds();
        plugin.getLogManager().logSession(target.getName(), session.getStaffName(), "SS_END", reason);
        webhook.sendSSEnd(target.getName(), session.getStaffName(), reason, duration);

        // Ban se richiesto
        if (banPlayer) {
            banPlayerSS(target, session.getStaffName(),
                plugin.getConfig().getString("settings.ban-reason-manual",
                    "[BetterSS] Bannato per cheat."));
        }

        return true;
    }

    /** Termina tutte le sessioni attive (usato allo shutdown) */
    public void endAllSessions(String reason) {
        new HashSet<>(activeSessions.keySet()).forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) endSession(p, reason, false);
            else activeSessions.remove(uuid); // player offline
        });
    }

    // =============================================
    //         FREEZE / UNFREEZE
    // =============================================

    public void freezePlayer(Player player) {
        SSSession session = activeSessions.get(player.getUniqueId());
        if (session != null) {
            session.setFrozen(true);
            player.sendMessage(msg.get("player-frozen"));
        }
    }

    public void unfreezePlayer(Player player) {
        SSSession session = activeSessions.get(player.getUniqueId());
        if (session != null) {
            session.setFrozen(false);
        }
    }

    // =============================================
    //         BAN
    // =============================================

    public void banPlayerSS(Player player, String staffName, String reason) {
        plugin.getLogManager().logSession(player.getName(), staffName, "BAN", reason);
        webhook.sendSSBan(player.getName(), staffName, reason);

        int duration = plugin.getConfig().getInt("settings.ban-duration", -1);
        // Utilizziamo il BanList nativo di Bukkit (nessuna dipendenza esterna)
        if (duration == -1) {
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME)
                .addBan(player.getName(), MessageUtil.color(reason), (Date) null, "BetterSS");
        } else {
            Date expires = new Date(System.currentTimeMillis() + duration * 60000L);
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME)
                .addBan(player.getName(), MessageUtil.color(reason), expires, "BetterSS");
        }
        player.kickPlayer(MessageUtil.color(reason));
    }

    // =============================================
    //         ANTI-RELOG
    // =============================================

    /** Chiama questo quando il player fa quit durante una SS */
    public void handleQuit(Player player) {
        SSSession session = activeSessions.get(player.getUniqueId());
        if (session == null) return;

        if (plugin.getConfig().getBoolean("settings.auto-ban-on-logout", true)) {
            pendingBan.add(player.getUniqueId());
            // Aspetta un tick per completare il quit prima del ban
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                String reason = plugin.getConfig().getString(
                    "settings.ban-reason-logout", "[BetterSS] Bannato per logout durante SS.");
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME)
                    .addBan(player.getName(), MessageUtil.color(reason), (Date) null, "BetterSS");
                plugin.getLogManager().logSession(player.getName(), session.getStaffName(), "BAN_RELOG", reason);
                webhook.sendSSBan(player.getName(), session.getStaffName(), "Anti-relog logout");
                pendingBan.remove(player.getUniqueId());

                // Notifica lo staff
                Player staff = Bukkit.getPlayer(session.getStaffUUID());
                if (staff != null) {
                    staff.sendMessage(MessageUtil.color("&c[SS] &e" + player.getName()
                        + " &cha abbandonato il server. Bannato automaticamente."));
                    teleportToMain(staff);
                }
            }, 1L);
        }

        // Rimuovi sessione
        cancelTasks(session);
        activeSessions.remove(player.getUniqueId());
    }

    // =============================================
    //         UTILITY
    // =============================================

    public boolean isInSession(UUID uuid) {
        return activeSessions.containsKey(uuid);
    }

    public SSSession getSession(UUID uuid) {
        return activeSessions.get(uuid);
    }

    public SSSession getSessionByStaff(UUID staffUUID) {
        return activeSessions.values().stream()
            .filter(s -> s.getStaffUUID().equals(staffUUID))
            .findFirst().orElse(null);
    }

    public Map<UUID, SSSession> getActiveSessions() {
        return Collections.unmodifiableMap(activeSessions);
    }

    private void teleportToSSWorld(Player player) {
        World ssWorld = plugin.getWorldManager().getSSWorld();
        if (ssWorld == null) { plugin.getLogger().warning("Mondo SS null!"); return; }
        Location spawn = ssWorld.getSpawnLocation().clone().add(0.5, 1, 0.5);
        player.teleport(spawn);
    }

    private void teleportToMain(Player player) {
        // Teletrasporta allo spawn del mondo principale
        World main = Bukkit.getWorlds().get(0);
        player.teleport(main.getSpawnLocation());
    }

    private void cancelTasks(SSSession session) {
        if (session.getTimerTaskId() != -1)
            Bukkit.getScheduler().cancelTask(session.getTimerTaskId());
        if (session.getMessageTaskId() != -1)
            Bukkit.getScheduler().cancelTask(session.getMessageTaskId());
        if (session.getScoreboardTaskId() != -1)
            Bukkit.getScheduler().cancelTask(session.getScoreboardTaskId());
    }
}