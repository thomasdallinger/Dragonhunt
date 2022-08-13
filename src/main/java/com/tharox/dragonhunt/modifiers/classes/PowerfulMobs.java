package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class PowerfulMobs extends Modifier {
    public static String title = "Powerful Mobs";
    public static String lore = "Certain mobs have armor and weapons.";
    public static Material material = Material.IRON_AXE;
    public static boolean positive = false;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        PowerfulMobs.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
