package net.krinsoft.killsuite.listeners;

import net.krinsoft.killsuite.KillSuite;
import net.krinsoft.killsuite.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class EntityListener implements Listener {
    private Set<UUID> reasons = new HashSet<UUID>();
    private final KillSuite plugin;
    
    public EntityListener(KillSuite plugin) {
        this.plugin = plugin;
        load();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    void entityDeath(EntityDeathEvent event) {
        long n = System.nanoTime();
        String world = event.getEntity().getWorld().getName();
        if (!plugin.validWorld(world)) { return; }
        // see if the event was an entity killing another entity
        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            // cast to entity damage by entity to check the cause of the damage
            EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
            Player killer;
            boolean pet = false;
            if (evt.getDamager() instanceof Player) {
                // damager was a player
                killer = (Player) evt.getDamager();
            } else if (evt.getDamager() instanceof Projectile) {
                // damager was a projectile
                if (((Projectile)evt.getDamager()).getShooter() instanceof Player) {
                    // shooter was a player
                    killer = (Player) ((Projectile)evt.getDamager()).getShooter();
                } else {
                    // shooter was a monster
                    reasons.remove(event.getEntity().getUniqueId());
                    return;
                }
            } else if (evt.getDamager() instanceof Tameable && ((Tameable)evt.getDamager()).isTamed() && ((Tameable)evt.getDamager()).getOwner() != null && ((Tameable)evt.getDamager()).getOwner() instanceof Player) {
                pet = true;
                killer = (Player) ((Tameable)evt.getDamager()).getOwner();
            } else {
                reasons.remove(event.getEntity().getUniqueId());
                return;
            }
            double mod = 1;
            if (reasons.contains(event.getEntity().getUniqueId())) {
                plugin.debug("Encountered spawned mob.");
                // check if the admin wants to pay users for spawner mobs
                if (plugin.getConfig().getBoolean("economy.spawner.payout", true)) {
                    // diminish the payout
                    mod = plugin.getConfig().getDouble("economy.spawner.diminish", 0.50);
                } else {
                    plugin.debug("Payout disabled for spawner mobs.");
                    // cancel the tracking / reward
                    reasons.remove(event.getEntity().getUniqueId());
                    return;
                }
            }
            Monster monster = Monster.getType(event.getEntity());
            double error = plugin.getManager().getKiller(killer.getName()).update(monster.getName());
            if (error > 0) {
                double amount = 0;
                if (plugin.getBank() != null) {
                    // economy is enabled, so let's find the reward
                    amount = plugin.getManager().getReward(monster.getName());
                    if (monster.getName().equals("player")) {
                        Player dead = (Player) event.getEntity();
                        if (plugin.getConfig().getBoolean("economy.players.percentage")) {
                            double balance = plugin.getBank().getBalance(dead, -1);
                            amount = balance * (amount / 100);
                        }
                        if (plugin.getConfig().getBoolean("economy.players.realism")) {
                            double balance = plugin.getBank().getBalance(dead, -1);
                            if (amount > balance) {
                                amount = balance;
                            }
                            plugin.getBank().take(dead, amount, -1);
                        }
                    }
                    amount = plugin.diminishReturn(killer, amount);
                    amount = amount * mod;
                    long bank = System.nanoTime();
                    plugin.getBank().give(killer, amount, -1);
                    plugin.profile("bank.update", System.nanoTime() - bank);
                }
                // report the earnings
                plugin.report(killer, monster, amount, pet);
            } else {
                plugin.getLogger().warning("An error occurred while incrementing the monster count for '" + killer.getName() + "'!");
                plugin.getLogger().warning(plugin.getManager().getKiller(killer.getName()).toString());
            }
        }
        n = System.nanoTime() - n;
        reasons.remove(event.getEntity().getUniqueId());
        plugin.profile("entity.death", n);
    }

    @EventHandler
    void creatureSpawn(CreatureSpawnEvent event) {
        if (!plugin.validWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        UUID id = event.getEntity().getUniqueId();
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER && !reasons.contains(id)) {
            reasons.add(id);
        }
    }

    public void save() {
        try {
            File idFile = new File(plugin.getDataFolder(), "uuid_spawner.dat");
            if (!idFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                idFile.createNewFile();
            }
            FileOutputStream fileOut = new FileOutputStream(idFile);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(reasons);
            objOut.close();
        } catch (FileNotFoundException e) {
            plugin.getLogger().warning(e.getMessage());
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
        }
    }

    void load() {
        try {
            File idFile = new File(plugin.getDataFolder(), "uuid_spawner.dat");
            if (idFile.exists()) {
                FileInputStream fileIn = new FileInputStream(idFile);
                ObjectInputStream objIn = new ObjectInputStream(fileIn);
                //noinspection unchecked
                reasons = (Set<UUID>) objIn.readObject();
                objIn.close();
                fileIn.close();
                //noinspection ResultOfMethodCallIgnored
                idFile.delete();
            }
        } catch (FileNotFoundException e) {
            plugin.getLogger().warning(e.getMessage());
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning(e.getMessage());
        }
    }

}
