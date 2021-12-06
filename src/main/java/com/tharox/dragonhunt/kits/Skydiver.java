package com.tharox.dragonhunt.kits;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Skydiver extends ChargeKit {

    static int chargeTime = 10;
    static int duration = 20;
    static int cooldown = 180;

    public static final String title = ChatColor.AQUA + "Skydiver";
    public static final String lore =
            ChatColor.WHITE.toString() + ChatColor.ITALIC + "Fly through the skies! \n" +
                    ChatColor.WHITE + "Get " + ChatColor.GREEN + "Jump Boost 50 " + ChatColor.WHITE + "and an "
                    + ChatColor.LIGHT_PURPLE + "Elytra " + ChatColor.WHITE + "after activation.\n" +
                    ChatColor.WHITE + "The Jump Boost lasts for 5 seconds.\n" +
                    ChatColor.LIGHT_PURPLE + "Charge Time: " + chargeTime + " seconds\n" +
                    ChatColor.LIGHT_PURPLE + "Duration: " + duration +  " seconds\n" +
                    ChatColor.RED + "Cooldown: " + cooldown +  " seconds\n";
    public static final Material material = Material.ELYTRA;

    public Skydiver(Player player) {
        super(player);
    }

    @Override
    public void executeAbility() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " uses "
                    + ChatColor.AQUA.toString() + ChatColor.BOLD + "Sky Dive" + ChatColor.WHITE + "!");
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 49));

        ItemStack chest = player.getInventory().getChestplate();
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));

        this.runKitAbility(20, duration, ()->{player.getInventory().setChestplate(chest);}, null);
    }

    @Override
    public int getChargeTime() {
        return chargeTime;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}