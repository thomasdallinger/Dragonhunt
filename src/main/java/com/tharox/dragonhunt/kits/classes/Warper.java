package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.arrows.WarperListener;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@DragonhuntKit
public class Warper extends Kit {
    static int cooldown = WarperListener.COOLDOWN / 20;

    public static final String title = HexColor.FLANKER + KitClass.FLANKER + " Warper";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Teleport wherever your arrow lands.\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + "Instantly\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.ENDER_PEARL;
    public static final int slot = 33;

    public Warper(Player player) {
        super(player);

        KitClass.flankers.add(player);
    }

    @Override
    public void executeAbility() {
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCooldown() {
        return cooldown - Upgrades.Cooldown.getOrDefault(player, 0);
    }
}
