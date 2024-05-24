package com.tharox.dragonhunt.upgrades;

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
    // a list of all weapons affected by melee weapon enchantments
    public static List<Material> meleeWeapons = List.of(
            Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.IRON_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);

    // Swords
    public static HashMap<Player, Integer> Sharpness = new HashMap<>();
    public static HashMap<Player, Integer> Knockback = new HashMap<>();
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

    // Special enchantments
    public static HashMap<Player, Boolean> OPUpgrades = new HashMap<>();

    public Enchantments() {
        // set default values: level 1 can be purchased
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sharpness.put(player, 1);
            Knockback.put(player, 1);
            FireAspect.put(player, 1);
            Power.put(player, 1);
            Punch.put(player, 1);
            Flame.put(player, 1);
            Multishot.put(player, 1);
            Protection.put(player, 1);
            Thorns.put(player, 1);
            FeatherFalling.put(player, 1);
            OPUpgrades.put(player, false);
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
                        if (meta == null) continue;

                        if (!meta.isUnbreakable() && item.getType().getMaxDurability() > 0) {
                            meta.setUnbreakable(true);
                            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                            item.setItemMeta(meta);
                        }

                        // Remove all arrows in a players inventory that are not generated
                        if ((item.getType() == Material.ARROW || item.getType() == Material.TIPPED_ARROW)
                                && !meta.getDisplayName().contains("Generated Arrow")) {
                            item.setType(Material.AIR);
                        }

                        // Swords
                        if (meleeWeapons.contains(item.getType())) {
                            if (Sharpness.get(player) > 1)
                                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, Sharpness.get(player) - 1);

                            if (Knockback.get(player) > 1)
                                item.addUnsafeEnchantment(Enchantment.KNOCKBACK, Knockback.get(player) - 1);

                            if (FireAspect.get(player) > 1)
                                item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, FireAspect.get(player) - 1);

                            if (OPUpgrades.get(player))
                                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                        }

                        // Bows
                        if (item.getType().equals(Material.BOW)) {
                            if (Power.get(player) > 1)
                                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, Power.get(player) - 1);

                            if (Punch.get(player) > 1)
                                item.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, Punch.get(player) - 1);

                            if (Flame.get(player) > 1)
                                item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, Flame.get(player) - 1);

                            if (OPUpgrades.get(player))
                                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
                        }

                        // Crossbows
                        if (item.getType().equals(Material.CROSSBOW) && !meta.getDisplayName().contains("Grappler")) {
                            if (Multishot.get(player) > 1)
                                item.addUnsafeEnchantment(Enchantment.MULTISHOT, Multishot.get(player) - 1);
                        }
                    }

                    // Armor
                    ItemStack[] armor = player.getInventory().getArmorContents();
                    ItemStack chestplate = player.getInventory().getChestplate();
                    ItemStack boots = player.getInventory().getBoots();

                    for (ItemStack armorPiece : armor) {
                        if (armorPiece == null) continue;

                        if (Protection.get(player) > 1) {
                            armorPiece.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,
                                    Protection.get(player) - 1);
                        }

                        if (Thorns.get(player) > 1) {
                            chestplate.addUnsafeEnchantment(Enchantment.THORNS, Thorns.get(player) - 1);
                        }

                        if (OPUpgrades.get(player))
                            armorPiece.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
                    }

                    // Boots only
                    if (boots != null) {
                        if (FeatherFalling.get(player) > 1) {
                            boots.addUnsafeEnchantment(Enchantment.PROTECTION_FALL,
                                    FeatherFalling.get(player) - 1);
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
