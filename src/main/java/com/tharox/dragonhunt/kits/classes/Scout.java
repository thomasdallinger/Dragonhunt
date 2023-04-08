package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.spectate.Spectate;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@DragonhuntKit
public class Scout extends Kit {
    static int duration = Spectate.getDuration();
    static int cooldown = 70;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Scout";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Use" + ChatColor.WHITE + " Spectator Mode\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.ENDER_EYE;
    public static final int slot = 21;

    public Scout(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    @Override
    public void executeAbility() {
        GameState.kitAbilityActive.put(player, getDuration());
        new Spectate(player);

        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
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
