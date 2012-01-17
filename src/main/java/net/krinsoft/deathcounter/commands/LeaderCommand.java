package net.krinsoft.deathcounter.commands;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.Monster;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class LeaderCommand extends DeathCommand {
    
    public LeaderCommand(DeathCounter plugin) {
        super(plugin);
        this.setName("DeathCounter Leaderboards");
        this.setCommandUsage("/dc leaders [monster]");
        this.addCommandExample("/dc leaders skeleton -- Show the top 5 killers for 'skeletons'");
        this.addCommandExample("/leaders player -- Show the top 5 player killers");
        this.setArgRange(0, 1);
        this.addKey("deathcounter leaders");
        this.addKey("dc leaders");
        this.addKey("leaders");
        this.setPermission("deathcounter.leaders", "Allows players to view the leaderboards.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Monster m = Monster.PLAYER;
        if (args.size() == 1) {
            m = Monster.getType(args.get(0));
        }
        plugin.displayLeaderboards(sender, m);
    }
}
