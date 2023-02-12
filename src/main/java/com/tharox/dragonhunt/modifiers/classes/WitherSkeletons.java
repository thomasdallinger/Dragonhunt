package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class WitherSkeletons extends Modifier {
    public static String title = "Danger Inbound!";
    public static String lore = "Every 2-5 minutes, a Wither Skeleton spawns at every player's location.";
    public static Material material = Material.WITHER_SKELETON_SPAWN_EGG;
    public static boolean positive = false;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        WitherSkeletons.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
