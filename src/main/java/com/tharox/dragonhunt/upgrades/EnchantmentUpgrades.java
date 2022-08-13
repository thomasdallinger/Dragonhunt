package com.tharox.dragonhunt.upgrades;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class EnchantmentUpgrades {

    // a list of all weapons affected by the Weapon Upgrades
    protected static List<Material> weapons = List.of(
            Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.IRON_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);

    // a list of all tools affected by the Tool Upgrades
    protected static List<Material> tools = List.of(
            Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
            Material.IRON_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            Material.IRON_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE,
            Material.IRON_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL);

    // Weapon Upgrades
    public static HashMap<Player, Integer> Sharpness = new HashMap<>();
    public static HashMap<Player, Integer> Knockback = new HashMap<>();
    public static HashMap<Player, Integer> FireAspect = new HashMap<>();

    // Bow Upgrades
    public static HashMap<Player, Integer> Power = new HashMap<>();
    public static HashMap<Player, Integer> Punch = new HashMap<>();
    public static HashMap<Player, Integer> Flame = new HashMap<>();

    // Tool Upgrades
    public static HashMap<Player, Integer> Efficiency = new HashMap<>();
    public static HashMap<Player, Integer> Unbreakable = new HashMap<>();

    // Armor Upgrades
    public static HashMap<Player, Integer> Protection = new HashMap<>();
    public static HashMap<Player, Integer> Thorns = new HashMap<>();
    public static HashMap<Player, Integer> FeatherFalling = new HashMap<>();

    public EnchantmentUpgrades() {
        enchantGear();
    }

    public void enchantGear() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (ItemStack item : player.getInventory()) {
                        if (item != null) {
                            // Weapons
                            if (weapons.contains(item.getType())) {
                                if (Sharpness.get(player) > 1)
                                    item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, Sharpness.get(player) - 1);

                                if (Knockback.get(player) > 1)
                                    item.addUnsafeEnchantment(Enchantment.KNOCKBACK, Knockback.get(player) - 1);

                                if (FireAspect.get(player) > 1)
                                    item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, FireAspect.get(player) - 1);
                            }

                            // Bow
                            if (item.getType().equals(Material.BOW)) {
                                if (Power.get(player) > 1)
                                    item.addEnchantment(Enchantment.ARROW_DAMAGE, Power.get(player) - 1);

                                if (Punch.get(player) > 1)
                                    item.addEnchantment(Enchantment.ARROW_KNOCKBACK, Punch.get(player) - 1);

                                if (Flame.get(player) > 1)
                                    item.addEnchantment(Enchantment.ARROW_FIRE, Flame.get(player) - 1);
                            }

                            // Tools
                            if (tools.contains(item.getType())) {
                                if (Efficiency.get(player) > 1)
                                    item.addEnchantment(Enchantment.DIG_SPEED, Efficiency.get(player));

                                if (Unbreakable.get(player) > 1) {
                                    ItemMeta meta = item.getItemMeta();
                                    if (meta != null) {
                                        // enchanted look
                                        meta.addEnchant(Enchantment.DURABILITY, 1, true);
                                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                        // make the item unbreakable
                                        meta.setUnbreakable(true);
                                    }
                                    item.setItemMeta(meta);
                                }
                            }
                        }
                    }

                    // Armor
                    ItemStack[] armor = player.getInventory().getArmorContents();
                    ItemStack chestplate = player.getInventory().getChestplate();
                    ItemStack boots = player.getInventory().getBoots();

                    for (ItemStack item : armor) {
                        if (item != null) {
                            if (Protection.get(player) > 1)
                                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, Protection.get(player) - 1);
                        }
                    }

                    if (chestplate != null) {
                        if (Thorns.get(player) > 1)
                            chestplate.addUnsafeEnchantment(Enchantment.THORNS, Thorns.get(player) - 1);
                    }

                    if (boots != null) {
                        if (FeatherFalling.get(player) > 1)
                            boots.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, FeatherFalling.get(player) - 1);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
