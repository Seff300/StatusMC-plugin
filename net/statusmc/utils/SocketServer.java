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
   private boolean connect_status;
   public static int port;
   public static ServerSocket listenSock;
   public static DataInputStream in;
   public static DataOutputStream out;
   public static Socket sock;

   static {
      port = ServerConfig.port;
      listenSock = null;
      in = null;
      out = null;
      sock = null;
   }

   public SocketServer(StatusMC plugin) {
      this.plugin = plugin;
   }

   public SocketServer() {
      this.connect_status = false;
   }

   public void run() {
      try {
         listenSock = new ServerSocket(port);

         label117:
         while(true) {
            sock = listenSock.accept();
            in = new DataInputStream(sock.getInputStream());
            out = new DataOutputStream(sock.getOutputStream());
            this.connect_status = true;
            InetAddress var1 = sock.getInetAddress();

            try {
               if (in.readByte() == 1) {
                  int random_code = (new SecureRandom()).nextInt();
                  int random_code_processed = Math.abs(random_code);
                  if (ServerConfig.developer_mode.equals(true)) {
                     Utils.log(Level.INFO, "Random code: " + random_code_processed);
                  }

                  out.writeInt(random_code_processed);
                  boolean success = Utils.readString(in, false).equals(Utils.hash(random_code_processed + ServerConfig.secretKey));
                  if (success) {
                     out.writeInt(1);
                     out.flush();
                     if (ServerConfig.disable_successful_connection_message.equals(false)) {
                        Utils.log(Level.INFO, Utils.format(ServerConfig.successfulLogin));
                     }
                  } else {
                     out.writeInt(0);
                     out.flush();
                     Utils.log(Level.ERROR, Utils.format(ServerConfig.wrongSecretKey));
                     this.connect_status = false;
                  }
               } else {
                  out.writeInt(0);
                  out.flush();
                  Utils.log(Level.ERROR, Utils.format(ServerConfig.unexpectederror));
                  this.connect_status = false;
               }

               while(true) {
                  while(true) {
                     while(true) {
                        if (!this.connect_status) {
                           continue label117;
                        }

                        byte packetNumber = in.readByte();
                        double maxMSPTValue;
                        if (packetNumber == 2) {
                           maxMSPTValue = (double)Math.round(SocketOnGetTPSEvent.getTPS() * 100.0D) / 100.0D;
                           out.writeDouble(maxMSPTValue);
                           if (ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_low_tps.equals(true) && maxMSPTValue <= ServerConfig.low_tps_threshold) {
                              Utils.log(Level.INFO, "The current TPS is " + maxMSPTValue + ".");
                           } else if (ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_low_tps.equals(false)) {
                              Utils.log(Level.INFO, "The current TPS is " + maxMSPTValue + ".");
                           }
                        } else if (packetNumber == 4) {
                           maxMSPTValue = SocketOnGetMSPTEvent.getMinMSPT();
                           out.writeDouble(maxMSPTValue);
                           if (ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_min_mspt.equals(true) && maxMSPTValue >= ServerConfig.high_min_mspt_threshold) {
                              Utils.log(Level.INFO, "Min MSPT for the last 1 minute: " + maxMSPTValue);
                           } else if (ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_min_mspt.equals(false)) {
                              Utils.log(Level.INFO, "Min MSPT for the last 1 minute: " + maxMSPTValue);
                           }
                        } else if (packetNumber == 5) {
                           maxMSPTValue = SocketOnGetMSPTEvent.getAvgMSPT();
                           out.writeDouble(maxMSPTValue);
                           if (ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_avg_mspt.equals(true) && maxMSPTValue >= ServerConfig.high_avg_mspt_threshold) {
                              Utils.log(Level.INFO, "Average MSPT for the last 1 minute: " + maxMSPTValue);
                           } else if (ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_avg_mspt.equals(false)) {
                              Utils.log(Level.INFO, "Average MSPT for the last 1 minute: " + maxMSPTValue);
                           }
                        } else if (packetNumber == 6) {
                           maxMSPTValue = SocketOnGetMSPTEvent.getMaxMSPT();
                           out.writeDouble(maxMSPTValue);
                           if (ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_max_mspt.equals(true) && maxMSPTValue >= ServerConfig.high_max_mspt_threshold) {
                              Utils.log(Level.INFO, "Max MSPT for the last 1 minute: " + maxMSPTValue);
                           } else if (ServerConfig.log_performance_metrics.equals(true) && ServerConfig.log_only_high_max_mspt.equals(false)) {
                              Utils.log(Level.INFO, "Max MSPT for the last 1 minute: " + maxMSPTValue);
                           }
                        } else if (packetNumber == 3) {
                           if (ServerConfig.developer_mode.equals(true)) {
                              Utils.log(Level.INFO, "Socket packet 3 close");
                           }

                           out.flush();
                           this.closeConnectionGracefully();
                        } else {
                           Utils.log(Level.INFO, "Packet not found! Packet: " + packetNumber + " Please contact StatusMC Support staff.");
                        }
                     }
                  }
               }
            } catch (IOException var5) {
               out.writeInt(0);
               out.flush();
               this.closeConnectionGracefully();
               Utils.log(Level.INFO, "IO exception 1: " + var5.getMessage());
               var5.printStackTrace();
            }
         }
      } catch (IOException var6) {
         if (ServerConfig.developer_mode.equals(true)) {
            Utils.log(Level.INFO, "IO exception 2: " + var6.getMessage());
            var6.printStackTrace();
         }

      }
   }

   private void closeConnectionGracefully() {
      try {
         in.close();
         out.close();
         sock.close();
         this.connect_status = false;
      } catch (IOException var2) {
         Utils.log(Level.WARNING, "Error while closing socket connection: " + var2.getMessage());
      }

   }
}
