package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.grappler.GrappleListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

@DragonhuntKit
public class Grappler extends Kit {
    static int cooldown = GrappleListener.cooldown / 20;

    public static final String title = HexColor.MOBILITY + KitClass.MOBILITY + " Grappler";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Pull yourself to different locations\n"
            + ChatColor.GRAY + "with your Grappling Hook.\n"
            + ChatColor.GRAY + "Arrows shot cannot do damage.\n"
            + ChatColor.GRAY + "Be aware of fall damage.\n\n"
            + ChatColor.GRAY + "Max. Arrow Distance: "
            + ChatColor.WHITE + GrappleListener.maxArrowDistance + " Blocks\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + "Instantly\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.CROSSBOW;
    public static final int slot = 29;

    public Grappler(Player player) {
        super(player);

        KitClass.mobilities.add(player);
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
        return cooldown;
    }
}
