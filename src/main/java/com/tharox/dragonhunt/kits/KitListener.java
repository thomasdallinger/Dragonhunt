package com.tharox.dragonhunt.kits;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.timer.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

public class KitListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Player player = (Player) event.getWhoClicked();
        Timer timer = Main.getInstance().getTimer();

        if (!event.getView().getTitle().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Pick a Kit")) return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String name = item.getItemMeta().getDisplayName();

        Kit kit = null;

        Reflections reflections = new Reflections("com.tharox.dragonhunt");
        Set<Class<?>> kitClasses = reflections.getTypesAnnotatedWith(DragonhuntKit.class);

        Class<Kit> kitClass = (Class<Kit>) kitClasses.stream().filter(
                (innerKitClass) -> {
                    if(Kit.class.isAssignableFrom(innerKitClass)) {
                        try {
                            return ((String) innerKitClass.getField("title").get(null)).equals(name);
                        } catch (IllegalAccessException | NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
        ).findFirst().get();

        kit = kitClass.getDeclaredConstructor(Player.class).newInstance(player);
        GameState.kit.put(player, kit);

        if(kit instanceof ChargeKit) {
            ChargeKit.kitItem(player, (Material) kitClass.getField("material").get(null), (String) kitClass.getField("lore").get(null));
        } else {
            lockedSlot(player);
        }

        /*if (name.contains("Skydiver")) {
            kit = new Skydiver(player);
            GameState.kit.put(player, kit);
            ChargeKit.kitItem(player, Skydiver.material, Skydiver.lore);
            //TODO: test substring
            player.sendMessage(name.substring(2));
        }*/

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        player.sendMessage(ChatColor.WHITE + "You have picked the " + name + ChatColor.WHITE + " Kit!");
        player.closeInventory();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();

        if (!event.getView().getTitle().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Pick a Kit")) return;
        if (GameState.kit.containsKey(player)) return; //Already has a kit

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> player.openInventory(inv), 1);
    }

    public void lockedSlot(Player player) {
        ItemStack item = new ItemStack(Material.BARRIER);

        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;
        meta.setDisplayName(ChatColor.RED + "Locked Slot");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        player.getInventory().setItem(8, item);
    }
}
