package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@DragonhuntKit
public class Thief extends Kit {
    static int cooldown = 120;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Thief";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Steals " + ChatColor.WHITE + "1" + ChatColor.GRAY + " random item from every\n"
            + ChatColor.GRAY + "other player and puts it in your inventory.\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + "Instantly\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.DIAMOND;
    public static final int slot = 23;

    public Thief(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    public void executeAbility() {
        Random random = new Random();
        List<Integer> slotsToProtect = Arrays.asList(7, 8, 17);
        int index;
        ItemStack item = null;
        int itemsToSteal = 1 + Upgrades.Effectiveness.getOrDefault(player, 0);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != player) {
                for (int i = 1; i <= itemsToSteal; i++) {
                    int possibleTries = 20; // avoids endless while loop

                    do {
                        if (possibleTries == 0) break;

                        possibleTries--;
                        index = random.nextInt(41);
                        item = online.getInventory().getItem(index);
                    } while (!slotsToProtect.contains(index) && item != null && !item.getType().equals(Material.ELYTRA));

                    if (item == null) continue;

                    online.getInventory().remove(item);

                    if (item.getAmount() > 1) {
                        // Amount caps at max stack size anyway - no need to check
                        GameState.putItemInInventory(player, new ItemStack(item.getType(),
                                item.getAmount() + Upgrades.Effectiveness2.getOrDefault(player, 0)));
                    } else {
                        GameState.putItemInInventory(player, item);
                    }
                }

                online.playSound(online.getLocation(), Sound.ENTITY_GLOW_ITEM_FRAME_REMOVE_ITEM, 1, 1);
                online.sendMessage(Prefixes.getImportantPrefix() + Prefixes.getKitAbilityPrefix() + ChatColor.WHITE
                        + player.getName() + ChatColor.RED + " stole " + HexColor.RED + itemsToSteal
                        + ChatColor.RED + " random items from your inventory as a " + title + ChatColor.RED + ".");
            }
        }

        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCooldown() {
        return cooldown - Upgrades.Cooldown.getOrDefault(player, 0);
    }
}
