package me.stivendarsi.sellMenu.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.miniplaceholders.api.MiniPlaceholders;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.Orbit;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.sellMenu.menu.SellingMenu;
import me.stivendarsi.sellMenu.menu.WorthBrowser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static me.stivendarsi.sellMenu.SellMenu.*;

public class SellCommands {
    public static int open(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player player)) return 0;

        SellingMenu mainMenu = new SellingMenu(player.getUniqueId());
        player.openInventory(mainMenu.getInventory());
        return 1;
    }

    public static int reload(CommandContext<CommandSourceStack> context) {
        sellMenuInstance().reloadConfig();

        mainHandler().load();
        context.getSource().getSender().sendRichMessage("<green>נטען מחדש");

        return 1;
    }

    public static int modifyMultiplier(CommandContext<CommandSourceStack> context) {

        String playerName = context.getArgument("player_name", String.class);

        UUID userUUID = Bukkit.getPlayerUniqueId(playerName);
        if (userUUID == null) return 0;

        String orbitIdentifier = context.getArgument("orbit_identifier", String.class);
        double multiplier = context.getArgument("multiplier", Double.class);
        boolean setIfHigher = context.getArgument("set_if_higher", Boolean.class);

        mainHandler().getMoneyHandler().updateMultiplier(userUUID, orbitIdentifier, multiplier, setIfHigher);

        return 1;
    }


    public static int getMultiplier(CommandContext<CommandSourceStack> context) {

        String playerName = context.getArgument("player_name", String.class);

        UUID userUUID = Bukkit.getPlayerUniqueId(playerName);
        if (userUUID == null) return 0;

        String orbitIdentifier = context.getArgument("orbit_identifier", String.class);

        double multiplier = mainHandler().getMoneyHandler().getUserMultiplier(userUUID, orbitIdentifier);
        context.getSource().getSender().sendRichMessage("<yellow>מכפלת שווי: " + multiplier);
        return 1;
    }

    public static int openValueBrowser(CommandContext<CommandSourceStack> context) {

        if (!(context.getSource().getExecutor() instanceof Player player)) return 0;

        WorthBrowser worthBrowser = new WorthBrowser(player);
        player.showDialog(worthBrowser.getDialog());
        return 1;
    }

    public static int getItemValue(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player player)) return 0;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        int value = mainHandler().getMoneyHandler().getItemValue(itemStack);

        OrbitData currentOrbit = Orbit.mainHandler().orbitHandler().getCurrentOrbit();
        if (currentOrbit == null) return 0;

        double multiplier = mainHandler().getMoneyHandler().getUserMultiplier(player.getUniqueId(), currentOrbit.identifier());



        int valueMultiplied = (int) (value * multiplier);

        Component msg = MiniMessage.miniMessage().deserialize("<white>value<dark_gray>:</dark_gray> " + mainHandler().getMoneyHandler().format(value) + "<newline><white>value<dark_gray>:</dark_gray> <cut_money_gold:" + mainHandler().getMoneyHandler().format(valueMultiplied) + ">", player, MiniPlaceholders.audienceGlobalPlaceholders());

        player.sendMessage(msg);
        return 1;
    }


}