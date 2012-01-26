package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class PlayerListener implements org.bukkit.event.Listener {
    private DeathCounter plugin;
    
    public PlayerListener(DeathCounter plugin) {
        this.plugin = plugin;
    }

    @EventHandler(event = PlayerJoinEvent.class, priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent event) {
        plugin.getManager().register(event.getPlayer().getName());
    }

    @EventHandler(event = PlayerQuitEvent.class, priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        plugin.getTracker().update(event.getPlayer().getName());
        plugin.getManager().unregister(event.getPlayer().getName());
    }
    
}
