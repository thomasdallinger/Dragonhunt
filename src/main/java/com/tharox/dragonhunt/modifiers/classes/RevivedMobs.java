package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class RevivedMobs extends Modifier {
    public static String title = "Revived Mobs";
    public static String lore = "Mobs have a 20% chance of respawning after being killed.";
    public static Material material = Material.ZOMBIE_SPAWN_EGG;
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
