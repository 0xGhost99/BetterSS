package it.betterss.utils;

import it.betterss.BetterSS;
import org.bukkit.Bukkit;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WebhookUtil {
    private final BetterSS plugin;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public WebhookUtil(BetterSS plugin) { this.plugin = plugin; }

    public void sendSSStart(String player, String staff) {
        send(buildEmbed("Screenshare Avviata", "Nuova sessione SS.", 0xFF0000, player, staff, "INIZIO"));
    }

    public void sendSSEnd(String player, String staff, String reason, long dur) {
        send(buildEmbed("Screenshare Terminata",
            "Motivo: " + reason + " | Durata: " + MessageUtil.formatTime(dur),
            0x00FF00, player, staff, "FINE"));
    }

    public void sendSSBan(String player, String staff, String reason) {
        send(buildEmbed("Player Bannato", "Motivo: " + reason, 0xFF0000, player, staff, "BAN"));
    }

    private String buildEmbed(String title, String desc, int color,
                               String player, String staff, String status) {
        String ts = LocalDateTime.now().format(FMT);
        return "{\"embeds\":[{\"title\":\"" + esc(title) + "\",\"description\":\"" + esc(desc) + "\","
             + "\"color\":" + color + ","
             + "\"fields\":["
             + "{\"name\":\"Player\",\"value\":\"" + esc(player) + "\",\"inline\":true},"
             + "{\"name\":\"Staff\",\"value\":\"" + esc(staff) + "\",\"inline\":true},"
             + "{\"name\":\"Stato\",\"value\":\"" + esc(status) + "\",\"inline\":true}],"
             + "\"footer\":{\"text\":\"BetterSS | " + esc(ts) + "\"}}]}";
    }

    private void send(String payload) {
        String url = plugin.getConfig().getString("settings.discord-webhook", "");
        if (url == null || url.isBlank()) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-Type", "application/json");
                c.setDoOutput(true); c.setConnectTimeout(5000); c.setReadTimeout(5000);
                try (OutputStream os = c.getOutputStream()) {
                    os.write(payload.getBytes(StandardCharsets.UTF_8));
                }
                c.getResponseCode(); c.disconnect();
            } catch (Exception e) {
                plugin.getLogger().warning("Webhook error: " + e.getMessage());
            }
        });
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\\","\\\\").replace("\"","\\\"");
    }
}