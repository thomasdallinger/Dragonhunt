package com.tharox.dragonhunt.positions;

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
}
