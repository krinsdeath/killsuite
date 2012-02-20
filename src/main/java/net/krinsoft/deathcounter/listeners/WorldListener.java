package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class WorldListener implements Listener {
    private DeathCounter plugin;
    
    public WorldListener(DeathCounter plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void worldLoad(WorldLoadEvent event) {
        if (plugin.validWorld(event.getWorld().getName())) {
            plugin.log("Tracking kills in '" + event.getWorld().getName() + "'");
        }
    }
    
}
