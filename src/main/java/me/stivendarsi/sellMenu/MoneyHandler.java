package me.stivendarsi.sellMenu;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

import static me.stivendarsi.sellMenu.SellMenu.luckPerms;
import static me.stivendarsi.sellMenu.SellMenu.sellMenuInstance;
import static org.bukkit.Bukkit.getServer;

public class MoneyHandler {
    private Map<ItemType, Integer> valueMap;
    private  List<Map.Entry<ItemType, Integer>> sorted;

    private Economy econ;

    public void load() {
        this.valueMap = new HashMap<>();
        this.sorted = new ArrayList<>();
        setupEconomy();
        loadValues();
    }

    public String format(double moneyAmount) {
        Locale israel = Locale.of("en", "IL");

        NumberFormat currency = NumberFormat.getCurrencyInstance(israel);
        currency.setMaximumFractionDigits(0);
        return currency.format(moneyAmount);
    }


    private void loadValues() {

        List<ItemType> leftItemType = new ArrayList<>(Registry.ITEM.stream().toList());

        for (String itemTypeName : sellMenuInstance().getConfig().getConfigurationSection("items").getKeys(false)) {
            ItemType itemType = Registry.ITEM.get(Key.key(itemTypeName.toLowerCase(Locale.ROOT)));
            if (itemType == null) {
                sellMenuInstance().getLogger().warning("Null item type: " + itemTypeName);
                continue;
            }
            int value = sellMenuInstance().getConfig().getInt("items." + itemTypeName, 0);
            valueMap.put(itemType, value);

            leftItemType.remove(itemType);
        }

        for (ItemType itemType : leftItemType) {
            sellMenuInstance().getLogger().warning("Missing Item: " + itemType.getKey());
        }

        this.sorted = this.valueMap.entrySet().stream()
                .sorted(Map.Entry.<ItemType, Integer>comparingByValue().reversed()) // highest first
                .toList();

    }

    public void deposit(UUID user, int amount) {
        getEconomy().depositPlayer(Bukkit.getOfflinePlayer(user), amount);

    }


    public void updateMultiplier(UUID userUUID, String orbitSeasonIdentifier, double multiplierAmount, boolean setIfHigher) {
        String key = getMultiplierKey(orbitSeasonIdentifier);
        User user = luckPerms().getUserManager().getUser(userUUID);
        if (user == null) throw new RuntimeException("Null user");

        double currentVal;
        if (setIfHigher) currentVal = Math.max(getUserMultiplier(user.getUniqueId(), orbitSeasonIdentifier), multiplierAmount);
        else currentVal = Math.max(1, multiplierAmount);

        MetaNode currentMultiplierNode = getMultiplierNode(user, orbitSeasonIdentifier);
        if (currentMultiplierNode != null) user.data().remove(currentMultiplierNode);

        MetaNode node = MetaNode.builder()
                .key(key)
                .value(String.valueOf(currentVal))
                .build();

        user.data().add(node);
        luckPerms().getUserManager().saveUser(user);
    }

    public int getItemValue(ItemStack itemStack) {
        return this.valueMap.getOrDefault(itemStack.getType().asItemType(), 0);
    }

    public Map<ItemType, Integer> valueMap() {
        return valueMap;
    }

    public String getMultiplierKey(String orbitIdentifier) {
        return "sell.muliplier." + orbitIdentifier;
    }

    public double getUserMultiplier(UUID userUUID, String orbitIdentifier) {
        User user = luckPerms().getUserManager().getUser(userUUID);
        if (user == null) return 1;

        MetaNode existingNode = getMultiplierNode(user, orbitIdentifier);
        if (existingNode != null) {
            double val = NumberUtils.toDouble(existingNode.getMetaValue(), 1);
            return Math.max(1, val);
        }
        return 1;
    }

    private @Nullable MetaNode getMultiplierNode(User user, String orbitIdentifier) {
        String key = getMultiplierKey(orbitIdentifier);
        Optional<MetaNode> existingNode = user.getNodes(NodeType.META)
                .stream()
                .filter(metaNode -> metaNode.getMetaKey().equals(key))
                .findFirst();
        return existingNode.orElse(null);
    }

    private void setupEconomy() {
        Preconditions.checkNotNull(getServer().getPluginManager().getPlugin("Vault"), "Vault null");

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        Preconditions.checkNotNull(rsp, "Rsp null");

        econ = rsp.getProvider();
    }

    public List<Map.Entry<ItemType, Integer>> getSorted() {
        return sorted;
    }

    public Economy getEconomy() {
        return econ;
    }
}
