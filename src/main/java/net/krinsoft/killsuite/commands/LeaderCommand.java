package net.krinsoft.killsuite.commands;

import net.krinsoft.killsuite.KillSuite;
import net.krinsoft.killsuite.Monster;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class LeaderCommand extends KillSuiteCommand {
    
    public LeaderCommand(KillSuite plugin) {
        super(plugin);
        this.setName("KillSuite: Leaders");
        this.setCommandUsage("/ks leaders [monster]");
        this.addCommandExample("/ks leaders skeleton -- Show the top 5 killers for 'skeletons'");
        this.addCommandExample("/leaders player -- Show the top 5 player killers");
        this.setArgRange(0, 2);
        this.addKey("killsuite leaders");
        this.addKey("ks leaders");
        this.addKey("leaders");
        this.setPermission("killsuite.leaders", "Allows players to view the leaderboards.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Monster m = Monster.PLAYER;
        if (args.size() >= 1) {
            m = Monster.getType(args.get(0));
        }
        try {
            plugin.displayLeaderboards(sender, m, (args.size() < 2 ? 0 : Integer.parseInt(args.get(1))));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "The page must be a " + ChatColor.GREEN + "positive number" + ChatColor.RED + ".");
        }
    }
}
