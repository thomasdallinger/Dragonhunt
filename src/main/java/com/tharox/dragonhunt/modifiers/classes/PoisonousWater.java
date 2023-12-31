package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class PoisonousWater extends Modifier {
    public static String title = "Poisonous Water";
    public static String lore = "Water gives you Poison";
    public static Material material = Material.WATER_BUCKET;
    public static boolean positive = false;
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
