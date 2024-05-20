package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class DoubleChestLoot extends Modifier {
    public static String title = "Sweet Loot";
    public static String lore = "Chests drop 2x loot.";
    public static Material material = Material.CHEST;
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
