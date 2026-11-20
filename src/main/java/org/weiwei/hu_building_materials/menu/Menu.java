package org.weiwei.hu_building_materials.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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

/**
 * 版面（2026/11 改版）：第 0 排建材點、折扣、三個大類（3～5）、買建材點；第 1 排常駐分類列；第 2～5 排商品 36 格。
 * 背景與目前位置的金色 ▼ 是材質包字元，寫在標題裡：背景 U+E0F0（200×158、最右欄 187），▼ 第 0 排 U+E0F1、第 1 排 U+E0F2。
 */
public class Menu {

    public static final int CATEGORY_START = 9;
    public static final int CATEGORY_SLOTS = 9;
    public static final int PRODUCT_START = 18;
    public static final int PRODUCT_SLOTS = 36;

    private static final String BG = "";
    private static final String MARK_TOP = "";
    private static final String MARK_CATEGORY = "";
    private static final int BG_ADVANCE = 189;   // 最右不透明欄 187 + 2
    private static final int MARK_ADVANCE = 6;   // ▼ 寬 5 + 1
    private static final String TITLE_COLOR = "§x§F§3§E§2§B§0";
    private static final int[] SPACE_WIDTHS = {160, 80, 40, 20, 16, 8, 4, 1};
    private static final String[] SPACE_CHARS = {"", "", "", "", "", "", "", ""};

    public record Category(Material material, String name, int type) {
    }

    /** 開商店：預設停在該大類的第一個分類，一打開就看得到商品 */
    public static void open(Player player, ShopInventoryHolder.ShopType shopType) {
        int category = firstCategory(shopType);
        ShopInventoryHolder holder = new ShopInventoryHolder(shopType, category, legacyComponent(title(shopType, category)));
        render(player, holder);
        player.openInventory(holder.getInventory());
    }

    /** 換大類或分類之後重畫整個介面並更新標題（標題裡有背景和 ▼ 的位置） */
    public static void switchTo(Player player, ShopInventoryHolder holder) {
        render(player, holder);
        InventoryView view = player.getOpenInventory();
        if (view.getTopInventory().getHolder() == holder) {
            view.setTitle(title(holder.getShopType(), holder.getCategory()));
        }
    }

    public static void render(Player player, ShopInventoryHolder holder) {
        Inventory inv = holder.getInventory();
        inv.clear();
        ShopInventoryHolder.ShopType current = holder.getShopType();

        inv.setItem(0, getPlayerCoinInfoItem(player));
        setVipDiscountInfo(player, inv);
        inv.setItem(3, tabItem(Material.ACACIA_LOG, "建材方塊", 1, current == ShopInventoryHolder.ShopType.BUILDING));
        inv.setItem(4, tabItem(Material.LIGHT_BLUE_WOOL, "染色方塊", 2, current == ShopInventoryHolder.ShopType.DYED));
        inv.setItem(5, tabItem(Material.OAK_SAPLING, "其他方塊", 3, current == ShopInventoryHolder.ShopType.OTHER));
        setCoinPurchaseItem(inv);

        List<Category> categories = categories(current);
        for (int i = 0; i < categories.size() && i < CATEGORY_SLOTS; i++) {
            Category category = categories.get(i);
            boolean selected = category.type() == holder.getCategory();
            ItemStack item = creatmatitem(category.material(), "§e" + category.name(), "type_int", category.type());
            setLore(item, List.of(" ", selected ? "§a目前這一類" : "§e左鍵 §f看這一類"));
            inv.setItem(CATEGORY_START + i, item);
        }

        double vipDiscount = Config.getVipDiscount(player);
        int slot = PRODUCT_START;
        for (String line : Config.TYPE_INT_getlist(section(current), "TYPE_" + holder.getCategory())) {
            if (slot >= PRODUCT_START + PRODUCT_SLOTS) break;
            if (line.equalsIgnoreCase("NULL")) continue;
            String[] parts = line.split(",", -1);
            Material material = Material.valueOf(parts[0].trim().toUpperCase(Locale.ROOT));
            inv.setItem(slot++, creatmatitem(material, "§f" + parts[1], Integer.parseInt(parts[2].trim()), vipDiscount));
        }
    }

