package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class Iron extends Modifier {
    public static String title = "Too Many Resources";
    public static String lore = "Iron ores drop 5 iron, chance to drop gold.";
    public static Material material = Material.IRON_INGOT;
    public static boolean positive = true;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        Iron.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
