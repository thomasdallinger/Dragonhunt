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

    /**
     * When diamond tools are converted to netherite tools, the efficiency enchantment stays on the tool.
     * Therefore, no recipe changes for the smithing table are needed.
     */
    public CustomRecipes() {
        changePickaxeRecipe(Material.IRON_PICKAXE, "wood_pickaxe", Material.OAK_PLANKS);
        changePickaxeRecipe(Material.IRON_PICKAXE, "gold_pickaxe", Material.GOLD_INGOT);
        changePickaxeRecipe(Material.IRON_PICKAXE, "stone_pickaxe", Material.COBBLESTONE);
        changePickaxeRecipe(Material.IRON_PICKAXE, "blackstone_pickaxe", Material.BLACKSTONE);
        changePickaxeRecipe(Material.DIAMOND_PICKAXE, "diamond_pickaxe", Material.DIAMOND);

        changeAxeRecipe(Material.IRON_AXE, "wood_axe", Material.OAK_PLANKS);
        changeAxeRecipe(Material.IRON_AXE, "gold_axe", Material.GOLD_INGOT);
        changeAxeRecipe(Material.IRON_AXE, "stone_axe", Material.COBBLESTONE);
        changeAxeRecipe(Material.IRON_AXE, "blackstone_axe", Material.BLACKSTONE);
        changeAxeRecipe(Material.DIAMOND_AXE, "diamond_axe", Material.DIAMOND);

        changeHoeRecipe(Material.IRON_HOE, "wood_hoe", Material.OAK_PLANKS);
        changeHoeRecipe(Material.IRON_HOE, "gold_hoe", Material.GOLD_INGOT);
        changeHoeRecipe(Material.IRON_HOE, "stone_hoe", Material.COBBLESTONE);
        changeHoeRecipe(Material.IRON_HOE, "blackstone_hoe", Material.BLACKSTONE);
        changeHoeRecipe(Material.DIAMOND_HOE, "diamond_hoe", Material.DIAMOND);

        changeShovelRecipe(Material.IRON_SHOVEL, "wood_shovel", Material.OAK_PLANKS);
        changeShovelRecipe(Material.IRON_SHOVEL, "gold_shovel", Material.GOLD_INGOT);
        changeShovelRecipe(Material.IRON_SHOVEL, "stone_shovel", Material.COBBLESTONE);
        changeShovelRecipe(Material.IRON_SHOVEL, "blackstone_shovel", Material.BLACKSTONE);
        changeShovelRecipe(Material.DIAMOND_SHOVEL, "diamond_shovel", Material.DIAMOND);

        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "wood_sword", Material.OAK_PLANKS);
        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "gold_sword", Material.GOLD_INGOT);
        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "stone_sword", Material.COBBLESTONE);
        changeSwordRecipe(new ItemStack(Material.IRON_SWORD), "blackstone_sword", Material.BLACKSTONE);

        changeShieldRecipe();

        addCopperGold();
        addCopperDiamond();
        addEnchantedGoldenApple();
        addBook();
    }

    /**
     * Updates an existing bukkit pickaxe recipe.
     *
     * @param item     item which the player receives when crafting the recipe
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this pickaxe
     */
    private void changePickaxeRecipe(Material item, String keyName, Material resource) {
        // Create the result ItemStack
        ItemStack pickaxe = new ItemStack(item);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 3);

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
     * @param item     item which the player receives when crafting the recipe
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this axe
     */
    private void changeAxeRecipe(Material item, String keyName, Material resource) {
        ItemStack axe = new ItemStack(item);
        axe.addEnchantment(Enchantment.DIG_SPEED, 3);

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
     * @param item     item which the player receives when crafting the recipe
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this hoe
     */
    private void changeHoeRecipe(Material item, String keyName, Material resource) {
        ItemStack hoe = new ItemStack(item);
        hoe.addEnchantment(Enchantment.DIG_SPEED, 3);

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
     * @param item     item which the player receives when crafting the recipe
     * @param keyName  used to identify this recipe
     * @param resource resource needed to craft this shovel
     */
    private void changeShovelRecipe(Material item, String keyName, Material resource) {
        ItemStack shovel = new ItemStack(item);
        shovel.addEnchantment(Enchantment.DIG_SPEED, 3);

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
     * @param item     item item which the player receives when crafting the recipe
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
     * Adds a new copper recipe: 4 copper ingots = 1 gold ingot.
     */
    private void addCopperGold() {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "copper_gold");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.GOLD_INGOT));
        recipe.shape("CC", "CC");
        recipe.setIngredient('C', Material.COPPER_INGOT);

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
     * Adds a new recipe: 8 diamonds + 1 golden apple = 1 enchanted golden apple.
     */
    private void addEnchantedGoldenApple() {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "enchanted_golden_apple");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
        recipe.shape("DDD", "DGD", "DDD");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('G', Material.GOLDEN_APPLE);

        Bukkit.addRecipe(recipe);
    }

    /**
     * Adds a new QoL recipe: 1 leather = 1 book.
     * Book needed to craft an enchanting table.
     */
    private void addBook() {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "book");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.BOOK));
        recipe.shape("L");
        recipe.setIngredient('L', Material.LEATHER);
        Bukkit.addRecipe(recipe);
    }
}
