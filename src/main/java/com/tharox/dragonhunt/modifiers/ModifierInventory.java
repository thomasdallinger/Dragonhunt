package com.tharox.dragonhunt.modifiers;

import com.tharox.dragonhunt.Main;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.reflections.Reflections;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ModifierInventory {
    public Inventory modifiers;
    TextComponent textComponent = new TextComponent(
            Main.getPrefix() + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "MODIFIERS: ");
    String tablistInformation = "";

    public ModifierInventory() {
        new ModifierSelection();
        createInventory();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(textComponent);
            player.setPlayerListFooter(player.getPlayerListFooter() + "\n" + tablistInformation);
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
                                    (Boolean) modifierClass.getField("active").get(null));

                            String lore = Modifier.displayModifierSelection(
                                    (String) modifierClass.getField("lore").get(null),
                                    (Boolean) modifierClass.getField("positive").get(null),
                                    (Boolean) modifierClass.getField("active").get(null));

                            if (message != null && lore != null) {
                                TextComponent modifier = new TextComponent(message + "  ");
                                modifier.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new Text(lore)));
                                textComponent.addExtra(modifier);
                                tablistInformation += message + "  ";
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
