package com.tharox.dragonhunt.kitupgrades;

import com.tharox.dragonhunt.kits.Platformer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class KitUpgrades {
    public static HashMap<Player, Integer> PlatformerUpgrade = new HashMap<>();
    public static HashMap<Player, Integer> SkydiverUpgrade = new HashMap<>();
    public static HashMap<Player, Integer> TrapcrafterUpgrade = new HashMap<>();
    public static HashMap<Player, Integer> VanisherUpgrade = new HashMap<>();
    public static HashMap<Player, Integer> Cooldown = new HashMap<>();
    public static HashMap<Player, Integer> Charge = new HashMap<>();
    public static HashMap<Player, Integer> Duration = new HashMap<>();
    public Player player;
    public int level;

    public KitUpgrades(Player player, String upgradeName, int level) {
        this.player = player;
        this.level = level;

        if (upgradeName.contains("Speed Boost")) {
            Platformer.amplifier.put(player, Platformer.amplifier.get(player) + 1);

            if (PlatformerUpgrade.get(player) <= 3) {
                PlatformerUpgrade.put(player, PlatformerUpgrade.get(player) + 1);
            }
        } else if (upgradeName.contains("a")) {
            if (SkydiverUpgrade.get(player) <= 3) {
                SkydiverUpgrade.put(player, SkydiverUpgrade.get(player) + 1);
            }
        } else if (upgradeName.contains("b")) {
            if (TrapcrafterUpgrade.get(player) <= 3) {
                TrapcrafterUpgrade.put(player, TrapcrafterUpgrade.get(player) + 1);
            }
        } else if (upgradeName.contains("c")) {
            if (VanisherUpgrade.get(player) <= 3) {
                VanisherUpgrade.put(player, VanisherUpgrade.get(player) + 1);
            }
        }  else if (upgradeName.contains("Reduce Cooldown")) {
            Platformer.cooldown.put(player, Platformer.cooldown.get(player) - 5);

            if (Cooldown.get(player) <= 3) {
                Cooldown.put(player, Cooldown.get(player) + 1);
            }
        } else if (upgradeName.contains("Quick Charge")) {
            Platformer.chargeTime.put(player, Platformer.chargeTime.get(player) - 1);

            if (Charge.get(player) <= 3) {
                Charge.put(player, Charge.get(player) + 1);
            }
        } else if (upgradeName.contains("Increase Duration")) {
            Platformer.duration.put(player, Platformer.duration.get(player) + 1);

            if (Duration.get(player) <= 3) {
                Duration.put(player, Duration.get(player) + 1);
            }
        }
    }
}
