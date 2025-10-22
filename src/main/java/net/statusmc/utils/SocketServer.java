package net.statusmc.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

import net.statusmc.events.SocketOnGetMSPTEvent;
import net.statusmc.events.SocketOnGetTPSEvent;
import net.statusmc.main.StatusMC;



public class SocketServer extends Thread {

	StatusMC plugin;

	//Tegime Chati publicuks ja sidusime "Parent" classiga.
	public SocketServer(StatusMC plugin) {
		this.plugin = plugin;
	}
	
	//SocketOnGetMSPTEvent msptCalculator = new SocketOnGetMSPTEvent();
	//private SocketOnGetMSPTEvent msptCalculator; // Instance of the MSPTCalculator class
	

    private boolean 					connect_status;
    public static int					port = ServerConfig.port;
    public static ServerSocket			listenSock = null;
    public static DataInputStream 		in = null;
    public static DataOutputStream 		out = null;
    public static Socket 				sock = null;

    public SocketServer(){
        this.connect_status = false;
    }

    @Override
    public void run(){
        try{
        	//msptCalculator = new SocketOnGetMSPTEvent(); // Initialize the MSPTCalculator instance
            listenSock = new ServerSocket(port);

            while(true){
                sock = listenSock.accept();
                in = new DataInputStream(sock.getInputStream());
                out = new DataOutputStream(sock.getOutputStream());
                this.connect_status = true;
                InetAddress addr = sock.getInetAddress();
                try{

                		if(in.readByte() == 1){
                            int random_code = new SecureRandom().nextInt();
                            
                            //See on lisatud siia juhuks, kui random_code peaks olema negatiivne. See funktsioon võtab random_codeist
                            //absoluutväärtuse, mis on alati positiivne. See on vajalik sellepärast kuna veebipoolel tekib int-i lugemisega
                            //probleeme kui arv on negatiivne
                            int random_code_processed = Math.abs(random_code);
                            if(ServerConfig.developer_mode.equals(true)) {
                            	Utils.log(Level.INFO, "Random code: "+random_code_processed);
                            }

                            out.writeInt(random_code_processed);
                            boolean success = Utils.readString(in, false).equals(Utils.hash(random_code_processed + ServerConfig.secretKey));
                            if(success){ //Kui secret key-ga on korras, 
                            	//Kõik on korras
	                			out.writeInt(1);
	                			out.flush();
	                			if(ServerConfig.disable_successful_connection_message.equals(false)) { //Annab võimaluse kasutajal disablida successfully connected message
	                				//hoides samal ajal ülejäänud logid sees
	                				Utils.log(Level.INFO, Utils.format(ServerConfig.successfulLogin));
	                			}
	                			
                            }else{
                                out.writeInt(0);
                                out.flush();
                                Utils.log(Level.ERROR, Utils.format(ServerConfig.wrongSecretKey));
                                this.connect_status = false;
                            }
                        }else{
                        	out.writeInt(0);
                        	out.flush();
                        	Utils.log(Level.ERROR, Utils.format(ServerConfig.unexpectederror));
                            this.connect_status = false;
                        }

                        while(this.connect_status){
                            final byte packetNumber = in.readByte();

                            if(packetNumber == 2) { //GET TPS FUNCTION
                            	double tps = Math.round(SocketOnGetTPSEvent.getTPS() * 100.0D) / 100.0D;
                                out.writeDouble(tps);
                                if(ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_low_tps.equals(true) && tps <= ServerConfig.low_tps_threshold) {
                                	Utils.log(Level.INFO, "The current TPS is " + tps + ".");
                                } else if(ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_low_tps.equals(false)) {
                                	Utils.log(Level.INFO, "The current TPS is " + tps + ".");
                                }
                               
                            } else if(packetNumber == 4) { //Saab min MSPT
                            	double minMSPTValue = SocketOnGetMSPTEvent.getMinMSPT();
                            	out.writeDouble(minMSPTValue);
                            	if(ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_min_mspt.equals(true) && minMSPTValue >= ServerConfig.high_min_mspt_threshold) {
                            		Utils.log(Level.INFO, "Min MSPT for the last 1 minute: "+minMSPTValue);
                            	} else if(ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_min_mspt.equals(false)) {
                            		Utils.log(Level.INFO, "Min MSPT for the last 1 minute: "+minMSPTValue);
                            	}
                            	
                            } else if(packetNumber == 5) { //Saab avg MSPT
                            	double avgMSPTValue = SocketOnGetMSPTEvent.getAvgMSPT();
                            	out.writeDouble(avgMSPTValue);
                            	if(ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_avg_mspt.equals(true) && avgMSPTValue >= ServerConfig.high_avg_mspt_threshold) {
                            		Utils.log(Level.INFO, "Average MSPT for the last 1 minute: "+avgMSPTValue);
                            	} else if(ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_avg_mspt.equals(false)) {
                            		Utils.log(Level.INFO, "Average MSPT for the last 1 minute: "+avgMSPTValue);
                            	}
                            	
                            } else if(packetNumber == 6) { //Saab max MSPT
                            	double maxMSPTValue = SocketOnGetMSPTEvent.getMaxMSPT();
                            	out.writeDouble(maxMSPTValue);
                            	if(ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_max_mspt.equals(true) && maxMSPTValue >= ServerConfig.high_max_mspt_threshold) {
                            		Utils.log(Level.INFO, "Max MSPT for the last 1 minute: "+maxMSPTValue);
                            	} else if(ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_max_mspt.equals(false)) {
                            		Utils.log(Level.INFO, "Max MSPT for the last 1 minute: "+maxMSPTValue);
                            	}
                            	
                            } else if(packetNumber == 3){ // CLOSE_CHANNEL
                            	if(ServerConfig.developer_mode.equals(true)) {
                            		Utils.log(Level.INFO, "Socket packet 3 close");
                            	}
                            	out.flush();
                            	closeConnectionGracefully();
                                
                            } else {
                            	Utils.log(Level.INFO, "Packet not found! Packet: " + packetNumber + " Please contact StatusMC Support staff.");
                            	
                            }
                        }

                }catch(IOException ex){
                	out.writeInt(0);
                	out.flush();
                	closeConnectionGracefully();
                	Utils.log(Level.INFO, "IO exception 1: "+ex.getMessage());
                	ex.printStackTrace();
                }
                
            }
        }catch(IOException ex){
        	if(ServerConfig.developer_mode.equals(true)) {
        		Utils.log(Level.INFO, "IO exception 2: "+ex.getMessage());
        		ex.printStackTrace();
        	}
        }
    }
    
    private void closeConnectionGracefully() {
    	try {
    		in.close();
    		out.close();
    		sock.close();
    		this.connect_status = false;
    	} catch (IOException e) {
    		Utils.log(Level.WARNING, "Error while closing socket connection: "+e.getMessage());
    	}
    }

}
