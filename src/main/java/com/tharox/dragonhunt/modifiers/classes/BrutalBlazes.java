package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class BrutalBlazes extends Modifier {
    public static String title = "Brutal Blazes";
    public static String lore = "Blazes have 5x health.";
    public static Material material = Material.BLAZE_ROD;
    public static boolean positive = false;
    public static int impact = 3;
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
