package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class WitherSkeletons extends Modifier {
    public static String title = "Dangerous Fighters";
    public static String lore = "Wither Skeletons have deadly Netherite Swords.";
    public static Material material = Material.NETHERITE_SWORD;
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
