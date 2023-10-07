package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

public class Fisher extends Modifier {
    public static String title = "Fisher";
    public static String lore = "Water mobs drop double food and XP.";
    public static Material material = Material.TROPICAL_FISH;
    public static boolean positive = true;
    public static int impact = 2;
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
