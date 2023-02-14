package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.spectate.Spectate;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Scout extends Kit {
    static int duration = (int) Spectate.duration[0];
    static int cooldown = 120;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Scout";
    public static final String lore = ChatColor.GRAY + "Be in Spectator Mode for a short time!\n\n"
            + ChatColor.DARK_GRAY + "ᴋɪᴛ ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation you will be put in "
            + HexColor.SUPPORT + "Spectator Mode" + ChatColor.GRAY + ".\n\n"
            + ChatColor.GRAY + "Duration: " + HexColor.SUPPORT + duration + " sec\n"
            + ChatColor.GRAY + "Cooldown: " + HexColor.RED + cooldown + " sec\n";
    public static final Material material = Material.ENDER_EYE;
    public static final int slot = 21;

    public Scout(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    @Override
    public void executeAbility() {
        GameState.activeKitAbilities.put(player, true);
        new Spectate(player);
        player.sendMessage(title + ChatColor.GRAY + " Kit Ability is active.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
