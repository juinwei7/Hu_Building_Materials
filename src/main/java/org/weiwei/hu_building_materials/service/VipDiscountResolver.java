package org.weiwei.hu_building_materials.service;

import java.util.Map;
import java.util.List;
import java.util.function.Predicate;

public final class VipDiscountResolver {

    private VipDiscountResolver() {
    }

    public static double resolve(double defaultDiscount,
                                 Map<String, Double> permissionDiscounts,
                                 Predicate<String> hasPermission) {
        double bestDiscount = defaultDiscount;

        for (Map.Entry<String, Double> entry : permissionDiscounts.entrySet()) {
            if (hasPermission.test(entry.getKey())) {
                bestDiscount = Math.min(bestDiscount, entry.getValue());
            }
        }

        return bestDiscount;
    }

    public static Tier resolveTier(Tier defaultTier,
                                   List<Tier> tiers,
                                   Predicate<String> hasPermission) {
        Tier bestTier = defaultTier;

        for (Tier tier : tiers) {
            if (tier.permission() != null
                    && hasPermission.test(tier.permission())
                    && tier.value() < bestTier.value()) {
                bestTier = tier;
            }
        }

        return bestTier;
    }

    public record Tier(String key, String permission, String name, double value) {
    }
}
