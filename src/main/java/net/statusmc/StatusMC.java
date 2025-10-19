package net.statusmc;

import org.bukkit.plugin.java.JavaPlugin;
import net.statusmc.managers.PluginManager;
import net.statusmc.listeners.PlayerListener;

public class StatusMC extends JavaPlugin {
    
    @Override
    public void onEnable() {
        
        // Initialize managers
        PluginManager.getInstance().initialize();
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        getLogger().info("StatusMC has been enabled! Built to a new directory.");
    }

    @Override
    public void onDisable() {
        getLogger().info("StatusMC has been disabled!");
    }
    
}