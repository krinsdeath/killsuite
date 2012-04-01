package net.krinsoft.deathcounter.commands;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.FancyMessage;
import net.krinsoft.deathcounter.Killer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class StatsCommand extends DeathCommand {
    
    public StatsCommand(DeathCounter plugin) {
        super(plugin);
        this.setName("DeathCounter: Stats");
        this.setCommandUsage("/dc stats [-aopm] [target]");
        this.addCommandExample("/dc stats -- Display your own kill statistics.");
        this.addCommandExample("/dc stats -a [target] -- Show your (or a target's) 'animal' kills");
        this.addCommandExample("/dc stats -o [target] -- Show your (or a target's) 'other' kills");
        this.addCommandExample("/dc stats -p [target] -- Show your (or a target's) 'player' kills");
        this.setArgRange(0, 2);
        this.addKey("deathcounter stats");
        this.addKey("dc stats");
        this.addKey("stats");
        this.setPermission("deathcounter.stats", "Allows users to check their statistics.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Killer target = plugin.getManager().getKiller(sender.getName());
        String category = "monsters";
        if (args.size() > 0) {
            String flag = args.get(0);
            String player = ((args.size() == 2) ? args.get(1) : sender.getName());
            if (flag.startsWith("-")) {
                if (flag.startsWith("-m")) {
                    category = "monsters";
                } else if (flag.startsWith("-a")) {
                    category = "animals";
                } else if (flag.startsWith("-o")) {
                    category = "others";
                } else if (flag.startsWith("-p")) {
                    category = "players";
                } else {
                    message(sender, ChatColor.RED + "Unknown flag.");
                }
            }
            target = plugin.getManager().getKiller(player);
        }
        if (target == null) {
            message(sender, ChatColor.RED + "That target did not exist.");
            return;
        }
        if (!target.getName().equals(sender.getName()) && !sender.hasPermission("deathcounter.stats.other")) {
            message(sender, ChatColor.RED + "You do not have permission to view other peoples' stats.");
            return;
        }
        if (target.getName().equalsIgnoreCase("console")) {
            message(sender, "The console cannot have recorded stats.");
            return;
        }
        plugin.debug("Using category '" + category + "' for player '" + target.getName() + "'");
        FancyMessage message = new FancyMessage(plugin.getTracker().fetch(target.getName()), category);
        sender.sendMessage(message.getHeader());
        for (String line : message.getLines()) {
            sender.sendMessage(line);
        }
    }
}
