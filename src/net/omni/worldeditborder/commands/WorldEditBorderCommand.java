package net.omni.worldeditborder.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.omni.worldeditborder.WorldEditBorder;
import net.omni.worldeditborder.objects.Border;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WorldEditBorderCommand implements CommandExecutor {

    private final WorldEditBorder plugin;

    public WorldEditBorderCommand(WorldEditBorder plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label,
                             @Nonnull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(getHelpCommands());
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                List<Border> borders = plugin.getBorderHandler().getBorders();

                if (borders == null || borders.isEmpty()) {
                    plugin.sendMessage(sender, "&cNo borders found,");
                    return true;
                }

                plugin.sendMessage(sender, "&aBorders:");
                plugin.sendMessage(sender, borders.stream().map(Border::getName).collect(Collectors.joining(", ")));
            } else {
                if (args[0].equalsIgnoreCase("create"))
                    plugin.sendMessage(sender, "&cUsage: /border create <id>");
                else if (args[0].equalsIgnoreCase("delete"))
                    plugin.sendMessage(sender, "&cUsage: /border delete <id>");
                else if (args[0].equalsIgnoreCase("check"))
                    plugin.sendMessage(sender, "&cUsage: /border check <id>");
                else
                    plugin.sendMessage(sender, "&cCommand not found.");
            }

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                if (!(sender instanceof Player)) {
                    plugin.sendMessage(sender, plugin.getMessageConfig().getPlayerOnly());
                    return true;
                }

                Player player = (Player) sender;

                if (plugin.getBorderHandler().getBorderByID(args[1]) != null) {
                    plugin.sendMessage(player, plugin.getMessageConfig().getAlreadyLoaded().replaceAll("%arg%", args[1]));
                    return true;
                }

                try {
                    LocalSession session = plugin.getWorldEdit().getSession(player);
                    Region region = session.getSelection(session.getSelectionWorld());

                    CuboidRegion cuboidRegion = CuboidRegion.makeCuboid(region);
                    Region walls = cuboidRegion.getWalls();
                    BlockVector3 minVec = walls.getMinimumPoint();
                    BlockVector3 maxVec = walls.getMaximumPoint();

                    int xMin = minVec.getBlockX();
                    int yMin = minVec.getBlockY();
                    int zMin = minVec.getBlockZ();

                    int xMax = maxVec.getBlockX();
                    int yMax = maxVec.getBlockY();
                    int zMax = maxVec.getBlockZ();

                    String id = args[1];
                    String name = id.replaceAll("_", " ");
                    Location center = new Location(player.getWorld(), region.getCenter().getX(),
                            region.getCenter().getY(), region.getCenter().getZ());
                    Location min = new Location(player.getWorld(), xMin, yMin, zMin);
                    Location max = new Location(player.getWorld(), xMax, yMax, zMax);


                    plugin.getBorderHandler().createBorder(id, name, player.getWorld().getName(), center, min, max);
                    plugin.sendMessage(player, plugin.getMessageConfig().getCreated().replaceAll("%border%", id));
                    return true;
                } catch (IncompleteRegionException e) {
                    plugin.sendMessage(player, "&cIncomplete region!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                Border border = plugin.getBorderHandler().getBorderByID(args[1]);

                if (border == null) {
                    plugin.sendMessage(sender, plugin.getMessageConfig().getBorderNotFound().replaceAll("%arg%", args[1]));
                    return true;
                }

                plugin.getBorderHandler().removeBorder(border.getID());
                plugin.sendMessage(sender, plugin.getMessageConfig().getDeleted().replaceAll("%border%", border.getID()));
                return true;
            } else if (args[0].equalsIgnoreCase("check")) {
                Border border = plugin.getBorderHandler().getBorderByID(args[1]);

                if (border == null) {
                    plugin.sendMessage(sender, plugin.getMessageConfig().getBorderNotFound().replaceAll("%arg%", args[1]));
                    return true;
                }

                for (Player online : Bukkit.getServer().getOnlinePlayers())
                    plugin.getBorderHandler().checkBorder(online);

                plugin.sendMessage(sender, plugin.getMessageConfig().getChecked().replaceAll("%border%", border.getID()));
            } else
                plugin.sendMessage(sender, plugin.getMessageConfig().getCommandNotFound());

            return true;
        } else {
            plugin.sendMessage(sender, plugin.getMessageConfig().getCommandNotFound());
            return true;
        }
    }

    public void register() {
        Objects.requireNonNull(plugin.getCommand("worldeditborder")).setExecutor(this);
    }

    private String getHelpCommands() {
        return plugin.translate("&7[&cWorldEditBorder&7]\n"
                + "&b/border list &8▻ Shows a list of all borders.\n"
                + "&b/border create <id> &8▻ Creates a border with the world edit selection.\n"
                + "&b/border delete <id> &8▻ Removes the specified border\n"
                + "&b/border check <id> &8▻ Re-checks the border for players.");
    }
}