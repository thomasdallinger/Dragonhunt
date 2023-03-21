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
    static int duration = (int) Spectate.getDuration();
    static int cooldown = 200;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Scout";
    public static final String lore = ChatColor.DARK_GRAY.toString()
            + ChatColor.ITALIC + "Use Spectator Mode\n\n"
            + ChatColor.DARK_GRAY + "ᴋɪᴛ ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation you will be put in "
            + ChatColor.WHITE + "Spectator Mode" + ChatColor.GRAY + ".\n\n"
            + ChatColor.GRAY + "Duration: " + ChatColor.WHITE + duration + " sec\n"
            + ChatColor.GRAY + "Cooldown: " + HexColor.RED + cooldown + " sec\n";
    public static final Material material = Material.ENDER_EYE;
    public static final int slot = 21;

    public Scout(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    @Override
    public void executeAbility() {
        GameState.actionBarNeededForKitAbility.put(player, true);
        new Spectate(player);
        player.sendMessage(title + ChatColor.GRAY + " Kit Ability is active.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
