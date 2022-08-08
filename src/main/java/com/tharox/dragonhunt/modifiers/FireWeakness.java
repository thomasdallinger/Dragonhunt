package com.tharox.dragonhunt.modifiers;

@DragonhuntModifier
public class FireWeakness extends Modifier {
    public static String title = "Weakened by Flames";
    public static String lore = "Entities on fire are weakened.";
    public static boolean positive = true;
    public static int impact = 2;
    public static boolean active = false;

    @Override
    public void setActive(boolean active) {
        FireWeakness.active = active;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }
}
