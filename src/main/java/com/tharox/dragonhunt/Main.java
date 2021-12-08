package com.tharox.dragonhunt;

import com.tharox.dragonhunt.blocks.BlockBreakListener;
import com.tharox.dragonhunt.blocks.BlockPlaceListener;
import com.tharox.dragonhunt.commands.DateCommand;
import com.tharox.dragonhunt.deathlisteners.EntityDeathListener;
import com.tharox.dragonhunt.deathlisteners.PlayerDeathListener;
import com.tharox.dragonhunt.interact.PlayerInteractListener;
import com.tharox.dragonhunt.kits.KitListener;
import com.tharox.dragonhunt.listeners.*;
import com.tharox.dragonhunt.playerlisteners.AdvancementListener;
import com.tharox.dragonhunt.playerlisteners.EntityDamageListener;
import com.tharox.dragonhunt.playerlisteners.InventoryListener;
import com.tharox.dragonhunt.playerlisteners.PlayerConsumeListener;
import com.tharox.dragonhunt.shop.ShopCommand;
import com.tharox.dragonhunt.shop.ShopInventory;
import com.tharox.dragonhunt.shop.ShopListener;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import com.tharox.dragonhunt.tracker.Tracker;
import com.tharox.dragonhunt.vehicles.VehicleListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    public Timer timer;
    public Countdown scoreboardCountdown; //single countdown for all player scoreboards - started in GameStart, used in Scoreboard
    public Tracker tracker; //single tracker for all players

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig(); //config.yml
        new ShopInventory(getConfig());

        Bukkit.getLogger().fine("Plugin activated");

        timer = new Timer(false, 0);
        scoreboardCountdown = new Countdown(false, 180);
        tracker = new Tracker();

        listenerRegistration();
        commandRegistration();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().fine("Plugin deactivated");
    }

    public static Main getInstance() {
        return instance;
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

    public static String getPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Dragon"
                + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Hunt" + ChatColor.DARK_GRAY + "] "
                + ChatColor.WHITE;
    }

    private void listenerRegistration() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinListener(), this);
        pluginManager.registerEvents(new QuitListener(), this);
        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new ShopListener(), this);
        pluginManager.registerEvents(new BlockBreakListener(), this);
        pluginManager.registerEvents(new BlockPlaceListener(), this);
        pluginManager.registerEvents(new PlayerInteractListener(), this);
        pluginManager.registerEvents(new VehicleListener(), this);
        pluginManager.registerEvents(new EntityDeathListener(), this);
        pluginManager.registerEvents(new PlayerDeathListener(), this);
        pluginManager.registerEvents(new KitListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new PlayerConsumeListener(), this);
        pluginManager.registerEvents(new EntityDamageListener(), this);
        pluginManager.registerEvents(new AdvancementListener(), this);
        pluginManager.registerEvents(new EntityDropListener(), this);
        pluginManager.registerEvents(tracker, this);
    }

    private void commandRegistration() {
        getCommand("date").setExecutor(new DateCommand());
        getCommand("shop").setExecutor(new ShopCommand());
    }
}
