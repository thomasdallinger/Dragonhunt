package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class NoAnnoyingMobs extends Modifier {
    public static String title = "No Annoying Mobs";
    public static String lore = "Piglin Brutes and Ghasts do not spawn.";
    public static Material material = Material.GHAST_TEAR;
    public static boolean positive = true;
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
