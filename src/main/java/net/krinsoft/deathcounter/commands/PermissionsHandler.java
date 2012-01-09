package net.krinsoft.deathcounter.commands;

import com.pneumaticraft.commandhandler.PermissionsInterface;
import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author krinsdeath
 */
public class PermissionsHandler implements PermissionsInterface {
    private DeathCounter plugin;
    
    public PermissionsHandler(DeathCounter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String node, boolean isOpRequired) {
        boolean has = sender.hasPermission(node);
        boolean is = sender.isPermissionSet(node);
        boolean all = sender.hasPermission("deathcounter.*");
        if (has) { return true; }
        else if (is && !has) { return false; }
        else if (all) { return true; }
        return false;
    }

    @Override
    public boolean hasAnyPermission(CommandSender sender, List<String> nodes, boolean opRequired) {
        for (String node : nodes) {
            if (hasPermission(sender, node, opRequired)) { return true; }
        }
        return false;
    }

    @Override
    public boolean hasAllPermission(CommandSender sender, List<String> nodes, boolean opRequired) {
        for (String node : nodes) {
            if (!hasPermission(sender, node, opRequired)) { return false; }
        }
        return true;
    }
}
