package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.ItemStack;

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
                advancementName = "Enter End";
                if (!GameState.playersReachedEnd) {
                    GameState.playersReachedEnd = true;

                    GameState.putItemInInventory(player, new ItemStack(Material.NETHERITE_INGOT));
                    player.sendMessage(ChatColor.GRAY + "1x " + ChatColor.DARK_PURPLE + "Netherite Ingot");

                    addBonusXP(player, "End");

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Prefixes.getImportantPrefix()
                                + HexColor.HIGHLIGHT + PlayerEndTeleport.getEyesNeeded()
                                + " Eyes of Ender " + ChatColor.GRAY + "needed to get the "
                                + ChatColor.LIGHT_PURPLE + "End Teleporter"
                                + ChatColor.GRAY + ".");
                    }
                }

                break;
        }

        if (advancementName.equals("")) {
            return;
        } else {
            Coins.addXP(player, Coins.advancementXP);

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Prefixes.getAdvancementPrefix() + ChatColor.WHITE + player.getName()
                        + ChatColor.AQUA + " has made the advancement "
                        + ChatColor.DARK_AQUA + advancementName + ChatColor.AQUA + " (#1).");
            }
        }

        addAdvancement(player, advancementName);
    }

    public void addBonusXP(Player player, String dimension) {
        Coins.addXP(player, Coins.advancementXP * 2);
        player.sendMessage(Prefixes.getXPPrefix(Coins.advancementXP) + "First in the "
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
