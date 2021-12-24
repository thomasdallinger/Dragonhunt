package com.tharox.dragonhunt.kits;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ChargeKit extends Kit {

    boolean moved;
    boolean charging;
    Timer timer = Main.getInstance().getTimer();
    int chargingFor = getChargeTime();
    int cooldownEnds;

    public ChargeKit(Player player) {
        super(player);

        moved = false;
        cooldownEnds = 120 + timer.getTime(); //kit ability not usable for 2 minutes after receiving the kit

        runActionBar();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (item.getItemMeta() == null) return;

        //Check if the Player has the Kit
        if (!p.equals(player)) return;

        EquipmentSlot e = event.getHand();
        if (e == null)
            return;

        if (e.equals(EquipmentSlot.HAND) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Kit Ability " + player.getName())) {
                //not able to equip / place
                event.setCancelled(true);

                if (p.getWorld().getName().equals("world_nether")) {
                    p.sendMessage(ChatColor.RED + "You can't use your kit ability in the nether!");
                    return;
                }

                //Check if the Player is on Cooldown
                if (cooldownEnds > timer.getTime()) {
                    player.sendMessage(ChatColor.RED + "Your ability is on cooldown...");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    return;
                }

                //Check if the Player is already charging the Ability
                if (charging) {
                    player.sendMessage(ChatColor.RED + "Already charging...");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    return;
                }

                chargingFor = getChargeTime();
                moved = false;
                charging = true;

                if (chargingFor == 0) {
                    setCooldownEnds();
                    executeAbility();
                    return;
                }

                chargeAbility();
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!p.equals(player)) return;

        if (chargingFor >= getChargeTime()) return;

        if (e.getFrom().getZ() != e.getTo().getZ() && e.getFrom().getX() != e.getTo().getX()) {
            moved = true;
        }
    }

    public void chargeAbility() {
        new BukkitRunnable() {
            @Override
            public void run() {

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                chargingFor--;

                if (moved) {
                    moved = false;
                    player.sendMessage(ChatColor.RED + "Charging cancelled. Don't move!");

                    //reset charging
                    chargingFor = getChargeTime();
                    charging = false;

                    //cancel charging
                    this.cancel();
                }

                if (chargingFor == 0) {
                    setCooldownEnds();

                    //reset charging
                    chargingFor = getChargeTime();
                    charging = false;

                    executeAbility();
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    public void setCooldownEnds() {
        cooldownEnds = getCooldown() + timer.getTime();
    }

    public abstract void executeAbility();

    public abstract int getChargeTime();

    public abstract int getCooldown();

    public void sendActionBar(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        int timeLeft;
        List<String> charge = new ArrayList<>();
        String chargeString;

        if (item.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Kit Ability " + player.getName())) {
            //Check if in nether
            if (player.getWorld().getName().equals("world_nether")) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED.toString() + ChatColor.BOLD
                        + "NOT USABLE IN NETHER"));
                return;
            }

            //Check if on Cooldown
            if (cooldownEnds > timer.getTime()) {
                timeLeft = cooldownEnds - timer.getTime();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED.toString() + ChatColor.BOLD + "READY IN: "
                        + ChatColor.WHITE + timeLeft + " second(s)"));
                return;
            }

            //Check if Charging
            if (charging) {
                charge.add(ChatColor.LIGHT_PURPLE + "■");
                for (int charges = 1; charges < getChargeTime(); charges++) {
                    if (charges <= getChargeTime() - chargingFor)
                        charge.add(ChatColor.LIGHT_PURPLE + "■");
                    else
                        charge.add(ChatColor.WHITE + "■");
                }

                chargeString = charge.toString();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "CHARGING: "
                        +  chargeString.substring(1, chargeString.length() - 1).replaceAll(",", "")));
            } else {
                if (timer.getTime() % 2 == 0)
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() + ChatColor.BOLD + "ABILITY READY"));
                else
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE.toString() + ChatColor.BOLD + "ABILITY READY"));
            }
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

    public static void kitItem(Player player, Material material, String lore) {
        ItemStack item;
        item = new ItemStack(material);

        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Kit Ability " + player.getName());
        meta.setLore(Arrays.asList(lore.split("\n")));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);

        player.getInventory().setItem(8, item);
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
                    if (executor != null) {
                        executor.execute();
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    this.cancel();
                }

                if (durationLeft <= 5 && durationLeft % 1 == 0) {
                    player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "TIME LEFT: " + ChatColor.WHITE + durationLeft + " second(s)");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                }

                if (player.isDead()) { //without a executor
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), ticks, ticks);
    }
}
