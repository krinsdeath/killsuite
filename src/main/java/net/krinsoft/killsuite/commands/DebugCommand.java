package net.krinsoft.killsuite.commands;

import net.krinsoft.killsuite.KillSuite;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class DebugCommand extends KillSuiteCommand {
    
    public DebugCommand(KillSuite plugin) {
        super(plugin);
        this.setName("KillSuite: Debug");
        this.setCommandUsage("/ks debug [true|false|on|off]");
        this.setArgRange(0, 1);
        this.addKey("killsuite debug");
        this.addKey("ks debug");
        this.setPermission("killsuite.debug", "Allows users to toggle KillSuite's debug mode", PermissionDefault.OP);
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
            }
        }
        message(sender, "Debug mode: " + (val ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
        plugin.debug(val);
    }
}
