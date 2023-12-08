package com.tharox.dragonhunt.modifiers.selection;

import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.modifiers.classes.*;

import java.sql.Statement;
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
        lowImpact.addAll(Arrays.asList(
                new Frozen(),
                new NoGhasts(),
                new SpeedyComback(),
                new NeverHungry(),

                new SpeedyMobs(),
                new ExplosiveCreepers(),
                new CaveSpiders(),
                new EndlessNight()
        ));
        mediumImpact.addAll(Arrays.asList(
                new MoreFood(),
                new ChristmasCookies(),
                new FireResistance(),
                new Fisher(),
                new EnhancedArrowEffects(),
                new FlyHigh(),
                new Iron(),
                new NoBrutes(),

                new WeakenedByFlames(),
                new WitherSkeletons(),
                new ImmuneSkeletons(),
                new BrutalBrutes(),
                new RevivedMobs(),
                new PoisonousWater()
        ));
        highImpact.addAll(Arrays.asList(
                new Diamonds(),
                new NoPillagers(),
                new BonusHearts(),
                new DoubleChestLoot(),
                new EndermanParty(),
                new HotHealing(),

                new BrutalBlazes(),
                new ForeverHunger(),
                new Heartbroken(),
                new FireMobs(),
                new PowerfulMobs(),
                new Bastion(),
                new BadOmen()
        ));

        Random rand = new Random();
        int random = rand.nextInt(lowImpact.size()); // generates random value from 0 to list size - 1
        lowImpact.get(random).setActive(true);

        int finalRandom = random;
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE games SET modifier_low = '" + lowImpact.get(finalRandom).getTitle()
                    + "' WHERE id = '" + GameState.gameID + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });

        if (lowImpact.get(random).isPositive()) {
            positive++;
        } else {
            negative++;
        }

        // pos pos -> fine, pos neg -> fine, neg neg -> re-roll
        do {
            random = rand.nextInt(mediumImpact.size());
            mediumImpact.get(random).setActive(true);

            int finalRandom2 = random;
            DatabaseExecutor.executeDatabaseHandler((con) -> {
                String query = "UPDATE games SET modifier_medium = '" + mediumImpact.get(finalRandom2).getTitle()
                        + "' WHERE id = '" + GameState.gameID + "'";

                Statement stmt = con.createStatement();
                stmt.execute(query);
                stmt.close();
            });

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

            int finalRandom2 = random;
            DatabaseExecutor.executeDatabaseHandler((con) -> {
                String query = "UPDATE games SET modifier_high = '" + highImpact.get(finalRandom2).getTitle()
                        + "' WHERE id = '" + GameState.gameID + "'";

                Statement stmt = con.createStatement();
                stmt.execute(query);
                stmt.close();
            });

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
