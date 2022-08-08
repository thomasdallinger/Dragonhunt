package com.tharox.dragonhunt.modifiers;

@DragonhuntModifier
public class EasyDifficulty extends Modifier {
    public static String title = "Difficulty: Easy";
    public static String lore = "Low hunger points depletion, less mobs spawns, weak mobs.";
    public static boolean positive = true;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        EasyDifficulty.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
