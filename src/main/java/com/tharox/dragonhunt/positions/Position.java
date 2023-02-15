package com.tharox.dragonhunt.positions;

import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;

import java.util.Objects;

public class Position {
    private final String name;
    private final Location location;

    public Position(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return HexColor.GREEN + name + ": " + ChatColor.GRAY + Objects.requireNonNull(location.getWorld()).getName()
                + ", x: " + (int) location.getX() + ", y: " + (int) location.getY() + ", z: " + (int) location.getZ();
    }
}
