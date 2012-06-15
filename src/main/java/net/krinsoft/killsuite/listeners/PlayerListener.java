package net.krinsoft.killsuite.listeners;

import net.krinsoft.killsuite.KillSuite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class PlayerListener implements Listener {
    private KillSuite plugin;
    
    public PlayerListener(KillSuite plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent event) {
        plugin.getManager().register(event.getPlayer().getName());
    }

}
