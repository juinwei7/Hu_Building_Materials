package org.weiwei.hu_building_materials.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.weiwei.hu_building_materials.Hu_Building_Materials;
import uilt.ItemTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 購買要點兩下：第一次只把那格標記起來，5 秒內再點同一格才扣點數。
 * 買完會再保留 5 秒，連續買同一樣東西不用每次重點兩下。
 */
final class PurchaseConfirm {

    static final long WINDOW_MS = 5000;
    private static final String MARK_LINE = "§e§l▶ 再點一次確認購買（5 秒內）";

    private record Pending(int slot, String key, long expiresAt, ItemStack original, ItemStack marked) {
    }

    private final Map<UUID, Pending> pending = new HashMap<>();

    boolean isConfirmed(Player player, int slot, String key) {
        Pending p = pending.get(player.getUniqueId());
        return p != null
                && p.slot() == slot
                && p.key().equals(key)
                && p.expiresAt() >= System.currentTimeMillis();
    }

    /** 標記這一格（或買完後延長），時間到沒再點就把圖示還原 */
    void arm(Player player, Inventory inv, int slot, String key) {
        Pending old = pending.get(player.getUniqueId());
        ItemStack original = old != null && old.slot() == slot && old.key().equals(key)
                ? old.original()
                : cloneOrNull(inv.getItem(slot));
        if (original == null) return;

        ItemStack marked = mark(original);
        inv.setItem(slot, marked);
        Pending now = new Pending(slot, key, System.currentTimeMillis() + WINDOW_MS, original, marked);
        pending.put(player.getUniqueId(), now);

        Bukkit.getScheduler().runTaskLater(Hu_Building_Materials.getInstance(), () -> {
            if (pending.get(player.getUniqueId()) != now) return;
            pending.remove(player.getUniqueId());
            ItemStack current = inv.getItem(slot);
            if (current != null && current.isSimilar(now.marked())) inv.setItem(slot, now.original());
        }, WINDOW_MS / 50);
    }

    void clear(Player player) {
        pending.remove(player.getUniqueId());
    }

    private static ItemStack mark(ItemStack original) {
        ItemStack marked = original.clone();
        ItemMeta meta = marked.getItemMeta();
        if (meta == null) return marked;
        List<Component> lore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());
        lore.add(Component.empty());
        lore.add(ItemTool.legacyComponent(MARK_LINE));
        meta.lore(lore);
        meta.setEnchantmentGlintOverride(true);
        marked.setItemMeta(meta);
        return marked;
    }

    private static ItemStack cloneOrNull(ItemStack item) {
        return item == null ? null : item.clone();
    }
}
