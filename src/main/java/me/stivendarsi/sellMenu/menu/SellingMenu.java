package me.stivendarsi.sellMenu.menu;

import me.stivendarsi.orbit.Orbit;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

import static me.stivendarsi.sellMenu.SellMenu.mainHandler;
import static me.stivendarsi.sellMenu.SellMenu.sellMenuInstance;

public class SellingMenu implements InventoryHolder {

    private final Inventory inventory;

    public SellingMenu(UUID uuid) {

        OrbitData orbitData = Orbit.mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) throw new RuntimeException("Null experience");

        double multiplier = mainHandler().getMoneyHandler().getUserMultiplier(uuid, orbitData.identifier());

        Component title = MiniMessage.miniMessage().deserialize("מכירת פריטים | הכפלת שווי: %sx".formatted(multiplier));

        this.inventory = sellMenuInstance().getServer().createInventory(this, 9 * 6, title);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}
