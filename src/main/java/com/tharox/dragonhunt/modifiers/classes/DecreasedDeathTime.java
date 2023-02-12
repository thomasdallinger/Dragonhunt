package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class DecreasedDeathTime extends Modifier {
    public static String title = "Speedy Comeback";
    public static String lore = "The death timer is reduced.";
    public static Material material = Material.CLOCK;
    public static boolean positive = true;
    public static int impact = 1;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        DecreasedDeathTime.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
