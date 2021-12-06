package com.tharox.dragonhunt.kits;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class Kit implements Listener {

    public final Player player;

    public Kit(Player player) {
        this.player = player;

        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public static final String title = null;
    public static final Material material = null;
    public static final String lore = null;

    public static ItemStack getDisplayItem(String title, Material material, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(title);
        meta.setLore(Arrays.asList(lore.split("\n")));

        item.setItemMeta(meta);

        return item;
    }
}
