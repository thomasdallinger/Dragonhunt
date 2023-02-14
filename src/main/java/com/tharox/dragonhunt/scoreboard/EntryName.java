package com.tharox.dragonhunt.scoreboard;

import net.md_5.bungee.api.ChatColor;

public enum EntryName {

    ENTRY_0(0, ChatColor.DARK_PURPLE.toString()),
    ENTRY_1(1, ChatColor.DARK_GRAY.toString()),
    ENTRY_2(2, ChatColor.BOLD.toString()),
    ENTRY_3(3, ChatColor.DARK_RED.toString()),
    ENTRY_4(4, ChatColor.GRAY.toString()),
    ENTRY_5(5, ChatColor.DARK_GREEN.toString()),
    ENTRY_6(6, ChatColor.LIGHT_PURPLE.toString()),
    ENTRY_7(7, ChatColor.GREEN.toString()),
    ENTRY_8(8, ChatColor.RED.toString()),
    ENTRY_9(9, ChatColor.BLUE.toString()),
    ENTRY_10(10, ChatColor.GOLD.toString());
    /*ENTRY_11(11, ChatColor.YELLOW.toString()),
    ENTRY_12(12, ChatColor.WHITE.toString()),
    ENTRY_13(13, ChatColor.AQUA.toString());*/

    private final int entry;
    private final String entryName;

    EntryName(int entry, String entryName) {
        this.entry = entry;
        this.entryName = entryName;
    }

    public int getEntry() {
        return entry;
    }

    public String getEntryName() {
        return entryName;
    }

}
