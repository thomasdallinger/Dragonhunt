package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class FireWeakness extends Modifier {
    public static String title = "Weakened By Flames";
    public static String lore = "Entities shot with a Flame Bow are weakened.";
    public static Material material = Material.FIRE_CHARGE;
    public static boolean positive = true;
    public static int impact = 2;
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
