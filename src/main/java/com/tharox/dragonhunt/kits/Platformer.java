package com.tharox.dragonhunt.kits;

import com.tharox.dragonhunt.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Platformer extends ChargeKit {

    static int chargeTime = 2;
    static int duration = 25;
    static int cooldown = 180;

    public static final String title = ChatColor.YELLOW + "Platformer";
    public static final String lore =
            ChatColor.WHITE.toString() + ChatColor.ITALIC + "Walk everywhere! \n" +
                    ChatColor.WHITE + "Receive " + ChatColor.AQUA + "Speed 1 " + ChatColor.WHITE + "on activation.\n" +
                    ChatColor.WHITE + "Every second, blocks will spawn below you \n" +
                    ChatColor.WHITE + "making it possible to walk everywhere. \n" +
                    ChatColor.WHITE + "You can go up, down and through walls. \n" +
                    ChatColor.LIGHT_PURPLE + "Charge Time: " + chargeTime + " seconds\n" +
                    ChatColor.LIGHT_PURPLE + "Duration: " + duration +  " seconds\n" +
                    ChatColor.RED + "Cooldown: " + cooldown +  " seconds\n";
    public static final Material material = Material.SMOOTH_SANDSTONE;

    public Platformer(Player player) {
        super(player);
    }

    @Override
    public void executeAbility() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " uses "
                    + ChatColor.YELLOW.toString() + ChatColor.BOLD + "Platform" + ChatColor.WHITE + "!");
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 500, 0));

        this.runKitAbility(5, duration, null, ()->{setBlocksBelowPlayer(player);});
    }

    @Override
    public int getChargeTime() {
        return chargeTime;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    public void setBlocksBelowPlayer(Player player) {
        Location playerLocation = player.getLocation();

        for(int offsetY = -1; offsetY != 3; offsetY++){

            Material material = Material.AIR;
            if(offsetY == -1)
                material = Material.SMOOTH_SANDSTONE;
            for(int offsetX = -1; offsetX != 3; offsetX++){
                for(int offsetZ = -1; offsetZ != 3; offsetZ++){
                    new Location(playerLocation.getWorld(),
                            playerLocation.getX() + offsetX,
                            playerLocation.getY() + offsetY,
                            playerLocation.getZ() + offsetZ)
                            .getBlock().setType(material);
                }
            }
        }
    }
}