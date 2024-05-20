package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class CaveSpiders extends Modifier {
    public static String title = "Cave Spiders";
    public static String lore = "All Spiders spawning are Cave Spiders with 3x health.";
    public static Material material = Material.SPIDER_EYE;
    public static boolean positive = false;
    public static int impact = 1;
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
