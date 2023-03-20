package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class DoubleChestLoot extends Modifier {
    public static String title = "Sweet Loot";
    public static String lore = "Chests drop double loot.";
    public static Material material = Material.CHEST;
    public static boolean positive = true;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        DoubleChestLoot.active = active;
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
