package org.weiwei.hu_building_materials.MENU;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import static uilt.uilt_all.creatmatitem;

public class Menu {

    public static Inventory creatinv(String guiname){
        Inventory inv = Bukkit.createInventory(null,54,guiname);
        ItemStack BUILDING_item = creatmatitem(Material.ACACIA_LOG,"§f建材方塊","MAT_TYPE",1);
        ItemStack DYED_item = creatmatitem(Material.LIGHT_BLUE_WOOL,"§f染色方塊","MAT_TYPE",2);
        ItemStack OTH_item = creatmatitem(Material.OAK_SAPLING,"§f其他方塊","MAT_TYPE",3);
        ItemStack item_null = creatmatitem(Material.GRAY_STAINED_GLASS_PANE," ", null);
        for (int i=0;i<9;i++) {
            if(i==3 || i==4 || i==5) {
                inv.setItem(3, BUILDING_item);
                inv.setItem(4, DYED_item);
                inv.setItem(5, OTH_item);

            }else {
                inv.setItem(i, item_null);
            }

        }
        return inv;
    }

}
