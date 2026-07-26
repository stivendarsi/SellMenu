package me.stivendarsi.sellMenu.menu;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.Locale;

public class CachedItemStack {
    private final ItemType itemType;
    private final ItemStack itemStack;
    private final int value;
    private final String name;

    public CachedItemStack(ItemType itemType, int value){
        this.itemType = itemType;
        this.itemStack = itemType.createItemStack();
        this.value = value;
        name = itemType.key().value().toLowerCase(Locale.ROOT);
    }


    public ItemType itemType() {
        return itemType;
    }

    public int value() {
        return value;
    }

    public String name() {
        return name;
    }

    public ItemStack itemStack() {
        return itemStack;
    }
}
