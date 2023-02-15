package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class EnhancedArrowEffects extends Modifier {
    public static String title = "Enhanced Arrow Effects";
    public static String lore = "Arrow Effects last 9 sec and are level 4.";
    public static Material material = Material.TIPPED_ARROW;
    public static boolean positive = true;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        EnhancedArrowEffects.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
