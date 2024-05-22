package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class KitTalents {
    public static HashMap<Player, Integer> selectedKitTalents = new HashMap<>();
    public static String key = "aRo^#2%wZ&mfh6";
    private FileConfiguration config;
    private String category;

    public KitTalents(Player player) {
        this.config = Main.getInstance().getConfig();
        this.category = GameState.selectedKit.get(player).substring(16);
        createMessage(config, category, player);
    }

    // if the player is on lvl 5 the options in the config file are 0, 1 and 2
    // if lvl 10 the options are 3,4,5
    // if lvl 15 the options are 6,7,8
    private void createMessage(FileConfiguration config, String category, Player player) {
        ConfigurationSection itemsSection = config.getConfigurationSection(category);
        if (itemsSection == null) return;

        int level = player.getLevel();
        int startOption = (level / 5 - 1) * 3;

        player.sendMessage("");
        TextComponent c = new TextComponent(ChatColor.WHITE.toString() + ChatColor.BOLD + "CHOOSE A KIT "
                + ChatColor.GOLD + ChatColor.BOLD + "TALENT" + ChatColor.GRAY + " (Left-Click)\n");

        for (int i = startOption; i < startOption + 3; i++) {
            String optionName = itemsSection.getString(i + ".name");
            if (optionName == null) continue;

            TextComponent effect = new TextComponent("» " + ChatColor.GOLD + "[" + optionName + "]\n");
            effect.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/talent "
                    + optionName.replace(" ", "")
                    + " " + key + " " + (startOption + 1)));
            effect.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.WHITE
                    + "Left-Click to select: " + ChatColor.GOLD + optionName)));
            c.addExtra(effect);
        }

        player.spigot().sendMessage(c);
        player.sendMessage("");
    }
}