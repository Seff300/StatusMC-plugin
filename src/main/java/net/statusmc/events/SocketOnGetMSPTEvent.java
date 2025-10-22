package net.statusmc.events;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

//import com.destroystokyo.paper.event.server.ServerTickEndEvent;
//import com.destroystokyo.paper.event.server.ServerTickStartEvent;

import net.statusmc.utils.ServerConfig;
import net.statusmc.utils.Utils;

public class SocketOnGetMSPTEvent implements Listener {

    private long tickStartTime;
    private double mspt = 0.0;
    
    private final static List<Double> tickTimes = new ArrayList<>(); // List to store tick times for the last 1 minute
    private static final ReadWriteLock lock = new ReentrantReadWriteLock(); //Thread 7 exception parandamiseks vajalik,
    //et saaks readida ja writeida samal ajal
	
	
    @EventHandler
    public void onServerTickStart(ServerTickStartEvent event) {
        // Record the start time of the tick
        tickStartTime = System.nanoTime();
    }
    
    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
    	double mspt = event.getTickDuration();    // accurate MSPT
        lock.writeLock().lock();
        try {
            tickTimes.add(mspt);
            if (tickTimes.size() > 1200) {
                tickTimes.remove(0);
            }
        } finally {
            lock.writeLock().unlock();
        }
        /*if (tickStartTime != 0) {
            //Arvutame aja, mis kulus tickile
            long tickEndTime = System.nanoTime();
            long timeDiff = tickEndTime - tickStartTime;

            // Convert nanoseconds to milliseconds
            mspt = timeDiff / 1_000_000.0;
            lock.writeLock().lock();
            try {
            	tickTimes.add(mspt);
            } finally {
                lock.writeLock().unlock();
            }
            
            //Tagan, et arrays saaks olla max 1 minuti data, 1 minutis on 1200 ticki ehk kui 1200 on täis, siis
            lock.writeLock().lock();
            try {
	            if (tickTimes.size() > 1200) {
	                tickTimes.remove(0); //Eemaldan vanima ticki
	            }
            } finally {
                lock.writeLock().unlock();
            }
	            
            //Utils.log(Level.INFO, "MSPT: "+mspt);
        }*/
    }
	
	//Saab min MSPT
    public static double getMinMSPT() {
		lock.readLock().lock();
		try {
	    	if(tickTimes.size() == 1200) {
		        double min = Collections.min(tickTimes);
		        double minMspt2 = Math.round(min * 10.0) / 10.0;
		        
		        return minMspt2;
	    	} else {
				Utils.log(Level.WARNING, Utils.format(ServerConfig.unableMinMSPT));
	            return 0.0;
	    	}
		} finally {
	        lock.readLock().unlock();
	    }
	}
	
	//Saab average MSPT
	public static double getAvgMSPT() {
		lock.readLock().lock();
		try {
			if(tickTimes.size() == 1200) {
				double average2 = tickTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
				double averageMspt2 = Math.round(average2 * 10.0) / 10.0;
	
		        return averageMspt2;
	
			} else {
				Utils.log(Level.WARNING, Utils.format(ServerConfig.unableAvgMSPT));
	            return 0.0;
			}
		} finally {
	        lock.readLock().unlock();
	    }
        
	}
	
	//Saab max MSPT
	public static double getMaxMSPT() {
		lock.readLock().lock();
		try {
			if(tickTimes.size() == 1200) {
				double max2 = Collections.max(tickTimes);
				double maxMspt2 = Math.round(max2 * 10.0) / 10.0;
		        
		        return maxMspt2;
			} else {
				Utils.log(Level.WARNING, Utils.format(ServerConfig.unableMaxMSPT));
	            return 0.0;
			}
		} finally {
	        lock.readLock().unlock();
	    }
	}

}
