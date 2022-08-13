package com.tharox.dragonhunt.modifiers;

import org.bukkit.Material;

@DragonhuntModifier
public class Lightweight extends Modifier {
    public static String title = "Lightweight";
    public static String lore = "All players have Weakness I permanently.";
    public static Material material = Material.WOODEN_SWORD;
    public static boolean positive = false;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        Lightweight.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
