package net.statusmc.main;

import java.lang.System.Logger.Level;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.statusmc.commands.ReloadCommand;
import net.statusmc.events.ServerTickEndEvent;
import net.statusmc.events.ServerTickStartEvent;
import net.statusmc.events.SocketOnGetMSPTEvent;
import net.statusmc.events.SocketOnGetTPSEvent;
import net.statusmc.utils.APIRequest;
import net.statusmc.utils.ServerConfig;
import net.statusmc.utils.SocketServer;
import net.statusmc.utils.Utils;


public class StatusMC extends JavaPlugin {
    // 50 ms per tick -> 50_000_000 ns
    private static final long TICK_LENGTH_NS = 50_000_000L;

    private int tickNumber;
    private long lastTickStartNs;
    private BukkitTask tickTask;
	
	FileConfiguration config;
	@SuppressWarnings("deprecation")
	PluginDescriptionFile pdf = this.getDescription();
	static StatusMC plugin;
	
	private static SocketOnGetMSPTEvent msptCalculator; // Instance of the MSPTCalculator class

    public StatusMC(){
        this.config = this.getConfig();
    }

	@Override
	public void onEnable() {
		getCommand("statusmc").setExecutor(new ReloadCommand(this));
		this.config.addDefault("port", ServerConfig.port);
        this.config.addDefault("secret_key", ServerConfig.secretKey);
        this.config.addDefault("prefix", ServerConfig.prefix);
        this.config.addDefault("wrongSecretKey", ServerConfig.wrongSecretKey);
        this.config.addDefault("unexpectederror", ServerConfig.unexpectederror); //Wrong data error
        this.config.addDefault("successfulLogin", ServerConfig.successfulLogin);
        this.config.addDefault("consoleInfo", ServerConfig.consoleInfo);
        this.config.addDefault("developer_mode", ServerConfig.developer_mode);
        this.config.addDefault("disable_successful_connection_message", ServerConfig.disable_successful_connection_message);
        this.config.addDefault("log_performance_metrics", ServerConfig.log_performance_metrics);
        this.config.addDefault("log_only_low_tps", ServerConfig.log_only_low_tps);
        this.config.addDefault("low_tps_threshold", ServerConfig.low_tps_threshold);
        this.config.addDefault("log_only_high_min_mspt", ServerConfig.log_only_high_min_mspt);
        this.config.addDefault("high_min_mspt_threshold", ServerConfig.high_min_mspt_threshold);
        this.config.addDefault("log_only_high_avg_mspt", ServerConfig.log_only_high_avg_mspt);
        this.config.addDefault("high_avg_mspt_threshold", ServerConfig.high_avg_mspt_threshold);
        this.config.addDefault("log_only_high_max_mspt", ServerConfig.log_only_high_max_mspt);
        this.config.addDefault("high_max_mspt_threshold", ServerConfig.high_max_mspt_threshold);
        this.config.addDefault("successfullyReloaded", ServerConfig.successfullyReloaded);
        this.config.addDefault("nopermissions", ServerConfig.nopermissions);
        this.config.addDefault("incorrectUsage", ServerConfig.incorrectUsage);
        this.config.addDefault("unableMinMSPT", ServerConfig.unableMinMSPT);
        this.config.addDefault("unableAvgMSPT", ServerConfig.unableAvgMSPT);
        this.config.addDefault("unableMaxMSPT", ServerConfig.unableMaxMSPT);
        this.config.options().copyDefaults(true);
        this.saveConfig();
        this.configLoad();
        Utils.log(Level.INFO, "§9StatusMC Monitoring plugin §7has been §aturned on§7!");

		Utils.log(Level.INFO, "Starting...");

        SocketServer socket = new SocketServer();
        socket.start();

        Utils.log(Level.INFO, "Started!");
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SocketOnGetTPSEvent(), 100L, 1L);
        tickNumber = 0;
        lastTickStartNs = System.nanoTime();

