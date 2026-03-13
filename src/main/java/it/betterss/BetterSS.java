package it.betterss;

import it.betterss.commands.SSCommand;
import it.betterss.listeners.*;
import it.betterss.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterSS extends JavaPlugin {

    private static BetterSS instance;
    private SSManager ssManager;
    private WorldManager worldManager;
    private LogManager logManager;

    // =============================================
    //   BetterSS - Made by 0xGhost99
    //   Rimozione credits VIETATA
    // =============================================
    private static final String CREDITS = "0xGhost99";
    private static final String PLUGIN_NAME = "BetterSS";

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.logManager   = new LogManager(this);
        this.worldManager = new WorldManager(this);
        this.ssManager    = new SSManager(this);

        SSCommand cmd = new SSCommand(this);
        getCommand("ss").setExecutor(cmd);
        getCommand("ss").setTabCompleter(cmd);

        getServer().getPluginManager().registerEvents(new FreezeListener(this),       this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this),   this);
        getServer().getPluginManager().registerEvents(new CommandBlockListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this),    this);
        getServer().getPluginManager().registerEvents(new TeleportListener(this),     this);

        worldManager.loadSSWorld();

        // Credits — non rimovibili
        printCredits();
    }

    @Override
    public void onDisable() {
        if (ssManager != null) ssManager.endAllSessions("Server shutdown");
        getLogger().info("BetterSS disabilitato. Made by " + CREDITS);
    }

    /**
     * Stampa i credits all'avvio. Non modificabile da config.
     * Rimozione di questo metodo viola i termini d'uso del plugin.
     */
    private void printCredits() {
        getLogger().info("§r");
        getLogger().info("====================================");
        getLogger().info("  " + PLUGIN_NAME + " v" + getDescription().getVersion());
        getLogger().info("  Made by " + CREDITS);
        getLogger().info("  Plugin ScreenShare Professionale");
        getLogger().info("  github.com/0xGhost99");
        getLogger().info("====================================");
        getLogger().info("§r");
    }

    public static BetterSS getInstance() { return instance; }
    public static String getCredits()    { return CREDITS; }
    public SSManager    getSSManager()   { return ssManager; }
    public WorldManager getWorldManager(){ return worldManager; }
    public LogManager   getLogManager()  { return logManager; }
}