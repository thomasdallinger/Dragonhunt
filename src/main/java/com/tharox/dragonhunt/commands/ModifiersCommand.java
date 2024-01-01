package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.modifiers.selection.ModifierInventory;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModifiersCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return false;

        ModifierInventory modifierInventory = new ModifierInventory(false);
        player.openInventory(modifierInventory.modifiers);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);

        return true;
    }
}
