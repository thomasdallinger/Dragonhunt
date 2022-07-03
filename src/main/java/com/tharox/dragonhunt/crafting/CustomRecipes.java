package com.tharox.dragonhunt.crafting;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomRecipes {

    public CustomRecipes() {
        changePickaxeRecipe(new ItemStack(Material.IRON_PICKAXE), "wood_pickaxe", Material.OAK_PLANKS);
        changePickaxeRecipe(new ItemStack(Material.IRON_PICKAXE), "gold_pickaxe", Material.GOLD_INGOT);
        changePickaxeRecipe(new ItemStack(Material.IRON_PICKAXE), "stone_pickaxe", Material.COBBLESTONE);
        changeAxeRecipe(new ItemStack(Material.IRON_AXE), "gold_axe", Material.OAK_PLANKS);
        changeAxeRecipe(new ItemStack(Material.IRON_AXE), "wood_axe", Material.GOLD_INGOT);
        changeAxeRecipe(new ItemStack(Material.IRON_AXE), "stone_axe", Material.COBBLESTONE);
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

        // Add the recipe to the bukkit recipes
        Bukkit.addRecipe(recipe);
    }

    /**
     * Updates an existing bukkit axe recipe.
     *
     * @param item     item which players get from crafting the recipe
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this pickaxe
     */
    private void changeAxeRecipe(ItemStack item, String keyName, Material resource) {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyName);
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape(" RR", " SR", " S ");
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
        if(meta == null) return;
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
