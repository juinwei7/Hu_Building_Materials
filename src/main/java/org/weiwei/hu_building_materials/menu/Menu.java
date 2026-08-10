package org.weiwei.hu_building_materials.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.weiwei.hu_building_materials.service.CoinService;
import org.weiwei.hu_building_materials.service.VipDiscountResolver;
import uilt.Config;
import uilt.ItemTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static uilt.ItemTool.creatmatitem;
import static uilt.ItemTool.legacyComponent;
import static uilt.ItemTool.setIntData;
import static uilt.seed.color;

public class Menu {

    public static Inventory creatInv(Player player, ShopInventoryHolder.ShopType shopType) {
        String guiName = switch (shopType) {
            case BUILDING -> Config.getConfig().getString(Config.BMB_GUINAME);
            case DYED -> Config.getConfig().getString(Config.DB_GUINAME);
            case OTHER -> Config.getConfig().getString(Config.OTH_GUINAME);
        };
        Inventory inv = new ShopInventoryHolder(shopType, legacyComponent(color(guiName))).getInventory();
        ItemStack BUILDING_item = creatmatitem(Material.ACACIA_LOG, "§f建材方塊", "mat_type", 1);
        ItemStack DYED_item = creatmatitem(Material.LIGHT_BLUE_WOOL, "§f染色方塊", "mat_type", 2);
        ItemStack OTH_item = creatmatitem(Material.OAK_SAPLING, "§f其他方塊", "mat_type", 3);
        ItemStack item_null = creatmatitem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, item_null);
        }
        inv.setItem(3, BUILDING_item);
        inv.setItem(4, DYED_item);
        inv.setItem(5, OTH_item);
        inv.setItem(0, getPlayerCoinInfoItem(player));
        setVipDiscountInfo(player, inv);
        setCoinPurchaseItem(inv);
        return inv;
    }

    public static void refreshInv(Player player, Inventory inv){
        inv.setItem(0, getPlayerCoinInfoItem(player));
        setVipDiscountInfo(player, inv);
    }

    public static void setVipDiscountInfo(Player player, Inventory inventory) {
        VipDiscountResolver.Tier appliedTier = Config.getAppliedVipTier(player);
        List<String> lore = new ArrayList<>();
        lore.add(" ");

        for (VipDiscountResolver.Tier tier : Config.getVipTiers()) {
            boolean isApplied = tier.key().equalsIgnoreCase(appliedTier.key());
            String marker = isApplied ? "§a▶ " : "§7• ";
            String salePercentage = formatPercentage(tier.value());
            String savedPercentage = formatDiscountPercentage(tier.value());
            lore.add(marker + color(tier.name())
                    + " §8│ §7售價 §f" + salePercentage + "%"
                    + " §8(§a折價 " + savedPercentage + "%§8)");
        }

        lore.add(" ");
        lore.add("§7目前套用：§f" + color(appliedTier.name()));
        ItemStack info = creatmatitem(
                Config.getVipDisplayMaterial(),
                color(Config.getVipDisplayName()),
                lore
        );
        inventory.setItem(Config.getVipDisplaySlot(), info);
    }

    public static void setCoinPurchaseItem(Inventory inventory) {
        if (!Config.isCoinPurchaseEnabled()) {
            return;
        }

        String displayName = formatCoinPurchaseText(Config.getCoinPurchaseDisplayName());
        List<String> lore = Config.getCoinPurchaseLore().stream()
                .map(Menu::formatCoinPurchaseText)
                .map(text -> color(text))
                .toList();
        ItemStack button = creatmatitem(
                Config.getCoinPurchaseMaterial(),
                color(displayName),
                lore
        );
        inventory.setItem(
                Config.getCoinPurchaseSlot(),
                setIntData(button, "coin_purchase", 1)
        );
    }

    public static String formatCoinPurchaseText(String text) {
        String price = String.format(Locale.US, "%,d", Config.getCoinPurchasePrice());
        return text
                .replace("%amount%", String.valueOf(Config.getCoinPurchaseAmount()))
                .replace("%price%", price);
    }

    private static String formatPercentage(double value) {
        return BigDecimal.valueOf(value)
                .movePointRight(2)
                .stripTrailingZeros()
                .toPlainString();
    }

    private static String formatDiscountPercentage(double priceMultiplier) {
        return BigDecimal.ONE
                .subtract(BigDecimal.valueOf(priceMultiplier))
                .movePointRight(2)
                .stripTrailingZeros()
                .toPlainString();
    }

    private static ItemStack getPlayerCoinInfoItem(Player player) {

        String coin = CoinService.getCoin(player.getUniqueId()).stripTrailingZeros().toPlainString();
        String name = "§7個人貨幣";

        ItemStack item = new ItemStack(Material.PAPER);
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§7當前建材點: " + coin);
        lore.add(" ");
        lore.add("§e(可點選本商店右上角)");
        lore.add("§e(使用遊戲幣購買建材點)");

        ItemMeta im = item.getItemMeta();
        if (im != null) {
            im.displayName(legacyComponent(name));
            im.lore(lore.stream().map(ItemTool::legacyComponent).toList());
            item.setItemMeta(im);
        }
        return item;

    }

}
