package com.tharox.dragonhunt.modifiers;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.reflections.Reflections;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ModifierInventory {
    public Inventory modifiers;

    public ModifierInventory() {
        new ModifierSelection();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Main.getPrefix() + ChatColor.DARK_GRAY + ChatColor.BOLD + "=======================");
            player.sendMessage(Main.getPrefix() + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "MODIFIERS: ");
        }

        createInventory();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Main.getPrefix() + ChatColor.DARK_GRAY + ChatColor.BOLD + "=======================");
        }
    }

    public void createInventory() {
        Inventory inv = Bukkit.createInventory(null, 18,
                ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "MODIFIERS");
        AtomicInteger index = new AtomicInteger();

        Reflections reflections = new Reflections("com.tharox.dragonhunt");
        Set<Class<?>> modifierClasses = reflections.getTypesAnnotatedWith(DragonhuntModifier.class);

        modifierClasses.forEach(
                (modifierClass) -> {
                    if (Modifier.class.isAssignableFrom(modifierClass)) {
                        try {
                            inv.setItem(index.getAndIncrement(),
                                    Modifier.getDisplayItem(
                                            (String) modifierClass.getField("title").get(null),
                                            (String) modifierClass.getField("lore").get(null),
                                            (Boolean) modifierClass.getField("positive").get(null),
                                            (Integer) modifierClass.getField("impact").get(null),
                                            (Boolean) modifierClass.getField("active").get(null)));
                            // get(null) because static

                            String message = Modifier.displayModifierSelection(
                                    (String) modifierClass.getField("title").get(null),
                                    (Boolean) modifierClass.getField("positive").get(null),
                                    (Integer) modifierClass.getField("impact").get(null),
                                    (Boolean) modifierClass.getField("active").get(null));

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (message != null) {
                                    player.sendMessage(message);
                                }
                            }

                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        modifiers = inv;
    }
}
