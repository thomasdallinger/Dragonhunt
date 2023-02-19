package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class Hunger extends Modifier {
    public static String title = "Forever Hungry";
    public static String lore = "All players have Hunger 1 permanently.";
    public static Material material = Material.ROTTEN_FLESH;
    public static boolean positive = false;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        Hunger.active = active;
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
