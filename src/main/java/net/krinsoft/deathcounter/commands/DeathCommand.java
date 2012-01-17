package net.krinsoft.deathcounter.commands;

import com.pneumaticraft.commandhandler.Command;
import net.krinsoft.deathcounter.DeathCounter;

/**
 * @author krinsdeath
 */
public abstract class DeathCommand extends Command {
    protected DeathCounter plugin;
    
    public DeathCommand(DeathCounter plugin) {
        super(plugin);
        this.plugin = plugin;
    }

}
