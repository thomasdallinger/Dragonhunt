package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
        Advancement advancement = event.getAdvancement();
        String advancementName = "";
        int pointsToAdd = 10;

        switch (advancement.getKey().toString()) {
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
                    pointsToAdd = 15;
                    bonusPointsNotification(player, "Nether");
                }

                PointsListener.scorePerPlayer.put(player.getUniqueId(),
                        PointsListener.scorePerPlayer.get(player.getUniqueId()) + pointsToAdd);

                break;
            case "minecraft:nether/distract_piglin":
                advancementName = "Piglin Trading";
                break;
            case "minecraft:nether/find_fortress":
                advancementName = "Find Fortress";
                break;
            case "minecraft:nether/find_bastion":
                advancementName = "Find Bastion";
                break;
            case "minecraft:story/enter_the_end":
                advancementName = "Enter The End";

                if (!GameState.playersReachedEnd) {
                    GameState.playersReachedEnd = true;
                    pointsToAdd = 15;
                    bonusPointsNotification(player, "End");

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Prefixes.getImportantPrefix()
                                + HexColor.YELLOW + PlayerEndTeleport.getEyesNeeded()
                                + " Eyes of Ender " + ChatColor.GRAY + "needed to be able to enter "
                                + ChatColor.WHITE + "The End" + ChatColor.GRAY + ".");
                    }

                    PointsListener.scorePerPlayer.put(player.getUniqueId(),
                            PointsListener.scorePerPlayer.get(player.getUniqueId()) + pointsToAdd);
                }

                break;
        }

        if (!advancementName.equals("")) {
            Coins.addXP(player, Coins.advancementXP);
            addAdvancement(player, advancementName);
            int rank = DbValues.getAdvancementRank(player, advancementName);

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Prefixes.getAdvancementPrefix()
                        + Prefixes.getXPPrefix(Coins.advancementXP) + ChatColor.WHITE + player.getName()
                        + ChatColor.GRAY + " has made the advancement " + ChatColor.AQUA + advancementName
                        + ChatColor.DARK_GRAY + " (#" + rank + ")"
                        + ChatColor.GRAY + ".");
            }
        }
    }

    public void bonusPointsNotification(Player player, String dimension) {
        player.sendMessage(ChatColor.GRAY + "First in the " + HexColor.YELLOW + dimension
                + ChatColor.GRAY + " dimension.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    public void addAdvancement(Player player, String advancement) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO advancements (game_id, player, advancement, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + advancement +
                    "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}
