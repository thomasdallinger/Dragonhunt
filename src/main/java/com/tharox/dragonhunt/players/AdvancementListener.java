package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.util.StructureSearchResult;

import java.sql.Statement;

public class AdvancementListener implements Listener {

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        String advancementName = "";

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
                    addBonusXP(player, "Nether");
                }

                player.performCommand("pos NetherPortal");
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
        } else {
            Coins.addXP(player, Coins.bonusXP);
            player.sendMessage(Prefixes.getAllPrefix() + HexColor.GREEN + "Advancement made: "
                    + ChatColor.DARK_GREEN + advancementName + HexColor.GREEN + ".");
        }

        addAdvancement(player, advancementName);
    }

    public void addBonusXP(Player player, String dimension) {
        Coins.addXP(player, Coins.bonusXP);
        player.sendMessage(Prefixes.getXPPrefix(Coins.bonusXP) + "First in the "
                + HexColor.HIGHLIGHT + dimension + ChatColor.GRAY + " dimension.");
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
