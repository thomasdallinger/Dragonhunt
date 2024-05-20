package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class Iron extends Modifier {
    public static String title = "Too Many Resources";
    public static String lore = "Iron ores drop 3x iron, 50% chance to drop gold too.";
    public static Material material = Material.IRON_INGOT;
    public static boolean positive = true;
    public static boolean active = false;

    @Override
    public void setActive(boolean isActive) {
        active = isActive;
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
