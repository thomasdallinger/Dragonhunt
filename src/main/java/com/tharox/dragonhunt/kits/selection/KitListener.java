package com.tharox.dragonhunt.kits.selection;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.kits.classes.DragonhuntKit;
import com.tharox.dragonhunt.kits.classes.Kit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class KitListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException, NoSuchFieldException {
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals(HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "KIT SELECTION"))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String name = item.getItemMeta().getDisplayName();

        // player clicked a kit class
        if (name.contains("Assasin") || name.contains("Tank")
                || name.contains("Support") || name.contains("Mobility")) return;

        Kit kit = null;

        Reflections reflections = new Reflections("com.tharox.dragonhunt");
        Set<Class<?>> kitClasses = reflections.getTypesAnnotatedWith(DragonhuntKit.class);

        Class<Kit> kitClass = (Class<Kit>) kitClasses.stream().filter(
                (innerKitClass) -> {
                    if (Kit.class.isAssignableFrom(innerKitClass)) {
                        try {
                            return ((String) innerKitClass.getField("title").get(null)).equals(name);
                        } catch (IllegalAccessException | NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
        ).findFirst().get();

        kit = kitClass.getDeclaredConstructor(Player.class).newInstance(player);

        player.getInventory().setItem(8,
                Kit.getDisplayItem((String) kitClass.getField("title").get(null),
                        (Material) kitClass.getField("material").get(null),
                        (String) kitClass.getField("lore").get(null)));

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        player.sendMessage(Prefixes.getAllPrefix() + "You have selected the "
                + HexColor.HIGHLIGHT + name + ChatColor.GRAY + " Kit.");
        player.closeInventory();

        GameState.selectedKit.put(player, (String) kitClass.getField("title").get(null));
        GameState.kitInventoryClose.put(player, true);

        //addKit(player, name); --> related to db
    }

    /*private void addKit(Player player, String name) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE Plays SET kit = '" + name.substring(2) + "' " +
                    "WHERE GameID = '" + DbValues.getGameID() + "' AND Player = '" + player.getName() + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }*/
}
