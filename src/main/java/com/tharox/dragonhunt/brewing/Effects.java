package com.tharox.dragonhunt.brewing;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Effects {
    // a list of all weapons affected by sword effects
    protected static List<Material> swords = List.of(
            Material.DIAMOND_SWORD, Material.NETHERITE_SWORD);

    // Permanent effects
    public static HashMap<Player, Integer> Speed = new HashMap<>();
    public static HashMap<Player, Integer> Haste = new HashMap<>();
    public static HashMap<Player, Integer> Resistance = new HashMap<>();
    public static HashMap<Player, Integer> Regeneration = new HashMap<>();
    public static HashMap<Player, Integer> ExtraHearts = new HashMap<>();

    // Arrow effects
    public static HashMap<Player, Integer> ArrowEffectLevel = new HashMap<>();
    public static HashMap<Player, PotionEffectType> ArrowEffectType = new HashMap<>();

    // Sword effects
    public static HashMap<Player, Integer> SwordEffectLevel = new HashMap<>();
    public static HashMap<Player, PotionEffectType> SwordEffectType = new HashMap<>();

    public Effects() {
        // set default values: level 1 can be purchased
        for (Player player : Bukkit.getOnlinePlayers()) {
            Speed.put(player, 1);
            Haste.put(player, 1);
            Resistance.put(player, 1);
            Regeneration.put(player, 1);
            ExtraHearts.put(player, 1);
        }

        applyEffects();
    }

    public void applyEffects() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (ItemStack item : player.getInventory()) {
                        if (item == null) continue;

                        // Effects Swords
                        if (swords.contains(item.getType())) {

                        }
                    }

                    if (Speed.get(player) > 1)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, Speed.get(player) - 2));

                    if (Haste.get(player) > 1)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 40, Haste.get(player) - 2));

                    if (Resistance.get(player) > 2)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, Resistance.get(player) - 2));

                    if (Regeneration.get(player) > 1)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, Regeneration.get(player) - 2));
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
