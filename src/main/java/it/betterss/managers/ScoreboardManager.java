package it.betterss.managers;

import it.betterss.BetterSS;
import it.betterss.SSSession;
import it.betterss.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

public class ScoreboardManager {
    private final BetterSS plugin;

    public ScoreboardManager(BetterSS plugin) { this.plugin = plugin; }

    /** Assegna la scoreboard SS al player */
    public void setSSScoreboard(Player player, SSSession session) {
        org.bukkit.scoreboard.ScoreboardManager sbm = Bukkit.getScoreboardManager();
        Scoreboard board = sbm.getNewScoreboard();

        String title = MessageUtil.color(
            plugin.getConfig().getString("scoreboard.title", "&c&lSCREENSHARE")
        );

        Objective obj = board.registerNewObjective("betterss", Criteria.DUMMY,
            net.kyori.adventure.text.Component.text(title));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateScoreboardLines(obj, player, session);
        player.setScoreboard(board);
    }

    /** Aggiorna le righe della scoreboard */
    public void updateScoreboard(Player player, SSSession session) {
        Scoreboard board = player.getScoreboard();
        Objective obj = board.getObjective("betterss");
        if (obj == null) {
            setSSScoreboard(player, session);
            return;
        }
        // Reset e ricostruzione (Paper 1.19 non supporta aggiornamento diretto facile)
        // Ricreiamo la board per evitare glitch
        setSSScoreboard(player, session);
    }

    private void updateScoreboardLines(Objective obj, Player player, SSSession session) {
        List<String> lines = plugin.getConfig().getStringList("scoreboard.lines");
        String discord = plugin.getConfig().getString("settings.discord-link", "discord.gg/...");
        String time = MessageUtil.formatTime(session.getElapsedSeconds());

        int score = lines.size();
        for (String line : lines) {
            line = line
                .replace("{player}", session.getPlayerName())
                .replace("{staff}",  session.getStaffName())
                .replace("{time}",   time)
                .replace("{discord}", discord);
            // Ogni riga deve essere unica: usiamo spazi invisibili come padding
            String entry = MessageUtil.color(line) + getInvisiblePad(score);
            obj.getScore(entry).setScore(score);
            score--;
        }
    }

    /** Rimuove la scoreboard SS e ripristina quella di default */
    public void removeSSScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    /** Genera padding invisibile per righe duplicate */
    private String getInvisiblePad(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append("\u00A7r");
        return sb.toString();
    }
}