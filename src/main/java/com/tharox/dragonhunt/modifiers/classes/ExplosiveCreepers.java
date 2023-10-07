package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class ExplosiveCreepers extends Modifier {
    public static String title = "Explosive Creepers";
    public static String lore = "Creepers are way more explosive than usual.";
    public static Material material = Material.TNT;
    public static boolean positive = false;
    public static int impact = 1;
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
