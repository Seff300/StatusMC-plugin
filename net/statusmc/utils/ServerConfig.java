package net.statusmc.utils;

public class ServerConfig {
   public static int port = 9876;
   public static String secretKey = "CHANGETHISSECRETKEY";
   public static String prefix = "&9StatusMC &7> ";
   public static String wrongSecretKey = "The secret key specified is incorrect!";
   public static String unexpectederror = "An unexpected error occured when trying to connect to the server.";
   public static String successfulLogin = "&aSuccessfully connected &7to the server!";
   public static Boolean consoleInfo = true;
   public static Boolean developer_mode = false;
   public static Boolean disable_successful_connection_message = false;
   public static Boolean log_performance_metrics = true;
   public static Boolean log_only_low_tps = false;
   public static Double low_tps_threshold = 18.0D;
   public static Boolean log_only_high_min_mspt = false;
   public static Double high_min_mspt_threshold = 75.0D;
   public static Boolean log_only_high_avg_mspt = false;
   public static Double high_avg_mspt_threshold = 70.0D;
   public static Boolean log_only_high_max_mspt = false;
   public static Double high_max_mspt_threshold = 170.0D;
   public static String successfullyReloaded = "&aSuccessfully reloaded &7the plugin!";
   public static String nopermissions = "&7You &cdo not &7have enough permissions!";
   public static String incorrectUsage = "&cIncorrect usage! &7Correct command syntax: &e/statusmc";
   public static String unableMinMSPT = "&cInsufficient &7data to calculate &eminimum MSPT&7.";
   public static String unableAvgMSPT = "&cInsufficient &7data to calculate &eaverage MSPT&7.";
   public static String unableMaxMSPT = "&cInsufficient &7data to calculate &emaximum MSPT&7.";
   public static String status;
}
