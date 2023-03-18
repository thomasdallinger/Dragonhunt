package com.tharox.dragonhunt.kits.selection;

import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.kits.classes.DragonhuntKit;
import com.tharox.dragonhunt.kits.classes.Kit;
import com.tharox.dragonhunt.kits.classes.KitClass;
import com.tharox.dragonhunt.players.DamageAdjustmentListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.reflections.Reflections;

import java.util.Set;

public class KitInventory {
    public Inventory kits;

    public KitInventory() {
        createInventory();
    }

    public void createInventory() {
        Inventory inv = Bukkit.createInventory(null, 36,
                HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "KIT SELECTION");

        // Set the kit classes
        inv.setItem(0, Kit.getDisplayItem(HexColor.ASSASSIN.toString() + ChatColor.BOLD
                        + KitClass.ASSASSIN + " Assassin" + ChatColor.GRAY + " (Class)",
                Material.NETHERITE_SWORD,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.WHITE + "+" + (int) (DamageAdjustmentListener.assassinDamageBuff * 100 - 100) + "%"
                        + ChatColor.GRAY + " Damage"));

        inv.setItem(9, Kit.getDisplayItem(HexColor.TANK.toString() + ChatColor.BOLD
                        + KitClass.TANK + " Tank" + ChatColor.GRAY + " (Class)",
                Material.NETHERITE_CHESTPLATE,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.WHITE + "+2 " + ChatColor.GRAY + "❤"));

        inv.setItem(18, Kit.getDisplayItem(HexColor.SUPPORT.toString() + ChatColor.BOLD
                        + KitClass.SUPPORT + " Support" + ChatColor.GRAY + " (Class)",
                Material.POTION,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.WHITE + "+20%" + ChatColor.GRAY + " Healing",
                Color.fromRGB(0x1F1F23)));

        inv.setItem(27, Kit.getDisplayItem(HexColor.MOBILITY.toString() + ChatColor.BOLD
                        + KitClass.MOBILITY + " Mobility" + ChatColor.GRAY + " (Class)",
                Material.NETHERITE_BOOTS,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.WHITE + "Double Jump" + ChatColor.GRAY + " Ability,\n"
                        + ChatColor.GRAY + "Haste " + ChatColor.WHITE + "1"));

        // Add the kits
        Reflections reflections = new Reflections("com.tharox.dragonhunt");
        Set<Class<?>> kitClasses = reflections.getTypesAnnotatedWith(DragonhuntKit.class);

        kitClasses.forEach(
                (kitClass) -> {
                    if (Kit.class.isAssignableFrom(kitClass)) {
                        try {
                            inv.setItem((Integer) kitClass.getField("slot").get(null),
                                    Kit.getDisplayItem((String) kitClass.getField("title").get(null),
                                            (Material) kitClass.getField("material").get(null),
                                            (String) kitClass.getField("lore").get(null)));
                            //get(null) because static
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        kits = inv;
    }
}
