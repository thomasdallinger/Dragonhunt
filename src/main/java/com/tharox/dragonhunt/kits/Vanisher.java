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
public class Vanisher extends ChargeKit {

    static int chargeTime = 0;
    static int duration = 15;
    static int cooldown = 120;

    public static final String title = ChatColor.GRAY + "Vanisher";
    public static final String lore =
            ChatColor.WHITE.toString() + ChatColor.ITALIC + "Disappear in front of your enemies! \n" +
                    ChatColor.WHITE + "Get a Potion that gives you " + ChatColor.AQUA + "Speed 3 " + ChatColor.WHITE
                    + "& " + ChatColor.DARK_GRAY + "Invisibility " + ChatColor.WHITE + "when drinking it.\n" +
                    ChatColor.WHITE + "You can't get damage and your Inventory will disappear\n" +
                    ChatColor.WHITE + "while your ability is active.\n" +
                    ChatColor.LIGHT_PURPLE + "Charge Time: " + chargeTime + " seconds\n" +
                    ChatColor.LIGHT_PURPLE + "Duration: " + duration +  " seconds\n" +
                    ChatColor.RED + "Cooldown: " + cooldown +  " seconds\n";
    public static final Material material = Material.ENDER_PEARL;

    public Vanisher(Player player) {
        super(player);
    }

    @Override
    public void executeAbility() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " uses "
                    + ChatColor.GRAY.toString() + ChatColor.BOLD + "Vanish" + ChatColor.WHITE + "!");
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 9));

        ItemStack[] items = player.getInventory().getContents();

        this.runKitAbility(20, duration, ()->{player.getInventory().setContents(items);}, ()->{player.getInventory().clear();});
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