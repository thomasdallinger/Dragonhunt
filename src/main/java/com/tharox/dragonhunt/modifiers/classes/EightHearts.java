package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class EightHearts extends Modifier {
    public static String title = "8 Hearts";
    public static String lore = "All players only have 8 hearts.";
    public static Material material = Material.BARRIER;
    public static boolean positive = false;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        EightHearts.active = active;
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
