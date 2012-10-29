package net.krinsoft.killsuite.commands;

import com.pneumaticraft.commandhandler.Command;
import net.krinsoft.killsuite.KillSuite;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author krinsdeath
 */
abstract class KillSuiteCommand extends Command {
    final KillSuite plugin;
    
    KillSuiteCommand(KillSuite plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    void message(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GOLD + "[KillSuite] " + ChatColor.WHITE + message);
    }

}
