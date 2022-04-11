package com.tharox.dragonhunt.kitupgrades;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitUpgradeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        if(Main.getInstance().getTimer().getTime() < 120) {
            player.sendMessage(ChatColor.RED + "Kit Upgrades are available after 2 minutes.");
            return true;
        }

        openInventory(player);

        return false;
    }

    public static void openInventory(Player player) {
        KitUpgradeInventory kitUpgradeInventory = new KitUpgradeInventory(Main.getInstance().getConfig(), player);

        //TODO: simplify
        if (GameState.kit.get(player).toString().contains("Platformer"))
            player.openInventory(kitUpgradeInventory.platformer);
        else if (GameState.kit.get(player).toString().contains("Skydiver"))
                player.openInventory(kitUpgradeInventory.skydiver);
        else if (GameState.kit.get(player).toString().contains("Trapcrafter"))
                player.openInventory(kitUpgradeInventory.trapcrafter);
        else if (GameState.kit.get(player).toString().contains("Vanisher"))
                player.openInventory(kitUpgradeInventory.vanisher);
    }
}
