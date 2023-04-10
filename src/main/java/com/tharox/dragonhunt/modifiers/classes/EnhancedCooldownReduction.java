package com.tharox.dragonhunt.modifiers.classes;

import org.bukkit.Material;

@DragonhuntModifier
public class EnhancedCooldownReduction extends Modifier {
    public static String title = "Enhanced Cooldown Reduction";
    public static String lore = "Cooldown Reduction is increased from 5 to 10 seconds per purchase.";
    public static Material material = Material.NETHER_STAR;
    public static boolean positive = true;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        EnhancedCooldownReduction.active = active;
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
