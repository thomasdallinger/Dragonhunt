package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class Diamonds extends Modifier {
    public static String title = "Diamonds, Diamonds, Diamonds!";
    public static String lore = "Diamond ores drop 2x diamonds.";
    public static Material material = Material.DIAMOND;
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
