package net.omni.worldeditborder;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.omni.worldeditborder.commands.WorldEditBorderCommand;
import net.omni.worldeditborder.config.MessageConfig;
import net.omni.worldeditborder.listeners.BorderListener;
import net.omni.worldeditborder.util.BorderHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldEditBorder extends JavaPlugin {

    private WorldEditPlugin worldEdit;
    private BorderHandler borderHandler;
    private MessageConfig messageConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.messageConfig = new MessageConfig(this);

        WorldEditHook hook = new WorldEditHook(this);

        hook.hook();

        if (hook.isHooked())
            sendConsole("&aSuccessfully hooked &1WorldEdit");
        else {
            sendConsole("&cWARNING! WorldEdit was not found, disabling...");

            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.worldEdit = hook.getWorldEdit();

        this.borderHandler = new BorderHandler(this);
        this.borderHandler.loadBorders();

        registerListeners();
        registerCommands();

        sendConsole("&aSuccessfully enabled WorldEditBorder v-" + this.getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        // TODO
        sendConsole("&aSuccessfully disabled WorldEditBorder");
    }

    public void sendConsole(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(translate(getMessageConfig().getPrefix() + " " + message));
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(translate(getMessageConfig().getPrefix() + " " + message));
    }

    public void registerCommands() {
        new WorldEditBorderCommand(this).register();
    }

    public void registerListeners() {
        new BorderListener(this).register();
    }

    public String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public WorldEditPlugin getWorldEdit() {
        return this.worldEdit;
    }

    public BorderHandler getBorderHandler() {
        return this.borderHandler;
    }

    public MessageConfig getMessageConfig() {
        return this.messageConfig;
    }
}