package net.krinsoft.killsuite.commands;

import net.krinsoft.killsuite.KillSuite;
import net.krinsoft.killsuite.FancyMessage;
import net.krinsoft.killsuite.Killer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class StatsCommand extends KillSuiteCommand {
    
    public StatsCommand(KillSuite plugin) {
        super(plugin);
        this.setName("KillSuite: Stats");
        this.setCommandUsage("/ks stats [-aopm] [target]");
        this.addCommandExample("/ks stats -- Display your own kill statistics.");
        this.addCommandExample("/stats -a [target] -- Show your (or a target's) 'animal' kills");
        this.addCommandExample("/killsuite stats -o [target] -- Show your (or a target's) 'other' kills");
        this.addCommandExample("/ks stats -p [target] -- Show your (or a target's) 'player' kills");
        this.setArgRange(0, 2);
        this.addKey("killsuite stats");
        this.addKey("ks stats");
        this.addKey("stats");
        this.setPermission("killsuite.stats", "Allows users to check their statistics.", PermissionDefault.TRUE);
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
        if (!target.getName().equals(sender.getName()) && !sender.hasPermission("killsuite.stats.other")) {
            message(sender, ChatColor.RED + "You do not have permission to view other peoples' stats.");
            return;
        }
        if (target.getName().equalsIgnoreCase("console")) {
            message(sender, "The console cannot have recorded stats.");
            return;
        }
        plugin.debug("Using category '" + category + "' for player '" + target.getName() + "'");
        FancyMessage message = new FancyMessage(plugin.getManager().getKiller(target.getName()), category);
        sender.sendMessage(message.getHeader());
        for (String line : message.getLines()) {
            sender.sendMessage(line);
        }
    }
}
