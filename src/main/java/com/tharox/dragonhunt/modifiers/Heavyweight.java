package com.tharox.dragonhunt.modifiers;

import org.bukkit.Material;

@DragonhuntModifier
public class Heavyweight extends Modifier {
    public static String title = "Heavyweight";
    public static String lore = "All players have Strength I permanently.";
    public static Material material = Material.NETHERITE_SWORD;
    public static boolean positive = true;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        Heavyweight.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
