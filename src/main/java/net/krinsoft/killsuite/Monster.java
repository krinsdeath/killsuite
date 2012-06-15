package net.krinsoft.killsuite;

import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author krinsdeath
 */
public enum Monster {
    CHICKEN("chicken", "Chicken", "animals"),
    COW("cow", "Cow", "animals"),
    PIG("pig", "Pig", "animals"),
    SHEEP("sheep", "Sheep", "animals"),
    SQUID("squid", "Squid", "animals"),
    WOLF("wolf", "Wolf", "animals"),
    CAVE_SPIDER("cavespider", "Cave Spider", "monsters"),
    CREEPER("creeper", "Creeper", "monsters"),
    ENDER_DRAGON("enderdragon", "Ender Dragon", "monsters"),
    ENDERMAN("enderman", "Enderman", "monsters"),
    GHAST("ghast", "Ghast", "monsters"),
    GIANT("giant", "Giant", "monsters"),
    PIG_ZOMBIE("pigzombie", "Pig Zombie", "monsters"),
    SILVERFISH("silverfish", "Silverfish", "monsters"),
    SKELETON("skeleton", "Skeleton Archer", "monsters"),
    SLIME("slime", "Slime", "monsters"),
    SPIDER("spider", "Spider", "monsters"),
    ZOMBIE("zombie", "Zombie", "monsters"),
    BLAZE("blaze", "Blaze", "others"),
    MAGMA_CUBE("lavaslime", "Magma Cube", "others"),
    MUSHROOM_COW("mushroomcow", "Mushroom Cow", "others"),
    SNOWMAN("snowman", "Snowman", "others"),
    VILLAGER("villager", "Villager", "others"),
    PLAYER("player", "Player", "players"),
    IRON_GOLEM("irongolem", "Iron Golem", "monsters"),
    //SNOW_GOLEM("snowgolem", "Snow Golem", "monsters"),
    OCELOT("ocelot", "Ocelot", "animals"),
    ;

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
        if (e instanceof Chicken) { return CHICKEN; }
        if (e instanceof Cow) {
            if (e instanceof MushroomCow) { return MUSHROOM_COW; }
            return COW;
        }
        if (e instanceof Pig) { return PIG; }
        if (e instanceof Sheep) { return SHEEP; }
        if (e instanceof Squid) { return SQUID; }
        if (e instanceof Wolf) { return WOLF; }
        if (e instanceof Spider) {
            if (e instanceof CaveSpider) { return CAVE_SPIDER; }
            return SPIDER;
        }
        if (e instanceof Creeper) { return CREEPER; }
        if (e instanceof EnderDragon) { return ENDER_DRAGON; }
        if (e instanceof Enderman) { return ENDERMAN; }
        if (e instanceof Ghast) { return GHAST; }
        if (e instanceof Giant) { return GIANT; }
        if (e instanceof Zombie) {
            if (e instanceof PigZombie) { return PIG_ZOMBIE; }
            return ZOMBIE;
        }
        if (e instanceof Silverfish) { return SILVERFISH; }
        if (e instanceof Skeleton) { return SKELETON; }
        if (e instanceof Slime) {
            if (e instanceof MagmaCube) { return MAGMA_CUBE; }
            return SLIME;
        }
        if (e instanceof Blaze) { return BLAZE; }
        if (e instanceof Snowman) { return SNOWMAN; }
        if (e instanceof Villager) { return VILLAGER; }
        if (e instanceof Player) { return PLAYER; }
        // 1.2 creatures
        if (e instanceof Golem) {
            if (e instanceof IronGolem) { return IRON_GOLEM; }
            //if (e instanceof SnowGolem) { return SNOW_GOLEM; }
            //return GOLEM;
        }
        if (e instanceof Ocelot) { return OCELOT; }
        return null;
    }
}
