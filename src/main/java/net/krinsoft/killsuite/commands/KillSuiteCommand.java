package net.krinsoft.killsuite.commands;

import com.pneumaticraft.commandhandler.Command;
import net.krinsoft.killsuite.KillSuite;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author krinsdeath
 */
public abstract class KillSuiteCommand extends Command {
    protected KillSuite plugin;
    
    public KillSuiteCommand(KillSuite plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public void message(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GOLD + "[KillSuite] " + ChatColor.WHITE + message);
    }

}
