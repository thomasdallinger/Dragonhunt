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

    public ModifierSelection() {
        lowImpact.addAll(Arrays.asList(
                new Frozen(),
                new FastComback(),
                new FireResistance(),

                new SpeedyMobs(),
                new ExplosiveCreepers(),
                new CaveSpiders(),
                new EndlessNight()
        ));
        mediumImpact.addAll(Arrays.asList(
                new NeverHungry(),
                new MoreFood(),
                new ChristmasCookies(),
                new Fisher(),
                new FlyHigh(),
                new Iron(),
                new NoAnnoyingMobs(),

                new ForeverHunger(),
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
                new Heartbroken(),
                new FireMobs(),
                new PowerfulMobs(),
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

        random = rand.nextInt(highImpact.size());
        highImpact.get(random).setActive(true);

        int finalRandom3 = random;
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE games SET modifier_high = '" + highImpact.get(finalRandom3).getTitle()
                    + "' WHERE id = '" + GameState.gameID + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}
