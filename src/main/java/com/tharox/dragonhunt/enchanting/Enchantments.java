package com.tharox.dragonhunt.enchanting;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class Enchantments {
    // a list of all weapons affected by sword enchantments
    protected static List<Material> swords = List.of(
            Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD);

    // Swords
    public static HashMap<Player, Integer> Sharpness = new HashMap<>();
    public static HashMap<Player, Integer> FireAspect = new HashMap<>();

    // Bows
    public static HashMap<Player, Integer> Power = new HashMap<>();
    public static HashMap<Player, Integer> Punch = new HashMap<>();
    public static HashMap<Player, Integer> Flame = new HashMap<>();

    // Crossbows
    public static HashMap<Player, Integer> Multishot = new HashMap<>();

    // Armor
    public static HashMap<Player, Integer> Protection = new HashMap<>();
    public static HashMap<Player, Integer> Thorns = new HashMap<>();
    public static HashMap<Player, Integer> FeatherFalling = new HashMap<>();

    public Enchantments() {
        // set default values: level 1 can be purchased
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sharpness.put(player, 1);
            FireAspect.put(player, 1);
            Power.put(player, 1);
            Punch.put(player, 1);
            Flame.put(player, 1);
            Multishot.put(player, 1);
            Protection.put(player, 1);
            Thorns.put(player, 1);
            FeatherFalling.put(player, 1);
        }

        enchantGear();
    }

    public void enchantGear() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (ItemStack item : player.getInventory()) {
                        if (item == null) continue;

                        // Make all items that are can break unbreakable (if they are not unbreakable already).
                        ItemMeta meta = item.getItemMeta();

                        if (meta != null && !meta.isUnbreakable() && item.getType().getMaxDurability() > 0) {
                            meta.setUnbreakable(true);
                            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                            item.setItemMeta(meta);
                        }

                        // Swords
                        if (swords.contains(item.getType())) {
                            if (Sharpness.get(player) > 1)
                                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, Sharpness.get(player) - 1);

                            if (FireAspect.get(player) > 1)
                                item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, FireAspect.get(player) - 1);
                        }

                        // Bows
                        if (item.getType().equals(Material.BOW)) {
                            if (Power.get(player) > 1)
                                item.addEnchantment(Enchantment.ARROW_DAMAGE, Power.get(player) - 1);

                            if (Punch.get(player) > 1)
                                item.addEnchantment(Enchantment.ARROW_KNOCKBACK, Punch.get(player) - 1);

                            if (Flame.get(player) > 1)
                                item.addEnchantment(Enchantment.ARROW_FIRE, Flame.get(player) - 1);
                        }

                        // Crossbows
                        if (item.getType().equals(Material.CROSSBOW)) {
                            if (Multishot.get(player) > 1)
                                item.addEnchantment(Enchantment.MULTISHOT, Multishot.get(player) - 1);
                        }
                    }

                    // Armor
                    ItemStack[] armor = player.getInventory().getArmorContents();
                    ItemStack chestplate = player.getInventory().getChestplate();
                    ItemStack boots = player.getInventory().getBoots();

                    for (ItemStack armorPiece : armor) {
                        if (armorPiece == null) continue;

                        if (Protection.get(player) > 1) {
                            armorPiece.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, Protection.get(player) - 1);
                        }

                        if (Thorns.get(player) > 1) {
                            chestplate.addUnsafeEnchantment(Enchantment.THORNS, Thorns.get(player) - 1);
                        }
                    }

                    // boots only
                    if (boots != null) {
                        if (FeatherFalling.get(player) > 1) {
                            boots.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, FeatherFalling.get(player) - 1);
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
