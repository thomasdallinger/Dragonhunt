package com.tharox.dragonhunt.gear;

import com.tharox.dragonhunt.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GearCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        GearUpgradeInventory gearUpgradeInventory = new GearUpgradeInventory(Main.getInstance().getConfig(), player);
        player.openInventory(gearUpgradeInventory.menu);
        return false;
    }
}
