package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.timer.Timer;
import com.tharox.dragonhunt.upgrades.Item;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandHelper {
    /**
     * Checks if the command is allowed to be executed or not.
     *
     * @return true if the command can't be executed
     */
    protected boolean checkIfValid(Player player, Timer timer) {
        if (!timer.isRunning()) {
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "This feature is currently disabled.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        if (player.getWorld().getName().equals("world_the_end")) {
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "This feature is disabled in the End.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        return false;
    }

    protected boolean checkIfEnoughCoins(Player player, String itemName) {
        if (Coins.getCoins(player) < getCommandCost(Main.getInstance().getConfig(), itemName)) {
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You don't have enough coins! Need "
                    + HexColor.RED + (getCommandCost(Main.getInstance().getConfig(), itemName) - Coins.getCoins(player))
                    + ChatColor.GRAY + " more.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        return false;
    }

    protected int getCommandCost(FileConfiguration config, String itemName) {
        ConfigurationSection itemsSection = config.getConfigurationSection("mobility");
        List<Item> itemList = new ArrayList<>();

        if (itemsSection == null) {
            Bukkit.getLogger().severe("Setup config.yml");
            return 0;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection section = itemsSection.getConfigurationSection(key);

            itemList.add(new Item(section));
        }

        for (Item item : itemList) {
            if (item.cost != 0 && item.NAME != null) { //if it has no cost it also isn't air -> air has no displayname
                if (item.NAME.equals(itemName)) {
                    return item.cost;
                }
            }
        }

        return 0;
    }
}
