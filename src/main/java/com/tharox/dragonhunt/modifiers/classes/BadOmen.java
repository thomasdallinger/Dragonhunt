package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class BadOmen extends Modifier {
    public static String title = "Bad Omen";
    public static String lore = "All Mobs in the Nether wither you.";
    public static Material material = Material.WITHER_SKELETON_SKULL;
    public static boolean positive = false;
    public static int impact = 3;
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
