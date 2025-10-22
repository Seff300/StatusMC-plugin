package net.statusmc.commands;

import java.lang.System.Logger.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.statusmc.main.StatusMC;
import net.statusmc.utils.ServerConfig;
import net.statusmc.utils.SocketServer;
import net.statusmc.utils.Utils;

public class ReloadCommand implements CommandExecutor {

	StatusMC plugin;

	private FileConfiguration config;

	//Tegime Chati publicuks ja sidusime "Parent" classiga.
	public ReloadCommand(StatusMC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("statusmc")) {
			if(!(sender instanceof Player)) {
				if(args.length == 0) {
					plugin.configLoad();
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
			        	ex.printStackTrace();
			        }
					Utils.log(Level.INFO, "Starting...");

			        SocketServer socket = new SocketServer();
			        socket.start();

			        Utils.log(Level.INFO, "Started!");
					sender.sendMessage(ServerConfig.successfullyReloaded);
				} else {
					sender.sendMessage(ServerConfig.incorrectUsage);
				}
				return true;
			}
			if(sender instanceof Player) {
				Player player = (Player) sender;
				Player p = (Player) sender;
				if(p.hasPermission("statusmc.reload")) { //Kui mängija on reload permission, siis reloadib plugina
					if(args.length == 0) { //Kui käsklus on ainult /statusmc, siis reloadib configi
						plugin.configLoad();
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
				        	ex.printStackTrace();
				        }
						Utils.log(Level.INFO, "Starting...");

				        SocketServer socket = new SocketServer();
				        socket.start();

				        Utils.log(Level.INFO, "Started!");
						player.sendMessage(ServerConfig.successfullyReloaded);
					} else {
						player.sendMessage(ServerConfig.incorrectUsage);
					}
				} else {
					player.sendMessage(ServerConfig.nopermissions);
				}
			}
		}

		return true;
	}

}
