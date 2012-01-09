package net.krinsoft.deathcounter.commands;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.FancyMessage;
import net.krinsoft.deathcounter.Killer;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class StatsCommand extends DeathCommand {
    
    public StatsCommand(DeathCounter plugin) {
        super(plugin);
        this.setName("DeathCounter Stats");
        this.setCommandUsage("/dc stats [target] [-ao]");
        this.addCommandExample("/dc stats -- Display your own kill statistics.");
        this.addCommandExample("/dc stats [target] -a -- Show your (or a target's) 'animal' kills");
        this.addCommandExample("/dc stats [target] -o -- Show your (or a target's) 'other' kills");
        this.setArgRange(0, 2);
        this.addKey("deathcounter stats");
        this.addKey("dc stats");
        this.addKey("stats");
        this.setPermission("deathcounter.stats", "Allows users to check their statistics.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        CommandSender target = sender;
        String category = "monsters";
        if (args.size() > 0) {
            String flag = ((args.size() == 2) ? args.get(1) : args.get(0));
            if (flag.startsWith("-")) {
                if (flag.contains("a")) {
                    category = "animals";
                } else if (flag.contains("o")) {
                    category = "others";
                }
            } else {
                target = plugin.getServer().getPlayer(flag);
            }
        }
        FancyMessage message = new FancyMessage(plugin.getTracker().fetch(target.getName()), category);
        sender.sendMessage(message.getHeader());
        for (String line : message.getLines()) {
            sender.sendMessage(line);
        }
    }
}
