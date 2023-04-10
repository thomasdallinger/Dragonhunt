package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class Bastion extends Modifier {
    public static String title = "Bastion Remnant";
    public static String lore = "The Nether spawn is a Bastion instead of a Fortress.";
    public static Material material = Material.POLISHED_BLACKSTONE_BRICKS;
    public static boolean positive = false;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        Bastion.active = active;
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
