package org.weiwei.hu_building_materials.service;

import org.weiwei.coinsCore.api.CoinsApi;

import java.math.BigDecimal;
import java.util.UUID;

public class CoinService {
    /**
     * 檢查金額是否足夠
     */
    public static boolean checkValue(UUID uuid, double coin) {
        CoinsApi coinsApi = CoinsApi.of("buildingCoin").orElse(null);
        if (coinsApi == null) return false;
        BigDecimal value = coinsApi.getBalance(uuid);
        return value.compareTo(BigDecimal.valueOf(coin)) >= 0;
    }

    public static boolean takeCoin(UUID uuid ,double coin, String reason) {
        CoinsApi coinsApi = CoinsApi.of("buildingCoin").orElse(null);
        if (coinsApi == null) return false;
        return coinsApi.take(uuid, BigDecimal.valueOf(coin), "building-plugin", reason);
    }

    public static double getCoin(UUID uuid){
        CoinsApi coinsApi = CoinsApi.of("buildingCoin").orElse(null);
        if (coinsApi == null) return 0;
        return coinsApi.getBalance(uuid).doubleValue();
    }

}
