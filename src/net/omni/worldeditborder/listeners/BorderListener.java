package net.omni.worldeditborder.listeners;

import net.omni.worldeditborder.WorldEditBorder;
import net.omni.worldeditborder.objects.Border;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class BorderListener implements Listener {
    private final WorldEditBorder plugin;

    public BorderListener(WorldEditBorder plugin) {
        this.plugin = plugin;

        startChecks();
    }

    /*
    runTaskTimer(() -> {
  players.forEach((player, border) -> {
    final Vector location = player.getLocation().toVector();

    if (!location.isInAABB(border.warningMin, border.warningMax)) {
      if (location.isInAABB(border.min, border.max)) border.points.stream()
        .filter(it -> it.distance(location) < DISTANCE)
        .forEach(point -> displayParticles(player, point));
      else //player is outside of the border.
    }
  });
}

     */

    private void startChecks() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
                plugin.getBorderHandler().getInside().forEach((player, border) -> {
                    if (player == null)
                        return;

                    if (border == null)
                        return;

                    border.getFrontBackWall().forEach(point -> player.spawnParticle(Particle.ENCHANTMENT_TABLE, point, 1));
                    border.getLeftRightWall().forEach(point -> player.spawnParticle(Particle.ENCHANTMENT_TABLE, point, 1));
                }), 20, 80);

        plugin.sendConsole("&aStarted the particles task.");

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Border border : plugin.getBorderHandler().getBorders()) {
                if (border == null)
                    continue;

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (player.getWorld().getName().equalsIgnoreCase(border.getWorld())) {
                        final Vector playerLoc = player.getLocation().toVector();

                        if (!playerLoc.isInAABB(border.getMinPoint().toVector(), border.getMaxPoint().toVector())) {
                            if (!player.hasPermission(border.getPermission())) {
                                plugin.sendMessage(player, plugin.getMessageConfig().getCannotEnter());
                                Location loc1 = player.getLocation();//Get the location from the source player
                                Location loc2 = border.getCenter();//Get the location from the target player

                                double deltaX = loc2.getX() - loc1.getX();//Get X Delta
                                double deltaZ = loc2.getZ() - loc1.getZ();//Get Z delta

                                Vector vec = new Vector(deltaX, 0, deltaZ);//Create new vector
                                vec.normalize();

                                player.setVelocity(vec.multiply(5 / (Math.sqrt(Math.pow(deltaX, 2.0)
                                        + Math.pow(deltaZ, 2.0)))));
                            }
                        }
                    }
                }
            }
        }, 0, 5);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
                Bukkit.getOnlinePlayers().forEach(player -> plugin.getBorderHandler().checkBorder(player)), 20, 20);

        plugin.sendConsole("&aStarted the border check tasks");
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getBorderHandler().checkBorder(event.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        plugin.getBorderHandler().checkBorder(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getBorderHandler().getInside().remove(event.getPlayer());
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
