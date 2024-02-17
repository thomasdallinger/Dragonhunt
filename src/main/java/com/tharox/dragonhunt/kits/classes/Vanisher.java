package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Vanisher extends Kit {
    static int duration = 8;
    static int cooldown = 40;

    public static final String title = HexColor.ASSASSIN + KitClass.ASSASSIN + " Vanisher";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Vanish in front of your enemies.\n"
            + ChatColor.GRAY + "You become invisible and invulnerable\n"
            + ChatColor.GRAY + "for a short time.\n"
            + HexColor.RED + "Your entire inventory disappears\n"
            + HexColor.RED + "while this ability is active.\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n"
            + HexColor.YELLOW + "⇡ Duration can be increased\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.ENDER_EYE;
    public static final int slot = 3;

    public Vanisher(Player player) {
        super(player);

        KitClass.assassins.add(player);
    }

    @Override
    public void executeAbility() {
        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, getDuration() * 20, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
                getDuration() * 20, 9));

        ItemStack[] items = player.getInventory().getContents();

        this.runKitAbility(20, getDuration(), () -> {
            player.getInventory().setContents(items);
        }, () -> {
            player.getInventory().clear();
        });
    }

    @Override
    public int getDuration() {
        return duration + Upgrades.Duration.getOrDefault(player, 0);
    }

    @Override
    public int getCooldown() {
        return cooldown - Upgrades.Cooldown.getOrDefault(player, 0);
    }
}
