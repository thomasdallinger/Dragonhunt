package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.arrows.GrapplerListener;
import com.tharox.dragonhunt.upgrades.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@DragonhuntKit
public class Grappler extends Kit {
    static int cooldown = GrapplerListener.cooldown / 20;

    public static final String title = HexColor.FLANKER + KitClass.FLANKER + " Grappler";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Pull yourself to different locations\n"
            + ChatColor.GRAY + "with this Grappling Hook.\n"
            + ChatColor.GRAY + "Arrows shot deal no damage.\n\n"
            + ChatColor.GRAY + "Max. Arrow Distance: "
            + ChatColor.WHITE + GrapplerListener.maxArrowDistance + " Blocks\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + "Instantly\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.CROSSBOW;
    public static final int slot = 29;

    public Grappler(Player player) {
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
