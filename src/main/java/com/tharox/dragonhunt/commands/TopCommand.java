package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Timer timer = Main.getInstance().getTimer();
        Player player;
        World world;
        double x, z;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return false;

        if (!timer.isRunning() || player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.sendMessage(Prefixes.getErrorPrefix() + "This feature is currently disabled.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        world = player.getWorld();
        x = player.getLocation().getX();
        z = player.getLocation().getZ();

        if (player.getLocation().getY() > world.getHighestBlockYAt((int) x, (int) z)) {
            player.sendMessage(Prefixes.getErrorPrefix() + "You are already on the surface.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        // Player has to have more than 5 hearts.
        // Players lose 5 hearts upon being teleported to the surface.
        if (player.getHealth() < 10.0) {
            player.sendMessage(Prefixes.getErrorPrefix() + "You need to have more than 5 ❤ " +
                    "to be able to teleport to the surface.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        player.setHealth(player.getHealth() - 10.0);
        player.teleport(new Location(world, x, world.getHighestBlockYAt((int) x, (int) z) + 1, z));

        player.sendMessage(HexColor.GREEN + "Teleported to the surface.");
        player.sendMessage(Prefixes.getImportantPrefix() + HexColor.RED + "-5 ❤ Penalty");
        player.sendTitle("", HexColor.GREEN + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", 0, 30, 10);

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);

        return true;
    }
}
