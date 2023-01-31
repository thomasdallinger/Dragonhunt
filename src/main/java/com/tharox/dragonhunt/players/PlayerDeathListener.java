package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.elytra.ElytraSwapRunnable;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.upgrades.XPUpgrade;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
            Coins.addXP(killer, Coins.playerXP);
            addPlayerKill(killer, player);
        }

        // create a chest at the death location and move the player's loot into it
        Block block = player.getWorld().getBlockAt(player.getLocation());
        Barrel barrel = null;

        if (block.getLocation().getY() > -64) {
            block.setType(Material.BARREL);

            barrel = (Barrel) block.getState();
            barrel.setCustomName(HexColor.RED + player.getName() + "'s Loot");
            barrel.update();
        }

        ItemStack itemToRemove;

        for (int index = 9; index <= 35; index++) {
            itemToRemove = player.getInventory().getItem(index);

            if (itemToRemove != null) {
                player.getInventory().remove(itemToRemove);

                if (barrel != null) {
                    barrel.getInventory().setItem(index - 9, itemToRemove);
                }
            }
        }

        // if the player had an Elytra, replace it with air or with the chestplate the player had
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            player.getInventory().setChestplate(ElytraSwapRunnable.playerChestplate.getOrDefault(player,
                    new ItemStack(Material.AIR)));
        }

        // drop no loot or XP
        event.setDroppedExp(0);
        event.setKeepLevel(true);
        event.getDrops().clear();

        // store the items to keep on death and give the player these items on respawn (PlayerRespawnListener)
        GameState.itemsToKeepOnDeath.put(player, player.getInventory().getContents());

        // send a message with the location where the player died
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
