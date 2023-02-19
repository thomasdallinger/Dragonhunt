package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class SpeedyComback extends Modifier {
    public static String title = "Speedy Comeback";
    public static String lore = "Death times are decreased.";
    public static Material material = Material.IRON_BARS;
    public static boolean positive = true;
    public static int impact = 1;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        SpeedyComback.active = active;
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
