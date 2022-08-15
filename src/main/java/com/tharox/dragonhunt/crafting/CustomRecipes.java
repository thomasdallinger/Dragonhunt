package com.tharox.dragonhunt.crafting;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Objects;

public class CustomRecipes {

    public CustomRecipes() {
        changePickaxeRecipe("wood_pickaxe", Material.OAK_PLANKS);
        changePickaxeRecipe("gold_pickaxe", Material.GOLD_INGOT);
        changePickaxeRecipe("stone_pickaxe", Material.COBBLESTONE);
        changePickaxeRecipe("blackstone_pickaxe", Material.BLACKSTONE);

        changeAxeRecipe("wood_axe", Material.OAK_PLANKS);
        changeAxeRecipe("gold_axe", Material.GOLD_INGOT);
        changeAxeRecipe("stone_axe", Material.COBBLESTONE);
        changeAxeRecipe("blackstone_axe", Material.BLACKSTONE);

        changeHoeRecipe("wood_hoe", Material.OAK_PLANKS);
        changeHoeRecipe("gold_hoe", Material.GOLD_INGOT);
        changeHoeRecipe("stone_hoe", Material.COBBLESTONE);
        changeHoeRecipe("blackstone_hoe", Material.BLACKSTONE);

        changeShovelRecipe("wood_shovel", Material.OAK_PLANKS);
        changeShovelRecipe("gold_shovel", Material.GOLD_INGOT);
        changeShovelRecipe("stone_shovel", Material.COBBLESTONE);
        changeShovelRecipe("blackstone_shovel", Material.BLACKSTONE);

        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "wood_sword", Material.OAK_PLANKS);
        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "gold_sword", Material.GOLD_INGOT);
        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "stone_sword", Material.COBBLESTONE);
        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "blackstone_sword", Material.BLACKSTONE);

        changeShieldRecipe();

        addCopperDiamond();
        addCopperGold();
    }

    /**
     * Updates an existing bukkit pickaxe recipe.
     *
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this pickaxe
     */
    private void changePickaxeRecipe(String keyName, Material resource) {
        // Create the result ItemStack
        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 1);

        // Create a NamespacedKey for the recipe.
        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyName);

        // Create a custom recipe variable.
        ShapedRecipe recipe = new ShapedRecipe(key, pickaxe);

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
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this axe
     */
    private void changeAxeRecipe(String keyName, Material resource) {
        ItemStack axe = new ItemStack(Material.IRON_AXE);
        axe.addEnchantment(Enchantment.DIG_SPEED, 1);

        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyName);
        ShapedRecipe recipe = new ShapedRecipe(key, axe);
        recipe.shape("RR", "SR", "S ");
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);

        // reversed
        key = new NamespacedKey(Main.getInstance(), keyName + "2");
        recipe = new ShapedRecipe(key, axe);
        recipe.shape("RR", "RS", " S");
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
    }

    /**
     * Updates an existing bukkit hoe recipe.
     *
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this hoe
     */
    private void changeHoeRecipe(String keyName, Material resource) {
        ItemStack hoe = new ItemStack(Material.IRON_HOE);
        hoe.addEnchantment(Enchantment.DIG_SPEED, 1);

        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyName);
        ShapedRecipe recipe = new ShapedRecipe(key, hoe);
        recipe.shape("RR", "S ", "S ");
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);

        // reversed
        key = new NamespacedKey(Main.getInstance(), keyName + "2");
        recipe = new ShapedRecipe(key, hoe);
        recipe.shape("RR", " S", " S");
        recipe.setIngredient('R', resource);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
    }

    /**
     * Updates an existing bukkit shovel recipe.
     *
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this shovel
     */
    private void changeShovelRecipe(String keyName, Material resource) {
        ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
        shovel.addEnchantment(Enchantment.DIG_SPEED, 1);

        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyName);
        ShapedRecipe recipe = new ShapedRecipe(key, shovel);
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
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "unbreakable_shield");
        ShapedRecipe recipe = new ShapedRecipe(key,
                Objects.requireNonNull(GameState.addItem(new ItemStack(Material.SHIELD))));
        recipe.shape("SRS", "SSS", " S ");
        recipe.setIngredient('R', Material.IRON_INGOT);
        recipe.setIngredient('S', Material.OAK_PLANKS);

        Bukkit.addRecipe(recipe);
    }

    /**
     * Adds a new copper recipe: 9 copper ingots = 1 diamond.
     */
    private void addCopperDiamond() {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "copper_diamond");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.DIAMOND));
        recipe.shape("CCC", "CCC", "CCC");
        recipe.setIngredient('C', Material.COPPER_INGOT);

        Bukkit.addRecipe(recipe);
    }

    /**
     * Adds a new copper recipe: 4 copper ingots = 1 gold ingot.
     */
    private void addCopperGold() {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "copper_gold");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.GOLD_INGOT));
        recipe.shape("CC", "CC");
        recipe.setIngredient('C', Material.COPPER_INGOT);

        Bukkit.addRecipe(recipe);
    }
}
