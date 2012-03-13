package net.krinsoft.deathcounter;

import com.fernferret.allpay.AllPay;
import com.fernferret.allpay.GenericBank;
import com.pneumaticraft.commandhandler.CommandHandler;
import net.krinsoft.deathcounter.commands.*;
import net.krinsoft.deathcounter.listeners.EntityListener;
import net.krinsoft.deathcounter.listeners.PlayerListener;
import net.krinsoft.deathcounter.listeners.ServerListener;
import net.krinsoft.deathcounter.listeners.WorldListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author krinsdeath
 */
public class DeathCounter extends JavaPlugin {
    // public static instance handle
    protected static DeathCounter instance;
    private boolean debug = false;
    private boolean economy = false;
    private boolean contract = false;
    private boolean report = true;
    private List<String> worlds = new ArrayList<String>();
    private int saveTask;

    private FileConfiguration configuration;
    private File configFile;

    private FileConfiguration leaderboards;
    private File leaderFile;

    private GenericBank bank;

    private CommandHandler commandHandler;
    
    private Manager manager;
    private Tracker tracker;

    @Override
    public void onEnable() {
        instance = this;
        registerConfig();

        if (economy) {
            if (validateAllPay()) {
                log("Economy successfully hooked.");
            }
        }
        
        // build the kill tracker
        tracker = new Tracker(this);
        
        // register all the players
        manager = new Manager(this);

        registerCommands();

        // event listeners
        EntityListener eListener = new EntityListener(this);
        PlayerListener pListener = new PlayerListener(this);
        ServerListener sListener = new ServerListener(this);
        WorldListener wListener = new WorldListener(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(eListener, this);
        pm.registerEvents(pListener, this);
        pm.registerEvents(sListener, this);
        pm.registerEvents(wListener, this);

        saveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                getTracker().save();
                saveLeaders();
            }
        }, 300, 300 * 20);
        
        log("Enabled successfully.");
    }

    @Override
    public void onDisable() {
        saveLeaders();
        getServer().getScheduler().cancelTasks(this);
        getServer().getScheduler().cancelTask(saveTask);
        tracker.save();
        manager.disable();
        tracker = null;
        manager = null;
        bank = null;
        log("Disabled successfully.");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("contract")) {
            sender.sendMessage(ChatColor.RED + "Not yet implemented.");
            return false;
        }
        List<String> arguments = new ArrayList<String>(Arrays.asList(args));
        arguments.add(0, label);
        return commandHandler.locateAndRunCommand(sender, arguments);
    }

    /**
     * Forces a reload of DeathCounter's configuration file
     * @param val true will reload the files; false does nothing
     */
    public void registerConfig(boolean val) {
        if (val) {
            configFile = null;
            configuration = null;
            registerConfig();
        }
    }

    protected void registerConfig() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getConfig().setDefaults(YamlConfiguration.loadConfiguration(this.getClass().getResourceAsStream("/config.yml")));
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        if (getConfig().get("economy.monsters.ocelot") == null) {
            List<Double> list = new ArrayList<Double>();
            list.add(3.0);
            list.add(5.0);
            getConfig().set("economy.monsters.ocelot", list);
            getConfig().set("economy.monsters.golem", list);
            getConfig().set("economy.monsters.irongolem", list);
            saveConfig();
        }

        leaderFile = new File(getDataFolder(), "leaders.yml");
        if (!leaderFile.exists()) {
            getLeaders().setDefaults(YamlConfiguration.loadConfiguration(this.getClass().getResourceAsStream("/leaders.yml")));
            getLeaders().options().copyDefaults(true);
            saveLeaders();
        }
        if (getLeaders().get("ocelot") == null) {
            List<String> list = new ArrayList<String>();
            getLeaders().set("ocelot", list);
            getLeaders().set("golem", list);
            getLeaders().set("irongolem", list);
            saveLeaders();
        }

        debug = getConfig().getBoolean("plugin.debug", false);
        economy = getConfig().getBoolean("plugin.economy", false);
        contract = getConfig().getBoolean("plugin.contracts", false);
        report = getConfig().getBoolean("plugin.report", true);
        worlds = getConfig().getStringList("plugin.exclude_worlds");

    }

    private void registerCommands() {
        PermissionsHandler permissionsHandler = new PermissionsHandler();
        commandHandler = new CommandHandler(this, permissionsHandler);
        commandHandler.registerCommand(new DebugCommand(this));
        commandHandler.registerCommand(new LeaderCommand(this));
        commandHandler.registerCommand(new ReloadCommand(this));
        commandHandler.registerCommand(new StatsCommand(this));
        if (contract) {
            //commandHandler.registerCommand(new ContractCommand(this));
        }
    }
    
    public void log(String message) {
        getLogger().info(message);
    }
    
    public void debug(boolean val) {
        getConfig().set("plugin.debug", val);
        debug = val;
        saveConfig();
    }
    
    public void debug(String message) {
        if (debug) {
            message = "[Debug] " + message;
            getLogger().info(message);
        }
    }

    public void report(Player p, Monster m, double amt) {
        if (report) {
            String message = ChatColor.YELLOW + "[Kill] " + ChatColor.WHITE + "You killed a " + m.getFancyName();
            if (getBank() != null) {
                try {
                    message = message + " worth " + getBank().getFormattedAmount(p, amt, -1);
                } catch (Exception e) {
                    debug("An error occurred while attempting to fetch the currency format string: " + e.getLocalizedMessage());
                }
            }
            p.sendMessage(message + ".");
        }
        updateLeaderboards(p, m);
    }
    
    public void displayLeaderboards(CommandSender s, Monster m) {
        if (m == null) { return; }
        s.sendMessage("=== Leaderboards: " + m.getFancyName() + " ===");
        try {
            int place = 1;
            String eKiller;
            int eKills;
            List<String> leaders = getLeaders().getStringList(m.getName());
            if (!leaders.isEmpty()) {
                for (String entry : leaders) {
                    eKiller = entry.split(":")[0];
                    eKills = Integer.parseInt(entry.split(":")[1]);
                    s.sendMessage(ChatColor.GOLD + "" + place + ") " + ChatColor.GREEN + eKiller + ChatColor.WHITE + " - " + ChatColor.AQUA + eKills);
                    place++;
                }
            }
        } catch (NullPointerException e) {
            debug("Something went wrong.");
        } catch (NumberFormatException e) {
            debug("An error occurred while parsing the leader list for '" + m.getName() + "'");
        } catch (IndexOutOfBoundsException e) {
            debug("An error occurred while parsing the leader list for '" + m.getName() + "'");
        }
    }
    
    private void updateLeaderboards(Player p, Monster m) {
        try {
            int eKills;
            int kills = manager.getKiller(p.getName()).get(m.getName());
            List<String> leaders = getLeaders().getStringList(m.getName());
            if (leaders.isEmpty()) {
                debug("Leader list was empty for " + m.getName());
                leaders.add(0, p.getName() + ":" + kills);
            }
            for (int i = -1; i < leaders.size(); i++) {
                if (i+1 > leaders.size() && i+1 <= 4) {
                    debug("Adding '" + p.getName() + ":" + kills +"' to list");
                    leaders.add(i+1, p.getName() + ":" + kills);
                    break;
                }
                String[] a = leaders.get(i+1).split(":");
                if (a[0].equals(p.getName())) {
                    leaders.set(i+1, p.getName() + ":" + kills);
                    break;
                }
                eKills = Integer.parseInt(a[1]);
                if (kills > eKills) {
                    leaders.add(i+1, p.getName() + ":" + kills);
                    break;
                }
            }
            getLeaders().set(m.getName(), leaders.subList(0, (leaders.size() > 4 ? 4 : leaders.size())));
        } catch (NullPointerException e) {
            debug("Something went wrong.");
        } catch (NumberFormatException e) {
            debug("An error occurred while parsing the leader list for '" + m.getName() + "'");
        } catch (IndexOutOfBoundsException e) {
            debug("An error occurred while parsing the leader list for '" + m.getName() + "'");
        }
    }
    
    public boolean validateAllPay() {
        if (bank != null) { return true; }
        double allpayVersion = 3.1;
        AllPay allpay = new AllPay(this, "[" + this + "] ");
        log("Validating AllPay at v" + allpayVersion + "...");
        if (allpay.getVersion() >= allpayVersion) {
            bank = allpay.loadEconPlugin();
            bank.toggleReceipts(false);
            log("Found economy plugin: " + bank.getEconUsed() + "... hooking...");
            return true;
        }
        return false;
    }
    
    public void validateAllPay(boolean val) {
        if (!val) {
            economy = false;
            bank = null;
            log("Economy plugin unhooked.");
            return;
        }
        validateAllPay();
    }
    
    public GenericBank getBank() {
        if (!economy) { return null; }
        if (bank == null) {
            validateAllPay();
        }
        return bank;
    }
    
    public Tracker getTracker() {
        return tracker;
    }

    public Manager getManager() {
        return manager;
    }

    public FileConfiguration getConfig() {
        if (configuration == null) {
            configuration = YamlConfiguration.loadConfiguration(configFile);
        }
        return configuration;
    }

    protected FileConfiguration getLeaders() {
        if (leaderboards == null) {
            leaderboards = YamlConfiguration.loadConfiguration(leaderFile);
        }
        return leaderboards;
    }
    
    public void saveLeaders() {
        try {
            leaderboards.save(new File(getDataFolder(), "leaders.yml"));
        } catch (IOException e) {
            debug("Error saving file 'leaders.yml'");
        }
    }
    
    public boolean validWorld(String world) {
        return !worlds.contains(world);
    }

    public double diminishReturn(Player killer, double amount) {
        int ret = getConfig().getInt("economy.diminish.return");
        int depth = getConfig().getInt("economy.diminish.depth");
        int player = (int) Math.floor(killer.getLocation().getY());
        double diminish = ((depth - player) * ret);
        diminish = ((diminish > 0 ? diminish : 0) / 100);
        amount = (amount - (amount * (diminish)));
        return (amount > 0 ? amount : 0);
    }
}
