package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.modifiers.classes.Heavyweight;
import com.tharox.dragonhunt.modifiers.classes.HighSpeed;
import com.tharox.dragonhunt.modifiers.classes.Lightweight;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PermanentEffect {
    PotionEffectType potionEffectType = null;
    PotionEffectType potionEffectType2 = null;

    public PermanentEffect() {
        if (Heavyweight.active) {
            potionEffectType = PotionEffectType.INCREASE_DAMAGE;
        } else if (Lightweight.active) {
            potionEffectType = PotionEffectType.WEAKNESS;
        }

        if (HighSpeed.active) {
            potionEffectType2 = PotionEffectType.SPEED;
        }

        if (potionEffectType != null || potionEffectType2 != null) {
            run();
        }
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (potionEffectType != null) {
                        player.addPotionEffect(new PotionEffect(potionEffectType, 40, 0));
                    }

                    if (potionEffectType2 != null) {
                        player.addPotionEffect(new PotionEffect(potionEffectType2, 40, 0));
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
