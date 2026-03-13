package it.betterss.managers;

import it.betterss.BetterSS;
import org.bukkit.*;

public class WorldManager {
    private final BetterSS plugin;
    private World ssWorld;

    public WorldManager(BetterSS plugin) { this.plugin = plugin; }

    /** Carica o crea il mondo SS dedicato */
    public void loadSSWorld() {
        String worldName = plugin.getConfig().getString("settings.ss-world", "betterss");
        ssWorld = Bukkit.getWorld(worldName);
        if (ssWorld == null) {
            plugin.getLogger().info("Mondo SS '" + worldName + "' non trovato, creazione in corso...");
            WorldCreator wc = new WorldCreator(worldName);
            wc.environment(World.Environment.NORMAL);
            wc.type(WorldType.FLAT);
            wc.generateStructures(false);
            ssWorld = Bukkit.createWorld(wc);
            if (ssWorld != null) {
                configureWorld(ssWorld);
                plugin.getLogger().info("Mondo SS creato con successo.");
            } else {
                plugin.getLogger().severe("ERRORE: impossibile creare il mondo SS!");
            }
        } else {
            configureWorld(ssWorld);
            plugin.getLogger().info("Mondo SS '" + worldName + "' caricato.");
        }
    }

    /** Configura il mondo SS con gamerules sicure */
    private void configureWorld(World world) {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setTime(6000);
        world.setStorm(false);
    }

    public World getSSWorld() { return ssWorld; }

    public boolean isSSWorld(World world) {
        return ssWorld != null && ssWorld.equals(world);
    }
}