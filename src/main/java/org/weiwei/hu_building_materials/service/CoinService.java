package org.weiwei.hu_building_materials.service;

import org.weiwei.coinsCore.api.CoinsApi;

import java.math.BigDecimal;
import java.util.UUID;

public class CoinService {
    /**
     * 檢查金額是否足夠
     */
    public static boolean checkValue(UUID uuid, int coin) {
        CoinsApi coinsApi = CoinsApi.of("buildingCoin").orElse(null);
        if (coinsApi == null) return false;
        BigDecimal value = coinsApi.getBalance(uuid);
        return value.compareTo(BigDecimal.valueOf(coin)) >= 0;
    }

    public static boolean takeCoin(UUID uuid, int coin, String reason) {
        CoinsApi coinsApi = CoinsApi.of("buildingCoin").orElse(null);
        if (coinsApi == null) return false;
        return coinsApi.take(uuid, BigDecimal.valueOf(coin), "building-plugin", reason);
    }

    public static boolean giveCoin(UUID uuid, int coin, String reason) {
        CoinsApi coinsApi = CoinsApi.of("buildingCoin").orElse(null);
        if (coinsApi == null) return false;
        return coinsApi.give(uuid, BigDecimal.valueOf(coin), "building-plugin", reason);
    }

    public static CoinReceiveStatus canReceiveCoin(UUID uuid, int coin) {
        CoinsApi coinsApi = CoinsApi.of("buildingCoin").orElse(null);
        if (coinsApi == null) return CoinReceiveStatus.API_UNAVAILABLE;

        BigDecimal balance = coinsApi.getBalance(uuid);
        BigDecimal maxValue = coinsApi.getMaxValue();
        if (balance == null || maxValue == null) return CoinReceiveStatus.API_UNAVAILABLE;

        return isWithinMaxValue(balance, coin, maxValue)
                ? CoinReceiveStatus.ALLOWED
                : CoinReceiveStatus.LIMIT_EXCEEDED;
    }

    static boolean isWithinMaxValue(BigDecimal balance, int coin, BigDecimal maxValue) {
        return balance.add(BigDecimal.valueOf(coin)).compareTo(maxValue) <= 0;
    }

    public static BigDecimal getCoin(UUID uuid){
        CoinsApi coinsApi = CoinsApi.of("buildingCoin").orElse(null);
        if (coinsApi == null) return BigDecimal.ZERO;
        return coinsApi.getBalance(uuid);
    }

    public enum CoinReceiveStatus {
        ALLOWED,
        LIMIT_EXCEEDED,
        API_UNAVAILABLE
    }

}
