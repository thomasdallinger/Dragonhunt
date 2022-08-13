package com.tharox.dragonhunt.modifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ModifierSelection {
    ArrayList<Modifier> lowImpact = new ArrayList<>();
    ArrayList<Modifier> mediumImpact = new ArrayList<>();
    ArrayList<Modifier> highImpact = new ArrayList<>();
    int positive = 0; // needs to be 1 after modifier selection
    int negative = 0; // needs to be 2 after modifier selection

    public ModifierSelection() {
        lowImpact.addAll(Arrays.asList(new HighSpeed(), new ExplosiveCreepers(), new CaveSpiders(), new PowderOnly(),
                new EndlessNight()));
        mediumImpact.addAll(Arrays.asList(new FireWeakness(), new EasyDifficulty(), new WitherSkeletons(),
                new ImmuneSkeletons(), new HardDifficulty()));
        highImpact.addAll(Arrays.asList(new Heavyweight(), new Diamonds(), new EightHearts(), new Lightweight(),
                new PowerfulMobs()));

        Random rand = new Random();
        int random = rand.nextInt(lowImpact.size()); // generates random value from 0 to list size - 1
        lowImpact.get(random).setActive(true);

        if (lowImpact.get(random).isPositive()) {
            positive++;
        } else {
            negative++;
        }

        // pos pos -> re-roll, 1 1, neg neg
        do {
            random = rand.nextInt(mediumImpact.size());
            mediumImpact.get(random).setActive(true);

            if (mediumImpact.get(random).isPositive()) {
                positive++;
                if (positive > 1) {
                    mediumImpact.get(random).setActive(false);
                }
            } else {
                negative++;
                positive = 1;
            }
        } while (positive > 1);

        // 2 1 neg -> re-roll, 1 1 neg, 1 1 pos -> re-roll
        do {
            random = rand.nextInt(highImpact.size());
            highImpact.get(random).setActive(true);

            if (highImpact.get(random).isPositive()) {
                positive++;
                if (positive > 1) {
                    highImpact.get(random).setActive(false);
                } else {
                    negative = 2;
                }
            } else {
                negative++;
                if (negative > 2) {
                    highImpact.get(random).setActive(false);
                    positive = 0;
                } else {
                    positive = 1;
                }
            }
        } while (positive > 1 || negative > 2);
    }
}
