package com.tharox.dragonhunt.spectate;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        if (player.getWorld().getName().equals("world_nether")) {
            player.sendMessage(ChatColor.RED + "The spectate ability is not usable in the nether.");
            return true;
        }

        if (Spectate.spectatesLeft.get(player) == 0) {
            player.sendMessage(ChatColor.RED + "You have no spectates left.");
            return true;
        }

        new Spectate(player);
        return false;
    }
}
