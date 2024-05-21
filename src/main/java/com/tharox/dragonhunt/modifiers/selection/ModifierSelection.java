package com.tharox.dragonhunt.modifiers.selection;

import com.tharox.dragonhunt.modifiers.classes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ModifierSelection {
    ArrayList<Modifier> posPool1 = new ArrayList<>();
    ArrayList<Modifier> posPool2 = new ArrayList<>();

    ArrayList<Modifier> negPool1 = new ArrayList<>();
    ArrayList<Modifier> negPool2 = new ArrayList<>();

    public ModifierSelection() {
        posPool1.addAll(Arrays.asList(
                new NeverHungry(),
                new ChristmasCookies(),
                new Fisher(),
                new NoAnnoyingMobs(),
                new Iron(),
                new FlyHigh(),
                new Frozen(),
                new FireResistance()
        ));
        posPool2.addAll(Arrays.asList(
                new MoreFood(),
                new Diamonds(),
                new NoPillagers(),
                new BonusHearts(),
                new DoubleChestLoot(),
                new EndermanParty(),
                new HotHealing(),
                new FastComback(),
                new WeakenedByFlames()
        ));
        negPool1.addAll(Arrays.asList(
                new ForeverHunger(),
                new WitherSkeletons(),
                new ImmuneSkeletons(),
                new BrutalBrutes(),
                new RevivedMobs(),
                new PoisonousWater(),
                new BrutalBlazes()
        ));
        negPool2.addAll(Arrays.asList(
                new SpeedyMobs(),
                new ExplosiveCreepers(),
                new CaveSpiders(),
                new EndlessNight(),
                new Heartbroken(),
                new FireMobs(),
                new PowerfulMobs(),
                new BadOmen()
        ));

        Random rand = new Random();
        int pool = rand.nextInt(2) + 1;

        if (pool == 1) {
            int random = rand.nextInt(posPool1.size()); // generates random value from 0 to list size - 1
            posPool1.get(random).setActive(true);

            int random2;
            do {
                random2 = rand.nextInt(posPool1.size());
            } while (random == random2);

            posPool1.get(random2).setActive(true);

            // NEG POOL 2

            random = rand.nextInt(negPool2.size());
            negPool2.get(random).setActive(true);

            do {
                random2 = rand.nextInt(negPool2.size());
            } while (random == random2);

            negPool2.get(random2).setActive(true);
        } else {
            int random = rand.nextInt(posPool2.size());
            posPool2.get(random).setActive(true);

            int random2;
            do {
                random2 = rand.nextInt(posPool2.size());
            } while (random == random2);

            posPool2.get(random2).setActive(true);

            // NEG POOL 1

            random = rand.nextInt(negPool1.size());
            negPool1.get(random).setActive(true);

            do {
                random2 = rand.nextInt(negPool1.size());
            } while (random == random2);

            negPool1.get(random2).setActive(true);
        }



        /*int finalRandom = random;
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE games SET modifier_low = '" + posPool1.get(finalRandom).getTitle()
                    + "' WHERE id = '" + GameState.gameID + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });*/

        /*int finalRandom2 = random;
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE games SET modifier_medium = '" + posPool2.get(finalRandom2).getTitle()
                    + "' WHERE id = '" + GameState.gameID + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });*/

        /*int finalRandom3 = random;
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE games SET modifier_high = '" + negPool1.get(finalRandom3).getTitle()
                    + "' WHERE id = '" + GameState.gameID + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });*/
    }
}