        //1. Schedule the start-of-tick callback every server tick
        tickTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            tickNumber++;
            lastTickStartNs = System.nanoTime();
            Bukkit.getPluginManager().callEvent(new ServerTickStartEvent(tickNumber));
        }, 0L, 1L);

        //2. Inject a proxy into NMS end-of-tick list via reflection
        try {
            Class<?> mcServerClass = Class.forName("net.minecraft.server.MinecraftServer");
            Field endTickListField = null;
            for (Field field : mcServerClass.getDeclaredFields()) {
                if (List.class.isAssignableFrom(field.getType())) {
                    endTickListField = field;
                    break;
                }
            }
            if (endTickListField != null) {
                endTickListField.setAccessible(true);
                Object nmsServer = getServer().getClass().getMethod("getServer").invoke(getServer());
                @SuppressWarnings("unchecked")
                List<Object> endList = (List<Object>) endTickListField.get(nmsServer);

                Object proxy = Proxy.newProxyInstance(
                    Runnable.class.getClassLoader(),
                    new Class[]{Runnable.class},
                    (proxyObj, method, args) -> {
                        long endNs = System.nanoTime();
                        double durationMs = (endNs - lastTickStartNs) / 1_000_000.0;
                        long remainNs = TICK_LENGTH_NS - (endNs - lastTickStartNs);
                        Bukkit.getPluginManager().callEvent(
                            new ServerTickEndEvent(tickNumber, durationMs, remainNs)
                        );
                        return null;
                    }
                );
                endList.add(proxy);
            } else {
            	Utils.log(Level.WARNING, "Could not find end-of-tick list field on MinecraftServer");
            }
        } catch (Exception e) {
        	Utils.log(Level.WARNING, "Failed to register end-of-tick proxy: " + e);
        }
        msptCalculator = new SocketOnGetMSPTEvent(); // Initialize the MSPTCalculator instance
        Bukkit.getPluginManager().registerEvents(msptCalculator, this);
        
        if(ServerConfig.secretKey.equals("CHANGETHISSECRETKEY")) {
        	Utils.log(Level.INFO, "§e-------------------");
        	Utils.log(Level.INFO, "§7To start using StatusMC's TPS and MSPT monitoring services, register for an");
        	Utils.log(Level.INFO, "account at §9https://statusmc.net/");
        	Utils.log(Level.INFO, "§7Detailed instructions on how to setup TPS/MSPT monitoring can be found here:");
        	Utils.log(Level.INFO, "§9https://support.statusmc.net/en/blog/setting-up-minecraft-tps-mspt-monitoring");
        	Utils.log(Level.INFO, "§e-------------------");
        }
        
        String pluginVersion = pdf.getVersion(); //Saan plugina versiooni plugin.yml failist
        if(APIRequest.checkVersion(pluginVersion)) {
        	//Returnides true ei tee midagi
        } else { //Ei returninud true ehk uus versioon on saadaval
        	//Annan teada sellest
        	Utils.log(Level.INFO, "§e-------------------");
        	Utils.log(Level.INFO, "§7A §anew §7version of the §9StatusMC §7plugin is §aavailable!");
        	Utils.log(Level.INFO, "Download it here: §9https://statusmc.net/downloads");
        	Utils.log(Level.INFO, "§e-------------------");
        }
        
	}

	@Override
	public void onDisable() {
		try{
        	if(SocketServer.listenSock != null && !SocketServer.listenSock.isClosed()){
                SocketServer.listenSock.close();
        	}
        	if(SocketServer.in != null){
        		SocketServer.in.close();
        	}
        	if(SocketServer.out != null){
        		SocketServer.out.close();
        	}
        	if(SocketServer.sock != null && !SocketServer.sock.isClosed()){
                SocketServer.sock.close();
        	}
        }catch(Exception ex){
        	if(ServerConfig.developer_mode.equals(true)) {
        		ex.printStackTrace();
        	}
        }
		
		 if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
		Utils.log(Level.INFO, "§9StatusMC Monitoring plugin §7has been §4turned off§7!");
	}

	public void configLoad() {
        this.reloadConfig();
        final FileConfiguration config = this.getConfig();

        ServerConfig.prefix = String.valueOf(String.valueOf(config.get("prefix")).replaceAll("&", "§")) + " ";
        ServerConfig.port = config.getInt("port");
        ServerConfig.secretKey = config.getString("secret_key");
        ServerConfig.wrongSecretKey = config.getString("wrongSecretKey").replaceAll("&", "§");
        ServerConfig.unexpectederror = config.getString("unexpectederror").replaceAll("&", "§");
        ServerConfig.successfulLogin = config.getString("successfulLogin").replaceAll("&", "§");
        ServerConfig.consoleInfo = config.getBoolean("consoleInfo");
        ServerConfig.developer_mode = config.getBoolean("developer_mode");
        ServerConfig.disable_successful_connection_message = config.getBoolean("disable_successful_connection_message");
        ServerConfig.log_performance_metrics = config.getBoolean("log_performance_metrics");
        ServerConfig.log_only_low_tps = config.getBoolean("log_only_low_tps");
        ServerConfig.low_tps_threshold = config.getDouble("low_tps_threshold");
        ServerConfig.log_only_high_min_mspt = config.getBoolean("log_only_high_min_mspt");
        ServerConfig.high_min_mspt_threshold = config.getDouble("high_min_mspt_threshold");
        ServerConfig.log_only_high_avg_mspt = config.getBoolean("log_only_high_avg_mspt");
        ServerConfig.high_avg_mspt_threshold = config.getDouble("high_avg_mspt_threshold");
        ServerConfig.log_only_high_max_mspt = config.getBoolean("log_only_high_max_mspt");
        ServerConfig.high_max_mspt_threshold = config.getDouble("high_max_mspt_threshold");
        ServerConfig.successfullyReloaded = config.getString("successfullyReloaded").replaceAll("&", "§");
        ServerConfig.nopermissions = config.getString("nopermissions").replaceAll("&", "§");
        ServerConfig.incorrectUsage = config.getString("incorrectUsage").replaceAll("&", "§");
        ServerConfig.unableMinMSPT = config.getString("unableMinMSPT").replaceAll("&", "§");
        ServerConfig.unableAvgMSPT = config.getString("unableAvgMSPT").replaceAll("&", "§");
        ServerConfig.unableMaxMSPT = config.getString("unableMaxMSPT").replaceAll("&", "§");
    }
    
    public SocketOnGetMSPTEvent getMsptCalculator() {
        return msptCalculator;
    }

}
