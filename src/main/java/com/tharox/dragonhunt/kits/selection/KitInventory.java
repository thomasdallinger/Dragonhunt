package com.tharox.dragonhunt.kits.selection;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.kits.classes.DragonhuntKit;
import com.tharox.dragonhunt.kits.classes.Kit;
import com.tharox.dragonhunt.kits.classes.KitClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.reflections.Reflections;

import java.util.Set;

public class KitInventory {
    public Inventory kits;

    public KitInventory(Player player) {
        createInventory(player);
    }

    public void createInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36,
                HexColor.PURPLE.toString() + ChatColor.BOLD + "PICK A KIT");

        // Set the kit classes
        inv.setItem(0, Kit.getDisplayItem(HexColor.ASSASSIN.toString() + ChatColor.BOLD
                        + KitClass.ASSASSIN + " Assassin" + ChatColor.GRAY + " (Class)",
                Material.NETHERITE_SWORD,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.WHITE + "+2 ⛃" + ChatColor.GRAY + " per Level Up"));

        inv.setItem(9, Kit.getDisplayItem(HexColor.TANK.toString() + ChatColor.BOLD
                        + KitClass.TANK + " Tank" + ChatColor.GRAY + " (Class)",
                Material.NETHERITE_CHESTPLATE,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.WHITE + "+2 ❤" + ChatColor.GRAY + " Total Health"));

        inv.setItem(18, Kit.getDisplayItem(HexColor.SUPPORT.toString() + ChatColor.BOLD
                        + KitClass.SUPPORT + " Support" + ChatColor.GRAY + " (Class)",
                Material.POTION,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.WHITE + "Double" + ChatColor.GRAY + " Health Regeneration",
                Color.fromRGB(0x1F1F23)));

        inv.setItem(27, Kit.getDisplayItem(HexColor.MOBILITY.toString() + ChatColor.BOLD
                        + KitClass.MOBILITY + " Mobility" + ChatColor.GRAY + " (Class)",
                Material.RECOVERY_COMPASS,
                ChatColor.DARK_GRAY + "ᴘᴀꜱꜱɪᴠᴇ\n"
                        + ChatColor.WHITE + "+20%" + ChatColor.GRAY + " Attack Speed,\n"
                        + ChatColor.WHITE + "+40%" + ChatColor.GRAY + " Mining Speed"));

        // Add the kits
        Reflections reflections = new Reflections("com.tharox.dragonhunt");
        Set<Class<?>> kitClasses = reflections.getTypesAnnotatedWith(DragonhuntKit.class);

        kitClasses.forEach(
                (kitClass) -> {
                    if (Kit.class.isAssignableFrom(kitClass)) {
                        try {
                            // Check if the player unlocked the kit.
                            // IMPORTANT: substring(17) needed to remove HexColor + Symbol
                            if (DbValues.getUnlockedKits(player).contains(
                                    ((String) kitClass.getField("title").get(null)).substring(17))
                                    || DbValues.getRole(player).equals("Premium")
                                    || DbValues.getRole(player).equals("VIP")
                                    || DbValues.getRole(player).equals("Admin")) {
                                inv.setItem((Integer) kitClass.getField("slot").get(null),
                                        Kit.getDisplayItem((String) kitClass.getField("title").get(null),
                                                (Material) kitClass.getField("material").get(null),
                                                (String) kitClass.getField("lore").get(null)));
                            }

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
