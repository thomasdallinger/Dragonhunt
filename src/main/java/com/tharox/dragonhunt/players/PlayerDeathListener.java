package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.Statement;
import java.util.HashMap;

public class PlayerDeathListener implements Listener {

    public static HashMap<Player, Location> deathLocation = new HashMap<>();

    /**
     * When a player dies, the player keeps the entire inventory and XP.
     */
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();
        deathLocation.put(player, player.getLocation());

        if (event.getDeathMessage() == null) return;
        // character "'" in some death messages causes SQL error
        addPlayerDeath(player, event.getDeathMessage().replaceAll("'", " "));

        // strike a lightning effect at the player's death location
        player.getWorld().strikeLightningEffect(player.getLocation());

        event.setDeathMessage(Prefixes.getDeathPrefix()
                + HexColor.RED + event.getDeathMessage().substring(0, player.getName().length())
                + ChatColor.GRAY + event.getDeathMessage().substring(player.getName().length()));

        // if the player has been killed by another player, give the killer regeneration, XP and other rewards
        if (killer != null) {
            CombatListener.combatList.remove(killer.getUniqueId());
            CombatListener.bossBar.removePlayer(killer);
            killer.sendMessage(Prefixes.getImportantPrefix() + HexColor.GREEN + "You are no longer in combat.");

            Coins.addXP(killer, Coins.playerXP);
            killer.sendMessage(Prefixes.getCoinPrefix(Coins.playerXP) + "You have killed "
                    + ChatColor.WHITE + player.getName() + ChatColor.GRAY + ".");

            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
            killer.sendMessage(HexColor.GREEN + "You received " + ChatColor.DARK_GREEN + "Regeneration 2"
                    + HexColor.GREEN + " for " + ChatColor.DARK_GREEN + "10" + HexColor.GREEN + " seconds.");

            addPlayerKill(killer, player);
        }

        // Set dead player out of combat
        CombatListener.combatList.remove(player.getUniqueId());
        CombatListener.bossBar.removePlayer(player);

        // Drop no XP
        event.setDroppedExp(0);
        event.setKeepLevel(true);
    }

    private void addPlayerDeath(Player player, String deathmessage) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO Deaths (GameID, Player, deathmessage, dimension, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + deathmessage
                    + "', '" + DbValues.getCurrentWorld(player.getLocation())
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }

    private void addPlayerKill(Player killer, Player killed) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO Kills (GameID, Killer, Killed, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + killer.getName() + "', '" + killed.getName()
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}
