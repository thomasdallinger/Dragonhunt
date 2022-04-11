package com.tharox.dragonhunt.shop;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public abstract class EnchantmentUpgrade {
    public Player player;
    public int level;
    public abstract void enchantGear(Enchantment enchantment);
}
