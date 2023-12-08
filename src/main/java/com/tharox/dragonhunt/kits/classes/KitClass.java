package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.modifiers.classes.Heartbroken;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;

public class KitClass implements Listener {
    public static final String ASSASSIN = "⚔";
    public static final String TANK = "\uD83D\uDEE1";
    public static final String SUPPORT = "❤";
    public static final String FLANKER = "▲";

    public static ArrayList<Player> assassins = new ArrayList<>();
    public static ArrayList<Player> tanks = new ArrayList<>();
    public static ArrayList<Player> supports = new ArrayList<>();
    public static ArrayList<Player> flankers = new ArrayList<>();

    public KitClass() {
        run();
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    double maxHealth = Objects.requireNonNull(
                            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

                    if (tanks.contains(player)) {
                        double heartsToAdd = 3;

                        if (maxHealth <= 20) {
                            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(
                                    maxHealth + heartsToAdd * 2);
                            player.setHealth(maxHealth + heartsToAdd * 2);
                        }

                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));

                        continue;
                    }

                    if (supports.contains(player)) {
                        double heartsToRemove = 2;

                        if (maxHealth >= 20 || (Heartbroken.active && maxHealth >= 16)) {
                            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(
                                    maxHealth - heartsToRemove * 2);
                            player.setHealth(maxHealth - heartsToRemove * 2);
                        }

                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0));

                        continue;
                    }

                    if (flankers.contains(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 40, 1));
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
