package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.leveling.XPManager;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

        // Block spectating players from getting advancements (such as "Enter Nether").
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        switch (advancement.getKey().toString()) {
            case "minecraft:story/smelt_iron":
                advancementName = "Gather Iron";
                break;
            case "minecraft:story/mine_diamond":
                advancementName = "Acquire Diamonds";
                break;
            case "minecraft:story/enter_the_nether":
                advancementName = "Enter the Nether";

                if (GameState.firstNetherEntry == null) {
                    GameState.firstNetherEntry = player.getLocation();
                }

                break;
            case "minecraft:nether/distract_piglin":
                advancementName = "Trade with Piglins";
                break;
            case "minecraft:nether/obtain_blaze_rod":
                advancementName = "Obtain Blaze Rods";
                break;
            case "minecraft:nether/find_bastion":
                advancementName = "Find a Bastion";
                break;
            case "minecraft:nether/netherite_armor":
                advancementName = "Acquire full Netherite Armor";
                break;
            case "minecraft:story/enter_the_end":
                advancementName = "Enter the End";

                if (!GameState.playersReachedEnd) {
                    GameState.playersReachedEnd = true;

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Prefixes.getImportantPrefix()
                                + HexColor.YELLOW + PlayerTeleport.getEyesNeeded()
                                + " Eyes of Ender " + ChatColor.GRAY + "needed to be able to enter "
                                + ChatColor.WHITE + "The End" + ChatColor.GRAY + ".");
                    }
                }

                break;
        }

        if (!advancementName.equals("")) {
            addAdvancement(player, advancementName);

            player.setLevel(player.getLevel() + 1);
            int rank = DbValues.getAdvancementRank(player, advancementName);
            //XPManager.addXP(player, XPManager.advancementXP - (rank - 1) * 2);

            // Add 8 - (Rank - 1) Elo
            GameState.eloPerPlayer.put(player, GameState.eloPerPlayer.get(player) + 8 - (rank - 1));

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Prefixes.getAdvancementPrefix()
                        + ChatColor.AQUA + ChatColor.BOLD + "ADVANCEMENT MADE! "
                        + ChatColor.WHITE + player.getName()
                        + ChatColor.GRAY + " got " + ChatColor.AQUA + advancementName
                        + ChatColor.GRAY + " as " + ChatColor.AQUA + "#" + rank
                        + ChatColor.DARK_GRAY + " (" + DbValues.getTimestamp() + ")" + ChatColor.GRAY + ".");
            }
        }
    }

    private void addAdvancement(Player player, String advancement) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO advancements (game_id, player, advancement, timestamp) " +
                    "VALUES ('" + GameState.gameID + "', '" + player.getUniqueId() + "', '" + advancement +
                    "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}
