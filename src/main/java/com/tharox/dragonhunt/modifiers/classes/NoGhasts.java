package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class NoGhasts extends Modifier {
    public static String title = "No Ghosts";
    public static String lore = "Ghasts do not spawn.";
    public static Material material = Material.GHAST_TEAR;
    public static boolean positive = true;
    public static int impact = 1;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        NoGhasts.active = active;
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
