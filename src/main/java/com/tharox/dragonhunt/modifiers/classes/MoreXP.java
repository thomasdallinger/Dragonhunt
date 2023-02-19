package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class MoreXP extends Modifier {
    public static String title = "More XP";
    public static String lore = "Players receive XP points for surviving every 3 min.";
    public static Material material = Material.EXPERIENCE_BOTTLE;
    public static boolean positive = true;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        MoreXP.active = active;
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
