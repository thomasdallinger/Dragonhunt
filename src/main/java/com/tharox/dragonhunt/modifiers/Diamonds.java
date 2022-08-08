package com.tharox.dragonhunt.modifiers;

@DragonhuntModifier
public class Diamonds extends Modifier {
    public static String title = "Diamonds, Diamonds, Diamonds!";
    public static String lore = "Iron, Gold and Copper Ores drop Diamonds.";
    public static boolean positive = true;
    public static int impact = 3;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        Diamonds.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
