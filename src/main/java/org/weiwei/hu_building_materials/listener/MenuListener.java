package org.weiwei.hu_building_materials.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.weiwei.hu_building_materials.menu.Menu;
import org.weiwei.hu_building_materials.service.CoinService;
import uilt.Config;

import java.util.List;

import static org.weiwei.hu_building_materials.menu.Menu.creatInv;
import static uilt.Config.TYPE_INT_getlist;
import static uilt.Config.vip_discount;
import static uilt.log.log;
import static uilt.seed.color;
import static uilt.seed.seed;
import static uilt.uilt_all.*;

public class MenuListener implements Listener {
    @EventHandler
    void onOpen(InventoryOpenEvent event) {
        String BMB_GUINAME = color(Config.getConfig().getString(Config.BMB_GUINAME));
        String DB_GUINAME = color(Config.getConfig().getString(Config.DB_GUINAME));
        String OTH_GUINAME = color(Config.getConfig().getString(Config.OTH_GUINAME));
        if (event.getView().getTitle().equals(BMB_GUINAME)) { //建材
            List<String> BLOCK_list = Config.getBMB_BLOCK_List();
            Inventory inv = event.getInventory();
            setinv(BLOCK_list, inv, "TYPE_INT", false, 0);
        }
        if (event.getView().getTitle().equals(DB_GUINAME)) { //染色
            List<String> BLOCK_list = Config.getDB_BLOCK_List();
            Inventory inv = event.getInventory();
            setinv(BLOCK_list, inv, "TYPE_INT", false, 0);
        }
        if (event.getView().getTitle().equals(OTH_GUINAME)) { //其他
            List<String> BLOCK_list = Config.getOTH_BLOCK_List();
            Inventory inv = event.getInventory();
            setinv(BLOCK_list, inv, "TYPE_INT", false, 0);
        }
    }

    @EventHandler
    void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        double vip_discount = vip_discount("DEFAULT"); //vip優惠
        if (player.hasPermission("hu_building.VIP5")) {
            vip_discount = vip_discount("VIP5");
        }
        if (player.hasPermission("hu_building.VIP6")) {
            vip_discount = vip_discount("VIP6");
        }
        if (player.hasPermission("hu_building.VIP7")) {
            vip_discount = vip_discount("VIP7");
        }
        if (player.hasPermission("hu_building.VIP8")) {
            vip_discount = vip_discount("VIP8");
        }


        String BMB_GUINAME = color(Config.getConfig().getString(Config.BMB_GUINAME));
        String DB_GUINAME = color(Config.getConfig().getString(Config.DB_GUINAME));
        String OTH_GUINAME = color(Config.getConfig().getString(Config.OTH_GUINAME));
        if (event.getView().getTitle().equals(BMB_GUINAME) || event.getView().getTitle().equals(DB_GUINAME) || event.getView().getTitle().equals(OTH_GUINAME)) {
            int slot = event.getRawSlot();

            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            if (item != null) {
                NBTItem nbtItem = new NBTItem(item);
                //第二層處理GUI，顯示購買的物品
                if (event.getView().getTitle().equals(BMB_GUINAME) && nbtItem.hasTag("TYPE_INT")) {
                    int TYPE_INT = nbtItem.getInteger("TYPE_INT");
                    List<String> TYPE_x = TYPE_INT_getlist("BUILDING_MATERIAL_BLOCK", "TYPE_" + TYPE_INT);
                    clearinv(inv);
                    setinv(TYPE_x, inv, "biold_money", true, vip_discount);
                }
                if (event.getView().getTitle().equals(DB_GUINAME) && nbtItem.hasTag("TYPE_INT")) {
                    int TYPE_INT = nbtItem.getInteger("TYPE_INT");
                    List<String> TYPE_x = TYPE_INT_getlist("DYED_BLOCK", "TYPE_" + TYPE_INT);
                    clearinv(inv);
                    setinv(TYPE_x, inv, "biold_money", true, vip_discount);
                }
                if (event.getView().getTitle().equals(OTH_GUINAME) && nbtItem.hasTag("TYPE_INT")) {
                    int TYPE_INT = nbtItem.getInteger("TYPE_INT");
                    List<String> TYPE_x = TYPE_INT_getlist("OTH_BLOCK", "TYPE_" + TYPE_INT);
                    clearinv(inv);
                    setinv(TYPE_x, inv, "biold_money", true, vip_discount);
                }

                // 建材方塊
                if (slot == 3) {
                    player.openInventory(creatInv(player, BMB_GUINAME));
                }
                // 染色方塊
                if (slot == 4) {
                    player.openInventory(creatInv(player, DB_GUINAME));
                }
                // 其他
                if (slot == 5) {
                    player.openInventory(creatInv(player, OTH_GUINAME));
                }

                if (!nbtItem.hasTag("biold_money")) return;

                int matcoin = nbtItem.getInteger("biold_money");

                if (!hasEmptySlots(player)) {
                    seed(player, Config.getConfig().getString(Config.MEG_INV_NOSLOT));
                    return;
                }

                boolean checkValue = CoinService.checkValue(player.getUniqueId(), matcoin);

                if (!checkValue) {
                    seed(player, Config.getConfig().getString(Config.MEG_NO_DOWN), matcoin);
                    return;
                }

                boolean taken = CoinService.takeCoin(player.getUniqueId(), matcoin, "購買建材 " + item.getType());

                if (!taken) {
                    seed(player, Config.getConfig().getString(Config.MEG_COIN_ERROR), matcoin);
                    return;
                }

                ItemStack giveitem = new ItemStack(item.getType(), 64);
                player.getInventory().addItem(giveitem); //給物品

                log(player.getName() + " 購買建材 " + giveitem.getType(), "log");
                seed(player, Config.getConfig().getString(Config.MEG_YES_DOWN), matcoin);

                Menu.refreshInv(player, inv);
            }
        }

    }

}
