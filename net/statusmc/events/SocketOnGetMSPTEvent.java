package net.statusmc.events;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.statusmc.utils.ServerConfig;
import net.statusmc.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SocketOnGetMSPTEvent implements Listener {
   private long tickStartTime;
   private double mspt = 0.0D;
   private static final List<Double> tickTimes = new ArrayList();
   private static final ReadWriteLock lock = new ReentrantReadWriteLock();

   @EventHandler
   public void onServerTickStart(ServerTickStartEvent event) {
      this.tickStartTime = System.nanoTime();
   }

   @EventHandler
   public void onServerTickEnd(ServerTickEndEvent event) {
      double mspt = event.getTickDuration();
      lock.writeLock().lock();

      try {
         tickTimes.add(mspt);
         if (tickTimes.size() > 1200) {
            tickTimes.remove(0);
         }
      } finally {
         lock.writeLock().unlock();
      }

   }

   public static double getMinMSPT() {
      lock.readLock().lock();

      try {
         if (tickTimes.size() == 1200) {
            double min = (Double)Collections.min(tickTimes);
            double minMspt2 = (double)Math.round(min * 10.0D) / 10.0D;
            double var5 = minMspt2;
            return var5;
         }

         Utils.log(Level.WARNING, Utils.format(ServerConfig.unableMinMSPT));
      } finally {
         lock.readLock().unlock();
      }

      return 0.0D;
   }

   public static double getAvgMSPT() {
      lock.readLock().lock();

      try {
         if (tickTimes.size() == 1200) {
            double average2 = tickTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0D);
            double averageMspt2 = (double)Math.round(average2 * 10.0D) / 10.0D;
            double var5 = averageMspt2;
            return var5;
         }

         Utils.log(Level.WARNING, Utils.format(ServerConfig.unableAvgMSPT));
      } finally {
         lock.readLock().unlock();
      }

      return 0.0D;
   }

   public static double getMaxMSPT() {
      lock.readLock().lock();

      try {
         if (tickTimes.size() == 1200) {
            double max2 = (Double)Collections.max(tickTimes);
            double maxMspt2 = (double)Math.round(max2 * 10.0D) / 10.0D;
            double var5 = maxMspt2;
            return var5;
         }

         Utils.log(Level.WARNING, Utils.format(ServerConfig.unableMaxMSPT));
      } finally {
         lock.readLock().unlock();
      }

      return 0.0D;
   }
}
