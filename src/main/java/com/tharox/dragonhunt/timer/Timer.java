package com.tharox.dragonhunt.timer;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameEnd;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.kits.classes.KitClass;
import com.tharox.dragonhunt.players.PlayerEndTeleport;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {
    private boolean running;
    private int time;

    public Timer(boolean running, int time) {
        this.running = running;
        this.time = time;

        run();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void sendActionBar() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            // if the player has his/her Tracker in the main hand, the time wont get displayed
            // if the player has his/her Kit Ability in the main hand, the time wont get displayed
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getItemMeta() != null) {
                if (item.getItemMeta().getDisplayName().contains("Tracker")
                        || item.getItemMeta().getDisplayName().contains("Set Respawn Point"))
                    continue;

                // Grappler shows the Timer - has no cooldown to display
                if ((item.getItemMeta().getDisplayName().contains(KitClass.ASSASSIN)
                        || item.getItemMeta().getDisplayName().contains(KitClass.TANK)
                        || item.getItemMeta().getDisplayName().contains(KitClass.SUPPORT)
                        || item.getItemMeta().getDisplayName().contains(KitClass.MOBILITY))
                        && !item.getItemMeta().getDisplayName().contains("Grappler"))
                    continue;

                if (GameState.actionBarNeededForKitAbility.containsKey(player)) {
                    if (GameState.actionBarNeededForKitAbility.get(player)) continue;
                }
            }

            // display the time
            // if the last 10 minutes have arrived, the time will get displayed in red
            ChatColor color = ChatColor.GOLD;

            if (getTime() >= 1500) {
                color = HexColor.RED;
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new ComponentBuilder(DbValues.getTimestamp()).color(color).bold(true).create());
        }
    }

    protected void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning()) {
                    return;
                }

                if (getTime() % 600 == 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (getTime() == 1200) { // 20 minutes played
                            int coinsToAdd = 30;
                            Coins.addCoins(player, coinsToAdd);
                            player.sendMessage(Prefixes.getCoinPrefix(coinsToAdd)
                                    + HexColor.HIGHLIGHT + ChatColor.BOLD + "Bonus resources delivered.");

                            GameState.putItemInInventory(player, new ItemStack(Material.GOLDEN_APPLE, 2));
                            GameState.putItemInInventory(player, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
                            GameState.putItemInInventory(player, new ItemStack(Material.NETHERITE_INGOT));
                        }

                        player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GRAY + "The game ends in "
                                + ChatColor.YELLOW + (2100 / 60 - getTime() / 60) + ChatColor.GRAY + " minutes.");
                    }
                }

                if (getTime() == 1500) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Prefixes.getAllPrefix() + HexColor.RED + "ATTENTION! "
                                + HexColor.HIGHLIGHT + PlayerEndTeleport.getEyesNeeded()
                                + " Eyes of Ender " + ChatColor.GRAY + "needed to get the "
                                + ChatColor.LIGHT_PURPLE + " End Teleporter"
                                + ChatColor.GRAY + ".");
                    }
                }

                // once the timer hits 35 minutes, the game will end without a winner
                if (getTime() == 2100) {
                    new GameEnd();
                    setRunning(false);
                }

                setTime(getTime() + 1);
                sendActionBar();
                Main.getInstance().getTablistManager().setAllPlayerTeams();

            }
        }.runTaskTimer(Main.getInstance(), 40, 20);
    }
}
