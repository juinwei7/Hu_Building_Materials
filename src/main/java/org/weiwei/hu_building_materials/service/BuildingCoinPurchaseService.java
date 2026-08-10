package org.weiwei.hu_building_materials.service;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class BuildingCoinPurchaseService {

    private BuildingCoinPurchaseService() {
    }

    public static PurchaseResult purchase(Player player, int price, int amount) {
        CoinService.CoinReceiveStatus receiveStatus =
                CoinService.canReceiveCoin(player.getUniqueId(), amount);
        if (receiveStatus == CoinService.CoinReceiveStatus.API_UNAVAILABLE) {
            return PurchaseResult.COIN_UNAVAILABLE;
        }
        if (receiveStatus == CoinService.CoinReceiveStatus.LIMIT_EXCEEDED) {
            return PurchaseResult.COIN_LIMIT_EXCEEDED;
        }

        RegisteredServiceProvider<Economy> registration =
                Bukkit.getServicesManager().getRegistration(Economy.class);
        if (registration == null || registration.getProvider() == null) {
            return PurchaseResult.ECONOMY_UNAVAILABLE;
        }

        Economy economy = registration.getProvider();
        if (!economy.has(player, price)) {
            return PurchaseResult.NOT_ENOUGH_MONEY;
        }

        EconomyResponse withdrawal = economy.withdrawPlayer(player, price);
        if (!withdrawal.transactionSuccess()) {
            return PurchaseResult.ECONOMY_UNAVAILABLE;
        }

        boolean given = CoinService.giveCoin(
                player.getUniqueId(),
                amount,
                "購買建材點，花費遊戲幣 " + price
        );
        if (given) {
            return PurchaseResult.SUCCESS;
        }

        EconomyResponse refund = economy.depositPlayer(player, price);
        return refund.transactionSuccess()
                ? PurchaseResult.COIN_ERROR
                : PurchaseResult.REFUND_ERROR;
    }

    public enum PurchaseResult {
        SUCCESS,
        NOT_ENOUGH_MONEY,
        ECONOMY_UNAVAILABLE,
        COIN_UNAVAILABLE,
        COIN_LIMIT_EXCEEDED,
        COIN_ERROR,
        REFUND_ERROR
    }
}
