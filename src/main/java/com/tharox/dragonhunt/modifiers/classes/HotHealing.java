package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class HotHealing extends Modifier {
    public static String title = "Hot Healing";
    public static String lore = "Fire heals 0.5 ❤ per second";
    public static Material material = Material.LAVA_BUCKET;
    public static boolean positive = true;
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
