package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class SpeedyMobs extends Modifier {
    public static String title = "Speedy Mobs";
    public static String lore = "All Mobs permanently have Speed 1.";
    public static Material material = Material.GOLDEN_BOOTS;
    public static boolean positive = false;
    public static int impact = 1;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        SpeedyMobs.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
