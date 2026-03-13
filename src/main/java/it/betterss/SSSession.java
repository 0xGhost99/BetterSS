package it.betterss;

import org.bukkit.entity.Player;
import java.time.Instant;
import java.util.UUID;

public class SSSession {
    private final UUID playerUUID, staffUUID;
    private final String playerName, staffName;
    private final long startTime;
    private boolean frozen;
    private int timerTaskId = -1, messageTaskId = -1, scoreboardTaskId = -1;

    public SSSession(Player player, Player staff) {
        this.playerUUID  = player.getUniqueId();
        this.playerName  = player.getName();
        this.staffUUID   = staff.getUniqueId();
        this.staffName   = staff.getName();
        this.startTime   = Instant.now().toEpochMilli();
        this.frozen      = true;
    }

    public long getElapsedSeconds() {
        return (Instant.now().toEpochMilli() - startTime) / 1000;
    }

    // Getters & Setters
    public UUID getPlayerUUID()  { return playerUUID; }
    public String getPlayerName(){ return playerName; }
    public UUID getStaffUUID()   { return staffUUID; }
    public String getStaffName() { return staffName; }
    public long getStartTime()   { return startTime; }
    public boolean isFrozen()    { return frozen; }
    public void setFrozen(boolean f) { this.frozen = f; }
    public int getTimerTaskId()      { return timerTaskId; }
    public void setTimerTaskId(int id)      { this.timerTaskId = id; }
    public int getMessageTaskId()    { return messageTaskId; }
    public void setMessageTaskId(int id)    { this.messageTaskId = id; }
    public int getScoreboardTaskId() { return scoreboardTaskId; }
    public void setScoreboardTaskId(int id) { this.scoreboardTaskId = id; }
}