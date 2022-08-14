package com.tharox.dragonhunt.crafting;

import com.tharox.dragonhunt.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomRecipes {

    public CustomRecipes() {
        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 1);

        changePickaxeRecipe(pickaxe, "wood_pickaxe", Material.OAK_PLANKS);
        changePickaxeRecipe(pickaxe, "gold_pickaxe", Material.GOLD_INGOT);
        changePickaxeRecipe(pickaxe, "stone_pickaxe", Material.COBBLESTONE);

        ItemStack axe = new ItemStack(Material.IRON_AXE);
        axe.addEnchantment(Enchantment.DIG_SPEED, 1);

        changeAxeRecipe(axe, "wood_axe", Material.OAK_PLANKS);
        changeAxeRecipe(axe, "gold_axe", Material.GOLD_INGOT);
        changeAxeRecipe(axe, "stone_axe", Material.COBBLESTONE);

        ItemStack hoe = new ItemStack(Material.IRON_HOE);
        hoe.addEnchantment(Enchantment.DIG_SPEED, 1);

        changeHoeRecipe(hoe, "wood_hoe", Material.OAK_PLANKS);
        changeHoeRecipe(hoe, "gold_hoe", Material.GOLD_INGOT);
        changeHoeRecipe(hoe, "stone_hoe", Material.COBBLESTONE);

        ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
        shovel.addEnchantment(Enchantment.DIG_SPEED, 1);

        changeShovelRecipe(shovel, "wood_shovel", Material.OAK_PLANKS);
        changeShovelRecipe(shovel, "gold_shovel", Material.GOLD_INGOT);
        changeShovelRecipe(shovel, "stone_shovel", Material.COBBLESTONE);

        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "wood_sword", Material.OAK_PLANKS);
        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "gold_sword", Material.GOLD_INGOT);
        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "stone_sword", Material.COBBLESTONE);

        changeShieldRecipe();
    }

    /**
     * Updates an existing bukkit pickaxe recipe.
     *
     * @param item     item which players get from crafting the recipe
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this pickaxe
     */
    private void changePickaxeRecipe(ItemStack item, String keyName, Material resource) {
        // Create a NamespacedKey for the recipe.
        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyName);

        // Create a custom recipe variable.
        ShapedRecipe recipe = new ShapedRecipe(key, item);

        // The places for the recipe are defined here.
        // The letters can represent anything, and the letters can be anything (Beware: case-sensitive).
        recipe.shape("RRR", " S ", " S ");

        // Set what the letters represent.
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        // Add the recipe to the bukkit recipes.
        Bukkit.addRecipe(recipe);
    }

    /**
     * Updates an existing bukkit axe recipe.
     *
     * @param item     item which players get from crafting the recipe
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this axe
     */
    private void changeAxeRecipe(ItemStack item, String keyName, Material resource) {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyName);
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("RR", "SR", "S ");
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);

        // reversed
        key = new NamespacedKey(Main.getInstance(), keyName + "2");
        recipe = new ShapedRecipe(key, item);
        recipe.shape("RR", "RS", " S");
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
    }

    /**
     * Updates an existing bukkit hoe recipe.
     *
     * @param item     item which players get from crafting the recipe
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this hoe
     */
    private void changeHoeRecipe(ItemStack item, String keyName, Material resource) {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyName);
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("RR", "S ", "S ");
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);

        // reversed
        key = new NamespacedKey(Main.getInstance(), keyName + "2");
        recipe = new ShapedRecipe(key, item);
        recipe.shape("RR", " S", " S");
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
    }

    /**
     * Updates an existing bukkit shovel recipe.
     *
     * @param item     item which players get from crafting the recipe
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this shovel
     */
    private void changeShovelRecipe(ItemStack item, String keyName, Material resource) {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyName);
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("R", "S", "S");
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
    }

    /**
     * Updates an existing bukkit sword recipe.
     *
     * @param item     item which players get from crafting the recipe
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this sword
     */
    private void changeSwordRecipe(ItemStack item, String keyName, Material resource) {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyName);
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("R", "R", "S");
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
    }

    /**
     * Updates the existing bukkit shield recipe.
     */
    private void changeShieldRecipe() {
        ItemStack item = new ItemStack(Material.SHIELD);

        // Change item properties and save the changes.
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.setUnbreakable(true);
        meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Shield");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "unbreakable_shield");
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("SRS", "SSS", " S ");
        recipe.setIngredient('R', Material.IRON_INGOT);
        recipe.setIngredient('S', Material.OAK_PLANKS);

        Bukkit.addRecipe(recipe);
    }
}
