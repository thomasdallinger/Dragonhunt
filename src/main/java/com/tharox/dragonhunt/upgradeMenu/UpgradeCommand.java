package com.tharox.dragonhunt.upgradeMenu;

import com.tharox.dragonhunt.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpgradeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        UpgradeInventory upgradeInventory = new UpgradeInventory(Main.getInstance().getConfig(), player);
        player.openInventory(upgradeInventory.menu);
        return false;
    }
}
