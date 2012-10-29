package net.krinsoft.killsuite;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author krinsdeath
 */
public class FancyMessage {
    private final String header;
    private final List<String> lines = new ArrayList<String>();
    
    public FancyMessage(Killer killer, String category) {
        List<Monster> monsters = Monster.getAllInCategory(category);
        category = parseCategory(category);
        header = ChatColor.WHITE + "=== Category: " + category + " ===";
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            String line = m.getFancyName() + ": " + killer.get(m.getName());
            while (line.length() <= 60 && i < monsters.size()) {
                i++;
                if (i >= monsters.size()) { break; }
                m = monsters.get(i);
                line = line + " / " + m.getFancyName() + ": " + killer.get(m.getName()); 
            }
            lines.add(line);
        }
    }

    public String getHeader() {
        return header;
    }

    public List<String> getLines() {
        return lines;
    }
    
    private String parseCategory(String category) {
        ChatColor color;
        if (category.equals("animals")) {
            color = ChatColor.GREEN;
        } else if (category.equals("monsters")) {
            color = ChatColor.RED;
        } else {
            color = ChatColor.LIGHT_PURPLE;
        }
        return color + category + ChatColor.WHITE;
    }
    
}
