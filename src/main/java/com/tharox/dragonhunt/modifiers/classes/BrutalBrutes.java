package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class BrutalBrutes extends Modifier {
    public static String title = "Brutal Brutes";
    public static String lore = "Piglin Brutes have enchanted Netherite Axes and Armor.";
    public static Material material = Material.NETHERITE_AXE;
    public static boolean positive = false;
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
