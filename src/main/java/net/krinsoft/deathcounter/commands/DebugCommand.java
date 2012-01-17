package net.krinsoft.deathcounter.commands;

import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class DebugCommand extends DeathCommand {
    
    public DebugCommand(DeathCounter plugin) {
        super(plugin);
        this.setName("DeathCounter debug");
        this.setCommandUsage("/dc debug [true|false|on|off]");
        this.setArgRange(0, 1);
        this.addKey("deathcounter debug");
        this.addKey("dc debug");
        this.setPermission("deathcounter.debug", "Allows users to toggle DeathCounter's debug mode", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        boolean val = true;
        if (args.size() > 0) {
            try {
                String t = (args.get(0).equalsIgnoreCase("on") || args.get(0).equalsIgnoreCase("true") ? "true" : "false");
                val = Boolean.parseBoolean(t);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid option.");
                plugin.debug("Invalid option specified.");
            }
        }
        if (val) {
            sender.sendMessage(ChatColor.GREEN + "Debug mode enabled.");
        } else {
            sender.sendMessage(ChatColor.RED + "Debug mode disabled.");
        }
        plugin.debug(val);
    }
}
