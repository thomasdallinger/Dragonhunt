package com.tharox.dragonhunt.modifiers;

@DragonhuntModifier
public class HighSpeed extends Modifier {
    public static String title = "High Speed";
    public static String lore = "All players have Speed I permanently.";
    public static boolean positive = true;
    public static int impact = 1;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        HighSpeed.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
