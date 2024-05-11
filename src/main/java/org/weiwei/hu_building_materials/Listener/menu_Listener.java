package org.weiwei.hu_building_materials.Listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import uilt.Config;

import java.util.List;

import static org.weiwei.hu_building_materials.MENU.Menu.creatinv;
import static uilt.Config.TYPE_INT_getlist;
import static uilt.Config.vip_discount;
import static uilt.log.log;
import static uilt.seed.color;
import static uilt.seed.seed;
import static uilt.uilt_all.*;

public class menu_Listener implements Listener {
    @EventHandler
    void OnOpen(InventoryOpenEvent event) {
        String BMB_GUINAME = color(Config.getConfig().getString(Config.BMB_GUINAME));
        String DB_GUINAME = color(Config.getConfig().getString(Config.DB_GUINAME));
        String OTH_GUINAME = color(Config.getConfig().getString(Config.OTH_GUINAME));
        if (event.getView().getTitle().equals(BMB_GUINAME)){ //建材
            List<String> BLOCK_list = Config.getBMB_BLOCK_List();
            Inventory inv = event.getInventory();
            setinv(BLOCK_list,inv,"TYPE_INT",false,0);
        }
        if (event.getView().getTitle().equals(DB_GUINAME)){ //染色
            List<String> BLOCK_list = Config.getDB_BLOCK_List();
            Inventory inv = event.getInventory();
            setinv(BLOCK_list,inv,"TYPE_INT",false,0);
        }
        if (event.getView().getTitle().equals(OTH_GUINAME)){ //其他
            List<String> BLOCK_list = Config.getOTH_BLOCK_List();
            Inventory inv = event.getInventory();
            setinv(BLOCK_list,inv,"TYPE_INT",false,0);
        }
    }
    @EventHandler
    void OnClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        double vip_discount = vip_discount("DEFAULT"); //vip優惠
        if (player.hasPermission("hu_building.VIP5")){vip_discount = vip_discount("VIP5");}
        if (player.hasPermission("hu_building.VIP6")){vip_discount = vip_discount("VIP6");}
        if (player.hasPermission("hu_building.VIP7")){vip_discount = vip_discount("VIP7");}
        if (player.hasPermission("hu_building.VIP8")){vip_discount = vip_discount("VIP8");}


        String BMB_GUINAME = color(Config.getConfig().getString(Config.BMB_GUINAME));
        String DB_GUINAME = color(Config.getConfig().getString(Config.DB_GUINAME));
        String OTH_GUINAME = color(Config.getConfig().getString(Config.OTH_GUINAME));
        if (event.getView().getTitle().equals(BMB_GUINAME) || event.getView().getTitle().equals(DB_GUINAME) || event.getView().getTitle().equals(OTH_GUINAME)) {
            int solt = event.getRawSlot();

            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            if (item != null) {
                NBTItem nbtItem = new NBTItem(item);
                //第二層處理GUI，顯示購買的物品
                if (event.getView().getTitle().equals(BMB_GUINAME) && nbtItem.hasTag("TYPE_INT")){
                    int TYPE_INT = nbtItem.getInteger("TYPE_INT");
                    List<String> TYPE_x = TYPE_INT_getlist("BUILDING_MATERIAL_BLOCK","TYPE_" + TYPE_INT);
                    clearinv(inv);
                    setinv(TYPE_x,inv,"biold_money",true,vip_discount);
                }
                if (event.getView().getTitle().equals(DB_GUINAME) && nbtItem.hasTag("TYPE_INT")){
                    int TYPE_INT = nbtItem.getInteger("TYPE_INT");
                    List<String> TYPE_x = TYPE_INT_getlist("DYED_BLOCK","TYPE_" + TYPE_INT);
                    clearinv(inv);
                    setinv(TYPE_x,inv,"biold_money",true,vip_discount);
                }
                if (event.getView().getTitle().equals(OTH_GUINAME) && nbtItem.hasTag("TYPE_INT")){
                    int TYPE_INT = nbtItem.getInteger("TYPE_INT");
                    List<String> TYPE_x = TYPE_INT_getlist("OTH_BLOCK","TYPE_" + TYPE_INT);
                    clearinv(inv);
                    setinv(TYPE_x,inv,"biold_money",true,vip_discount);
                }

                if (solt == 3){player.openInventory(creatinv(BMB_GUINAME));}
                if (solt == 4){player.openInventory(creatinv(DB_GUINAME));}
                if (solt == 5){player.openInventory(creatinv(OTH_GUINAME));}

                int matcoin = nbtItem.getInteger("biold_money");

                if (nbtItem.hasTag("biold_money")) { //處理購買
                    Currency currency = CoinsEngineAPI.getCurrency("BuildCoin"); // Get currency called 'coins'.
                    if (currency == null) {
                        seed(player,Config.getConfig().getString(Config.MEG_COIN_ERROR));
                        return;
                    }
                    double playerbalance = CoinsEngineAPI.getBalance(player, currency);

                    if (hasEmptySlots(player)) {
                        if(matcoin<playerbalance) { //確認玩家的coin夠多  
                            CoinsEngineAPI.removeBalance(player, currency, matcoin);
                            ItemStack giveitem = new ItemStack(item.getType());

                            for (int i = 0;i<64;i++){player.getInventory().addItem(giveitem);} //給物品

                            log(player.getName() + " 購買建材 " + giveitem.getType(),"log");
                            seed(player, Config.getConfig().getString(Config.MEG_YES_DOWN),matcoin);

                        }else {
                            seed(player, Config.getConfig().getString(Config.MEG_NO_DOWN),matcoin);
                        }
                    } else {
                        seed(player, Config.getConfig().getString(Config.MEG_INV_NOSLOT));
                    }

                }
            }
        }

    }

}
