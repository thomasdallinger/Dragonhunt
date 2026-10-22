# Dragonhunt

An action-packed PvPvE Minecraft gamemode, built as a Spigot plugin.

Inspired by the Minecraft Manhunt videos on YouTube, Dragonhunt drops up to 8 players into a fresh random world. Every match has different Modifiers, and every player picks a Kit with its own abilities. The race is on to reach the End and take down the Ender Dragon first.

Dragonhunt was a personal project from 2021 to 2024 and ran publicly on `playdragonhunt.net`. The server is no longer online. The code is published here for reference.

More about the project: [thomasdallinger.com/dragonhunt.html](https://thomasdallinger.com/dragonhunt.html)

## How the game works

### How to win
- Deal **300 damage to the Ender Dragon**. The dragon has infinite health, so the first player to hit 300 damage wins.
- If nobody gets there within the **30 minute time limit**, the player who has dealt the most dragon damage wins.

### Reaching the End
- Gather **8 Ender Pearls and Blaze Rods** to craft Eyes of Ender. You then receive a **Teleporter** to the End.
- **Ender Pearls:** kill Endermen (spawn rates are drastically increased), or trade with Piglins in the Nether.
- **Blaze Rods:** kill Blazes in Nether Fortresses. You enter the Nether right next to a Fortress.
- Once any player is in the End, everyone else only needs **4 Eyes of Ender** for a Teleporter.

### Modifiers
Each match randomly activates 2 positive and 2 negative Modifiers that change the rules for everyone. Examples:
- Endless Night
- Explosive Creepers
- Double Chest Loot
- Poisonous Water
- Fly High
- Wither Skeletons

You have to adapt to every combination. Use `/modifiers` to see the active ones.

### Kits
There are 20 Kits in four classes: **Assassins ⚔, Tanks ★, Supports ❤ and Flankers ▲**. Examples:
- Sniper
- Grappler
- Healer
- Berserker
- Warper
- Frostmaster
- Vanisher

Each Kit has an active ability with a cooldown, a class passive and selectable talents (`/talent`).

### Shop and leveling
- Earn **10 Coins per Level Up**. You level up by mining ores, earning advancements, and killing monsters and players.
- Spend Coins in the Shop (an item in your inventory) on Enchantments, permanent Effects and Kit Upgrades.

### Faster, action-packed gameplay
- **Auto-smelting:** ores are smelted and animals drop steak, so you never need a furnace.
- **Elytra mechanic:** while you are 6+ blocks above the ground, your chestplate is swapped for an Elytra. You get it back close to the ground.
- **Keep everything on death:** you never lose items or XP. Instead, your respawn timer grows with your XP level.
- **Faster crafting:** wood or stone instantly crafts iron tools.
- **Scout:** `/scout` puts you in Spectator Mode for a short time. `/scout tp` teleports you to where you were scouting.
- **Tracker:** points towards players and structures and shows the distance.
- **Respawn Anchor:** set and teleport to your own respawn points anywhere.

### Leaderboards
Every match you gain or lose **Elo** based on your performance. Elo, Rank and League reset each Season. All other stats are kept and are viewable with `/stats [player]`.

## Commands

| Command | Description |
| --- | --- |
| `/modifiers`, `/m` | Show the active Modifiers |
| `/talent` | Apply your selected Kit talent |
| `/scout` | Short Spectator Mode scouting (`/scout tp` to teleport) |
| `/top` | Teleport to the surface |
| `/stats [player]` | Show your stats or another player's |
| `/hub`, `/lobby`, `/l` | Return to the lobby server |
| `/date` | Show the current date |

## Architecture

Dragonhunt was one part of a small BungeeCord network:

```
            BungeeCord proxy
           /                \
   Lobby server         Game nodes "MhNode<id>"
 (Dragonhunt-Lobby)      (this plugin, one match per node)
           \                /
             MySQL database
   (players, games, plays, kills, deaths, advancements, ...)
```

- **[Dragonhunt-Lobby](https://github.com/thomasdallinger/Dragonhunt-Lobby)** runs the lobby. It queues players and starts a game node, then moves the players to it over the BungeeCord `Connect` channel.
- **This plugin** runs the match on the node and records stats, kills, deaths, advancements and kit usage in the shared database.
- When the match ends, it sends everyone back to the `lobby` server.

## Building

Requirements: Java 8+ and Maven. The plugin targets the Spigot API **1.19** and needs Minecraft: Java Edition 1.19+ to join.

```bash
mvn package
```

`pom.xml` writes the jar to a hard-coded local plugin folder (`maven-jar-plugin` → `outputDirectory`). Change or remove that line before building on your machine.

### Database

Database credentials are read from `src/main/resources/db.txt`. The file is bundled into the jar and ignored by git. It contains three lines:

```
jdbc:mysql://<host>:3306/<database>
<user>
<password>
```

Gameplay settings (shop items, prices, kit talents) live in `src/main/resources/config.yml`.

## License

[MIT](LICENSE) © Thomas Dallinger
