package uilt;

import org.bukkit.entity.Player;

import java.util.List;

public class seed {

    public static String color(String s){
        String new_s = s.replaceAll("&","§");

        return new_s;
    }
    public static List<String> color(List<String> line_list){
        for (int i = 0;i<line_list.size();i++){
            String newline = line_list.get(i).replaceAll("&","§");
            line_list.set(i,newline);
        }

        return line_list;
    }

    public static void seed(Player player, String s){
        player.sendMessage(color(s));
    }
    public static void seed(Player player,String s,int matcoin){
        String PREFIX = color(Config.getConfig().getString(Config.PREFIX));
        if (s.contains("%matcoin%")) {
            s = s.replaceAll("%matcoin%", String.valueOf(matcoin));
        }
        player.sendMessage(PREFIX + color(s));
    }
}
