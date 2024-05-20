package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class NeverHungry extends Modifier {
    public static String title = "Never Hungry";
    public static String lore = "Players never loose hunger.";
    public static Material material = Material.GOLDEN_CARROT;
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
