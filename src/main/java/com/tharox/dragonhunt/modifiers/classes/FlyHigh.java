package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class FlyHigh extends Modifier {
    public static String title = "Fly High";
    public static String lore = "Levitated enemies float longer.";
    public static Material material = Material.FEATHER;
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
