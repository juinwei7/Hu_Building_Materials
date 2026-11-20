package org.weiwei.hu_building_materials.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.weiwei.hu_building_materials.menu.Menu;
import org.weiwei.hu_building_materials.menu.ShopInventoryHolder;
import org.weiwei.hu_building_materials.service.BuildingCoinPurchaseService;
import org.weiwei.hu_building_materials.service.CoinService;
import uilt.Config;

import static org.weiwei.hu_building_materials.menu.ShopInventoryHolder.ShopType.BUILDING;
import static org.weiwei.hu_building_materials.menu.ShopInventoryHolder.ShopType.DYED;
import static org.weiwei.hu_building_materials.menu.ShopInventoryHolder.ShopType.OTHER;
import static uilt.BuyLog.log;
import static uilt.seed.seed;
import static uilt.ItemTool.*;

public class MenuListener implements Listener {
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

        if (getIntData(item, "coin_purchase") != null) {
            handleCoinPurchase(player, inv);
            return;
        }

        ShopInventoryHolder.ShopType tab = switch (slot) {
            case 3 -> BUILDING;
            case 4 -> DYED;
            case 5 -> OTHER;
            default -> null;
        };
        if (tab != null) {
            if (tab != holder.getShopType()) {
                holder.setShopType(tab);
                holder.setCategory(Menu.firstCategory(tab));
                Menu.switchTo(player, holder);
            }
            return;
        }

        Integer typeInt = getIntData(item, "type_int");
        if (typeInt != null && slot >= Menu.CATEGORY_START && slot < Menu.CATEGORY_START + Menu.CATEGORY_SLOTS) {
            if (typeInt != holder.getCategory()) {
                holder.setCategory(typeInt);
                Menu.switchTo(player, holder);
            }
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

    private void handleCoinPurchase(Player player, Inventory inventory) {
        int price = Config.getCoinPurchasePrice();
        int amount = Config.getCoinPurchaseAmount();
        BuildingCoinPurchaseService.PurchaseResult result =
                BuildingCoinPurchaseService.purchase(player, price, amount);

        String messageKey = switch (result) {
            case SUCCESS -> "SUCCESS";
            case NOT_ENOUGH_MONEY -> "NO_MONEY";
            case ECONOMY_UNAVAILABLE -> "ECONOMY_UNAVAILABLE";
            case COIN_UNAVAILABLE -> "COIN_UNAVAILABLE";
            case COIN_LIMIT_EXCEEDED -> "COIN_LIMIT";
            case COIN_ERROR -> "COIN_ERROR";
            case REFUND_ERROR -> "REFUND_ERROR";
        };
        seed(player, Menu.formatCoinPurchaseText(Config.getCoinPurchaseMessage(messageKey)));

        if (result == BuildingCoinPurchaseService.PurchaseResult.SUCCESS) {
            log(player.getName() + " 購買 " + amount + " 建材點，花費 " + price + " 遊戲幣", "log");
            Menu.refreshInv(player, inventory);
        } else if (result == BuildingCoinPurchaseService.PurchaseResult.REFUND_ERROR) {
            log(player.getName() + " 購買建材點失敗，且遊戲幣自動退款失敗", "error");
        }
    }
}
