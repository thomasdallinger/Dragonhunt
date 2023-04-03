package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class BrutalBrutes extends Modifier {
    public static String title = "Brutal Brutes";
    public static String lore = "Piglin Brutes have enchanted Netherite Axes.";
    public static Material material = Material.NETHERITE_AXE;
    public static boolean positive = false;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        BrutalBrutes.active = active;
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
