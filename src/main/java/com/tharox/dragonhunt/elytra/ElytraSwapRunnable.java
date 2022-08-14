package com.tharox.dragonhunt.elytra;

import com.tharox.dragonhunt.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ElytraSwapRunnable extends BukkitRunnable {
    private final HashMap<Player, ItemStack> playerChestplate = new HashMap<>();
    private final HashMap<Player, Boolean> playerWasInAir = new HashMap<>();

    public ElytraSwapRunnable() {
        Bukkit.getOnlinePlayers().forEach(player -> playerWasInAir.put(player, false));
    }

    /**
     * Checks if the player is at least 2 or 6 blocks above the ground.
     *
     * @param player player to be checked if in the air
     * @return true if the player is at least 2 blocks above the ground and was in the air,<br>
     * true if the player is at least 6 blocks above the ground and was not in the air
     */
    private boolean isPlayerNowInTheAir(Player player) {
        if (playerWasInAir.get(player)) {
            for (int blocksBelow = 1; blocksBelow <= 2; blocksBelow++) {
                if (player.getLocation().subtract(0, blocksBelow, 0).getBlock().getType() != Material.AIR) {
                    return false;
                }
            }
        } else {
            for (int blocksBelow = 1; blocksBelow <= 6; blocksBelow++) {
                if (player.getLocation().subtract(0, blocksBelow, 0).getBlock().getType() != Material.AIR) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // player is no longer in the air - give the chestplate back (if it wasn't air)
            if (playerWasInAir.get(player) && !isPlayerNowInTheAir(player)) {
                playerWasInAir.put(player, false);
                player.getInventory().setChestplate(playerChestplate.get(player));

                // player is currently in the air - replace the chestplate with an elytra
            } else if (!playerWasInAir.get(player) && isPlayerNowInTheAir(player)) {
                playerWasInAir.put(player, true);
                ItemStack chestplate = player.getInventory().getChestplate();

                if (chestplate != null) {
                    if (chestplate.getType() == Material.ELYTRA) {
                        playerChestplate.put(player, new ItemStack(Material.AIR));
                    } else {
                        playerChestplate.put(player, chestplate);
                    }
                } else {
                    playerChestplate.put(player, new ItemStack(Material.AIR));
                }

                player.getInventory().setChestplate(GameState.addItem(new ItemStack(Material.ELYTRA)));
            }

            // make the player glow while in the air
            if (isPlayerNowInTheAir(player)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10, 0));
            }
        }
    }
}
