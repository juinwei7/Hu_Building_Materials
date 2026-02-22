package org.weiwei.hu_building_materials.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.weiwei.hu_building_materials.Hu_Building_Materials;
import uilt.Config;

import static org.weiwei.hu_building_materials.menu.Menu.creatInv;
import static uilt.seed.color;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if(!Hu_Building_Materials.isCoinsCore()) {
                sender.sendMessage("§cCoinsEngine is not enabled!");
                return false;
            }
            String guiName = color(Config.getConfig().getString(Config.BMB_GUINAME));
            player.openInventory(creatInv(guiName));
        }
        return false;
    }
}
