package org.weiwei.hu_building_materials.menu;

import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public final class ShopInventoryHolder implements InventoryHolder {

    private final Inventory inventory;
    private final ShopType shopType;

    public ShopInventoryHolder(ShopType shopType, Component title) {
        this.shopType = shopType;
        this.inventory = Bukkit.createInventory(this, 54, title);
    }

    public ShopType getShopType() {
        return shopType;
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
