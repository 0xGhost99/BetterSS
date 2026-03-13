package it.betterss.managers;

import it.betterss.BetterSS;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {
    private final BetterSS plugin;
    private final File logFile;
    private YamlConfiguration logConfig;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");

    // Credits hardcoded nel log
    private static final String LOG_CREDITS = "BetterSS by 0xGhost99";

    public LogManager(BetterSS plugin) {
        this.plugin = plugin;
        this.logFile = new File(plugin.getDataFolder(), "ss-logs.yml");
        loadLog();
    }

    private void loadLog() {
        if (!logFile.exists()) {
            try {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Impossibile creare il file di log: " + e.getMessage());
            }
        }
        logConfig = YamlConfiguration.loadConfiguration(logFile);

        // Credits hardcoded nel file YAML — non rimovibili
        logConfig.set("_credits", LOG_CREDITS);
        logConfig.set("_plugin", "BetterSS");
        logConfig.set("_author", "0xGhost99");
        saveLog();
    }

    public void logSession(String playerName, String staffName,
                           String event, String extra) {
        if (!plugin.getConfig().getBoolean("settings.enable-logs", true)) return;

        String timestamp = LocalDateTime.now().format(FMT);
        String key = "logs." + timestamp + "_" + playerName;

        logConfig.set(key + ".player",    playerName);
        logConfig.set(key + ".staff",     staffName);
        logConfig.set(key + ".event",     event);
        logConfig.set(key + ".timestamp", timestamp);
        logConfig.set(key + ".plugin",    LOG_CREDITS); // credits su ogni entry
        if (extra != null && !extra.isEmpty())
            logConfig.set(key + ".note", extra);

        // Forza riscrittura credits (non rimovibili)
        logConfig.set("_credits", LOG_CREDITS);
        logConfig.set("_author", "0xGhost99");
        saveLog();
    }

    public void log(String message) {
        plugin.getLogger().info("[LOG] " + message);
    }

    private void saveLog() {
        try { logConfig.save(logFile); }
        catch (IOException e) { plugin.getLogger().warning("Errore salvataggio log: " + e.getMessage()); }
    }
}