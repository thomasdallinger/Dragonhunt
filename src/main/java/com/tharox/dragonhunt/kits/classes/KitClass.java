package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.Main;
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
    public static final String MOBILITY = "▲";

    public static ArrayList<Player> assassins = new ArrayList<>();
    public static ArrayList<Player> tanks = new ArrayList<>();
    public static ArrayList<Player> supports = new ArrayList<>();
    public static ArrayList<Player> mobilities = new ArrayList<>();

    public KitClass() {
        run();
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (tanks.contains(player)) {
                        double maxHealth = Objects.requireNonNull(
                                player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
                        double heartsToAdd = 2;

                        if (maxHealth <= 20) {
                            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(
                                    maxHealth + heartsToAdd * 2);
                            player.setHealth(maxHealth + heartsToAdd * 2);
                        }
                        continue;
                    }

                    if (mobilities.contains(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 40, 1));
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        Player player;

        if (entity instanceof Player) {
            player = (Player) entity;
        } else return;

        if (!KitClass.supports.contains(player)) return;

        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.EATING
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) {
            event.setAmount(event.getAmount() * 1.2);
        }
    }
}
