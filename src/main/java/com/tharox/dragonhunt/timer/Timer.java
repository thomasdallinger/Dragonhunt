package com.tharox.dragonhunt.timer;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameEnd;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.kits.classes.KitClass;
import com.tharox.dragonhunt.players.CombatListener;
import com.tharox.dragonhunt.players.PlayerEndTeleport;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
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
                        || item.getItemMeta().getDisplayName().contains("Respawn Anchor"))
                    continue;

                // Grappler shows the Timer - has no cooldown to display
                if ((item.getItemMeta().getDisplayName().contains(KitClass.ASSASSIN)
                        || item.getItemMeta().getDisplayName().contains(KitClass.TANK)
                        || item.getItemMeta().getDisplayName().contains(KitClass.SUPPORT)
                        || item.getItemMeta().getDisplayName().contains(KitClass.MOBILITY))
                        && !item.getItemMeta().getDisplayName().contains("Grappler"))
                    continue;

                if (GameState.kitAbilityActive.containsKey(player)) {
                    if (GameState.kitAbilityActive.get(player)) continue;
                }
            }

            // Display the time (with or without in combat alert)
            ChatColor color = HexColor.YELLOW;

            if (CombatListener.combatList.containsKey(player.getUniqueId())) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new ComponentBuilder(DbValues.getTimestamp()).color(color).bold(true)
                                .append(" (").color(ChatColor.GRAY).bold(false)
                                .append(DbValues.getTime(1800 - getTime())).color(ChatColor.GRAY)
                                .append(")").color(ChatColor.GRAY)
                                .append(" » ").color(ChatColor.DARK_GRAY)
                                .append("In Combat for ").color(ChatColor.GRAY)
                                .append(String.valueOf(CombatListener.combatList.get(player.getUniqueId())))
                                .color(HexColor.RED).bold(true)
                                .append(" seconds").color(ChatColor.GRAY).bold(false).create());
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new ComponentBuilder(DbValues.getTimestamp()).color(color).bold(true)
                                .append(" (").color(ChatColor.GRAY).bold(false)
                                .append(DbValues.getTime(1800 - getTime())).color(ChatColor.GRAY)
                                .append(")").color(ChatColor.GRAY).create());
            }
        }
    }

    protected void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning()) {
                    return;
                }

                // once the timer hits 30 minutes, the game ends
                // winner: player with the highest score
                if (getTime() == 1800) {
                    new GameEnd();
                    setRunning(false);
                }

                // every 5 minutes
                if (getTime() % 300 == 0 && getTime() != 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(Prefixes.getImportantPrefix() + ChatColor.GRAY + "The Game ends in "
                                + HexColor.YELLOW + (1800 / 60 - getTime() / 60) + ChatColor.GRAY + " minutes.");
                    }
                }

                if (getTime() == 1200) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Prefixes.getImportantPrefix()
                                + HexColor.YELLOW + PlayerEndTeleport.getEyesNeeded()
                                + " Eyes of Ender " + ChatColor.GRAY + "needed to be able to enter "
                                + ChatColor.WHITE + "The End" + ChatColor.GRAY + ".");
                    }
                }

                setTime(getTime() + 1);
                sendActionBar();
                Main.getInstance().getTablistManager().setAllPlayerTeams();

            }
        }.runTaskTimer(Main.getInstance(), 40, 20);
    }
}
