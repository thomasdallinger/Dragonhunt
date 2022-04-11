package com.tharox.dragonhunt.kits;

import com.tharox.dragonhunt.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

@DragonhuntKit
public class Platformer extends ChargeKit {
    public static HashMap<Player, Integer> chargeTime = new HashMap<>();
    public static HashMap<Player, Integer> duration = new HashMap<>();
    public static HashMap<Player, Integer> cooldown = new HashMap<>();
    public static HashMap<Player, Integer> amplifier = new HashMap<>();
    public static int defaultChargeTime = 3, defaultDuration = 25, defaultCooldown = 180, defaultAmplifier = 0;

    public static final String title = ChatColor.YELLOW + "Platformer";
    public static final String lore =
            ChatColor.WHITE.toString() + ChatColor.ITALIC + "Walk everywhere! \n" +
                    ChatColor.WHITE + "Receive " + ChatColor.AQUA + "Speed " + (defaultAmplifier + 1) + ChatColor.WHITE + " on activation.\n" +
                    ChatColor.WHITE + "Every second, blocks will spawn below you \n" +
                    ChatColor.WHITE + "making it possible to walk everywhere. \n" +
                    ChatColor.WHITE + "You can go up, down and through walls. \n" +
                    ChatColor.LIGHT_PURPLE + "Charge Time: " + defaultChargeTime + " seconds\n" +
                    ChatColor.LIGHT_PURPLE + "Duration: " + defaultDuration +  " seconds\n" +
                    ChatColor.RED + "Cooldown: " + defaultCooldown +  " seconds\n";
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

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration.get(player) * 20, amplifier.get(player)));

        this.runKitAbility(5, duration.get(player), null, ()->{setBlocksBelowPlayer(player);});
    }

    @Override
    public int getChargeTime() {
        return chargeTime.get(player);
    }

    @Override
    public int getCooldown() {
        return cooldown.get(player);
    }

    public void setBlocksBelowPlayer(Player player) {
        Location playerLocation = player.getLocation();
        Location offsetLocation;

        for(int offsetY = -1; offsetY != 3; offsetY++){

            Material material = Material.AIR;
            if(offsetY == -1)
                material = Material.SMOOTH_SANDSTONE;
            for(int offsetX = -1; offsetX != 3; offsetX++){
                for(int offsetZ = -1; offsetZ != 3; offsetZ++){
                    offsetLocation = new Location(playerLocation.getWorld(),
                            playerLocation.getX() + offsetX,
                            playerLocation.getY() + offsetY,
                            playerLocation.getZ() + offsetZ);
                    if(offsetLocation.getBlock().getType() != Material.OBSIDIAN && offsetLocation.getBlock().getType() != Material.END_PORTAL_FRAME) {
                        offsetLocation.getBlock().setType(material);
                    }
                }
            }
        }
    }
}