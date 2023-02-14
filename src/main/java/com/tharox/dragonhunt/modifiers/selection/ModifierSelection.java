package com.tharox.dragonhunt.modifiers.selection;

import com.tharox.dragonhunt.modifiers.classes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ModifierSelection {
    ArrayList<Modifier> lowImpact = new ArrayList<>();
    ArrayList<Modifier> mediumImpact = new ArrayList<>();
    ArrayList<Modifier> highImpact = new ArrayList<>();
    int positive = 0; // needs to be 2 after modifier selection
    int negative = 0; // needs to be 1 after modifier selection

    public ModifierSelection() {
        // PowderOnly -> Remove
        lowImpact.addAll(Arrays.asList(
                new SpeedyMobs(),
                new ExplosiveCreepers(),
                new CaveSpiders(),
                new SpeedyComback(),
                new EndlessNight()));
        mediumImpact.addAll(Arrays.asList(
                new FireWeakness(),
                new MoreXP(),
                new WitherSkeletons(),
                new ImmuneSkeletons(),
                new EnhancedArrowEffects()));
        highImpact.addAll(Arrays.asList(
                new Hunger(),
                new Diamonds(),
                new EightHearts(),
                new FireMobs(),
                new PowerfulMobs()));

        Random rand = new Random();
        int random = rand.nextInt(lowImpact.size()); // generates random value from 0 to list size - 1
        lowImpact.get(random).setActive(true);

        if (lowImpact.get(random).isPositive()) {
            positive++;
        } else {
            negative++;
        }

        // pos pos -> fine, pos neg -> fine, neg neg -> re-roll
        do {
            random = rand.nextInt(mediumImpact.size());
            mediumImpact.get(random).setActive(true);

            if (!mediumImpact.get(random).isPositive()) {
                negative++;
                if (negative > 1) {
                    mediumImpact.get(random).setActive(false);
                }
            } else {
                positive++;
                negative = 1;
            }
        } while (negative > 1);

        // 2 pos 1 neg -> fine, 1 pos 2 neg -> re-roll
        do {
            random = rand.nextInt(highImpact.size());
            highImpact.get(random).setActive(true);

            if (!highImpact.get(random).isPositive()) {
                negative++;
                if (negative > 1) {
                    highImpact.get(random).setActive(false);
                } else {
                    positive = 2;
                }
            } else {
                positive++;
                if (positive > 2) {
                    highImpact.get(random).setActive(false);
                    negative = 0;
                } else {
                    negative = 1;
                }
            }
        } while (negative > 1 || positive > 2);
    }
}
