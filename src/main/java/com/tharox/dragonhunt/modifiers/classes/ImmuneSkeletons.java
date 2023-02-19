package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class ImmuneSkeletons extends Modifier {
    public static String title = "Fighting Is Useless";
    public static String lore = "Skeletons and Wither Skeletons are invulnerable.";
    public static Material material = Material.NETHERITE_CHESTPLATE;
    public static boolean positive = false;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        ImmuneSkeletons.active = active;
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
