package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Vanisher extends Kit {
    static int duration = 15;
    static int cooldown = 120;

    public static final String title = HexColor.ASSASSIN + KitClass.ASSASSIN + " Vanisher";
    public static final String lore = ChatColor.GRAY + "Disappear in front of your enemies!\n\n"
            + ChatColor.DARK_GRAY + "ᴋɪᴛ ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation you get " + HexColor.ASSASSIN + "Speed 3"
            + ChatColor.GRAY + " and " + HexColor.ASSASSIN + "Invisibility" + ChatColor.GRAY + ".\n"
            + ChatColor.GRAY + "You cannot take any damage and your inventory\n"
            + ChatColor.GRAY + "will disappear while your ability is active.\n\n"
            + ChatColor.GRAY + "Duration: " + HexColor.ASSASSIN + duration + " sec\n"
            + ChatColor.GRAY + "Cooldown: " + HexColor.RED + cooldown + " sec\n";
    public static final Material material = Material.ENDER_PEARL;
    public static final int slot = 2;

    public Vanisher(Player player) {
        super(player);

        KitClass.assassins.add(player);
    }

    @Override
    public void executeAbility() {
        GameState.actionBarNeededForKitAbility.put(player, true);
        player.sendMessage(title + ChatColor.GRAY + " Kit Ability is active.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration * 20, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration * 20, 9));

        ItemStack[] items = player.getInventory().getContents();

        this.runKitAbility(2, duration, () -> {
            player.getInventory().setContents(items);
        }, () -> {
            player.getInventory().clear();
        });
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
