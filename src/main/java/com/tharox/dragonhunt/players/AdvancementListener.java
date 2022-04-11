package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import org.bukkit.Bukkit;
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
        Advancement adv = event.getAdvancement();
        String completed = "";

        /*if (adv.getKey().toString().equals("minecraft:story/find_fortress") || adv.getKey().toString().equals("minecraft:story/find_bastion")) {
            for(String c: adv.getCriteria()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + event.getPlayer().getName() + " only " + event.getAdvancement().getKey().getKey());
                player.getAdvancementProgress(adv).revokeCriteria(c);
            }
        }*/

        switch (adv.getKey().toString()) {
            case "minecraft:story/smelt_iron":
                completed = "Gather Iron";
                break;
            case "minecraft:story/mine_diamond":
                completed = "Acquire Diamonds";
                break;
            case "minecraft:story/enter_the_nether":
                completed = "Enter Nether";
                break;
            case "minecraft:nether/distract_piglin":
                completed = "Piglin Trading";
                break;
            case "minecraft:story/enter_the_end":
                completed = "Enter End";
                GameState.minOnePlayerInEnd = true;
                break;
        }

        if(completed.equals("")) {
            return;
        }

        String finalCompleted = completed;
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO Advancements (GameID, Player, advancement, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + finalCompleted +
                    "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}
