package net.krinsoft.deathcounter.listeners;

import com.fernferret.allpay.AllPay;
import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.Arrays;

/**
 * @author krinsdeath
 */
public class ServerListener extends org.bukkit.event.server.ServerListener {
    private DeathCounter plugin;
    
    public ServerListener(DeathCounter plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (Arrays.asList(AllPay.validEconPlugins).contains(event.getPlugin().getDescription().getName())) {
            if (this.plugin.validateAllPay()) {
                this.plugin.log("Economy plugin successfully hooked.");
            }
        }
    }
    
    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (Arrays.asList(AllPay.validEconPlugins).contains(event.getPlugin().getDescription().getName())) {
            this.plugin.validateAllPay(false);
        }
    }

}
