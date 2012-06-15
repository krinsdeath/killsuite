package net.krinsoft.killsuite.commands;

import net.krinsoft.killsuite.KillSuite;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class ReloadCommand extends KillSuiteCommand {

    public ReloadCommand(KillSuite instance) {
        super(instance);
        setName("KillSuite: Reload");
        setCommandUsage("/ks reload");
        setArgRange(0, 1);
        addKey("killsuite reload");
        addKey("ks reload");
        setPermission("killsuite.reload", "Allows users to reload KillSuite's config file.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        long t = System.currentTimeMillis();
        plugin.registerConfig(true);
        t = System.currentTimeMillis() - t;
        sender.sendMessage(ChatColor.GOLD + "[KillSuite] " + ChatColor.WHITE + "Configuration reloaded. (" + ChatColor.GREEN + t + ChatColor.WHITE + "ms)");
        plugin.log("Configuration reloaded. (" + t + "ms)");
    }
}
