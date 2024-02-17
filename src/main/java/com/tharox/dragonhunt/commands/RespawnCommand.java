package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.players.CombatListener;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class RespawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Timer timer = Main.getInstance().getTimer();
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return false;

        World world = player.getWorld();

        if (!timer.isRunning()) {
            player.sendMessage(Prefixes.getErrorPrefix() + "This feature is currently disabled.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        if (world.getName().equals("world_the_end")) {
            player.sendMessage(Prefixes.getErrorPrefix() + "This feature is disabled in the End dimension.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        if (CombatListener.combatList.containsKey(player.getUniqueId())) {
            player.sendMessage(Prefixes.getErrorPrefix() + "This feature is disabled while in combat.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        if (player.getHealth() < 10.0) {
            player.sendMessage(Prefixes.getErrorPrefix() + "You need to have more than 5 ❤ " +
                    "to set your respawn point or teleport to it.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        if (args.length == 0) {
            player.setBedSpawnLocation(player.getLocation(), true);
            player.sendMessage(HexColor.GREEN + "Respawn Point set at "
                    + ChatColor.DARK_GREEN + "[" + DbValues.getCurrentWorld(player.getLocation())
                    + ", x: " + (int) player.getLocation().getX()
                    + ", y: " + (int) player.getLocation().getY()
                    + ", z: " + (int) player.getLocation().getZ() + "]"
                    + HexColor.GREEN + ".");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

            return true;
        }

        if (args[0].equalsIgnoreCase("tp")) {
            if (player.getBedSpawnLocation() != null) {
                player.teleport(player.getBedSpawnLocation());
            } else {
                player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
            }

            player.sendMessage(HexColor.GREEN + "Teleported to your Respawn Point.");
            player.sendTitle("", HexColor.GREEN + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", 0, 30, 10);

            player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 1);

            return true;
        }

        return true;
    }
}
