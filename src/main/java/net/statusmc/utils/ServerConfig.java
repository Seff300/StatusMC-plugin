package net.statusmc.utils;


public class ServerConfig {

    public static int    port;
    public static String secretKey;
    public static String prefix;
    public static String wrongSecretKey;
    public static String unexpectederror;
    public static String successfulLogin;
    public static Boolean consoleInfo;
    public static Boolean developer_mode;
    public static Boolean disable_successful_connection_message;
    public static Boolean log_performance_metrics;
    public static Boolean log_only_low_tps;
    public static Double low_tps_threshold;
    public static Boolean log_only_high_min_mspt;
    public static Double high_min_mspt_threshold;
    public static Boolean log_only_high_avg_mspt;
    public static Double high_avg_mspt_threshold;
    public static Boolean log_only_high_max_mspt;
    public static Double high_max_mspt_threshold;
    public static String successfullyReloaded;
    public static String nopermissions;
    public static String incorrectUsage;
    public static String unableMinMSPT;
    public static String unableAvgMSPT;
    public static String unableMaxMSPT;
    public static String status;

    static {
    	ServerConfig.port              = 9876;
    	ServerConfig.secretKey         = "CHANGETHISSECRETKEY";
    	ServerConfig.prefix            = "&9StatusMC &7> ";
    	ServerConfig.wrongSecretKey    = "The secret key specified is incorrect!";
        ServerConfig.unexpectederror   = "An unexpected error occured when trying to connect to the server.";
        ServerConfig.successfulLogin   = "&aSuccessfully connected &7to the server!";
        ServerConfig.consoleInfo       = true;
        ServerConfig.developer_mode       = false;
        ServerConfig.disable_successful_connection_message = false;
        ServerConfig.log_performance_metrics = true;
        ServerConfig.log_only_low_tps = false;
        ServerConfig.low_tps_threshold = 18.0;
        ServerConfig.log_only_high_min_mspt = false;
        ServerConfig.high_min_mspt_threshold = 75.0;
        ServerConfig.log_only_high_avg_mspt = false;
        ServerConfig.high_avg_mspt_threshold = 70.0;
        ServerConfig.log_only_high_max_mspt = false;
        ServerConfig.high_max_mspt_threshold = 170.0;
        ServerConfig.successfullyReloaded  = "&aSuccessfully reloaded &7the plugin!";
        ServerConfig.nopermissions     = "&7You &cdo not &7have enough permissions!";
        ServerConfig.incorrectUsage     = "&cIncorrect usage! &7Correct command syntax: &e/statusmc";
        ServerConfig.unableMinMSPT     = "&cInsufficient &7data to calculate &eminimum MSPT&7.";
        ServerConfig.unableAvgMSPT     = "&cInsufficient &7data to calculate &eaverage MSPT&7.";
        ServerConfig.unableMaxMSPT     = "&cInsufficient &7data to calculate &emaximum MSPT&7.";
    }

}
