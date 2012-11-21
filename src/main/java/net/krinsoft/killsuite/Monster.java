package net.krinsoft.killsuite;

import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author krinsdeath
 */
public enum Monster {
    BAT("bat", "Bat", "animals"),
    BLAZE("blaze", "Blaze", "others"),
    CAVE_SPIDER("cavespider", "Cave Spider", "monsters"),
    CHICKEN("chicken", "Chicken", "animals"),
    COW("cow", "Cow", "animals"),
    CREEPER("creeper", "Creeper", "monsters"),
    ENDER_DRAGON("enderdragon", "Ender Dragon", "monsters"),
    ENDERMAN("enderman", "Enderman", "monsters"),
    GHAST("ghast", "Ghast", "monsters"),
    GIANT("giant", "Giant", "monsters"),
    IRON_GOLEM("irongolem", "Iron Golem", "monsters"),
    MAGMA_CUBE("lavaslime", "Magma Cube", "others"),
    MUSHROOM_COW("mushroomcow", "Mushroom Cow", "others"),
    OCELOT("ocelot", "Ocelot", "animals"),
    PIG("pig", "Pig", "animals"),
    PIG_ZOMBIE("pigzombie", "Pig Zombie", "monsters"),
    PLAYER("player", "Player", "players"),
    SHEEP("sheep", "Sheep", "animals"),
    SILVERFISH("silverfish", "Silverfish", "monsters"),
    SKELETON("skeleton", "Skeleton", "monsters"),
    SKELETON_WITHER("witherskeleton", "Wither Skeleton", "monsters"),
    SLIME("slime", "Slime", "monsters"),
    SNOWMAN("snowman", "Snowman", "others"),
    SQUID("squid", "Squid", "animals"),
    SPIDER("spider", "Spider", "monsters"),
    VILLAGER("villager", "Villager", "others"),
    WITCH("witch", "Witch", "monsters"),
    WITHER_BOSS("witherboss", "Wither Boss", "monsters"),
    WOLF("wolf", "Wolf", "animals"),
    ZOMBIE("zombie", "Zombie", "monsters"),
    ;
    private final static Map<Object, Monster> monsters = new HashMap<Object, Monster>(Monster.values().length) {
        {
            put(EntityType.CHICKEN, CHICKEN);
            put(EntityType.COW, COW);
            put(EntityType.PIG, PIG);
            put(EntityType.SHEEP, SHEEP);
            put(EntityType.SQUID, SQUID);
            put(EntityType.WOLF, WOLF);
            put(EntityType.CAVE_SPIDER, CAVE_SPIDER);
            put(EntityType.CREEPER, CREEPER);
            put(EntityType.ENDER_DRAGON, ENDER_DRAGON);
            put(EntityType.ENDERMAN, ENDERMAN);
            put(EntityType.GHAST, GHAST);
            put(EntityType.GIANT, GIANT);
            put(EntityType.PIG_ZOMBIE, PIG_ZOMBIE);
            put(EntityType.SILVERFISH, SILVERFISH);
            put(Skeleton.SkeletonType.NORMAL, SKELETON);
            put(Skeleton.SkeletonType.WITHER, SKELETON_WITHER);
            put(EntityType.SLIME, SLIME);
            put(EntityType.SPIDER, SPIDER);
            put(EntityType.ZOMBIE, ZOMBIE);
            put(EntityType.BLAZE, BLAZE);
            put(EntityType.MAGMA_CUBE, MAGMA_CUBE);
            put(EntityType.MUSHROOM_COW, MUSHROOM_COW);
            put(EntityType.SNOWMAN, SNOWMAN);
            put(EntityType.VILLAGER, VILLAGER);
            put(EntityType.PLAYER, PLAYER);
            put(EntityType.IRON_GOLEM, IRON_GOLEM);
            put(EntityType.OCELOT, OCELOT);
            put(EntityType.BAT, BAT);
            put(EntityType.WITCH, WITCH);
            put(EntityType.WITHER, WITHER_BOSS);
        }
    };

    private final String dbname;
    private final String name;
    private final String category;
    
    Monster(String dbname, String name, String category) {
        this.dbname = dbname;
        this.name = name;
        this.category = category;
    }
    
    public String getName() { return this.dbname; }
    
    public String getFancyName() {
        ChatColor color;
        if (category.equals("animals")) {
            color = ChatColor.GREEN;
        } else if (category.equals("monsters")) {
            color = ChatColor.RED;
        } else {
            color = ChatColor.LIGHT_PURPLE;
        }
        return color + name + ChatColor.WHITE;
    }
    
    public String getCategory() { return this.category; }
    
    public static Monster getType(String name) {
        for (Monster m : Monster.values()) {
            if (m.dbname.equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }
    
    public static List<Monster> getAllInCategory(String category) {
        List<Monster> monsters = new ArrayList<Monster>();
        for (Monster m : Monster.values()) {
            if (m.getCategory().equalsIgnoreCase(category)) {
                monsters.add(m);
            }
        }
        return monsters;
    }
    
    public static Monster getType(Entity e) {
        Object type = e.getType();
        if (e instanceof Skeleton) {
            type = ((Skeleton) e).getSkeletonType();
        }
        return monsters.get(type);
    }
}
