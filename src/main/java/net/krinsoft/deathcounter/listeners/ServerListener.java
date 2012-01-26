package net.krinsoft.deathcounter.listeners;

import com.fernferret.allpay.AllPay;
import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.Arrays;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class ServerListener implements org.bukkit.event.Listener {
    private DeathCounter plugin;
    
    public ServerListener(DeathCounter plugin) {
        this.plugin = plugin;
    }

    @EventHandler(event = PluginEnableEvent.class, priority = EventPriority.MONITOR)
    public void pluginEnable(PluginEnableEvent event) {
        if (Arrays.asList(AllPay.validEconPlugins).contains(event.getPlugin().getDescription().getName())) {
            if (this.plugin.validateAllPay()) {
                this.plugin.log("Economy plugin successfully hooked.");
            }
        }
    }

    @EventHandler(event = PluginDisableEvent.class, priority = EventPriority.MONITOR)
    public void pluginDisable(PluginDisableEvent event) {
        if (Arrays.asList(AllPay.validEconPlugins).contains(event.getPlugin().getDescription().getName())) {
            this.plugin.validateAllPay(false);
        }
    }

}
