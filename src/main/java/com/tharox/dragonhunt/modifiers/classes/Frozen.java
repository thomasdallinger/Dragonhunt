package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class Frozen extends Modifier {
    public static String title = "Frozen";
    public static String lore = "Blinded enemies become frozen.";
    public static Material material = Material.BLUE_ICE;
    public static boolean positive = true;
    public static int impact = 1;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        Frozen.active = active;
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
