package com.tharox.dragonhunt.brewing;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.modifiers.classes.Hunger;
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
    public static HashMap<Player, Integer> Resistance = new HashMap<>();
    public static HashMap<Player, Integer> Regeneration = new HashMap<>();
    public static HashMap<Player, Integer> ExtraHearts = new HashMap<>();

    // Arrow effects
    public static HashMap<Player, Boolean> ArrowEffectsUnlocked = new HashMap<>();
    public static HashMap<Player, ItemStack> ArrowEffectType = new HashMap<>();

    public Effects() {
        // set default values: level 1 can be purchased
        for (Player player : Bukkit.getOnlinePlayers()) {
            Speed.put(player, 1);
            JumpBoost.put(player, 1);
            Resistance.put(player, 1);
            Regeneration.put(player, 1);
            ExtraHearts.put(player, 1);

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
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, Speed.get(player) - 2));

                    if (JumpBoost.get(player) > 1) // Higher amplifier than any other permanent effect due to Jump Boost 1 being very weak.
                        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 40, JumpBoost.get(player)));

                    if (Resistance.get(player) > 2)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, Resistance.get(player) - 2));

                    if (Regeneration.get(player) > 1)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, Regeneration.get(player) - 2));

                    if (player.getWorld().getName().equals("world")) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, 0));
                        // Duration needs to be longer to avoid flashing.
                    }

                    if (Hunger.active) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40, 0));
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
