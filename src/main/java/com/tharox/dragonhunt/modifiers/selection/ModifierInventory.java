package com.tharox.dragonhunt.modifiers.selection;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.modifiers.classes.DragonhuntModifier;
import com.tharox.dragonhunt.modifiers.classes.Modifier;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.reflections.Reflections;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ModifierInventory {
    public Inventory modifiers;

    public ModifierInventory() {
        new ModifierSelection();
        createInventory();
    }

    public void createInventory() {
        Inventory inv = Bukkit.createInventory(null, 18, HexColor.HIGHLIGHT + "Modifiers");
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
                                            (Material) modifierClass.getField("material").get(null),
                                            (Boolean) modifierClass.getField("positive").get(null),
                                            (Boolean) modifierClass.getField("active").get(null)));
                            // get(null) because static

                            String title = Modifier.displayModifierSelection(
                                    (String) modifierClass.getField("title").get(null),
                                    (Boolean) modifierClass.getField("positive").get(null),
                                    (Boolean) modifierClass.getField("active").get(null));

                            String lore = Modifier.getLore(
                                    (String) modifierClass.getField("lore").get(null),
                                    (Boolean) modifierClass.getField("active").get(null));

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (title != null && lore != null) {
                                    player.sendMessage(Prefixes.getModifierPrefix() + title
                                            + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + lore);
                                    player.setPlayerListFooter(player.getPlayerListFooter() + title + "\n");
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
