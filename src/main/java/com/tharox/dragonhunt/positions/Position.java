package com.tharox.dragonhunt.positions;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;

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
        return ChatColor.DARK_GREEN + name + ChatColor.DARK_GRAY + ": "
                + ChatColor.GRAY + DbValues.getCurrentWorld(location)
                + HexColor.GREEN + " [" + (int) location.getX() + ", " + (int) location.getY()
                + ", " + (int) location.getZ() + "]";
    }
}
