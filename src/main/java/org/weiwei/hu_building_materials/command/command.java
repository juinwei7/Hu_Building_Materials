package org.weiwei.hu_building_materials.command;

import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uilt.Config;

import static org.weiwei.hu_building_materials.MENU.Menu.creatinv;
import static uilt.seed.color;

public class command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
                    String guiname = color(Config.getConfig().getString(Config.BMB_GUINAME));
                    player.openInventory(creatinv(guiname));
        }
        return false;
    }
}
