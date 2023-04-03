package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.chat.HexColor;
import net.md_5.bungee.api.ChatColor;
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
        } else return false;

        Date date = new Date();
        player.sendMessage(ChatColor.DARK_GRAY + "[" + HexColor.YELLOW + ChatColor.BOLD + "DATE"
                + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + date.toString());

        return true;
    }
}
