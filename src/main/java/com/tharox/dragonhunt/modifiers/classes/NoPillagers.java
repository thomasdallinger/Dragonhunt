package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class NoPillagers extends Modifier {
    public static String title = "No Pillagers";
    public static String lore = "The End contains no pillagers.";
    public static Material material = Material.CROSSBOW;
    public static boolean positive = true;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        NoPillagers.active = active;
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
