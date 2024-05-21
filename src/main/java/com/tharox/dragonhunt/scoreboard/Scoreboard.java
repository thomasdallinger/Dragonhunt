package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.players.PlayerEndTeleport;
import com.tharox.dragonhunt.players.PointsListener;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class Scoreboard extends ScoreboardBuilder {
    Timer timer = Main.getInstance().getTimer();

    public Scoreboard(Player player) {
        super(player, Main.getTitle());

        run(player, Main.getInstance().getScoreboardCountdown());
    }

    @Override
    public void createScoreboard() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");
        LocalDateTime now = LocalDateTime.now();

        setScore(ChatColor.GRAY + dtf.format(now) + ChatColor.DARK_GRAY + " v" + DbValues.getVersion() + " ["
                + DbValues.getServerID() + "]", 13);
        setScore("", 12);
        setScore("", 11);
        setScore("", 10);
        setScore("", 9);
        setScore("", 8);
        setScore("", 7);
        setScore("", 6);
        setScore("", 5);
        setScore("", 4);
        setScore("", 3);
        setScore("", 2);
        setScore("", 1);
        setScore("", 0);
    }

    @Override
    public void update(Player player, Countdown countdown) {
        if (GameState.eloPerPlayer.getOrDefault(player, 0) >= 0) {
            setScore(HexColor.GREEN + "+" + GameState.eloPerPlayer.getOrDefault(player, 0)
                    + ChatColor.WHITE + " ✪", 12);
        } else {
            setScore(HexColor.RED.toString() + GameState.eloPerPlayer.getOrDefault(player, 0)
                    + ChatColor.WHITE + " ✪", 12);
        }

        if (GameState.selectedKit.containsKey(player)) {
            setScore(GameState.selectedKit.get(player), 10);
        }

        setScore("☻ " + Bukkit.getOnlinePlayers().size() + " "
                + "☠ " + DbValues.getKills(player, true) + " "
                + "✟ " + DbValues.getKills(player, false), 9);

        // if the player has not got the Enter the nether advancement
        if (!hasAdvancement(player, "story/enter_the_nether")) {
            setScore(ChatColor.GOLD + "Build a Nether Portal", 7);

            setScore(((hasItems(player, Material.IRON_INGOT, 4) && hasItems(player, Material.FLINT, 1))
                    || (hasItems(player, Material.WATER_BUCKET, 1) && hasItems(player, Material.FLINT_AND_STEEL, 1)) ? HexColor.GREEN + "✔" : HexColor.RED + "✘")
                    + ChatColor.GRAY + " Get " + ChatColor.WHITE + "4 Iron" + ChatColor.GRAY + ", " + ChatColor.WHITE + "1 Flint", 6);

            setScore(HexColor.YELLOW + "»" + ChatColor.GRAY + " Find " + ChatColor.WHITE + "Lava", 5);
        }
        // if the player has not got 8 blaze powder in his inventory
        else if (!hasItems(player, Material.BLAZE_POWDER, PlayerEndTeleport.getEyesNeeded())
                && !hasDisplayNameItem(player, "Enter the End")
                && !player.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            setScore(ChatColor.GOLD + "Get " + PlayerEndTeleport.getEyesNeeded() + " Blaze Powder", 7);

            setScore((hasAdvancement(player, "nether/find_fortress") ? HexColor.GREEN + "✔" : HexColor.RED + "✘")
                    + ChatColor.GRAY + " Enter the " + ChatColor.WHITE + "Fortress", 6);

            setScore((hasItems(player, Material.BLAZE_POWDER, 1) ? HexColor.GREEN + "✔" : HexColor.RED + "✘")
                    + ChatColor.GRAY + " Kill " + ChatColor.WHITE + "Blazes", 5);
        }
        // if the player has not got 8 ender pearls in his inventory
        else if (!hasItems(player, Material.ENDER_PEARL, PlayerEndTeleport.getEyesNeeded())
                && !hasDisplayNameItem(player, "Enter the End")
                && !player.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            setScore(ChatColor.GOLD + "Get " + PlayerEndTeleport.getEyesNeeded() + " Ender Pearls", 7);

            setScore((!hasDisplayNameItem(player, "Enter Bastion") ? HexColor.GREEN + "✔" : HexColor.RED + "✘")
                    + ChatColor.GRAY + " Use the " + ChatColor.WHITE + "Teleporter", 6);

            setScore((hasAdvancement(player, "nether/distract_piglin") ? HexColor.GREEN + "✔" : HexColor.RED + "✘")
                    + ChatColor.GRAY + " Trade " + ChatColor.WHITE + "Gold" + ChatColor.GRAY + " with " + ChatColor.WHITE + "Piglins", 5);
        }
        // if the player has not got the End Teleporter yet
        else if (!hasDisplayNameItem(player, "Enter the End")
                && !player.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            setScore(ChatColor.GOLD + "Craft Ender Eyes", 7);
            setScore("", 6);
            setScore("", 5);
        }
        // if the player has an item in his inventory that contains the displayname "Enter the End"
        else if (hasDisplayNameItem(player, "Enter the End")) {
            setScore(ChatColor.GOLD + "Enter the End", 7);
            setScore(HexColor.YELLOW + "»" + ChatColor.GRAY + " Use the " + ChatColor.WHITE + "Teleporter", 6);
            setScore("", 5);
        }
        // if the player is in the End
        else if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
            player.sendMessage(ChatColor.GOLD + "Damage the Dragon");
            setScore("", 6);
            setScore("", 5);
        }

        List<UUID> sortedKeys = GameState.sortPlayersByPoints(PointsListener.scorePerPlayer);

        for (int index = 0; index < sortedKeys.size(); index++) {
            Player p = Bukkit.getPlayer(sortedKeys.get(index));
            if (p == null) continue;

            if (index < 3) {
                if (p == player) {
                    setScore(ChatColor.GRAY + "#" + (index + 1) + " "
                            + HexColor.YELLOW + p.getName() + " "
                            + HexColor.YELLOW + PointsListener.scorePerPlayer.get(p.getUniqueId()), 3 - index);
                } else {
                    setScore(ChatColor.GRAY + "#" + (index + 1) + " "
                            + ChatColor.WHITE + p.getName() + " "
                            + HexColor.YELLOW + PointsListener.scorePerPlayer.get(p.getUniqueId()), 3 - index);
                }
            }
            // Player is not in the top 3
            else {
                if (p == player) {
                    setScore(ChatColor.GRAY + "#" + (index + 1) + " "
                            + HexColor.YELLOW + player.getName() + " "
                            + HexColor.YELLOW + PointsListener.scorePerPlayer.get(player.getUniqueId()), 0);
                }
            }
        }
    }

    private void run(Player player, Countdown countdown) {
        new BukkitRunnable() {
            @Override
            public void run() {
                update(player, countdown);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    private String countdownFormat(int time) {

        String minutes = "";
        String seconds = "";
        if ((time / 60) % 60 < 10)
            minutes = "0";
        minutes += (time / 60) % 60;
        if (time % 60 < 10)
            seconds = "0";
        seconds += time % 60;

        return minutes + ":" + seconds;
    }

    private boolean hasItems(Player player, Material material, int amount) {
        return player.getInventory().contains(material, amount);
    }

    private boolean hasAdvancement(Player player, String key) {
        Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft(key));
        return player.getAdvancementProgress(advancement).isDone();
    }

    private boolean hasDisplayNameItem(Player player, String displayName) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                    && item.getItemMeta().getDisplayName().contains(displayName)) {
                return true;
            }
        }
        return false;
    }
}
