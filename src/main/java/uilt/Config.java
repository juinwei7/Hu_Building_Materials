package uilt;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.weiwei.hu_building_materials.Hu_Building_Materials;
import org.weiwei.hu_building_materials.service.VipDiscountResolver;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Config {

    public static final String PREFIX = "PREFIX";

    public static final String PERMISSIONS_USE = "PERMISSIONS.USE";
    public static final String PERMISSIONS_REQUIRE_USE = "PERMISSIONS.REQUIRE_USE_PERMISSION";

    public static final String ITEM_LORE = "ITEM_LORE";

    public static final String COIN_PURCHASE = "BUILDING_COIN_PURCHASE";

    public static final String BMB_GUINAME = "BUILDING_MATERIAL_BLOCK.GUINAME";
    public static final String BMB_BLOCK = "BUILDING_MATERIAL_BLOCK.BLOCK";

    public static final String DB_GUINAME = "DYED_BLOCK.GUINAME";
    public static final String DB_BLOCK = "DYED_BLOCK.BLOCK";

    public static final String OTH_GUINAME = "OTH_BLOCK.GUINAME";
    public static final String OTH_BLOCK = "OTH_BLOCK.BLOCK";


    public static final String MEG_YES_DOWN = "MESSAGE.YES_DOWN";
    public static final String MEG_NO_DOWN = "MESSAGE.NO_DOWN";
    public static final String MEG_INV_NOSLOT = "MESSAGE.INV_NOSLOT";
    public static final String MEG_COIN_ERROR = "MESSAGE.COIN_ERROR";
    public static final String MEG_NO_PERMISSION = "MESSAGE.NO_PERMISSION";


    @Getter
    private static YamlConfiguration config;

    @Getter
    private static List<String> ITEM_LORE_List;

    @Getter
    private static List<String> BMB_BLOCK_List;
    @Getter
    private static List<String> DB_BLOCK_List;
    @Getter
    private static List<String> OTH_BLOCK_List;

    public static boolean loadConfig() {
        File file = new File(Hu_Building_Materials.getInstance().getDataFolder(), "Config.yml");
        if (!file.exists()) {
            Hu_Building_Materials.getInstance().getLogger().info("Create Config.yml");
            Hu_Building_Materials.getInstance().saveResource("Config.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(file);
        applyMissingDefaults();

        BMB_BLOCK_List = config.getStringList(BMB_BLOCK);
        DB_BLOCK_List = config.getStringList(DB_BLOCK);
        OTH_BLOCK_List = config.getStringList(OTH_BLOCK);
        ITEM_LORE_List = config.getStringList(ITEM_LORE);

        return validateConfig();
    }

    public static List<String> TYPE_INT_getlist(String MAT_TYPE,String TYPE_INT){
        List<String> newlist;
        newlist = config.getStringList(MAT_TYPE + "." + TYPE_INT);
        return newlist;
    }
    public static double getVipDiscount(Player player) {
        ConfigurationSection vipSection = config.getConfigurationSection("PERMISSIONS.VIP");
        if (vipSection == null) {
            return 1.0;
        }

        double defaultDiscount = vipSection.getDouble("DEFAULT", 1.0);
        Map<String, Double> permissionDiscounts = new LinkedHashMap<>();

        for (String vip : vipSection.getKeys(false)) {
            if (vip.equalsIgnoreCase("DEFAULT")) {
                continue;
            }

            permissionDiscounts.put("hu_building." + vip, vipSection.getDouble(vip, defaultDiscount));
        }

        return VipDiscountResolver.resolve(defaultDiscount, permissionDiscounts, player::hasPermission);
    }

    public static boolean isUsePermissionRequired() {
        return config.getBoolean(PERMISSIONS_REQUIRE_USE, false);
    }

    public static String getUsePermission() {
        return config.getString(PERMISSIONS_USE, "hu_building.shop");
    }

    public static boolean isCoinPurchaseEnabled() {
        return config.getBoolean(COIN_PURCHASE + ".ENABLED", false);
    }

    public static int getCoinPurchaseSlot() {
        return config.getInt(COIN_PURCHASE + ".SLOT", 48);
    }

    public static Material getCoinPurchaseMaterial() {
        String material = config.getString(COIN_PURCHASE + ".MATERIAL", "PAPER");
        return Material.valueOf(material.trim().toUpperCase(Locale.ROOT));
    }

    public static int getCoinPurchaseAmount() {
        return config.getInt(COIN_PURCHASE + ".AMOUNT", 100);
    }

    public static int getCoinPurchasePrice() {
        return config.getInt(COIN_PURCHASE + ".PRICE", 1850);
    }

    public static String getCoinPurchaseDisplayName() {
        return config.getString(COIN_PURCHASE + ".DISPLAY_NAME", "&6⎋建材點 x 100");
    }

    public static List<String> getCoinPurchaseLore() {
        return config.getStringList(COIN_PURCHASE + ".LORE");
    }

    public static String getCoinPurchaseMessage(String key) {
        return config.getString(COIN_PURCHASE + ".MESSAGE." + key, "&c建材點交易失敗");
    }

    private static boolean validateConfig() {
        List<String> errors = new ArrayList<>();

        validateRequiredString(PREFIX, errors);
        validateRequiredString(BMB_GUINAME, errors);
        validateRequiredString(DB_GUINAME, errors);
        validateRequiredString(OTH_GUINAME, errors);
        validateRequiredString(MEG_YES_DOWN, errors);
        validateRequiredString(MEG_NO_DOWN, errors);
        validateRequiredString(MEG_INV_NOSLOT, errors);
        validateRequiredString(MEG_COIN_ERROR, errors);

        if (isUsePermissionRequired()) {
            validateRequiredString(PERMISSIONS_USE, errors);
            validateRequiredString(MEG_NO_PERMISSION, errors);
        }

        validateVipDiscounts(errors);
        validateCoinPurchase(errors);
        validateShopSection("BUILDING_MATERIAL_BLOCK", errors);
        validateShopSection("DYED_BLOCK", errors);
        validateShopSection("OTH_BLOCK", errors);

        if (!errors.isEmpty()) {
            Hu_Building_Materials plugin = Hu_Building_Materials.getInstance();
            plugin.getLogger().severe("Config.yml 驗證失敗，插件將停用：");
            errors.forEach(error -> plugin.getLogger().severe("- " + error));
            return false;
        }

        return true;
    }

    private static void validateRequiredString(String path, List<String> errors) {
        String value = config.getString(path);
        if (value == null || value.isBlank()) {
            errors.add(path + " 不可為空");
        }
    }

    private static void validateVipDiscounts(List<String> errors) {
        ConfigurationSection vipSection = config.getConfigurationSection("PERMISSIONS.VIP");
        if (vipSection == null || !vipSection.contains("DEFAULT")) {
            errors.add("PERMISSIONS.VIP.DEFAULT 未設定");
            return;
        }

        for (String vip : vipSection.getKeys(false)) {
            Object rawValue = vipSection.get(vip);
            if (!(rawValue instanceof Number number)) {
                errors.add("PERMISSIONS.VIP." + vip + " 必須是數字");
                continue;
            }

            double discount = number.doubleValue();
            if (discount <= 0.0 || discount > 1.0) {
                errors.add("PERMISSIONS.VIP." + vip + " 必須大於 0 且小於或等於 1");
            }
        }
    }

    private static void validateCoinPurchase(List<String> errors) {
        if (!isCoinPurchaseEnabled()) {
            return;
        }

        int slot = getCoinPurchaseSlot();
        if (slot < 9 || slot > 53) {
            errors.add(COIN_PURCHASE + ".SLOT 必須介於 9 到 53");
        }

        String materialName = config.getString(COIN_PURCHASE + ".MATERIAL");
        if (materialName == null || Material.getMaterial(materialName.trim().toUpperCase(Locale.ROOT)) == null) {
            errors.add(COIN_PURCHASE + ".MATERIAL 無效：" + materialName);
        }

        validatePositiveInteger(COIN_PURCHASE + ".AMOUNT", errors);
        validatePositiveInteger(COIN_PURCHASE + ".PRICE", errors);
        validateRequiredString(COIN_PURCHASE + ".DISPLAY_NAME", errors);
        validateRequiredString(COIN_PURCHASE + ".MESSAGE.SUCCESS", errors);
        validateRequiredString(COIN_PURCHASE + ".MESSAGE.NO_MONEY", errors);
        validateRequiredString(COIN_PURCHASE + ".MESSAGE.ECONOMY_UNAVAILABLE", errors);
        validateRequiredString(COIN_PURCHASE + ".MESSAGE.COIN_UNAVAILABLE", errors);
        validateRequiredString(COIN_PURCHASE + ".MESSAGE.COIN_LIMIT", errors);
        validateRequiredString(COIN_PURCHASE + ".MESSAGE.COIN_ERROR", errors);
        validateRequiredString(COIN_PURCHASE + ".MESSAGE.REFUND_ERROR", errors);
    }

    private static void validatePositiveInteger(String path, List<String> errors) {
        Object rawValue = config.get(path);
        if (!(rawValue instanceof Number number) || number.intValue() <= 0) {
            errors.add(path + " 必須是大於 0 的整數");
        }
    }

    private static void validateShopSection(String sectionPath, List<String> errors) {
        ConfigurationSection section = config.getConfigurationSection(sectionPath);
        if (section == null) {
            errors.add(sectionPath + " 區段不存在");
            return;
        }

        validateItemList(sectionPath + ".BLOCK", section.getStringList("BLOCK"), false, errors);

        for (String key : section.getKeys(false)) {
            if (key.startsWith("TYPE_")) {
                validateItemList(sectionPath + "." + key, section.getStringList(key), true, errors);
            }
        }
    }

    private static void validateItemList(String path, List<String> items, boolean isProductList, List<String> errors) {
        if (items.isEmpty()) {
            errors.add(path + " 沒有任何設定");
            return;
        }

        int maxItems = isCoinPurchaseEnabled() ? 44 : 45;
        if (items.size() > maxItems) {
            errors.add(path + " 共 " + items.size() + " 格，超過 GUI 上限 " + maxItems + " 格");
        }

        for (int index = 0; index < items.size(); index++) {
            String itemLine = items.get(index);
            if (itemLine.equalsIgnoreCase("NULL")) {
                continue;
            }

            String location = path + "[" + index + "]";
            String[] parts = itemLine.split(",", -1);
            if (parts.length != 3) {
                errors.add(location + " 必須是 MATERIAL,顯示名稱," + (isProductList ? "價格" : "分類編號"));
                continue;
            }

            Material material = Material.getMaterial(parts[0].trim().toUpperCase(Locale.ROOT));
            if (material == null) {
                errors.add(location + " 的 Material 無效：" + parts[0]);
            }

            if (parts[1].isBlank()) {
                errors.add(location + " 的顯示名稱不可為空");
            }

            try {
                int value = Integer.parseInt(parts[2].trim());
                if (isProductList && value < 0) {
                    errors.add(location + " 的價格不可為負數");
                } else if (!isProductList && value <= 0) {
                    errors.add(location + " 的分類編號必須大於 0");
                } else if (!isProductList && !sectionTypeExists(path, value)) {
                    errors.add(location + " 指向不存在的 TYPE_" + value);
                }
            } catch (NumberFormatException exception) {
                errors.add(location + " 的" + (isProductList ? "價格" : "分類編號") + "必須是整數");
            }
        }
    }

    private static boolean sectionTypeExists(String blockPath, int type) {
        String sectionPath = blockPath.substring(0, blockPath.length() - ".BLOCK".length());
        return config.isList(sectionPath + ".TYPE_" + type);
    }

    private static void applyMissingDefaults() {
        Hu_Building_Materials plugin = Hu_Building_Materials.getInstance();
        try (InputStream input = plugin.getResource("Config.yml")) {
            if (input == null) {
                return;
            }

            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(input, StandardCharsets.UTF_8)
            );
            config.setDefaults(defaults);
            config.options().copyDefaults(true);
        } catch (Exception exception) {
            plugin.getLogger().warning("無法讀取 Config.yml 預設值：" + exception.getMessage());
        }
    }
}
