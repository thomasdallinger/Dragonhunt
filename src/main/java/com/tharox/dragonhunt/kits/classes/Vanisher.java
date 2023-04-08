package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Vanisher extends Kit {
    static int duration = 10;
    static int cooldown = 60;

    public static final String title = HexColor.ASSASSIN + KitClass.ASSASSIN + " Vanisher";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Get " + ChatColor.WHITE + "Speed 3" + ChatColor.GRAY + " and become\n"
            + ChatColor.WHITE + "invisible" + ChatColor.GRAY + " and " + ChatColor.WHITE + "invulnerable"
            + ChatColor.GRAY + ".\n"
            + ChatColor.GRAY + "Your inventory will disappear\n"
            + ChatColor.GRAY + "for the abilities duration.\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.ENDER_EYE;
    public static final int slot = 2;

    public Vanisher(Player player) {
        super(player);

        KitClass.assassins.add(player);
    }

    @Override
    public void executeAbility() {
        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration * 20, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration * 20, 9));

        ItemStack[] items = player.getInventory().getContents();

        this.runKitAbility(20, duration, () -> {
            player.getInventory().setContents(items);
        }, () -> {
            player.getInventory().clear();
        });
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
