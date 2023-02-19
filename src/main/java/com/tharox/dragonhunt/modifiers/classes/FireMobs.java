package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class FireMobs extends Modifier {
    public static String title = "On Fire, Literally";
    public static String lore = "Certain mobs fight with fire.";
    public static Material material = Material.LAVA_BUCKET;
    public static boolean positive = false;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        FireMobs.active = active;
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
