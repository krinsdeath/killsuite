package net.krinsoft.deathcounter.commands;

import com.pneumaticraft.commandhandler.Command;
import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author krinsdeath
 */
public abstract class DeathCommand extends Command {
    protected DeathCounter plugin;
    
    public DeathCommand(DeathCounter plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public void message(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GOLD + "[DeathCounter] " + ChatColor.WHITE + message);
    }

}
