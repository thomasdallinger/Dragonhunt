package com.tharox.dragonhunt.modifiers;

import org.bukkit.Material;

@DragonhuntModifier
public class HardDifficulty extends Modifier {
    public static String title = "Difficulty: Hard";
    public static String lore = "Quick hunger points depletion, frequent mobs spawns, powerful mobs.";
    public static Material material = Material.RED_CONCRETE;
    public static boolean positive = false;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        HardDifficulty.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
