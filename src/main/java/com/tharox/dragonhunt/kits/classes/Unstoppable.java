package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Unstoppable extends Kit {
    static int duration = 12;
    static int cooldown = 120;

    public static final String title = HexColor.TANK + KitClass.TANK + " Unstoppable";
    public static final String lore = ChatColor.GRAY + "Become unstoppable for a short time!\n\n"
            + ChatColor.DARK_GRAY + "ᴋɪᴛ ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation you become " + HexColor.TANK + "invulnerable\n"
            + ChatColor.GRAY + "to all incoming damage.\n\n"
            + ChatColor.GRAY + "Duration: " + HexColor.TANK + duration + " sec\n"
            + ChatColor.GRAY + "Cooldown: " + HexColor.RED + cooldown + " sec\n";
    public static final Material material = Material.BARRIER;
    public static final int slot = 11;

    public Unstoppable(Player player) {
        super(player);

        KitClass.tanks.add(player);
    }

    @Override
    public void executeAbility() {
        GameState.actionBarNeededForKitAbility.put(player, true);
        player.sendMessage(title + ChatColor.GRAY + " Kit Ability is active.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration * 20, 9));

        this.runKitAbility(2, duration, null, null);
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
