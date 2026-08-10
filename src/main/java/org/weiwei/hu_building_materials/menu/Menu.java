package org.weiwei.hu_building_materials.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.weiwei.hu_building_materials.service.CoinService;
import uilt.uilt_all;

import java.util.ArrayList;
import java.util.List;

import static uilt.uilt_all.creatmatitem;

public class Menu {

    public static Inventory creatInv(Player player, String guiname) {
        Inventory inv = Bukkit.createInventory(null, 54, guiname);
        ItemStack BUILDING_item = creatmatitem(Material.ACACIA_LOG, "§f建材方塊", "MAT_TYPE", 1);
        ItemStack DYED_item = creatmatitem(Material.LIGHT_BLUE_WOOL, "§f染色方塊", "MAT_TYPE", 2);
        ItemStack OTH_item = creatmatitem(Material.OAK_SAPLING, "§f其他方塊", "MAT_TYPE", 3);
        ItemStack item_null = creatmatitem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 9; i++) {
            if (i == 3 || i == 4 || i == 5) {
                inv.setItem(3, BUILDING_item);
                inv.setItem(4, DYED_item);
                inv.setItem(5, OTH_item);

            } else {
                inv.setItem(i, item_null);
            }
        }
        inv.setItem(0, getPlayerCoinInfoItem(player));
        return inv;
    }

    public static void refreshInv(Player player, Inventory inv){
        inv.setItem(0, getPlayerCoinInfoItem(player));
    }

    private static ItemStack getPlayerCoinInfoItem(Player player) {

        double coin = CoinService.getCoin(player.getUniqueId());
        String name = "§7個人貨幣";

        ItemStack item = new ItemStack(Material.PAPER);
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§7當前建材點: " + coin);
        lore.add(" ");
        lore.add("§e(購買建材點數請到選單商城)");
        lore.add("§e(第一排第四個 -> 商城系統)");

        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        if (im != null) {
            im.setLore(lore);
            item.setItemMeta(im);
        }
        return item;

    }

}
