package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class NoBrutes extends Modifier {
    public static String title = "No Piglin Brutes";
    public static String lore = "Piglin Brutes do not spawn.";
    public static Material material = Material.GOLDEN_AXE;
    public static boolean positive = true;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        NoBrutes.active = active;
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
