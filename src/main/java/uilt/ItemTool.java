package uilt;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.weiwei.hu_building_materials.Hu_Building_Materials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemTool {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    public static ItemStack creatmatitem(Material material,String itemname,int money,double vip_discount){
        int newcoin = (int) (money*vip_discount);
        ItemStack item = new ItemStack(material);
        ItemMeta im = item.getItemMeta();
        im.displayName(legacyComponent(itemname));
        List<String> lore = new ArrayList<>();
        if (vip_discount == 1) {
            lore.add(" ");
            lore.add("§7➡ 建議售價 ⎋" + money + " 建材點");
        }else {
            lore.add(" ");
            lore.add("§7➡ 建議售價 §c⎋§r§c§m" + money + "§r§7 建材點");
            lore.add("§7➡ 優惠售價 §a⎋" + newcoin + "§7 建材點");
            lore.add(" ");
            lore.add("§3" + (int)(100-(vip_discount*100)) + "% 折扣");
        }
        if (Config.getITEM_LORE_List() != null && !Config.getITEM_LORE_List().isEmpty()) {
            lore.add(" ");
            lore.addAll(Config.getITEM_LORE_List().stream().map(seed::color).toList());
        }
        im.lore(lore.stream().map(ItemTool::legacyComponent).toList());
        im.getPersistentDataContainer().set(dataKey("build_money"), PersistentDataType.INTEGER, newcoin);
        item.setItemMeta(im);

        return item;
    }

    public static ItemStack creatmatitem(Material material,String itemname, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta im = item.getItemMeta();
        im.displayName(legacyComponent(itemname));
        im.lore(lore == null ? null : lore.stream().map(ItemTool::legacyComponent).toList());
        item.setItemMeta(im);

        return item;
    }

    public static ItemStack creatmatitem(Material material,String itemname,String nbt,int nbt_val){
        ItemStack item = new ItemStack(material);
        ItemMeta im = item.getItemMeta();
        im.displayName(legacyComponent(itemname));
        im.getPersistentDataContainer().set(dataKey(nbt), PersistentDataType.INTEGER, nbt_val);
        item.setItemMeta(im);

        return item;
    }

    public static Integer getIntData(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer().get(dataKey(key), PersistentDataType.INTEGER);
    }

    public static ItemStack setIntData(ItemStack item, String key, int value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.getPersistentDataContainer().set(dataKey(key), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
        return item;
    }

    private static NamespacedKey dataKey(String key) {
        return new NamespacedKey(
                Hu_Building_Materials.getInstance(),
                key.toLowerCase(Locale.ROOT)
        );
    }

    public static Component legacyComponent(String text) {
        return LEGACY_SERIALIZER.deserialize(text);
    }

    //設定GUI
    public static void setinv(List<String> BLOCK_list, Inventory inventory,String nbt,boolean haslore,double vip_discount){
        int i = 9;
        int reservedSlot = Config.isCoinPurchaseEnabled() ? Config.getCoinPurchaseSlot() : -1;

        for (String itemline : BLOCK_list) {

            if (i == reservedSlot) {
                i++;
            }

            if (!itemline.equalsIgnoreCase("NULL")) { //如果物品為null，跳過那一格
                String[] parts = itemline.split(",", -1);
                String mat = parts[0].trim();
                String matname = parts[1];
                int matcoin_or_nbt = Integer.parseInt(parts[2].trim());
                Material material = Material.valueOf(mat.toUpperCase());
                ItemStack item;
                if (haslore) {
                    item = creatmatitem(material, "§f" + matname, matcoin_or_nbt, vip_discount);
                } else {
                    item = creatmatitem(material, "§e" + matname, nbt, matcoin_or_nbt);
                }
                if (i<54) {
                    inventory.setItem(i, item);
                }
            }

            i++;
        }
    }

    //清除9-53
    public static void clearinv(Inventory inventory){

        for(int i = 9 ; i<54 ; i++){
            inventory.setItem(i,null);
        }
    }


    // 確認玩家背包能完整容納物品，包含可合併的現有堆疊
    public static boolean canFitItem(Player player, ItemStack item) {
        int remaining = item.getAmount();

        for (ItemStack current : player.getInventory().getStorageContents()) {
            if (current == null || current.getType().isAir()) {
                remaining -= item.getMaxStackSize();
            } else if (current.isSimilar(item)) {
                remaining -= Math.max(0, current.getMaxStackSize() - current.getAmount());
            }

            if (remaining <= 0) {
                return true;
            }
        }

        return false;
    }

}
