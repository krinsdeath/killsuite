package net.krinsoft.deathcounter;

import com.fernferret.allpay.AllPay;
import com.fernferret.allpay.GenericBank;
import com.pneumaticraft.commandhandler.CommandHandler;
import net.krinsoft.deathcounter.commands.PermissionsHandler;
import net.krinsoft.deathcounter.commands.StatsCommand;
import net.krinsoft.deathcounter.listeners.EntityListener;
import net.krinsoft.deathcounter.listeners.PlayerListener;
import net.krinsoft.deathcounter.listeners.ServerListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author krinsdeath
 */
public class DeathCounter extends JavaPlugin {
    // public static instance handle
    protected static DeathCounter instance;
    private final static Logger LOGGER = Logger.getLogger("DeathCounter");
    private boolean debug = false;
    private boolean economy = false;
    private boolean report = true;
    private int saveTask;

    private GenericBank bank;
    
    private PermissionsHandler permissionsHandler;
    private CommandHandler commandHandler;
    
    private Manager manager;
    private Tracker tracker;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().setDefaults(YamlConfiguration.loadConfiguration(this.getClass().getResourceAsStream("/config.yml")));
        getConfig().options().copyDefaults(true);
        saveConfig();

        debug = getConfig().getBoolean("plugin.debug", false);
        economy = getConfig().getBoolean("plugin.economy", false);
        report = getConfig().getBoolean("plugin.report", true);

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
        PluginManager pm = getServer().getPluginManager();
        // entity death; for kill tracking
        pm.registerEvent(Event.Type.ENTITY_DEATH, eListener, Event.Priority.Monitor, this);
        // player events; dynamic player loading
        pm.registerEvent(Event.Type.PLAYER_JOIN, pListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, pListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_KICK, pListener, Event.Priority.Monitor, this);
        // server listener for economy plugins
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, sListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, sListener, Event.Priority.Monitor, this);
        
        saveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                DeathCounter.instance.getTracker().save();
            }
        }, 300, 300);
        
        log("Enabled successfully.");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        getServer().getScheduler().cancelTask(saveTask);
        tracker.save();
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

    private void registerCommands() {
        this.permissionsHandler = new PermissionsHandler(this);
        this.commandHandler = new CommandHandler(this, this.permissionsHandler);
        this.commandHandler.registerCommand(new StatsCommand(this));
        //this.commandHandler.registerCommand(new ContractCommand(this));
    }
    
    public void log(String message) {
        message = "[" + this + "] " + message;
        LOGGER.info(message);
    }
    
    public void debug(String message) {
        if (debug) {
            message = "[" + this + "] [Debug] " + message;
            LOGGER.info(message);
        }
    }

    public void report(Player p, Monster m, double amt) {
        if (report) {
            String message = ChatColor.YELLOW + "[Kill] " + ChatColor.WHITE + "You killed a " + m.getFancyName();
            if (getBank() != null) {
                message = message + " worth " + getBank().getFormattedAmount(p, amt, -1);
            }
            p.sendMessage(message + ".");
        }
    }
    
    public boolean validateAllPay() {
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

}
