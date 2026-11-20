package org.weiwei.hu_building_materials.menu;

import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public final class ShopInventoryHolder implements InventoryHolder {

    private final Inventory inventory;
    private ShopType shopType;
    private int category; // 目前分類（設定檔 TYPE_n 的 n）

    public ShopInventoryHolder(ShopType shopType, int category, Component title) {
        this.shopType = shopType;
        this.category = category;
        this.inventory = Bukkit.createInventory(this, 54, title);
    }

    public ShopType getShopType() {
        return shopType;
    }

    public void setShopType(ShopType shopType) {
        this.shopType = shopType;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public enum ShopType {
        BUILDING,
        DYED,
        OTHER
    }
}
