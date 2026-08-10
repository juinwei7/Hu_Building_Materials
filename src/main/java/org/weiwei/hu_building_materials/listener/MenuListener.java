package org.weiwei.hu_building_materials.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.weiwei.hu_building_materials.menu.Menu;
import org.weiwei.hu_building_materials.menu.ShopInventoryHolder;
import org.weiwei.hu_building_materials.service.CoinService;
import uilt.Config;

import static org.weiwei.hu_building_materials.menu.Menu.creatInv;
import static org.weiwei.hu_building_materials.menu.ShopInventoryHolder.ShopType.BUILDING;
import static org.weiwei.hu_building_materials.menu.ShopInventoryHolder.ShopType.DYED;
import static org.weiwei.hu_building_materials.menu.ShopInventoryHolder.ShopType.OTHER;
import static uilt.Config.TYPE_INT_getlist;
import static uilt.Config.getVipDiscount;
import static uilt.BuyLog.log;
import static uilt.seed.seed;
import static uilt.ItemTool.*;

public class MenuListener implements Listener {
    @EventHandler
    void onOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof ShopInventoryHolder holder)) {
            return;
        }

        var categories = switch (holder.getShopType()) {
            case BUILDING -> Config.getBMB_BLOCK_List();
            case DYED -> Config.getDB_BLOCK_List();
            case OTHER -> Config.getOTH_BLOCK_List();
        };
        setinv(categories, event.getInventory(), "type_int", false, 0);
    }

    @EventHandler
    void onClick(InventoryClickEvent event) {
        Inventory inv = event.getView().getTopInventory();
        if (!(inv.getHolder() instanceof ShopInventoryHolder holder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= inv.getSize()) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }

        if (slot == 3) {
            player.openInventory(creatInv(player, BUILDING));
            return;
        }
        if (slot == 4) {
            player.openInventory(creatInv(player, DYED));
            return;
        }
        if (slot == 5) {
            player.openInventory(creatInv(player, OTHER));
            return;
        }

        Integer typeInt = getIntData(item, "type_int");
        if (typeInt != null) {
            double vipDiscount = getVipDiscount(player);
            String configSection = switch (holder.getShopType()) {
                case BUILDING -> "BUILDING_MATERIAL_BLOCK";
                case DYED -> "DYED_BLOCK";
                case OTHER -> "OTH_BLOCK";
            };
            var products = TYPE_INT_getlist(configSection, "TYPE_" + typeInt);
            clearinv(inv);
            setinv(products, inv, "build_money", true, vipDiscount);
            return;
        }

        Integer matcoinData = getIntData(item, "build_money");
        if (matcoinData == null) {
            return;
        }

        int matcoin = matcoinData;
        ItemStack giveitem = new ItemStack(item.getType(), 64);

        if (!canFitItem(player, giveitem)) {
            seed(player, Config.getConfig().getString(Config.MEG_INV_NOSLOT));
            return;
        }

        if (!CoinService.checkValue(player.getUniqueId(), matcoin)) {
            seed(player, Config.getConfig().getString(Config.MEG_NO_DOWN), matcoin);
            return;
        }

        boolean taken = CoinService.takeCoin(player.getUniqueId(), matcoin, "購買建材 " + item.getType());
        if (!taken) {
            seed(player, Config.getConfig().getString(Config.MEG_COIN_ERROR), matcoin);
            return;
        }

        // 預檢與發放都在主執行緒完成；若仍有意外剩餘，改為掉落以避免玩家已扣款卻遺失物品
        player.getInventory().addItem(giveitem).values().forEach(
                leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover)
        );

        log(player.getName() + " 購買建材 " + giveitem.getType(), "log");
        seed(player, Config.getConfig().getString(Config.MEG_YES_DOWN), matcoin);
        Menu.refreshInv(player, inv);
    }
}
