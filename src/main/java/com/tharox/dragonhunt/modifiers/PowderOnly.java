package com.tharox.dragonhunt.modifiers;

@DragonhuntModifier
public class PowderOnly extends Modifier {
    public static String title = "Powder Only";
    public static String lore = "Blazes drop one Blaze Powder on death.";
    public static boolean positive = false;
    public static int impact = 1;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        PowderOnly.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
