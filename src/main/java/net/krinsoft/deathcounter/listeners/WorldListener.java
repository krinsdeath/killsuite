package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * @author krinsdeath
 */
public class WorldListener extends org.bukkit.event.world.WorldListener {
    private DeathCounter plugin;
    
    public WorldListener(DeathCounter plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onWorldLoad(WorldLoadEvent event) {
        if (plugin.validWorld(event.getWorld().getName())) {
            plugin.log("Tracking kills in '" + event.getWorld().getName() + "'");
        }
    }
    
}
