package net.omni.worldeditborder.util;

import net.omni.worldeditborder.WorldEditBorder;
import net.omni.worldeditborder.objects.Border;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public class BorderHandler {
    private static final List<Border> BORDERS = new ArrayList<>();
    private static final IdentityHashMap<Player, Border> INSIDE = new IdentityHashMap<>();

    private final WorldEditBorder plugin;

    public BorderHandler(WorldEditBorder plugin) {
        this.plugin = plugin;
    }

    public void loadBorders() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("borders");

        if (section == null) {
            plugin.sendConsole("&cBorders not found in config.yml.");
            return;
        }

        for (String key : section.getKeys(false)) {
            plugin.sendConsole("&aLoading " + key);

            String name = plugin.getConfig().getString("borders." + key + ".name");

            if (name == null) {
                plugin.sendConsole("&cName not found for " + key);
                continue;
            }

            String world = plugin.getConfig().getString("borders." + key + ".world");

            if (world == null) {
                plugin.sendConsole("&cWorld not found for " + key);
                continue;
            }

            String center = plugin.getConfig().getString("borders." + key + ".center");

            if (center == null) {
                plugin.sendConsole("&cCenter not found for " + key);
                continue;
            }

            String min = plugin.getConfig().getString("borders." + key + ".min");

            if (min == null) {
                plugin.sendConsole("&cMinimum point for " + key + " not found.");
                continue;
            }

            String max = plugin.getConfig().getString("borders." + key + ".max");

            if (max == null) {
                plugin.sendConsole("&cMaximum point for " + key + " not found.");
                continue;
            }

            String[] centerCoords = center.split(",");

            if (centerCoords.length <= 1) {
                plugin.sendConsole("&cCenter coordinates is less than or equal to 2! (Must be 3)");
                continue;
            }

            String[] minCoords = min.split(",");

            if (minCoords.length <= 1) {
                plugin.sendConsole("&cMinimum point coordinates is less than or equal to 2! (Must be 3)");
                continue;
            }

            String[] maxCoords = max.split(",");

            if (maxCoords.length <= 1) {
                plugin.sendConsole("&cMaximum point coordinates is less than or equal to 2! (Must be 3)");
                continue;
            }

            int xMin;
            int yMin;
            int zMin;

            try {
                xMin = Integer.parseInt(minCoords[0]);
                yMin = Integer.parseInt(minCoords[1]);
                zMin = Integer.parseInt(minCoords[2]);
            } catch (NumberFormatException e) {
                plugin.sendConsole("&cSomething went wrong when parsing integer from config.yml. " +
                        "Extra data: id='" + key + "', name='" + name + "', type='min'");
                continue;
            }

            int xMax;
            int yMax;
            int zMax;

            try {
                xMax = Integer.parseInt(maxCoords[0]);
                yMax = Integer.parseInt(maxCoords[1]);
                zMax = Integer.parseInt(maxCoords[2]);
            } catch (NumberFormatException e) {
                plugin.sendConsole("&cSomething went wrong when parsing integer from config.yml " +
                        "Extra data: id='" + key + "', name='" + name + "', type='max'");
                continue;
            }

            World worldInstance = Bukkit.getWorld(world);

            if (worldInstance == null) {
                plugin.sendConsole("&cInstance of world name '" + world + "' not found.");
                continue;
            }

            int xCenter;
            int yCenter;
            int zCenter;
            try {
                xCenter = Integer.parseInt(centerCoords[0]);
                yCenter = Integer.parseInt(centerCoords[1]);
                zCenter = Integer.parseInt(centerCoords[2]);
            } catch (NumberFormatException e) {
                plugin.sendConsole("&cSomethingw ent wrong when nparsing integer from config.yml " +
                        "Extra data: id='" + key + "', name='" + name + "', type='center'");
                continue;
            }

            Location minLocationInstance = new Location(worldInstance, xMin, yMin, zMin);
            Location maxLocationInstance = new Location(worldInstance, xMax, yMax, zMax);
            Location centerLocationInstance = new Location(worldInstance, xCenter, yCenter, zCenter);

            createBorder(key, name, world, centerLocationInstance, minLocationInstance, maxLocationInstance);
        }
    }

    public Border getBorderByID(String id) {
        return BORDERS.stream().filter(border -> ChatColor.stripColor(border.getID().toLowerCase()).
                equals(ChatColor.stripColor(id.toLowerCase()))).findFirst().orElse(null);
    }

    public Border getBorderByName(String name) {
        return BORDERS.stream().filter(border -> ChatColor.stripColor(border.getName().toLowerCase()).
                equals(ChatColor.stripColor(name.toLowerCase()))).findFirst().orElse(null);
    }

    public void createBorder(String id, String name, String world, Location center, Location min, Location max) {
        Border border = new Border(id, name, world, center, min, max);
        BORDERS.add(border);

        String path = "borders." + id + ".";

        plugin.getConfig().set(path + "name", name);
        plugin.getConfig().set(path + "world", world);
        plugin.getConfig().set(path + "center", "" + center.getBlockX() + "," + center.getBlockY() + "," + center.getBlockZ());
        plugin.getConfig().set(path + "min", "" + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ());
        plugin.getConfig().set(path + "max", "" + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ());
        plugin.saveConfig();

        plugin.sendConsole("&aCreated " + id);
    }

    public void removeBorder(String id) {
        Border border = getBorderByID(id);

        if (border == null) {
            plugin.sendConsole("Tried to remove " + id + " but it was not found!");
            return;
        }

        if (getInside().containsValue(border)) {
            for (Player player : getInside().keySet()) {
                if (getInside().get(player).getID().equals(border.getID()))
                    getInside().remove(player);
            }
        }

        BORDERS.remove(border);

        plugin.getConfig().set("borders." + id, null);
        plugin.saveConfig();

        plugin.sendConsole("&aRemoved and deleted " + id);
    }

    public void checkBorder(Player player) {
        for (Border currentBorders : plugin.getBorderHandler().getBorders()) {
            if (currentBorders == null)
                continue;

            if (currentBorders.isInArea(player.getLocation())) {
                if (INSIDE.containsKey(player)) { // if player is in the INSIDE map
                    if (!INSIDE.get(player).getID().equals(currentBorders.getID())) {
                        // if returned border  from the map is not equal to their current area border id
                        INSIDE.put(player, currentBorders);
                    }
                } else
                    INSIDE.put(player, currentBorders);

                break;
            }
        }
    }

    public List<Border> getBorders() {
        return BORDERS;
    }

    public IdentityHashMap<Player, Border> getInside() {
        return INSIDE;
    }
}