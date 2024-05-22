package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.kits.classes.KitTalents;
import com.tharox.dragonhunt.shop.Upgrades;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TalentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        int talentNumber;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return false;

        if (args.length != 3) {
            return false;
        }

        if (!args[1].equals(KitTalents.key)) {
            return false;
        }

        try {
            talentNumber = Integer.parseInt(args[2]);
        } catch (IllegalArgumentException e) {
            return false;
        }

        if (KitTalents.selectedKitTalents.getOrDefault(player, 0) == talentNumber) {
            player.sendMessage(Prefixes.getErrorPrefix() + "You can only choose 1 talent option.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        int value = getValueFromName(args[0]);
        if (args[0].contains("Cooldown")) {
            Upgrades.Cooldown.put(player, value);
        } else if (args[0].contains("Duration")) {
            Upgrades.Duration.put(player, value);
        } else if (args[0].contains("Effect")) {
            Upgrades.Effectiveness.put(player, value);
        } else if (args[0].contains("Effect2")) {
            Upgrades.Effectiveness2.put(player, value);
        }

        KitTalents.selectedKitTalents.put(player, talentNumber);

        return true;
    }

    private int getValueFromName(String name) {
        String stringNumber = name.replaceAll("[^0-9]", "");
        if (!stringNumber.equals("")) {
            return Integer.parseInt(stringNumber);
        }
        return 0;
    }
}
