package net.krinsoft.deathcounter.commands;

import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class ReloadCommand extends DeathCommand {

    public ReloadCommand(DeathCounter instance) {
        super(instance);
        setName("DeathCounter: Reload");
        setCommandUsage("/dc reload");
        setArgRange(0, 1);
        addKey("deathcounter reload");
        addKey("dc reload");
        setPermission("deathcounter.reload", "Allows users to reload DeathCounter's config file.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        long t = System.currentTimeMillis();
        plugin.registerConfig(true);
        t = System.currentTimeMillis() - t;
        sender.sendMessage(ChatColor.GOLD + "[DeathCounter] " + ChatColor.WHITE + "Configuration reloaded. (" + ChatColor.GREEN + t + ChatColor.WHITE + "ms)");
        plugin.log("Configuration reloaded. (" + t + "ms)");
    }
}
