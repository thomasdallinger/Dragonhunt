package com.tharox.dragonhunt.kits.selection;

import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.kits.classes.DragonhuntKit;
import com.tharox.dragonhunt.kits.classes.Kit;
import com.tharox.dragonhunt.kits.classes.KitClass;
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
        inv.setItem(0, Kit.getDisplayItem(HexColor.ASSASSIN + KitClass.ASSASSIN + " Assassin",
                Material.NETHERITE_SWORD,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.GRAY + "Permanent Effect: " + HexColor.ASSASSIN + "5%"
                        + ChatColor.GRAY + " Damage buff"));

        inv.setItem(9, Kit.getDisplayItem(HexColor.TANK + KitClass.TANK + " Tank",
                Material.NETHERITE_CHESTPLATE,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.GRAY + "Base max. Health: " + HexColor.TANK + "12 Hearts"));

        inv.setItem(18, Kit.getDisplayItem(HexColor.SUPPORT + KitClass.SUPPORT + " Support",
                Material.POTION,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.GRAY + "Gains " + HexColor.SUPPORT + "20% more Health"
                        + ChatColor.GRAY + " from all\n"
                        + ChatColor.GRAY + "healing sources (food, saturation, effects)",
                Color.fromRGB(0x1F1F23)));

        inv.setItem(27, Kit.getDisplayItem(HexColor.MOBILITY + KitClass.MOBILITY + " Mobility",
                Material.NETHERITE_BOOTS,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.GRAY + "Special Ability: " + HexColor.MOBILITY + "Double Jump\n"
                        + ChatColor.GRAY + "Permanent Effect: " + HexColor.MOBILITY + "Haste 1"));

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
