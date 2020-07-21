package net.omni.worldeditborder;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class WorldEditHook {

	private final WorldEditBorder plugin;
	private boolean hooked;

	private WorldEditPlugin worldEdit;

	public WorldEditHook(WorldEditBorder plugin) {
		this.plugin = plugin;
		this.hooked = false;
		this.worldEdit = null;
	}

	public void hook() {
		plugin.sendConsole("Searching for WorldEdit...");

		this.worldEdit = WorldEditBorder.getPlugin(WorldEditPlugin.class);

		if (!Bukkit.getPluginManager().isPluginEnabled(worldEdit)) {
			plugin.sendConsole("&c&lWorldEdit was not found!");
			this.hooked = false;
			return;
		}

		this.hooked = true;
	}

	public WorldEditPlugin getWorldEdit() {
		return this.worldEdit;
	}

	public boolean isHooked() {
		return this.hooked;
	}
}