package net.krinsoft.killsuite.listeners;

import net.krinsoft.killsuite.KillSuite;
import net.krinsoft.killsuite.Monster;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class EntityListener implements Listener {
    private final LazyMetadataValue meta;
    private final KillSuite plugin;

    private final boolean spawner_payout;
    private final double spawner_mod;

    private final boolean player_percent;
    private final boolean player_realism;
    
    public EntityListener(KillSuite plugin) {
        this.plugin = plugin;
        this.meta = new FixedMetadataValue(plugin, true);
        // spawner options
        this.spawner_payout = plugin.getConfig().getBoolean("economy.spawner.payout", true);
        this.spawner_mod = plugin.getConfig().getDouble("economy.spawner.diminish", 0.50);
        // player options
        this.player_percent = plugin.getConfig().getBoolean("economy.players.percentage", false);
        this.player_realism = plugin.getConfig().getBoolean("economy.players.realism", false);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    void entityDeath(EntityDeathEvent event) {
        long n = System.nanoTime();
        String world = event.getEntity().getWorld().getName();
        if (!plugin.validWorld(world)) { return; }
        KillSuite.addProfileMessage("entity.death.start", System.nanoTime() - n);
        // see if the event was an entity killing another entity
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }
        // cast to entity damage by entity to check the cause of the damage
        EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
        KillSuite.addProfileMessage("killer.find.start", System.nanoTime() - n);
        Player killer;
        boolean pet = false;
        Entity e = evt.getDamager();
        if (e instanceof Player) {
            // damager was a player
            killer = (Player) e;
        } else if (e instanceof Projectile) {
            // damager was a projectile
            if (((Projectile) e).getShooter() instanceof Player) {
                // shooter was a player
                killer = (Player) ((Projectile) e).getShooter();
            } else {
                // shooter was a monster
                return;
            }
        } else if (e instanceof Tameable && ((Tameable) e).isTamed() && ((Tameable) e).getOwner() != null && ((Tameable) e).getOwner() instanceof Player) {
            pet = true;
            killer = (Player) ((Tameable) e).getOwner();
        } else {
            return;
        }
        KillSuite.addProfileMessage("killer.find.end", System.nanoTime() - n);
        KillSuite.addProfileMessage("monster.spawner.start", System.nanoTime() - n);
        double mod = 1;
        if (event.getEntity().hasMetadata("spawner")) {
            //plugin.debug("Encountered spawned mob.");
            // check if the admin wants to pay users for spawner mobs
            if (spawner_payout) {
                // diminish the payout
                mod = spawner_mod;
            } else {
                plugin.debug("Payout disabled for spawner mobs.");
                // cancel the tracking / reward
                return;
            }
        }
        KillSuite.addProfileMessage("monster.spawner.end", System.nanoTime() - n);
        KillSuite.addProfileMessage("monster.fetch.start", System.nanoTime() - n);
        Monster monster = Monster.getType(event.getEntity());
        KillSuite.addProfileMessage("monster.fetch.end", System.nanoTime() - n);
        double error = plugin.getManager().getKiller(killer.getName()).update(monster.getName());
        if (error > 0) {
            double amount = 0;
            if (plugin.getBank() != null) {
                KillSuite.addProfileMessage("bank.calculate.start", System.nanoTime() - n);
                // economy is enabled, so let's find the reward
                amount = plugin.getManager().getReward(monster.getName());
                if (monster.getName().equals("player")) {
                    Player dead = (Player) event.getEntity();
                    if (player_percent) {
                        double balance = plugin.getBank().getBalance(dead, -1);
                        amount = balance * (amount / 100);
                    }
                    if (player_realism) {
                        double balance = plugin.getBank().getBalance(dead, -1);
                        if (amount > balance) {
                            amount = balance;
                        }
                        plugin.getBank().take(dead, amount, -1);
                    }
                }
                amount = plugin.diminishReturn(killer, amount);
                amount = amount * mod;
                KillSuite.addProfileMessage("bank.calculate.end", System.nanoTime() - n);
                KillSuite.addProfileMessage("bank.update.start", System.nanoTime() - n);
                KillSuite.addBankTransaction(killer.getName(), amount, -1);
                KillSuite.addProfileMessage("bank.update.end", System.nanoTime() - n);
            }
            // report the earnings
            plugin.report(killer, monster, amount, pet);
        } else {
            plugin.getLogger().warning("An error occurred while incrementing the monster count for '" + killer.getName() + "'!");
            plugin.getLogger().warning(plugin.getManager().getKiller(killer.getName()).toString());
        }
        KillSuite.addProfileMessage("entity.death.end", System.nanoTime() - n);
    }

    @EventHandler
    void creatureSpawn(CreatureSpawnEvent event) {
        if (plugin.validWorld(event.getEntity().getWorld().getName()) && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            event.getEntity().setMetadata("spawner", meta);
        }
    }

}
