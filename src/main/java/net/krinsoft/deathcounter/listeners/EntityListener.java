package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.Monster;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author krinsdeath
 */
public class EntityListener extends org.bukkit.event.entity.EntityListener {
    private DeathCounter plugin;
    
    public EntityListener(DeathCounter plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        // see if the event was an entity killing another entity
        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            // cast to entity damage by entity to check the cause of the damage
            EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
            if (evt.getDamager() instanceof Player) {
                Player killer = (Player) evt.getDamager();
                Monster monster = Monster.getType(event.getEntity());
                plugin.getManager().getKiller(killer.getName()).update(monster.getName());
                double amount = 0;
                if (plugin.getBank() != null) {
                    try {
                        if (!monster.getCategory().equalsIgnoreCase("players")) {
                            List<Double> range = plugin.getConfig().getDoubleList("economy." + monster.getCategory() + "." + monster.getName());
                            double min = range.get(0);
                            double max = range.get(1);
                            amount = Double.valueOf(new DecimalFormat("#.##").format(min + (Math.random() * ((max - min) + 1))));
                        } else {
                            Player dead = (Player) event.getEntity();
                            List<Double> range = plugin.getConfig().getDoubleList("economy.players.reward");
                            double min = range.get(0);
                            double max = range.get(1);
                            amount = Double.valueOf(new DecimalFormat("#.##").format(min + (Math.random() * ((max - min) + 1))));
                            if (plugin.getConfig().getBoolean("economy.players.percentage")) {
                                double balance = plugin.getBank().getBalance(dead, -1);
                                amount = balance * (amount / 100);
                            }
                            if (plugin.getConfig().getBoolean("economy.players.realism")) {
                                double balance = plugin.getBank().getBalance(dead, -1);
                                if (amount > balance) {
                                    amount = balance;
                                }
                                plugin.getBank().pay(dead, amount, -1);
                            }
                        }
                        plugin.getBank().give(killer, amount, -1);
                    } catch (NullPointerException e) {
                        plugin.debug(e.getLocalizedMessage() + ": Found null path at 'economy." + monster.getCategory() + "." + monster.getName() + "' in 'config.yml'");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        plugin.debug(e.getLocalizedMessage() + ": Invalid list at 'economy." + monster.getCategory() + "." + monster.getName() + "'");
                    }
                }
                plugin.report(killer, monster, amount);
            }
        }
    }
    
}
