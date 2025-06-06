package net.statusmc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerTickEndEvent extends Event {
   private static final HandlerList HANDLER_LIST = new HandlerList();
   private final int tickNumber;
   private final double tickDuration;
   private final long timeEnd;

   public ServerTickEndEvent(int tickNumber, double tickDuration, long timeRemaining) {
      this.tickNumber = tickNumber;
      this.tickDuration = tickDuration;
      this.timeEnd = System.nanoTime() + timeRemaining;
   }

   public int getTickNumber() {
      return this.tickNumber;
   }

   public double getTickDuration() {
      return this.tickDuration;
   }

   public long getTimeRemaining() {
      return this.timeEnd - System.nanoTime();
   }

   public HandlerList getHandlers() {
      return HANDLER_LIST;
   }

   public static HandlerList getHandlerList() {
      return HANDLER_LIST;
   }
}
