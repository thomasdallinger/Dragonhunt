package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class PowerfulMobs extends Modifier {
    public static String title = "Powerful Mobs";
    public static String lore = "Equippable mobs have weapons and armor.";
    public static Material material = Material.IRON_AXE;
    public static boolean positive = false;
    public static int impact = 3;
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
