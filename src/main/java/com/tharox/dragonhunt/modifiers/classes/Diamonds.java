package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class Diamonds extends Modifier {
    public static String title = "Diamonds, Diamonds, Diamonds!";
    public static String lore = "Diamond ores drop 5 diamonds.";
    public static Material material = Material.DIAMOND;
    public static boolean positive = true;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        Diamonds.active = active;
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
