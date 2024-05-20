package com.tharox.dragonhunt.coins;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.players.PointsListener;
import com.tharox.dragonhunt.shop.Effects;
import com.tharox.dragonhunt.shop.Enchantments;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class LevelUp implements Listener {
    List<Object> allUpgrades = new ArrayList<>();

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        int level = event.getNewLevel();
        boolean opUpgrades = false;
        boolean resetLeadersUpgrades = false;

        if (level == 0) return;

        // List of all upgrades
        allUpgrades.addAll(Arrays.asList(
                Enchantments.Sharpness, Enchantments.Knockback, Enchantments.FireAspect,
                Enchantments.Power, Enchantments.Punch, Enchantments.Flame,
                Enchantments.Multishot, Enchantments.Protection, Enchantments.Thorns,
                Enchantments.FeatherFalling, Effects.Speed, Effects.JumpBoost,
                Effects.Haste, Effects.DolphinsGrace, Effects.FireResistance,
                Effects.Resistance, Effects.Strength, Effects.ArrowMiningFatigue,
                Effects.ArrowPoison, Effects.ArrowBlindness, Effects.ArrowWeakness,
                Effects.ArrowLevitation
        ));
        // Add the golden apple drops to the upgrade pool
        allUpgrades.add("Golden Apple");
        allUpgrades.add("Enchanted Golden Apple");
        allUpgrades.add("3 Golden Apples");
        allUpgrades.add("3 Enchanted Golden Apples");

        Random rand = new Random();
        Object selectedUpgrade = null;
        int upgradeLevel = 1;

        do {
            // Check if the player has 0 points and first player reached end -> player can get special upgrades
            if (PointsListener.scorePerPlayer.get(player.getUniqueId()) == 0 && GameState.playersReachedEnd) {
                int random = rand.nextInt(5);

                if (random == 0) {
                    Effects.ResetLeadersUpgrades.put(player, true);
                    // reset all Effects and Enchantments to integer value 1 of the player with the most points, if all have equal points select a random upgrade instead

                    resetLeadersUpgrades = resetLeadersUpgrades();
                    if (!resetLeadersUpgrades) {
                        selectedUpgrade = allUpgrades.get(rand.nextInt(allUpgrades.size()));
                    }

                } else if (random == 1 || random == 2) {
                    Enchantments.OPUpgrades.put(player, true);
                    opUpgrades = true;
                } else {
                    selectedUpgrade = allUpgrades.get(rand.nextInt(allUpgrades.size()));
                }

            } else {
                // Select a random upgrade
                selectedUpgrade = allUpgrades.get(rand.nextInt(allUpgrades.size()));
            }

            // Determine the upgrade level based on the player's level and the type of the upgrade
            if (selectedUpgrade instanceof HashMap) {
                upgradeLevel = rand.nextInt(4) + 2;  // Level 1-4

                // Check for max levels
                if (selectedUpgrade == Enchantments.Multishot && upgradeLevel > 2) {
                    upgradeLevel = 2;
                } else if (selectedUpgrade == Enchantments.Flame && upgradeLevel > 2) {
                    upgradeLevel = 2;
                } else if (selectedUpgrade == Effects.DolphinsGrace && upgradeLevel > 2) {
                    upgradeLevel = 2;
                } else if (selectedUpgrade == Effects.FireResistance && upgradeLevel > 2) {
                    upgradeLevel = 2;
                } else if (selectedUpgrade == Effects.Resistance && upgradeLevel > 3) {
                    upgradeLevel = 3;
                } else if (selectedUpgrade == Effects.Strength && upgradeLevel > 3) {
                    upgradeLevel = 3;
                }
            }
        } while (selectedUpgrade instanceof HashMap && ((HashMap<Player, Integer>) selectedUpgrade).get(player) >= upgradeLevel
                || isThereHigherOrEqualArrowEffect(player, upgradeLevel));
        // Re-roll if the player already has this level or higher or there is another arrow effect with a higher or equal level

        // Upgrade the player
        if (selectedUpgrade instanceof HashMap) {
            ((HashMap<Player, Integer>) selectedUpgrade).put(player, upgradeLevel);
        } else {
            // Add the item to the player's inventory
            ItemStack itemStack;
            switch ((String) selectedUpgrade) {
                case "Golden Apple":
                    itemStack = new ItemStack(Material.GOLDEN_APPLE, 1);
                    break;
                case "Enchanted Golden Apple":
                    itemStack = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
                    break;
                case "3 Golden Apples":
                    itemStack = new ItemStack(Material.GOLDEN_APPLE, 3);
                    break;
                case "3 Enchanted Golden Apples":
                    itemStack = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + selectedUpgrade);
            }
            GameState.putItemInInventory(player, itemStack);
        }

        // Send 10 random subtitles before the actual upgrade
        for (int i = 0; i < 10; i++) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                Object randomUpgrade = allUpgrades.get(rand.nextInt(allUpgrades.size()));
                int randomLevel;
                if (randomUpgrade instanceof HashMap) {
                    randomLevel = rand.nextInt(4) + 2;  // Level 1-4
                    player.sendTitle("", getUpgradeName((HashMap<Player, Integer>) randomUpgrade) + " LVL " + (randomLevel - 1), 0, 5, 0);
                } else {
                    player.sendTitle("", ChatColor.GOLD + (String) randomUpgrade, 0, 5, 0);
                }
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
            }, 2L * i);
        }

        // Send the player a subtitle with the upgrade they received
        HashMap<Player, Integer> finalSelectedUpgrade = selectedUpgrade instanceof HashMap ? (HashMap<Player, Integer>) selectedUpgrade : null;
        int finalUpgradeLevel = upgradeLevel;
        Object finalSelectedUpgrade1 = selectedUpgrade;
        boolean finalOpUpgrades = opUpgrades;
        boolean finalResetLeadersUpgrades = resetLeadersUpgrades;

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (finalSelectedUpgrade != null) {
                player.sendTitle("", getUpgradeName(finalSelectedUpgrade) + " LVL " + (finalUpgradeLevel - 1), 0, 40, 10);
                player.sendMessage(ChatColor.GRAY + "You received " + getUpgradeName(finalSelectedUpgrade) + " LVL " + (finalUpgradeLevel - 1) + ChatColor.GRAY + ".");
            } else if (finalSelectedUpgrade1 == null) {
                String upgrade = "";

                if (finalOpUpgrades) {
                    upgrade = "OP Enchantments";
                } else if (finalResetLeadersUpgrades) {
                    upgrade = "Reset Leader's Level";
                }

                player.sendTitle("", ChatColor.DARK_RED.toString() + ChatColor.MAGIC + ";; "
                        + ChatColor.RED + upgrade
                        + ChatColor.DARK_RED.toString() + ChatColor.MAGIC + " ;;", 0, 40, 10);

                player.sendMessage(ChatColor.GRAY + "You received "
                        + ChatColor.DARK_RED.toString() + ChatColor.MAGIC + ";; "
                        + ChatColor.RED + upgrade
                        + ChatColor.DARK_RED.toString() + ChatColor.MAGIC + " ;;" + ChatColor.GRAY + ".");
            } else {
                player.sendTitle("", ChatColor.GOLD + (String) finalSelectedUpgrade1, 0, 40, 10);
                player.sendMessage(ChatColor.GRAY + "You received " + ChatColor.GOLD + finalSelectedUpgrade1 + ChatColor.GRAY + ".");
            }
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
        }, 20L);  // Delay in ticks
    }

    public String getUpgradeName(HashMap<Player, Integer> upgrade) {
        if (upgrade == Enchantments.Sharpness) return ChatColor.AQUA + "Sharpness";
        if (upgrade == Enchantments.Knockback) return ChatColor.AQUA + "Knockback";
        if (upgrade == Enchantments.FireAspect) return ChatColor.AQUA + "Fire Aspect";
        if (upgrade == Enchantments.Power) return ChatColor.AQUA + "Power";
        if (upgrade == Enchantments.Punch) return ChatColor.AQUA + "Punch";
        if (upgrade == Enchantments.Flame) return ChatColor.AQUA + "Flame";
        if (upgrade == Enchantments.Multishot) return ChatColor.AQUA + "Multishot";
        if (upgrade == Enchantments.Protection) return ChatColor.AQUA + "Protection";
        if (upgrade == Enchantments.Thorns) return ChatColor.AQUA + "Thorns";
        if (upgrade == Enchantments.FeatherFalling) return ChatColor.AQUA + "Feather Falling";
        if (upgrade == Effects.Speed) return ChatColor.LIGHT_PURPLE + "Speed";
        if (upgrade == Effects.JumpBoost) return ChatColor.LIGHT_PURPLE + "Jump Boost";
        if (upgrade == Effects.Haste) return ChatColor.LIGHT_PURPLE + "Haste";
        if (upgrade == Effects.DolphinsGrace) return ChatColor.LIGHT_PURPLE + "Dolphin's Grace";
        if (upgrade == Effects.FireResistance) return ChatColor.LIGHT_PURPLE + "Fire Resistance";
        if (upgrade == Effects.Resistance) return ChatColor.LIGHT_PURPLE + "Resistance";
        if (upgrade == Effects.Strength) return ChatColor.LIGHT_PURPLE + "Strength";
        if (upgrade == Effects.ArrowMiningFatigue) return ChatColor.YELLOW + "Mining Fatigue Arrows";
        if (upgrade == Effects.ArrowPoison) return ChatColor.YELLOW + "Poison Arrows";
        if (upgrade == Effects.ArrowBlindness) return ChatColor.YELLOW + "Blindness Arrows";
        if (upgrade == Effects.ArrowWeakness) return ChatColor.YELLOW + "Weakness Arrows";
        if (upgrade == Effects.ArrowLevitation) return ChatColor.YELLOW + "Levitation Arrows";
        return "";
    }

    private boolean isThereHigherOrEqualArrowEffect(Player player, int level) {
        List<HashMap<Player, Integer>> allArrowEffects = Arrays.asList(
                Effects.ArrowMiningFatigue, Effects.ArrowPoison, Effects.ArrowBlindness,
                Effects.ArrowWeakness, Effects.ArrowLevitation
        );

        for (HashMap<Player, Integer> arrowEffect : allArrowEffects) {
            if (arrowEffect.get(player) >= level) {
                return true;
            }
        }

        return false;
    }

    public boolean resetLeadersUpgrades() {
        UUID leader = null;
        int maxPoints = -1;

        // Find the player with the most points
        for (Map.Entry<UUID, Integer> entry : PointsListener.scorePerPlayer.entrySet()) {
            if (entry.getValue() > maxPoints) {
                leader = entry.getKey();
                maxPoints = entry.getValue();
            }
        }

        // If all players have equal points, select a random upgrade instead
        if (leader == null) {
            return false;
        }

        Player leaderPlayer = Bukkit.getPlayer(leader);
        if (leaderPlayer == null) return false;

        // Reset all effects and enchantments of the leader to integer value 1
        // Filter out the HashMap instances
        List<HashMap<Player, Integer>> hashMapUpgrades = allUpgrades.stream()
                .filter(upgrade -> upgrade instanceof HashMap)
                .map(upgrade -> (HashMap<Player, Integer>) upgrade)
                .collect(Collectors.toList());

        // Loop over the HashMap upgrades
        for (HashMap<Player, Integer> upgrade : hashMapUpgrades) {
            upgrade.put(leaderPlayer, 1);
        }

        // Reset the leaders level
        leaderPlayer.setLevel(0);

        return true;
    }
}
