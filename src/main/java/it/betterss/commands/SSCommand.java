package it.betterss.commands;

import it.betterss.BetterSS;
import it.betterss.SSSession;
import it.betterss.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class SSCommand implements CommandExecutor, TabCompleter {
    private final BetterSS plugin;
    private final MessageUtil msg;

    // Credits hardcoded — non rimovibili
    private static final String CREDITS_MSG =
        MessageUtil.color("&8[&c&lBetterSS&8] &7Made by &b0xGhost99 &7| Plugin ScreenShare Professionale");

    public SSCommand(BetterSS plugin) {
        this.plugin = plugin;
        this.msg    = new MessageUtil(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player staff)) {
            sender.sendMessage(MessageUtil.color("&cUsa questo comando in game."));
            return true;
        }

        // /ss senza argomenti → mostra usage + credits
        if (args.length == 0) {
            staff.sendMessage(CREDITS_MSG);
            staff.sendMessage(msg.get("usage-main"));
            return true;
        }

        if (args.length == 1) {
            if (!staff.hasPermission("betterss.use")) {
                staff.sendMessage(msg.get("no-permission")); return true;
            }
            // Mostra credits anche all'avvio SS
            staff.sendMessage(CREDITS_MSG);

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                staff.sendMessage(msg.replace(msg.get("player-not-found"), "{player}", args[0]));
                return true;
            }
            boolean started = plugin.getSSManager().startSession(staff, target);
            if (!started) {
                staff.sendMessage(msg.replace(msg.get("ss-already-active"), "{player}", target.getName()));
            } else {
                staff.sendMessage(msg.replace(msg.get("ss-started"), "{player}", target.getName()));
            }
            return true;
        }

        String sub   = args[0].toLowerCase();
        String pName = args[1];
        Player target = Bukkit.getPlayerExact(pName);

        if (!staff.hasPermission("betterss.admin")) {
            staff.sendMessage(msg.get("no-permission")); return true;
        }

        switch (sub) {
            case "freeze" -> {
                if (target == null) { noPlayer(staff, pName); return true; }
                plugin.getSSManager().freezePlayer(target);
                staff.sendMessage(msg.replace(msg.get("ss-frozen"), "{player}", target.getName()));
            }
            case "unfreeze" -> {
                if (target == null) { noPlayer(staff, pName); return true; }
                plugin.getSSManager().unfreezePlayer(target);
                staff.sendMessage(msg.replace(msg.get("ss-unfrozen"), "{player}", target.getName()));
            }
            case "clean" -> {
                if (target == null) { noPlayer(staff, pName); return true; }
                boolean ended = plugin.getSSManager().endSession(target, "Staff terminazione", false);
                if (ended) staff.sendMessage(msg.replace(msg.get("ss-ended"), "{player}", target.getName()));
                else       staff.sendMessage(msg.replace(msg.get("ss-not-active"), "{player}", target.getName()));
            }
            case "ban" -> {
                if (!staff.hasPermission("betterss.ban")) {
                    staff.sendMessage(msg.get("no-permission")); return true;
                }
                if (target == null) { noPlayer(staff, pName); return true; }
                String reason = plugin.getConfig().getString("settings.ban-reason-manual",
                    "[BetterSS] Bannato per cheat.");
                boolean ended = plugin.getSSManager().endSession(target, "Ban staff", true);
                if (!ended) plugin.getSSManager().banPlayerSS(target, staff.getName(), reason);
                staff.sendMessage(msg.replace(msg.get("ss-banned"), "{player}", target.getName()));
            }
            case "discord" -> {
                if (target == null) { noPlayer(staff, pName); return true; }
                String link = plugin.getConfig().getString("settings.discord-link", "discord.gg/...");
                target.sendMessage(msg.replace(msg.get("player-discord"), "{discord}", link));
                staff.sendMessage(msg.replace(msg.get("ss-discord-sent"), "{player}", target.getName()));
            }
            case "status" -> {
                UUID uuid = target != null
                    ? target.getUniqueId()
                    : Bukkit.getOfflinePlayer(pName).getUniqueId();
                SSSession session = plugin.getSSManager().getSession(uuid);
                if (session == null) {
                    staff.sendMessage(msg.replace(msg.get("ss-status-none"), "{player}", pName));
                } else {
                    staff.sendMessage(msg.replace(msg.get("ss-status-active"),
                        "{player}", session.getPlayerName(),
                        "{staff}",  session.getStaffName(),
                        "{time}",   String.valueOf(session.getElapsedSeconds())
                    ));
                }
            }
            case "credits" -> {
                // Comando /ss credits sempre disponibile
                staff.sendMessage(CREDITS_MSG);
                staff.sendMessage(MessageUtil.color("&7Versione: &f" + plugin.getDescription().getVersion()));
                staff.sendMessage(MessageUtil.color("&7Autore: &b0xGhost99"));
            }
            default -> staff.sendMessage(msg.get("usage-main"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList(
                "freeze","unfreeze","clean","ban","discord","status","credits"));
            Bukkit.getOnlinePlayers().forEach(p -> completions.add(p.getName()));
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void noPlayer(Player staff, String name) {
        staff.sendMessage(msg.replace(msg.get("player-not-found"), "{player}", name));
    }
}