package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.elytra.ElytraSwapRunnable;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
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
        player.performCommand("pos Death" + DbValues.getKills(player, false));

        event.setDeathMessage(Prefixes.getDeathPrefix()
                + HexColor.RED + event.getDeathMessage().substring(0, player.getName().length())
                + ChatColor.GRAY + event.getDeathMessage().substring(player.getName().length()));

        // if the player has been killed by another player, give the killer regeneration and XP
        if (killer != null) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
            Coins.addXP(killer, Coins.playerXP);
            addPlayerKill(killer, player);
        }

        // drop no XP
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
