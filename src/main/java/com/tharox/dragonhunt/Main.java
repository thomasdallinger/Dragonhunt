package com.tharox.dragonhunt;

import com.tharox.dragonhunt.blocks.BlockBreakListener;
import com.tharox.dragonhunt.blocks.BlockExplodeListener;
import com.tharox.dragonhunt.blocks.BlockPlaceListener;
import com.tharox.dragonhunt.commands.DateCommand;
import com.tharox.dragonhunt.crafting.CustomRecipes;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.dimensions.DimensionListener;
import com.tharox.dragonhunt.entities.CreatureSpawnListener;
import com.tharox.dragonhunt.entities.EntityDamageListener;
import com.tharox.dragonhunt.entities.EntityDeathListener;
import com.tharox.dragonhunt.entities.EntityDropListener;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.grappler.GrappleListener;
import com.tharox.dragonhunt.listeners.ChatListener;
import com.tharox.dragonhunt.listeners.JoinListener;
import com.tharox.dragonhunt.listeners.QuitListener;
import com.tharox.dragonhunt.players.*;
import com.tharox.dragonhunt.points.LevelUp;
import com.tharox.dragonhunt.positions.PositionCommand;
import com.tharox.dragonhunt.scoreboard.ResetScoreboardCountdown;
import com.tharox.dragonhunt.spectate.Spectate;
import com.tharox.dragonhunt.spectate.SpectateCommand;
import com.tharox.dragonhunt.tablist.TablistManager;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import com.tharox.dragonhunt.tracker.Tracker;
import com.tharox.dragonhunt.upgradeMenu.UpgradeCommand;
import com.tharox.dragonhunt.upgradeMenu.UpgradeListener;
import com.tharox.dragonhunt.vehicles.VehicleListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

public final class Main extends JavaPlugin implements PluginMessageListener {

    private static Main instance;

    public Timer timer;
    public Countdown scoreboardCountdown; // single countdown for all player scoreboards (started in GameStart)
    public Tracker tracker; // single tracker for all players
    private TablistManager tablistManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig(); //config.yml

        Bukkit.getLogger().fine("Plugin activated");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        timer = new Timer(false, 0);
        scoreboardCountdown = new Countdown(false, 300);
        ResetScoreboardCountdown.countdownHitsZero(Main.getInstance().getScoreboardCountdown());
        tracker = new Tracker();
        tablistManager = new TablistManager();
        new CustomRecipes(); // adds all custom recipes to the game

        listenerRegistration();
        commandRegistration();

        stopServerIn();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().fine("Plugin deactivated");
    }

    public static Main getInstance() {
        return instance;
    }

    /**
     * If one or more of the players, who is/are supposed to join the game,
     * are not joining the players on the server will get moved back to the lobby after 60 seconds.
     * The time starts once the plugin gets enabled.
     */
    private void stopServerIn() {
        new BukkitRunnable() {
            int timeLeft = 60;

            @Override
            public void run() {
                timeLeft--;

                if (timeLeft == 0) {
                    GameState.movePlayersToLobby();
                }

                if (DbValues.playersAllowedToPlay.size() == 0) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
    }

    public Timer getTimer() {
        return timer;
    }

    public Countdown getScoreboardCountdown() {
        return scoreboardCountdown;
    }

    public Tracker getTracker() {
        return tracker;
    }

    public TablistManager getTablistManager() {
        return tablistManager;
    }

    public static String getPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "DRAGON"
                + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "HUNT" + ChatColor.DARK_GRAY + "] "
                + ChatColor.WHITE;
    }

    public static String getNamePrefix(Player player) {
        return ChatColor.DARK_GRAY + "["
                + ChatColor.GREEN.toString() + DbValues.getKills(player, true)
                + ChatColor.DARK_GRAY + " | "
                + ChatColor.GOLD + Spectate.spectatesLeft.get(player)
                + ChatColor.DARK_GRAY + " | "
                + ChatColor.LIGHT_PURPLE + player.getLevel()
                + ChatColor.DARK_GRAY + "] ";
    }

    public static void elytraInInventory() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (ItemStack item : player.getInventory()) {
                        if (item != null && item.getItemMeta() != null) {
                            if (item.getType() == Material.ELYTRA) {
                                player.getInventory().remove(item);
                                player.getInventory().setChestplate(item);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    private void listenerRegistration() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinListener(), this);
        pluginManager.registerEvents(new QuitListener(), this);
        pluginManager.registerEvents(new ChatListener(), this);

        pluginManager.registerEvents(new GrappleListener(), this);

        pluginManager.registerEvents(new LevelUp(), this);
        pluginManager.registerEvents(new UpgradeListener(), this);

        pluginManager.registerEvents(new BlockBreakListener(), this);
        pluginManager.registerEvents(new BlockExplodeListener(), this);
        pluginManager.registerEvents(new BlockPlaceListener(), this);

        pluginManager.registerEvents(new DimensionListener(), this);

        pluginManager.registerEvents(new VehicleListener(), this);
        pluginManager.registerEvents(new CreatureSpawnListener(), this);
        pluginManager.registerEvents(new EntityDeathListener(), this);
        pluginManager.registerEvents(new EntityDropListener(), this);
        pluginManager.registerEvents(new EntityDamageListener(), this);

        pluginManager.registerEvents(new AdvancementListener(), this);
        pluginManager.registerEvents(new BukketEmptyListener(), this);
        pluginManager.registerEvents(new PlayerDamageListener(), this);
        pluginManager.registerEvents(new PlayerDeathListener(), this);
        pluginManager.registerEvents(new PlayerInteractListener(), this);
        pluginManager.registerEvents(new PlayerInventoryListener(), this);
        pluginManager.registerEvents(new PlayerRespawnListener(), this);

        pluginManager.registerEvents(tracker, this);
    }

    private void commandRegistration() {
        getCommand("date").setExecutor(new DateCommand());
        getCommand("upgrades").setExecutor(new UpgradeCommand());
        getCommand("spec").setExecutor(new SpectateCommand());
        getCommand("pos").setExecutor(new PositionCommand());
    }
}
