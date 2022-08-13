package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        ShopInventory shopInventory = new ShopInventory(Main.getInstance().getConfig(), player);
        player.openInventory(shopInventory.menu);
        return false;
    }
}
