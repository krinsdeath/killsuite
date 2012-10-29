package net.krinsoft.killsuite.listeners;

import net.krinsoft.killsuite.KillSuite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class WorldListener implements Listener {
    private final KillSuite plugin;
    
    public WorldListener(KillSuite plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void worldLoad(WorldLoadEvent event) {
        if (plugin.validWorld(event.getWorld().getName())) {
            plugin.debug("Tracking kills in '" + event.getWorld().getName() + "'");
        }
    }
    
}
