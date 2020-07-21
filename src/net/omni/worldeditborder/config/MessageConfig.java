package net.omni.worldeditborder.config;

import net.omni.worldeditborder.WorldEditBorder;

public class MessageConfig {

    private final WorldEditBorder plugin;
    private final String prefix;
    private final String cannotEnter;
    private final String playerOnly;
    private final String commandNotFound;
    private final String borderNotFound;
    private final String alreadyLoaded;
    private final String created;
    private final String deleted;
    private final String checked;

    public MessageConfig(WorldEditBorder plugin) {
        this.plugin = plugin;

        this.prefix = plugin.translate(getString("prefix"));
        this.playerOnly = plugin.translate(getString("playerOnly"));
        this.cannotEnter = plugin.translate(getString("cannotEnter"));
        this.commandNotFound = plugin.translate(getString("commandNotFound"));
        this.borderNotFound = plugin.translate(getString("borderNotFound"));
        this.alreadyLoaded = plugin.translate(getString("alreadyLoaded"));
        this.created = plugin.translate(getString("created"));
        this.deleted = plugin.translate(getString("deleted"));
        this.checked = plugin.translate(getString("checked"));
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getCannotEnter() {
        return this.cannotEnter;
    }

    public String getPlayerOnly() {
        return this.playerOnly;
    }

    public String getCommandNotFound() {
        return this.commandNotFound;
    }

    public String getBorderNotFound() {
        return this.borderNotFound;
    }

    public String getAlreadyLoaded() {
        return this.alreadyLoaded;
    }

    public String getCreated() {
        return this.created;
    }

    public String getChecked() {
        return this.checked;
    }

    public String getDeleted() {
        return this.deleted;
    }

    private String getString(String path) {
        return plugin.getConfig().getString("messages." + path);
    }
}
