package com.tharox.dragonhunt;

import com.tharox.dragonhunt.arrows.ProjectileHitListener;
import com.tharox.dragonhunt.blocks.BlockBreakListener;
import com.tharox.dragonhunt.blocks.BlockExplodeListener;
import com.tharox.dragonhunt.blocks.BlockPlaceListener;
import com.tharox.dragonhunt.brewing.BrewingListener;
import com.tharox.dragonhunt.chat.ChatListener;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.LevelUp;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.commands.DateCommand;
import com.tharox.dragonhunt.commands.HubCommand;
import com.tharox.dragonhunt.commands.TopCommand;
import com.tharox.dragonhunt.crafting.CustomRecipes;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.dimensions.DimensionListener;
import com.tharox.dragonhunt.enchanting.EnchantingListener;
import com.tharox.dragonhunt.entities.CreatureSpawnListener;
import com.tharox.dragonhunt.entities.EntityDamageListener;
import com.tharox.dragonhunt.entities.EntityDeathListener;
import com.tharox.dragonhunt.entities.EntityDropListener;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.grappler.GrappleListener;
import com.tharox.dragonhunt.kits.classes.KitClass;
import com.tharox.dragonhunt.kits.selection.KitListener;
import com.tharox.dragonhunt.listeners.JoinListener;
import com.tharox.dragonhunt.listeners.QuitListener;
import com.tharox.dragonhunt.players.*;
import com.tharox.dragonhunt.respawn.RespawnAnchor;
import com.tharox.dragonhunt.scoreboard.ResetScoreboardCountdown;
import com.tharox.dragonhunt.tablist.TablistManager;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import com.tharox.dragonhunt.tracker.Tracker;
import com.tharox.dragonhunt.vehicles.VehicleListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public final class Main extends JavaPlugin implements PluginMessageListener {

    private static Main instance;
    public static boolean firstPlayerJoined = false;

    public Timer timer;
    public Countdown scoreboardCountdown; // single countdown for all player scoreboards (started in GameStart)
    public Tracker tracker; // single tracker for all players
    public RespawnAnchor respawn;
    private TablistManager tablistManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig(); // config.yml

        Bukkit.getLogger().fine("Plugin activated");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        timer = new Timer(false, 0);
        scoreboardCountdown = new Countdown(false, 180);
        ResetScoreboardCountdown.countdownHitsZero(Main.getInstance().getScoreboardCountdown());
        tracker = new Tracker();
        respawn = new RespawnAnchor();
        tablistManager = new TablistManager();
        new CustomRecipes(); // adds all custom recipes to the game
        setWorldTime();

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
     * Sets the time in the OVERWORLD to one of the available times.
     */
    private void setWorldTime() {
        World overworld = Bukkit.getServer().getWorld("world");

        if (overworld == null) {
            Bukkit.getServer().broadcastMessage(Prefixes.getErrorPrefix() + "World name is incorrect.");
            return;
        }

        ArrayList<Integer> availableTimes = new ArrayList<>();
        availableTimes.add(16000);
        availableTimes.add(13000);
        availableTimes.add(12000);
        availableTimes.add(11000);
        availableTimes.add(10000);
        availableTimes.add(0);

        Random random = new Random();
        int index = random.nextInt(availableTimes.size());

        overworld.setTime(availableTimes.get(index));
    }

    /**
     * If one or more of the players, who are supposed to join the game,
     * are not joining, the players on the server will get moved back to the lobby 90 seconds
     * after the plugin has been enabled.
     * Once no more players are on this server, the server closes and the database is updated.
     * <p>
     * If no players join the server the server also closes 90 seconds after the plugin has been enabled.
     * This can happen if the server is started but the game countdown in the LOBBY gets cancelled
     * due to players leaving the queue.
     * <p>
     * This method is only called once.
     */
    public static void stopServerIn() {
        new BukkitRunnable() {
            int timeLeft = 90;

            @Override
            public void run() {
                timeLeft--;

                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.sendTitle(Main.getTitle(), HexColor.PURPLE + "Players are joining",
                            0, 25, 0);
                }

                if (timeLeft == 0) {
                    GameState.movePlayersToLobby();
                    this.cancel();
                }

                if (Main.firstPlayerJoined && DbValues.playersAllowedToPlay.size() == 0) {
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

    public static String getTitle() {
        return ChatColor.of("#800ce9").toString() + ChatColor.BOLD + "D"
                + ChatColor.of("#8f11e7").toString() + ChatColor.BOLD + "R"
                + ChatColor.of("#9c12e7").toString() + ChatColor.BOLD + "A"
                + ChatColor.of("#aa17e5").toString() + ChatColor.BOLD + "G"
                + ChatColor.of("#b818e2").toString() + ChatColor.BOLD + "O"
                + ChatColor.of("#c61de2").toString() + ChatColor.BOLD + "N"
                + ChatColor.of("#d520e0").toString() + ChatColor.BOLD + "H"
                + ChatColor.of("#e222de").toString() + ChatColor.BOLD + "U"
                + ChatColor.of("#f127dc").toString() + ChatColor.BOLD + "N"
                + ChatColor.of("#fe28dc").toString() + ChatColor.BOLD + "T";
    }

    private void listenerRegistration() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinListener(), this);
        pluginManager.registerEvents(new QuitListener(), this);
        pluginManager.registerEvents(new ChatListener(), this);

        pluginManager.registerEvents(new GrappleListener(), this);

        pluginManager.registerEvents(new ProjectileHitListener(), this);

        pluginManager.registerEvents(new KitClass(), this);
        pluginManager.registerEvents(new KitListener(), this);

        pluginManager.registerEvents(new LevelUp(), this);
        pluginManager.registerEvents(new EnchantingListener(), this);
        pluginManager.registerEvents(new BrewingListener(), this);

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
        pluginManager.registerEvents(new BucketEmptyListener(), this);
        pluginManager.registerEvents(new CombatListener(), this);
        pluginManager.registerEvents(new DamageAdjustmentListener(), this);
        pluginManager.registerEvents(new ItemConsumeListener(), this);
        pluginManager.registerEvents(new PlayerDamageListener(), this);
        pluginManager.registerEvents(new PlayerBedEnterListener(), this);
        pluginManager.registerEvents(new PlayerDeathListener(), this);
        pluginManager.registerEvents(new PlayerEndTeleport(), this);
        pluginManager.registerEvents(new PlayerInteractListener(), this);
        pluginManager.registerEvents(new PlayerInventoryListener(), this);
        pluginManager.registerEvents(new PlayerRespawnListener(), this);
        pluginManager.registerEvents(new PointsListener(), this);

        pluginManager.registerEvents(tracker, this);
        pluginManager.registerEvents(respawn, this);
    }

    private void commandRegistration() {
        getCommand("date").setExecutor(new DateCommand());
        getCommand("top").setExecutor(new TopCommand());
        getCommand("hub").setExecutor(new HubCommand());
        getCommand("lobby").setExecutor(new HubCommand());
        getCommand("l").setExecutor(new HubCommand());
    }
}
