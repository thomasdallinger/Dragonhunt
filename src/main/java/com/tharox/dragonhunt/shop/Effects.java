package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.modifiers.classes.ForeverHunger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Effects {
    // Permanent effects
    public static HashMap<Player, Integer> Speed = new HashMap<>();
    public static HashMap<Player, Integer> JumpBoost = new HashMap<>();
    public static HashMap<Player, Integer> Haste = new HashMap<>();
    public static HashMap<Player, Integer> DolphinsGrace = new HashMap<>();
    public static HashMap<Player, Integer> FireResistance = new HashMap<>();
    public static HashMap<Player, Integer> Resistance = new HashMap<>();
    public static HashMap<Player, Integer> Strength = new HashMap<>();

    // Arrow effects
    public static HashMap<Player, Boolean> ArrowEffectsUnlocked = new HashMap<>();
    public static HashMap<Player, ItemStack> ArrowEffectType = new HashMap<>();

    // Arrow effects
    public static HashMap<Player, Integer> ArrowMiningFatigue = new HashMap<>();
    public static HashMap<Player, Integer> ArrowPoison = new HashMap<>();
    public static HashMap<Player, Integer> ArrowBlindness = new HashMap<>();
    public static HashMap<Player, Integer> ArrowWeakness = new HashMap<>();
    public static HashMap<Player, Integer> ArrowLevitation = new HashMap<>();

    // Special effects
    public static HashMap<Player, Boolean> ResetLeadersUpgrades = new HashMap<>();

    public Effects() {
        // set default values: level 1 can be purchased
        for (Player player : Bukkit.getOnlinePlayers()) {
            Speed.put(player, 1);
            JumpBoost.put(player, 1);
            Haste.put(player, 1);
            DolphinsGrace.put(player, 1);
            FireResistance.put(player, 1);
            Resistance.put(player, 1);
            Strength.put(player, 1);

            ArrowMiningFatigue.put(player, 1);
            ArrowPoison.put(player, 1);
            ArrowBlindness.put(player, 1);
            ArrowWeakness.put(player, 1);
            ArrowLevitation.put(player, 1);

            ResetLeadersUpgrades.put(player, false);

            ArrowEffectsUnlocked.put(player, false);
            ArrowEffectType.put(player, new ItemStack(Material.ARROW));
        }

        applyEffects();
    }

    public void applyEffects() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Speed.get(player) > 1)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40,
                                Speed.get(player) - 2));

                    if (JumpBoost.get(player) > 1)
                        // Higher amplifier than any other permanent effect due to Jump Boost 1 being very weak.
                        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 40,
                                JumpBoost.get(player) - 1));

                    if (Haste.get(player) > 1)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,
                                40, Haste.get(player) - 2));

                    if (DolphinsGrace.get(player) > 1)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE,
                                40, DolphinsGrace.get(player) - 2));

                    if (FireResistance.get(player) > 1)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
                                40, FireResistance.get(player) - 2));

                    if (Resistance.get(player) > 1)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
                                40, Resistance.get(player) - 2));

                    if (Strength.get(player) > 1)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
                                40, Strength.get(player) - 2));

                    if (player.getWorld().getName().equals("world")) {
                        // Duration of Night Vision needs to be longer to avoid flashing.
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,
                                400, 0));
                    }

                    if (ForeverHunger.active) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40, 2));
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
