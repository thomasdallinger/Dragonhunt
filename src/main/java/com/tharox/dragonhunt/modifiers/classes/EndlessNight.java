package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class EndlessNight extends Modifier {
    public static String title = "Bad Weather";
    public static String lore = "Endless Night and Rain.";
    public static Material material = Material.CLOCK;
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
