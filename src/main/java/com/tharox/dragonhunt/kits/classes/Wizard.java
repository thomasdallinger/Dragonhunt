package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.enchanting.EnchantingInventory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@DragonhuntKit
public class Wizard extends Kit {
    static int cooldown = 0;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Wizard";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Opens the " + ChatColor.WHITE + "Enchanting" + ChatColor.GRAY + " inventory.\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + "Instantly\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.ENCHANTING_TABLE;
    public static final int slot = 21;

    public Wizard(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    @Override
    public void executeAbility() {
        player.openInventory(new EnchantingInventory(Main.getInstance().getConfig(), player).enchant);
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
