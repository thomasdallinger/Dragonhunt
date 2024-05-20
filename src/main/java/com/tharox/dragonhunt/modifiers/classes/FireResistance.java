package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class FireResistance extends Modifier {
    public static String title = "Short Resistance";
    public static String lore = "Consuming food grants Fire Resistance.";
    public static Material material = Material.FLINT_AND_STEEL;
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
