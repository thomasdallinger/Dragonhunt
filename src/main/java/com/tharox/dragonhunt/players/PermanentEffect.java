package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.modifiers.Heavyweight;
import com.tharox.dragonhunt.modifiers.HighSpeed;
import com.tharox.dragonhunt.modifiers.Lightweight;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PermanentEffect {
    PotionEffectType potionEffectType = null;

    public PermanentEffect() {
        if (Heavyweight.active) {
            potionEffectType = PotionEffectType.INCREASE_DAMAGE;
        } else if (Lightweight.active) {
            potionEffectType = PotionEffectType.WEAKNESS;
        } else if (HighSpeed.active) {
            potionEffectType = PotionEffectType.SPEED;
        }

        if (potionEffectType != null) {
            run();
        }
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(new PotionEffect(potionEffectType, 40, 0));
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
