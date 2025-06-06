package net.statusmc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerTickStartEvent extends Event {
   private static final HandlerList HANDLER_LIST = new HandlerList();
   private final int tickNumber;

   public ServerTickStartEvent(int tickNumber) {
      this.tickNumber = tickNumber;
   }

   public int getTickNumber() {
      return this.tickNumber;
   }

   public HandlerList getHandlers() {
      return HANDLER_LIST;
   }

   public static HandlerList getHandlerList() {
      return HANDLER_LIST;
   }
}
