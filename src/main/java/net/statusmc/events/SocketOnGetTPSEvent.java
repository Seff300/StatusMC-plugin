package net.statusmc.events;


public class SocketOnGetTPSEvent implements Runnable {

	public static int TICK_COUNT= 0;
	public static long[] TICKS= new long[1300];
	public static long LAST_TICK= 0L;

	public static double getTPS() {
	  return getTPS(1200);
	}

	public static double getTPS(int ticks) {
		 // Clamp ticks to the buffer size
	    if (ticks > TICKS.length) {
	        ticks = TICKS.length; // Use the maximum buffer size
	    }
	    
	  if (TICK_COUNT< ticks) {
	    return 20.0D;
	  }
	  int target = (TICK_COUNT- 1 - ticks) % TICKS.length;
	  long elapsed = System.currentTimeMillis() - TICKS[target];
	
	  return ticks / (elapsed / 1000.0D);
	}

	public static long getElapsed(int tickID) {
	  if (TICK_COUNT- tickID >= TICKS.length) {
	  }

	  long time = TICKS[(tickID % TICKS.length)];
	  return System.currentTimeMillis() - time;
	}

	public void run() {
	  TICKS[(TICK_COUNT% TICKS.length)] = System.currentTimeMillis();

	  TICK_COUNT+= 1;
	}
}
