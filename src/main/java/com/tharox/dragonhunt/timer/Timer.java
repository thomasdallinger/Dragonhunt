package com.tharox.dragonhunt.timer;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameEnd;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.kits.classes.KitClass;
import com.tharox.dragonhunt.players.CombatListener;
import com.tharox.dragonhunt.players.PlayerTeleport;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

            if (player.getGameMode().equals(GameMode.SPECTATOR)) continue;

            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                String title = meta.getDisplayName();
                Material materialToCheck = null;

                // Kit Ability - active Kit Ability overrules everything else in the action bar
                if (GameState.kitAbilityActive.containsKey(player)) {
                    // Kit Ability is active
                    if (GameState.kitAbilityActive.get(player) > 0) {
                        // Displaying the duration left in the action bar
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder("DURATION").color(HexColor.YELLOW).bold(true)
                                        .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                        .append(GameState.kitAbilityActive.get(player) + " seconds")
                                        .color(ChatColor.GRAY).create());
                        continue;
                    }
                }

                if (title.contains("Scout"))
                    continue;

                // Kit item
                if (title.contains(KitClass.ASSASSIN) || title.contains(KitClass.TANK)
                        || title.contains(KitClass.SUPPORT) || title.contains(KitClass.FLANKER)) {

                    if (player.getCooldown(Material.CROSSBOW) > 0) {
                        materialToCheck = Material.CROSSBOW;
                    } else if (player.getCooldown(Material.BOW) > 0) {
                        materialToCheck = Material.BOW;
                    } else if (player.getCooldown(Material.NETHER_STAR) > 0) {
                        materialToCheck = Material.NETHER_STAR;
                    }

                    // Cooldown is active
                    if (materialToCheck != null) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder("COOLDOWN").color(HexColor.RED).bold(true)
                                        .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                        .append(String.valueOf((player.getCooldown(materialToCheck) / 20)))
                                        .color(ChatColor.RED)
                                        .append(" seconds").color(ChatColor.RED).create());
                    }
                    // Ability is ready
                    else {
                        if (getTime() % 2 == 0) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    new ComponentBuilder("ABILITY READY").color(HexColor.YELLOW).bold(true)
                                            .append(" (Right-Click)").color(ChatColor.GRAY).bold(false)
                                            .create());
                        } else {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    new ComponentBuilder("ABILITY READY").color(ChatColor.WHITE).bold(true)
                                            .append(" (Right-Click)").color(ChatColor.GRAY).bold(false)
                                            .create());
                        }
                    }

                    continue;
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

                // last minute
                if (getTime() == 1740 || getTime() >= 1785) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(Prefixes.getImportantPrefix() + ChatColor.GRAY + "The Game ends in "
                                + HexColor.YELLOW + (1800 - getTime()) + ChatColor.GRAY + " seconds.");
                    }
                }

                if (getTime() == 900 && !GameState.playersReachedEnd) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Prefixes.getImportantPrefix()
                                + HexColor.YELLOW + PlayerTeleport.getEyesNeeded()
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
