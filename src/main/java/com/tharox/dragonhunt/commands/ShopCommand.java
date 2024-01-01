package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.shop.ShopInventory;
import com.tharox.dragonhunt.timer.Timer;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Timer timer = Main.getInstance().getTimer();
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return false;

        if (!timer.isRunning() || player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.sendMessage(Prefixes.getErrorPrefix() + "This feature is currently disabled.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        ShopInventory brewingInventory = new ShopInventory(Main.getInstance().getConfig(), player);
        player.openInventory(brewingInventory.menu);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);

        return true;
    }
}
