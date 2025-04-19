package net.statusmc.utils;

public class ServerConfig {
   public static int port = 9876;
   public static String secretKey = "CHANGETHISSECRETKEY";
   public static String prefix = "&9StatusMC &7> ";
   public static String wrongSecretKey = "The secret key specified is incorrect!";
   public static String unexpectederror = "An unexpected error occured when trying to connect to the server.";
   public static String successfulLogin = "&aSuccessfully connected &7to the server!";
   public static Boolean consoleInfo = true;
   public static Boolean log_performance_metrics = true;
   public static String successfullyReloaded = "&aSuccessfully reloaded &7the plugin!";
   public static String nopermissions = "&7You &cdo not &7have enough permissions!";
   public static String incorrectUsage = "&cIncorrect usage! &7Correct command syntax: &e/statusmc";
   public static String unableMinMSPT = "&cInsufficient &7data to calculate &eminimum MSPT&7.";
   public static String unableAvgMSPT = "&cInsufficient &7data to calculate &eaverage MSPT&7.";
   public static String unableMaxMSPT = "&cInsufficient &7data to calculate &emaximum MSPT&7.";
   public static String status;
}
