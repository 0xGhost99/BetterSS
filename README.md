<div align="center">

# 🔵 BetterSS
### Plugin ScreenShare Professionale per Server PvP Minecraft

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/0xGhost99/BetterSS/releases)
[![Paper](https://img.shields.io/badge/Paper-1.19+-green.svg)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://adoptium.net)
[![License](https://img.shields.io/badge/license-MIT-red.svg)](LICENSE)

*Plugin professionale per la gestione completa degli ScreenShare su server PvP Minecraft*

</div>

---

## 📌 Descrizione

**BetterSS** è un plugin Paper per Minecraft 1.19+ che fornisce un sistema di ScreenShare
professionale per server PvP. Permette allo staff di avviare sessioni SS, freezare i player,
bloccare comandi e movimenti, bannare automaticamente chi tenta di disconnettersi e molto altro.

---

## ✨ Funzionalità

- 🌍 **Mondo dedicato** — Crea automaticamente un mondo `betterss` isolato
- 🧊 **Freeze avanzato** — Blocca movimento, comandi, enderpearl, inventario
- 🚫 **Anti-relog** — Ban automatico se il player disconnette durante la SS
- 📋 **Scoreboard live** — Sidebar aggiornata in tempo reale con info SS
- 💬 **Messaggi automatici** — Avvisi periodici al player configurabili
- ⏱️ **Timer massimo** — SS terminata automaticamente allo scadere del tempo
- 📁 **Log YAML** — Ogni sessione viene salvata su file
- 🔔 **Webhook Discord** — Notifiche automatiche per inizio/fine/ban SS
- ⚙️ **Config completa** — Tutto personalizzabile da `config.yml`

---

## 📋 Requisiti

| Requisito | Versione |
|-----------|----------|
| Server | Paper 1.19+ |
| Java | 17+ |
| Minecraft | 1.19+ |

---

## 🚀 Installazione

1. Scarica l'ultima versione da [Releases](https://github.com/0xGhost99/BetterSS/releases)
2. Metti `BetterSS.jar` nella cartella `plugins/` del server
3. Riavvia il server
4. Configura `plugins/BetterSS/config.yml`
5. Riavvia di nuovo o usa `/reload confirm`

---

## 🎮 Comandi

| Comando | Descrizione | Permesso |
|---------|-------------|----------|
| `/ss <player>` | Avvia una sessione SS | `betterss.use` |
| `/ss freeze <player>` | Freeza un player | `betterss.admin` |
| `/ss unfreeze <player>` | Defeeza un player | `betterss.admin` |
| `/ss clean <player>` | Termina la SS | `betterss.admin` |
| `/ss ban <player>` | Banna il player | `betterss.ban` |
| `/ss discord <player>` | Invia link Discord | `betterss.admin` |
| `/ss status <player>` | Stato sessione SS | `betterss.admin` |
| `/ss credits` | Mostra i credits | nessuno |

---

## 🔐 Permessi

| Permesso | Descrizione | Default |
|----------|-------------|---------|
| `betterss.use` | Avvia una SS | OP |
| `betterss.admin` | Tutti i comandi staff | OP |
| `betterss.ban` | Banna tramite SS | OP |
| `betterss.bypass` | Bypass freeze | OP |

---

## ⚙️ Configurazione
```yaml
settings:
  ss-world: "betterss"
  max-ss-time: 600
  message-interval: 5
  block-inventory: true
  auto-ban-on-logout: true
  discord-link: "https://discord.gg/tuoserver"
  discord-webhook: ""
  enable-logs: true
```

---

## 🛡️ Sicurezza Anti-Bypass

- ❌ Anti-relog (ban automatico)
- ❌ Anti-teleport
- ❌ Anti-enderpearl
- ❌ Blocco comandi non autorizzati
- ❌ Blocco inventario e drop items

---

## 🔨 Compilazione
```bash
git clone https://github.com/0xGhost99/BetterSS.git
cd BetterSS
mvn clean package
# Output: target/BetterSS-1.0.0.jar
```

---

## 📜 Changelog

### v1.0.0
- 🎉 Release iniziale
- ✅ Sistema SS completo
- ✅ Mondo dedicato automatico
- ✅ Anti-relog
- ✅ Scoreboard live
- ✅ Webhook Discord
- ✅ Log YAML

---

## 👤 Autore

**0xGhost99** — [@0xGhost99](https://github.com/0xGhost99)

---

<div align="center">
Made with ❤️ by 0xGhost99
</div>
