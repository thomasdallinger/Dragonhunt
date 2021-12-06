package com.tharox.dragonhunt.kits;

import com.tharox.dragonhunt.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Trapcrafter extends ChargeKit {

    static int chargeTime = 10;
    static int duration = 35;
    static int cooldown = 300;

    public static final String title = ChatColor.RED + "Trapcrafter";
    public static final String lore =
            ChatColor.WHITE.toString() + ChatColor.ITALIC + "Explosion incoming! \n" +
                    ChatColor.WHITE + "On activation, 3 layers of " + ChatColor.RED + "TNT "
                    + ChatColor.WHITE + "will spawn around you.\n" +
                    ChatColor.WHITE + "You will not be able to move or get damage for the entire ability duration.\n" +
                    ChatColor.WHITE + "Be careful, you don't want to explode yourself.\n" +
                    ChatColor.LIGHT_PURPLE + "Charge Time: " + chargeTime + " seconds\n" +
                    ChatColor.LIGHT_PURPLE + "Duration: " + duration +  " seconds\n" +
                    ChatColor.RED + "Cooldown: " + cooldown +  " seconds\n";
    public static final Material material = Material.TNT;

    public Trapcrafter(Player player) {
        super(player);
    }

    @Override
    public void executeAbility() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " uses "
                    + ChatColor.RED.toString() + ChatColor.BOLD + "Explosion" + ChatColor.WHITE + "at "
                    + ChatColor.GOLD + "[" + player.getWorld().getName() + ", x: " + (int) player.getLocation().getX()
                    + ", y: " + (int) player.getLocation().getZ() + ", z: " + (int) player.getLocation().getY() + "]");
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 35 * 20, 9));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 35 * 20, 9));

        Location location = player.getLocation();

        if(location.getWorld() == null) return;

        for (int offsetX = -25; offsetX < 26; offsetX += 3) {
            for (int offsetZ = -25; offsetZ < 26; offsetZ += 3) {
                if (Math.abs(offsetX) < 6 && Math.abs(offsetZ) < 6)
                    continue;

                Location tntLocation = new Location(location.getWorld(), location.getX() + offsetX, location.getY() + 2, location.getZ() + offsetZ);
                tntLocation.getBlock().setType(Material.AIR);
                location.getWorld().spawn(tntLocation, TNTPrimed.class);

                Location tntLocation2 = new Location(location.getWorld(), location.getX() + offsetX, location.getY(), location.getZ() + offsetZ);
                tntLocation2.getBlock().setType(Material.AIR);
                location.getWorld().spawn(tntLocation2, TNTPrimed.class);

                Location tntLocation3 = new Location(location.getWorld(), location.getX() + offsetX, location.getY() - 2, location.getZ() + offsetZ);
                tntLocation3.getBlock().setType(Material.AIR);
                location.getWorld().spawn(tntLocation3, TNTPrimed.class);
            }
        }

        this.runKitAbility(20, duration, null, null);
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