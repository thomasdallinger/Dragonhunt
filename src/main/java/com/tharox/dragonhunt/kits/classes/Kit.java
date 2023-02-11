package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public abstract class Kit implements Listener {

    public final Player player;

    public static final String title = null;
    public static final Material material = null;
    public static final String lore = null;
    public static final int slot = 0;

    Timer timer = Main.getInstance().getTimer();
    int cooldownEnds;
    private boolean kitAbilityActive = false;

    public Kit(Player player) {
        this.player = player;

        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());

        runActionBar();
    }

    public abstract int getCooldown();

    public abstract void executeAbility();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (item.getItemMeta() == null) return;

        // Check if the Player has the Kit
        if (!p.equals(player)) return;

        EquipmentSlot e = event.getHand();
        if (e == null)
            return;

        if (e.equals(EquipmentSlot.HAND) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains(KitClass.ASSASSIN)
                    || item.getItemMeta().getDisplayName().contains(KitClass.TANK)
                    || item.getItemMeta().getDisplayName().contains(KitClass.SUPPORT)
                    || item.getItemMeta().getDisplayName().contains(KitClass.MOBILITY)) {
                // Not able to equip / place the item
                event.setCancelled(true);

                // Check if the Player is on Cooldown
                if (cooldownEnds > timer.getTime()) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "Your ability is on cooldown.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    return;
                }

                //addKitAbilityUsage(); TODO: db related
                executeAbility();
                kitAbilityActive = true;
                setCooldownEnds();
            }
        }
    }

    public void setCooldownEnds() {
        cooldownEnds = getCooldown() + timer.getTime();
    }

    public void sendActionBar(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        int timeLeft;

        if (item.getItemMeta().getDisplayName().contains(KitClass.ASSASSIN)
                || item.getItemMeta().getDisplayName().contains(KitClass.TANK)
                || item.getItemMeta().getDisplayName().contains(KitClass.SUPPORT)
                || item.getItemMeta().getDisplayName().contains(KitClass.MOBILITY)) {
            // Check if on Cooldown
            if (cooldownEnds > timer.getTime()) {
                timeLeft = cooldownEnds - timer.getTime();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "COOLDOWN: "
                        + ChatColor.WHITE + timeLeft + " sec"));
                return;
            }

            if (kitAbilityActive) { // Kit ability is active - time left is being displayed
                return;
            }

            if (timer.getTime() % 2 == 0)
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() + ChatColor.BOLD + "ABILITY READY"));
            else
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE.toString() + ChatColor.BOLD + "ABILITY READY"));
        }
    }

    protected void runActionBar() {
        new BukkitRunnable() {
            @Override
            public void run() {

                sendActionBar(player);

            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    protected void runKitAbility(int ticks, int duration, CustomKitAbilityExecutor executor, EverySecondKitAbilityExecutor everySecondExecutor) {
        new BukkitRunnable() {
            double durationLeft = duration;

            @Override
            public void run() {

                if (everySecondExecutor != null) {
                    everySecondExecutor.executeEverySecond();
                }

                durationLeft = durationLeft - (double) ticks / 20;

                if (durationLeft <= 0) {
                    kitAbilityActive = false;

                    if (executor != null) {
                        executor.execute();
                    }

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    this.cancel();
                }

                // Displaying the duration left in the action bar
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                        ChatColor.WHITE.toString() + ChatColor.BOLD + "» "
                                + String.format("%1.1f", durationLeft) + " sec"
                                + " «"));

                if (player.isDead()) { //without a executor
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), ticks, ticks);
    }

    /*private void addKitAbilityUsage() {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO KitAbility (GameID, Player, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }*/

    public static ItemStack getDisplayItem(String title, Material material, String lore) {
        ItemStack item = new ItemStack(material);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(title);
        meta.setLore(Arrays.asList(lore.split("\n")));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);

        return item;
    }
}
