package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class FireMobs extends Modifier {
    public static String title = "On Fire, Literally";
    public static String lore = "Mobs have Fire Aspect 2 Swords and Flame Bows.";
    public static Material material = Material.LAVA_BUCKET;
    public static boolean positive = false;
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
