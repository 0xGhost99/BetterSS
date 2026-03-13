package it.betterss.utils;

import it.betterss.BetterSS;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtil {
    private final BetterSS plugin;

    public MessageUtil(BetterSS plugin) { this.plugin = plugin; }

    public String get(String path) {
        String prefix = color(plugin.getConfig().getString("messages.prefix", "[SS] "));
        String msg    = plugin.getConfig().getString("messages." + path, "&cMsg mancante: " + path);
        return prefix + color(msg);
    }

    public String replace(String message, String... kv) {
        for (int i = 0; i < kv.length - 1; i += 2)
            message = message.replace(kv[i], kv[i + 1]);
        return message;
    }

    public void send(Player p, String path, String... kv) {
        p.sendMessage(replace(get(path), kv));
    }

    public static String color(String t) {
        return t == null ? "" : ChatColor.translateAlternateColorCodes('&', t);
    }

    public static String formatTime(long seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }
}