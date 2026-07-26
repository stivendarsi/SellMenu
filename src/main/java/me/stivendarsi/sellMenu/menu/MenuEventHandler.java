package me.stivendarsi.sellMenu.menu;


import io.github.miniplaceholders.api.MiniPlaceholders;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.Orbit;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Arrays;
import java.util.UUID;

import static me.stivendarsi.sellMenu.SellMenu.mainHandler;

public class MenuEventHandler implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof SellingMenu sellingMenu)) return;
        UUID userUUID = event.getPlayer().getUniqueId();


        int inventoryValue = mainHandler().getMoneyHandler().sumOfValues(Arrays.asList(event.getInventory().getContents()));

        OrbitData currentOrbitData = Orbit.mainHandler().orbitHandler().getCurrentOrbit();
        if (currentOrbitData == null) throw new RuntimeException("Null orbit");


        double multiplier = mainHandler().getMoneyHandler().getUserMultiplier(userUUID, currentOrbitData.identifier());

        inventoryValue = (int) (inventoryValue * multiplier);

        if (inventoryValue <= 0) return;
        mainHandler().getMoneyHandler().deposit(userUUID, inventoryValue);


        if (!(event.getPlayer() instanceof Player player)) return;

        String formatted = mainHandler().getMoneyHandler().format(inventoryValue);


        TagResolver currencyResolver = TagResolver.builder().tag("amount", Tag.preProcessParsed(formatted)).build();

        Component msg = MiniMessage.miniMessage().deserialize(mainHandler().getMessageHandler().getReceivedMessage(), player, currencyResolver, MiniPlaceholders.audienceGlobalPlaceholders());

        player.sendActionBar(msg);
        player.sendMessage(msg);

        player.playSound(Constants.pingSound);
    }
}
