package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author krinsdeath
 */
public class PlayerListener extends org.bukkit.event.player.PlayerListener {
    private DeathCounter plugin;
    
    public PlayerListener(DeathCounter plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getManager().register(event.getPlayer().getName());
    }
    
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTracker().update(event.getPlayer().getName());
        plugin.getManager().unregister(event.getPlayer().getName());
    }
    
    @Override
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) { return; }
        plugin.getTracker().update(event.getPlayer().getName());
        plugin.getManager().unregister(event.getPlayer().getName());
    }
}
