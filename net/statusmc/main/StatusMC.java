package net.statusmc.main;

import java.lang.System.Logger.Level;
import net.statusmc.commands.ReloadCommand;
import net.statusmc.events.SocketOnGetMSPTEvent;
import net.statusmc.events.SocketOnGetTPSEvent;
import net.statusmc.utils.ServerConfig;
import net.statusmc.utils.SocketServer;
import net.statusmc.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class StatusMC extends JavaPlugin {
   FileConfiguration config = this.getConfig();
   static StatusMC plugin;
   private static SocketOnGetMSPTEvent msptCalculator;

   public void onEnable() {
      Utils.log(Level.INFO, "§aStatusMC TPS Monitor has been turned on!");
      this.getCommand("statusmc").setExecutor(new ReloadCommand(this));
      this.config.addDefault("port", ServerConfig.port);
      this.config.addDefault("secret_key", ServerConfig.secretKey);
      this.config.addDefault("prefix", ServerConfig.prefix);
      this.config.addDefault("wrongSecretKey", ServerConfig.wrongSecretKey);
      this.config.addDefault("unexpectederror", ServerConfig.unexpectederror);
      this.config.addDefault("successfulLogin", ServerConfig.successfulLogin);
      this.config.addDefault("consoleInfo", ServerConfig.consoleInfo);
      this.config.addDefault("log_performance_metrics", ServerConfig.log_performance_metrics);
      this.config.addDefault("successfullyReloaded", ServerConfig.successfullyReloaded);
      this.config.addDefault("nopermissions", ServerConfig.nopermissions);
      this.config.addDefault("incorrectUsage", ServerConfig.incorrectUsage);
      this.config.addDefault("unableMinMSPT", ServerConfig.unableMinMSPT);
      this.config.addDefault("unableAvgMSPT", ServerConfig.unableAvgMSPT);
      this.config.addDefault("unableMaxMSPT", ServerConfig.unableMaxMSPT);
      this.config.options().copyDefaults(true);
      this.saveConfig();
      this.configLoad();
      Utils.log(Level.INFO, "Starting...");
      SocketServer socket = new SocketServer();
      socket.start();
      Utils.log(Level.INFO, "Started!");
      Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SocketOnGetTPSEvent(), 100L, 1L);
      msptCalculator = new SocketOnGetMSPTEvent();
      Bukkit.getPluginManager().registerEvents(msptCalculator, this);
   }

   public void onDisable() {
      try {
         if (SocketServer.listenSock != null && !SocketServer.listenSock.isClosed()) {
            SocketServer.listenSock.close();
         }

         if (SocketServer.in != null) {
            SocketServer.in.close();
         }

         if (SocketServer.out != null) {
            SocketServer.out.close();
         }

         if (SocketServer.sock != null && !SocketServer.sock.isClosed()) {
            SocketServer.sock.close();
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      Utils.log(Level.INFO, "§cStatusMC TPS Monitor has been turned off!");
   }

   public void configLoad() {
      this.reloadConfig();
      FileConfiguration config = this.getConfig();
      ServerConfig.prefix = String.valueOf(String.valueOf(config.get("prefix")).replaceAll("&", "§")) + " ";
      ServerConfig.port = config.getInt("port");
      ServerConfig.secretKey = config.getString("secret_key");
      ServerConfig.wrongSecretKey = config.getString("wrongSecretKey").replaceAll("&", "§");
      ServerConfig.unexpectederror = config.getString("unexpectederror").replaceAll("&", "§");
      ServerConfig.successfulLogin = config.getString("successfulLogin").replaceAll("&", "§");
      ServerConfig.consoleInfo = config.getBoolean("consoleInfo");
      ServerConfig.log_performance_metrics = config.getBoolean("log_performance_metrics");
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
