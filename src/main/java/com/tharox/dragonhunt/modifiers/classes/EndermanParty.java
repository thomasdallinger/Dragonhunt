package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class EndermanParty extends Modifier {
    public static String title = "Enderman Party";
    public static String lore = "Endermans have greatly increased spawn rates.";
    public static Material material = Material.ENDER_PEARL;
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
