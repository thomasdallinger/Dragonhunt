package com.tharox.dragonhunt.modifiers;

@DragonhuntModifier
public class EndlessNight extends Modifier {
    public static String title = "Endless Night";
    public static String lore = "The sun does not rise.";
    public static boolean positive = false;
    public static int impact = 1;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        EndlessNight.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
