package com.tharox.dragonhunt.shop;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class Upgrades {
    public static HashMap<Player, Integer> Cooldown = new HashMap<>();
    public static HashMap<Player, Integer> CooldownUpgradeLvl = new HashMap<>();
    public static HashMap<Player, Integer> Duration = new HashMap<>();
    public static HashMap<Player, Integer> DurationUpgradeLvl = new HashMap<>();
    public static HashMap<Player, Integer> Effectiveness = new HashMap<>();
    public static HashMap<Player, Integer> EffectivenessUpgradeLvl = new HashMap<>();
}
