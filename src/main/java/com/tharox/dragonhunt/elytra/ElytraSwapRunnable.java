package com.tharox.dragonhunt.elytra;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ElytraSwapRunnable extends BukkitRunnable {
    private final HashMap<Player, ItemStack> playerChestPlate = new HashMap<>();
    private final HashMap<Player, Boolean> playerWasInAir = new HashMap<>();

    private boolean isPlayerNowInTheAir(Player p) {
        return p.getLocation().subtract(0,1,0).getBlock().getType() == Material.AIR
                && p.getLocation().subtract(0,2,0).getBlock().getType() == Material.AIR;
    }

    private ItemStack getUnbreakableElytra() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ElytraSwapRunnable() {
        Bukkit.getOnlinePlayers().forEach(player -> playerWasInAir.put(player, false));
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(playerWasInAir.get(player) && !isPlayerNowInTheAir(player)){
                playerWasInAir.put(player, false);
                player.getInventory().setChestplate(playerChestPlate.get(player));
            } else if (!playerWasInAir.get(player) && isPlayerNowInTheAir(player)) {
                playerWasInAir.put(player, true);
                playerChestPlate.put(player, player.getInventory().getChestplate());
                player.getInventory().setChestplate(getUnbreakableElytra());
            }
        });
    }
}
