package net.statusmc.utils;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.System.Logger.Level;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;
import net.statusmc.main.StatusMC;

public class Utils {

	public static StatusMC plugin;

    void test(String string) {

        plugin.getConfig();

    }



	public static String format(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}


	public static void log(Level level, String msg) {
		if(ServerConfig.consoleInfo.equals(true)){
			Bukkit.getServer().getConsoleSender().sendMessage(ServerConfig.prefix + msg);
		}

	}


	public static String hash(String input){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(input.getBytes());

            String result = new BigInteger(1, md.digest()).toString(16);
            if(result.length() % 2 != 0){
                result = "0" + result;
            }

            return result;
        }catch(Exception ex){
            return "";
        }
    }


	public static String readString(DataInputStream in, boolean base64) throws IOException {
	    // Read the length of the hexadecimal string (sent as an integer)
	    int stringSize = in.readInt();

	    // Read the raw bytes representing the hexadecimal string
	    byte[] rawBytes = new byte[stringSize];
	    in.readFully(rawBytes);

	    // Convert the raw bytes back to a string (as UTF-8 for compatibility)
	    if(ServerConfig.developer_mode.equals(true)) {
	    	Utils.log(Level.INFO, new String(rawBytes, StandardCharsets.UTF_8));
	    }
	    return new String(rawBytes, StandardCharsets.UTF_8);

    }

	public static String DecodeBASE64(String text) throws UnsupportedEncodingException {
		byte[] bytes = Base64.getDecoder().decode(text);
		return new String(bytes, "UTF-8");
	}
}
