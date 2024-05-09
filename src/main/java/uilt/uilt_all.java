package uilt;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class uilt_all {

    public static ItemStack creatmatitem(Material material,String itemname,int money,double vip_discount){
        int newcoin = (int) (money*vip_discount);
        ItemStack item = new ItemStack(material);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(itemname);
        List<String> lore = new ArrayList<>();
        if (vip_discount == 1) {
            lore.add(" ");
            lore.add("§7➡ 建議售價 ⎋" + money + " 建材點");
            im.setLore(lore);
        }else {
            lore.add(" ");
            lore.add("§7➡ 建議售價 §c⎋§r§c§m" + money + "§r§7 建材點");
            lore.add("§7➡ 優惠售價 §a⎋" + newcoin + "§7 建材點");
            lore.add(" ");
            lore.add("§3" + (int)(100-(vip_discount*100)) + "% 折扣");
        }
        im.setLore(lore);
        item.setItemMeta(im);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger("biold_money", (int) (money*vip_discount));

        return nbtItem.getItem();
    }

    public static ItemStack creatmatitem(Material material,String itemname, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(itemname);
        im.setLore(lore);
        item.setItemMeta(im);

        return item;
    }

    public static ItemStack creatmatitem(Material material,String itemname,String nbt,int nbt_val){
        ItemStack item = new ItemStack(material);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(itemname);
        item.setItemMeta(im);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(nbt,nbt_val);

        return nbtItem.getItem();
    }

    //設定GUI
    public static void setinv(List<String> BLOCK_list, Inventory inventory,String nbt,boolean haslore,double vip_discount){
        int i = 9;

        for (String itemline : BLOCK_list) {

            if (!itemline.equalsIgnoreCase("NULL")) { //如果物品為null，跳過那一格
                String[] parts = itemline.split(",");
                String mat = parts[0];
                String matname = parts[1];
                int matcoin_or_nbt = Integer.parseInt(parts[2]);
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


    //確認玩家背包有空位
    public static boolean hasEmptySlots(Player player) {
        Inventory inventory = player.getInventory();
        int emptySlots = 0;
        for (int i = 0  ; i<=inventory.getSize() ; i++) {
            if (inventory.getItem(i) == null) {
                if (i<inventory.getSize()-5) {
                    emptySlots++;
                }
            }
        }
        return emptySlots > 0;

        }

}
