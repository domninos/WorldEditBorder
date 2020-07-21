package net.omni.worldeditborder.objects;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class Border {

    private final String ID;
    private final String name;
    private final String world;
    private final Location min;
    private final Location max;
    private final String permission;
    private final Location center;
    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;
    private final List<Location> leftRightWall;
    private final List<Location> frontBackWall;

    public Border(String ID, String name, String world, Location center, Location min, Location max) {
        this.ID = ID;
        this.name = name;
        this.world = world;
        this.min = min;
        this.minX = Math.min(min.getX(), max.getX());
        this.minY = Math.min(min.getY(), max.getY());
        this.minZ = Math.min(min.getZ(), max.getZ());
        this.max = max;
        this.maxX = Math.max(min.getX(), max.getX());
        this.maxY = Math.max(min.getY(), max.getY());
        this.maxZ = Math.max(min.getZ(), max.getZ());
        this.center = center;

        this.frontBackWall = getFrontAndBack(center.getWorld());
        this.leftRightWall = getLeftAndRight(center.getWorld());

        this.permission = "worldeditborder." + ID.toLowerCase() + ".pass";
    }

    private List<Location> getFrontAndBack(World world) {
        List<Location> result = new ArrayList<>();

        // 2 sides (front & back)
        for (double x = minX; x <= maxX; x += 0.2D) {
            for (double y = minY; y <= maxY; y += 0.2D) {
                result.add(new Location(world, x, y, minZ));
                result.add(new Location(world, x, y, maxZ));
            }
        }

        return result;
    }

    private List<Location> getLeftAndRight(World world) {
        List<Location> result = new ArrayList<>();

        // 2 sides (left & right)
        for (double z = minZ; z <= maxZ; z += 0.2D) {
            for (double y = minY; y <= maxY; y += 0.2D) {
                result.add(new Location(world, minX, y, z));
                result.add(new Location(world, maxX, y, z));
            }
        }

        return result;
    }

        /*
        // 2 areas = cover and below -> up and down
        for (double x = minX; x <= maxX; x += 0.2D) {
            for (double z = minZ; z <= maxZ; z += 0.2D) {
                result.add(new Location(world, x, minY, z));
                result.add(new Location(world, x, maxY, z));
            }
        }
         */

    public boolean isInArea(Location playerLoc) {
        return playerLoc.toVector().isInAABB(getMinPoint().toVector(), getMaxPoint().toVector());
    }

    public String getID() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }

    public String getWorld() {
        return this.world;
    }

    public Location getMinPoint() {
        return this.min;
    }

    public Location getMaxPoint() {
        return this.max;
    }

    public String getPermission() {
        return this.permission;
    }

    public Location getCenter() {
        return this.center;
    }

    public List<Location> getLeftRightWall() {
        return this.leftRightWall;
    }

    public List<Location> getFrontBackWall() {
        return this.frontBackWall;
    }
}
