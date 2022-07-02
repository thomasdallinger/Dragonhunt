package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.gear.XPUpgrade;
import com.tharox.dragonhunt.points.GearPoints;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.sql.Statement;

/**
 * Don't display the advancement for finding a fortress or a bastion when walking through a nether dungeon.
 */
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
                }
                break;
            case "minecraft:nether/distract_piglin":
                advancementName = "Piglin Trading";
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
        player.giveExp(GearPoints.enderchestXP + XPUpgrade.MoreXP.get(player) - 1);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        player.sendMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "BONUS XP: "
                + ChatColor.AQUA + "First in " + dimension);
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
