package com.tharox.dragonhunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class DateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        Date date = new Date();
        player.sendMessage("It is " + date.toString());

        return false;
    }
}
