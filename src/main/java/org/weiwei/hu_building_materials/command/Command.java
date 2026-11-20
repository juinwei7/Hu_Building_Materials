package org.weiwei.hu_building_materials.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.weiwei.hu_building_materials.menu.Menu;
import uilt.Config;

import static org.weiwei.hu_building_materials.menu.ShopInventoryHolder.ShopType.BUILDING;
import static uilt.seed.color;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (Config.isUsePermissionRequired() && !player.hasPermission(Config.getUsePermission())) {
                sender.sendMessage(color(Config.getConfig().getString(Config.MEG_NO_PERMISSION)));
                return true;
            }
            Menu.open(player, BUILDING);
        }
        return true;
    }
}