    /** 這個大類有商品的分類（全是 NULL 的「未開放」分類不列） */
    public static List<Category> categories(ShopInventoryHolder.ShopType shopType) {
        List<String> blockList = switch (shopType) {
            case BUILDING -> Config.getBMB_BLOCK_List();
            case DYED -> Config.getDB_BLOCK_List();
            case OTHER -> Config.getOTH_BLOCK_List();
        };
        List<Category> categories = new ArrayList<>();
        for (String line : blockList) {
            if (line.equalsIgnoreCase("NULL")) continue;
            String[] parts = line.split(",", -1);
            int type = Integer.parseInt(parts[2].trim());
            boolean hasProduct = Config.TYPE_INT_getlist(section(shopType), "TYPE_" + type).stream()
                    .anyMatch(product -> !product.equalsIgnoreCase("NULL"));
            if (!hasProduct) continue;
            categories.add(new Category(Material.valueOf(parts[0].trim().toUpperCase(Locale.ROOT)), parts[1].trim(), type));
        }
        return categories;
    }

    public static int firstCategory(ShopInventoryHolder.ShopType shopType) {
        List<Category> categories = categories(shopType);
        return categories.isEmpty() ? 0 : categories.get(0).type();
    }

    public static String section(ShopInventoryHolder.ShopType shopType) {
        return switch (shopType) {
            case BUILDING -> "BUILDING_MATERIAL_BLOCK";
            case DYED -> "DYED_BLOCK";
            case OTHER -> "OTH_BLOCK";
        };
    }

    /**
     * 標題：「往左 20＋背景＋」把筆往回移到目前大類、目前分類的格子正上方各畫一個 ▼（由右往左畫，只需要負間距），
     * 最後回到原版標題位置寫「建材商店 › 分類」。位置都相對原版標題起點（介面 x=8）。
     */
    public static String title(ShopInventoryHolder.ShopType shopType, int categoryType) {
        int tabColumn = switch (shopType) {
            case BUILDING -> 3;
            case DYED -> 4;
            case OTHER -> 5;
        };
        List<Category> categories = categories(shopType);
        int categoryIndex = 0;
        String categoryName = "";
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).type() == categoryType) {
                categoryIndex = i;
                categoryName = categories.get(i).name();
            }
        }

        StringBuilder title = new StringBuilder("§f").append(space(20)).append(BG);
        int pen = BG_ADVANCE - 20;
        int tabX = 6 + 18 * tabColumn;
        int categoryX = 6 + 18 * categoryIndex;
        String[][] marks = tabX >= categoryX
                ? new String[][]{{String.valueOf(tabX), MARK_TOP}, {String.valueOf(categoryX), MARK_CATEGORY}}
                : new String[][]{{String.valueOf(categoryX), MARK_CATEGORY}, {String.valueOf(tabX), MARK_TOP}};
        for (String[] mark : marks) {
            int x = Integer.parseInt(mark[0]);
            title.append(space(pen - x)).append(mark[1]);
            pen = x + MARK_ADVANCE;
        }
        title.append(space(pen));
        title.append(TITLE_COLOR).append("建材商店 §7› ").append(TITLE_COLOR).append(categoryName);
        return title.toString();
    }

    /** 往左移 n 格的負間距字元組合 */
    private static String space(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SPACE_WIDTHS.length && n > 0; i++) {
            while (n >= SPACE_WIDTHS[i]) {
                sb.append(SPACE_CHARS[i]);
                n -= SPACE_WIDTHS[i];
            }
        }
        return sb.toString();
    }

    private static ItemStack tabItem(Material material, String name, int type, boolean selected) {
        ItemStack item = creatmatitem(material, "§f" + name, "mat_type", type);
        setLore(item, List.of(" ", selected ? "§a目前在這裡" : "§e左鍵 §f切換"));
        return item;
    }

    private static void setLore(ItemStack item, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.lore(lore.stream().map(ItemTool::legacyComponent).toList());
        item.setItemMeta(meta);
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
