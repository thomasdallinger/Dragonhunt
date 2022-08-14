package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.upgrades.XPUpgrade;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.Statement;

public class PlayerDeathListener implements Listener {

    /**
     * The player always keeps:
     * - Hotbar (9 items, including TRACKER and GRAPPLER)
     * - Offhand (1 item)
     * - Armor (4 items, 3 items if the player had an elytra)
     * - XP (level only)
     * <p>
     * The killer gets a certain amount of XP.
     */
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();

        // TODO: change DB implementation
        addPlayerDeath(player, event.getDeathMessage());

        // strike a lightning effect at the player's death location
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.performCommand("pos Death" + DbValues.getKills(player, false));

        if (killer != null) {
            event.setDeathMessage(Prefixes.getKillPrefix()
                    + HexColor.RED + event.getDeathMessage().substring(0, player.getName().length())
                    + ChatColor.GRAY + event.getDeathMessage().substring(player.getName().length()));

            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
            killer.giveExp(Coins.playerXP + XPUpgrade.MoreXP.get(killer) - 1);
            killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            addPlayerKill(killer, player);
        } else {
            event.setDeathMessage(Prefixes.getDeathPrefix()
                    + HexColor.RED + event.getDeathMessage().substring(0, player.getName().length())
                    + ChatColor.GRAY + event.getDeathMessage().substring(player.getName().length()));
        }

        ItemStack itemToRemove;
        for (int index = 9; index <= 35; index++) {
            itemToRemove = player.getInventory().getItem(index);

            if (itemToRemove != null) {
                player.getInventory().remove(itemToRemove);
                player.getWorld().dropItemNaturally(player.getLocation(), itemToRemove);
            }
        }

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            player.getInventory().setChestplate(new ItemStack(Material.AIR));
        }

        event.setDroppedExp(0);
        event.setKeepLevel(true);
        player.setExp(0); // reset XP progress towards the next level
        event.getDrops().clear();

        GameState.itemsToKeepOnDeath.put(player, player.getInventory().getContents());

        player.sendMessage(Prefixes.getDeathPrefix() + ChatColor.GRAY.toString() + "You died at "
                + ChatColor.GOLD + DbValues.locationToString(player.getLocation())
                + ChatColor.GRAY + ".");
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
