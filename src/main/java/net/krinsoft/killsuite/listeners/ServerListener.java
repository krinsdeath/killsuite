package net.krinsoft.killsuite.listeners;

import com.fernferret.allpay.AllPay;
import net.krinsoft.killsuite.KillSuite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.Arrays;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class ServerListener implements Listener {
    private KillSuite plugin;
    
    public ServerListener(KillSuite plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void pluginEnable(PluginEnableEvent event) {
        if (Arrays.asList(AllPay.getValidEconPlugins()).contains(event.getPlugin().getDescription().getName())) {
            if (this.plugin.validateAllPay()) {
                this.plugin.log("Economy plugin successfully hooked.");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void pluginDisable(PluginDisableEvent event) {
        if (Arrays.asList(AllPay.getValidEconPlugins()).contains(event.getPlugin().getDescription().getName())) {
            this.plugin.validateAllPay(false);
        }
    }

}
