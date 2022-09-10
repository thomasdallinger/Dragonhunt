package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.grappler.Grappler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Objects;

public class PlayerEndTeleport implements Listener {

    public PlayerEndTeleport() {
        run();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        World world = player.getWorld();

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;

        EquipmentSlot e = event.getHand(); //Get the hand of the event and set it to 'e'.
        if (e == null)
            return;

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().equals(HexColor.HIGHLIGHT.toString()
                    + ChatColor.BOLD + "TP TO END")) {
                event.setCancelled(true); // do not place the end stone block

                if (world.getName().equals("world_the_end")) {
                    player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "Already in the END dimension.");
                    return;
                }

                player.getLocation().getBlock().setType(Material.END_PORTAL);

                Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                        task -> player.getLocation().getBlock().setType(Material.AIR), 5L);
            }
        }
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Inventory inventory = player.getInventory();

                    // 1: give the player the GRAPPLER back - if in the end
                    if (inventory.contains(createEndTeleport())
                            && player.getWorld().getName().equals("world_the_end")) {
                        new Grappler(player);
                        continue;
                    }

                    // 2: give the player the END TELEPORTER back
                    // if the player has been in the end and is currently not in the end
                    if (!inventory.contains(createEndTeleport())
                            && !player.getWorld().getName().equals("world_the_end")) {
                        Advancement advancement = Bukkit.getAdvancement(
                                Objects.requireNonNull(NamespacedKey.fromString("story/enter_the_end")));

                        if (advancement != null) {
                            AdvancementProgress progress = player.getAdvancementProgress(advancement);

                            if (progress.isDone()) {
                                inventory.setItem(8, createEndTeleport());
                                continue;
                            }
                        }
                    }

                    // 3: the player never had a END TELEPORTER yet
                    for (ItemStack item : inventory) {
                        if (item == null) continue;

                        // only returns true if both the type and amount of the ItemStack is correct
                        if (item.getType() == Material.ENDER_EYE && item.getAmount() >= getEyesNeeded()) {
                            inventory.remove(item);

                            // replace the Grappler with the END TELEPORTER item
                            inventory.setItem(8, createEndTeleport());
                            player.sendMessage(ChatColor.DARK_PURPLE + "✮" + ChatColor.DARK_GRAY + " | "
                                    + HexColor.HIGHLIGHT + ChatColor.BOLD + "Teleport to the End dimension unlocked.");
                            player.sendMessage(ChatColor.DARK_PURPLE + "✮" + ChatColor.DARK_GRAY + " | "
                                    + HexColor.GREEN + "Right-Click" + ChatColor.GRAY + " the Endstone to teleport.");
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    /**
     * Used in the scoreboard.
     *
     * @return the amount of ender eyes needed to get the "TP TO END" item
     */
    public static int getEyesNeeded() {
        int eyesNeeded = 16;

        if (Main.getInstance().getTimer().getTime() >= 2400) { // last 10 minutes of the game
            eyesNeeded = 8;
        } else {
            if (GameState.playersReachedEnd) {
                eyesNeeded = 12;
            }
        }

        return eyesNeeded;
    }

    private ItemStack createEndTeleport() {
        ItemStack item = new ItemStack(Material.END_STONE);
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "TP TO END");
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Right-Click: " + ChatColor.LIGHT_PURPLE + "Teleports you to the spawn platform",
                ChatColor.LIGHT_PURPLE + "in the END dimension.",
                "",
                HexColor.RED + "ONLY WAY BACK IS DYING"));
        item.setItemMeta(meta);

        return item;
    }
}
