package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class FireResistance extends Modifier {
    public static String title = "Fire Resistance";
    public static String lore = "Consuming food grants short-term Fire Resistance.";
    public static Material material = Material.LAVA_CAULDRON;
    public static boolean positive = true;
    public static int impact = 1;
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
