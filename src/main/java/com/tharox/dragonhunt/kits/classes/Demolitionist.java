package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Demolitionist extends Kit {
    static int duration = 35;
    static int cooldown = 180;

    public static final String title = HexColor.ASSASSIN + KitClass.ASSASSIN + " Demolitionist";
    public static final String lore = ChatColor.DARK_GRAY.toString()
            + ChatColor.ITALIC + "Explosions Everywhere\n\n"
            + ChatColor.DARK_GRAY + "ᴋɪᴛ ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation " + ChatColor.WHITE + "3" + ChatColor.GRAY + " layers of "
            + ChatColor.WHITE + "TNT" + ChatColor.GRAY + " will spawn around you.\n"
            + ChatColor.GRAY + "You cannot take any damage from the explosion.\n"
            + ChatColor.GRAY + "You cannot move while the explosion occurs.\n\n"
            + ChatColor.GRAY + "Duration: " + ChatColor.WHITE + duration + " sec\n"
            + ChatColor.GRAY + "Cooldown: " + HexColor.RED + cooldown + " sec\n";
    public static final Material material = Material.TNT;
    public static final int slot = 3;

    public Demolitionist(Player player) {
        super(player);

        KitClass.assassins.add(player);
    }

    @Override
    public void executeAbility() {
        GameState.actionBarNeededForKitAbility.put(player, true);
        player.sendMessage(title + ChatColor.GRAY + " Kit Ability is active.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration * 20, 9));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration * 20, 9));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration * 20, 100));

        Location location = player.getLocation();

        if (location.getWorld() == null) return;

        for (int offsetX = -25; offsetX < 26; offsetX += 3) {
            for (int offsetZ = -25; offsetZ < 26; offsetZ += 3) {
                if (Math.abs(offsetX) < 6 && Math.abs(offsetZ) < 6)
                    continue;

                Location tntLocation = new Location(location.getWorld(),
                        location.getX() + offsetX, location.getY() + 2, location.getZ() + offsetZ);
                tntLocation.getBlock().setType(Material.AIR);
                location.getWorld().spawn(tntLocation, TNTPrimed.class);

                Location tntLocation2 = new Location(location.getWorld(),
                        location.getX() + offsetX, location.getY(), location.getZ() + offsetZ);
                tntLocation2.getBlock().setType(Material.AIR);
                location.getWorld().spawn(tntLocation2, TNTPrimed.class);

                Location tntLocation3 = new Location(location.getWorld(),
                        location.getX() + offsetX, location.getY() - 2, location.getZ() + offsetZ);
                tntLocation3.getBlock().setType(Material.AIR);
                location.getWorld().spawn(tntLocation3, TNTPrimed.class);
            }
        }

        this.runKitAbility(2, duration, null, null);
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}