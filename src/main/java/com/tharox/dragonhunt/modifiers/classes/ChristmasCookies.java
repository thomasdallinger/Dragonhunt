package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class ChristmasCookies extends Modifier {
    public static String title = "Christmas Cookies";
    public static String lore = "Animals drop cookies which grant Regeneration 2.";
    public static Material material = Material.COOKIE;
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