package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.points.UpgradePoints;
import com.tharox.dragonhunt.upgrades.XPUpgrade;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.sql.Statement;

public class AdvancementListener implements Listener {

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancementValue = event.getAdvancement();
        String advancementName = "";

        switch (advancementValue.getKey().toString()) {
            case "minecraft:story/smelt_iron":
                advancementName = "Gather Iron";
                break;
            case "minecraft:story/mine_diamond":
                advancementName = "Acquire Diamonds";
                break;
            case "minecraft:story/enter_the_nether":
                advancementName = "Enter Nether";
                if (GameState.firstNetherEntry == null) {
                    GameState.firstNetherEntry = player.getLocation();
                    addBonusXP(player, "Nether");
                    GameState.distanceScore = getDistanceScore(player);
                }
                break;
            case "minecraft:nether/distract_piglin":
                advancementName = "Piglin Trading";
                break;
            case "minecraft:nether/find_fortress":
                advancementName = "Fortress";
                break;
            case "minecraft:nether/find_bastion":
                advancementName = "Bastion";
                break;
            case "minecraft:story/enter_the_end":
                advancementName = "Enter End";
                if (!GameState.playersReachedEnd) {
                    GameState.playersReachedEnd = true;
                    addBonusXP(player, "End");
                }
                break;
        }

        if (advancementName.equals("")) {
            return;
        }

        addAdvancement(player, advancementName);
    }

    public void addBonusXP(Player player, String dimension) {
        player.giveExp(UpgradePoints.bonusXP + XPUpgrade.MoreXP.get(player) - 1);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        player.sendMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "BONUS XP: "
                + ChatColor.AQUA + "First in " + dimension);
    }

    /**
     * Calculates the distance score using the nearest Fortress and Bastion locations.
     * @param player first player to enter the Nether
     * @return the distance score (the lower, the better the NETHER spawn)
     */
    public int getDistanceScore(Player player) {
        World world = player.getWorld();
        Location fortress = world.locateNearestStructure(
                player.getLocation(), StructureType.NETHER_FORTRESS, 32, false);
        Location bastion = world.locateNearestStructure(
                player.getLocation(), StructureType.BASTION_REMNANT, 32, false);
        int fortressDistance = 0, bastionDistance = 0;

        if (fortress != null)
            fortressDistance = (int) player.getLocation().distance(fortress);
        if (bastion != null)
            bastionDistance = (int) player.getLocation().distance(bastion);

        return fortressDistance + bastionDistance;
    }

    public void addAdvancement(Player player, String advancement) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO Advancements (GameID, Player, advancement, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + advancement +
                    "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}
