package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Statement;
import java.util.Arrays;

public abstract class Kit implements Listener {

    public final Player player;

    public static final String title = null;
    public static final Material material = null;
    public static final String lore = null;
    public static final int slot = 0;

    public Kit(Player player) {
        this.player = player;

        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public abstract int getDuration();

    public abstract int getCooldown();

    public abstract void executeAbility();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (item.getItemMeta() == null) return;
        if (item.getItemMeta().getDisplayName().contains("Grappler")) return;

        // Check if the Player has the Kit
        if (!p.equals(player)) return;

        EquipmentSlot e = event.getHand();
        if (e == null)
            return;

        // Do nothing on Left-Click: cancel event
        if (e.equals(EquipmentSlot.HAND) && (event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains(KitClass.ASSASSIN)
                    || item.getItemMeta().getDisplayName().contains(KitClass.TANK)
                    || item.getItemMeta().getDisplayName().contains(KitClass.SUPPORT)
                    || item.getItemMeta().getDisplayName().contains(KitClass.MOBILITY)) {

                event.setCancelled(true);
            }
        }

        // Execute Kit Ability on Right-Click (if not on cooldown)
        if (e.equals(EquipmentSlot.HAND) && (event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains(KitClass.ASSASSIN)
                    || item.getItemMeta().getDisplayName().contains(KitClass.TANK)
                    || item.getItemMeta().getDisplayName().contains(KitClass.SUPPORT)
                    || item.getItemMeta().getDisplayName().contains(KitClass.MOBILITY)) {

                // Not able to equip / place the item
                event.setCancelled(true);

                // Check if the Player is on Cooldown
                if (player.getCooldown(Material.NETHER_STAR) > 0) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "Your ability is on cooldown for "
                            + HexColor.RED + (player.getCooldown(Material.NETHER_STAR) / 20)
                            + ChatColor.RED + " seconds.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    return;
                }

                executeAbility();
                addKitAbilityUsageToDB(player);

                // Cooldown
                GameState.kitAbilityActive.put(player, getDuration());
                setCooldownEnds();
            }
        }
    }

    public void setCooldownEnds() {
        int cooldownToApply = getCooldown() - GameState.kitCooldownReduction.get(player);

        if (cooldownToApply > 0) {
            player.setCooldown(Material.NETHER_STAR, cooldownToApply * 20);

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
                player.sendMessage(Prefixes.getImportantPrefix() + GameState.selectedKit.get(player) + ChatColor.GRAY
                        + " Kit Ability is " + HexColor.GREEN + "ready" + ChatColor.GRAY + ".");
            }, cooldownToApply * 20L);
        }
    }

    protected void runKitAbility(int ticks, int duration, CustomKitAbilityExecutor executor,
                                 EverySecondKitAbilityExecutor everySecondExecutor) {
        new BukkitRunnable() {
            double durationLeft = duration;
            Timer timer = Main.getInstance().getTimer();

            @Override
            public void run() {

                if (everySecondExecutor != null) {
                    everySecondExecutor.executeEverySecond();
                }

                if (player.isDead() || !timer.isRunning()) { // without a executor
                    this.cancel();
                }

                if (durationLeft <= 0.0) {
                    if (executor != null) {
                        executor.execute();
                    }

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    this.cancel();
                }

                if (durationLeft % 1 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                }

                durationLeft = durationLeft - (double) ticks / 20;
                GameState.kitAbilityActive.put(player, (int) durationLeft);
            }
        }.runTaskTimer(Main.getInstance(), ticks, ticks);
    }

    private void addKitAbilityUsageToDB(Player player) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO kit_abilities (game_id, player, dimension, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getUniqueId() + "', '" +
                    DbValues.getCurrentWorld(player.getLocation()) + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }

    /**
     * Without material but with nether star used to apply the item cooldown.
     */
    public static ItemStack getDisplayItem(String title, String lore) {
        ItemStack item;

        if (title.contains("Grappler")) {
            item = new ItemStack(Material.CROSSBOW);
        } else {
            item = new ItemStack(Material.NETHER_STAR);
        }

        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(title);
        meta.setLore(Arrays.asList(lore.split("\n")));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack getDisplayItem(String title, Material material, String lore) {
        ItemStack item = new ItemStack(material);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(title);
        meta.setLore(Arrays.asList(lore.split("\n")));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Includes PotionMeta and Color for Potions, Splash Potions, Lingering Potions and Tipped Arrows.
     */
    public static ItemStack getDisplayItem(String title, Material material, String lore, Color color) {
        ItemStack item = new ItemStack(material);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(title);
        meta.setLore(Arrays.asList(lore.split("\n")));
        meta.setColor(color);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);

        return item;
    }
}
