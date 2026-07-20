package me.stivendarsi.sellMenu.menu;

import io.github.miniplaceholders.api.MiniPlaceholders;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.stivendarsi.orbit.Orbit;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.sellMenu.SellMenu.mainHandler;

public class WorthBrowser {
    private final UUID userUUID;
    private final Player viewer;

    public WorthBrowser(Player viewer) {
        this.userUUID = viewer.getUniqueId();
        this.viewer = viewer;
    }

    public Dialog getDialog() {
        return Dialog.create(builder -> {
            builder.empty().type(DialogType.notice())
                    .base(getBase());
        });
    }

    private DialogBase getBase() {
        OrbitData orbitData = Orbit.mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) throw new RuntimeException("Null orbit");

        double multiplier = mainHandler().getMoneyHandler().getUserMultiplier(userUUID, orbitData.identifier());

        List<DialogBody> itemValues = getBodies(multiplier);

        return DialogBase.builder(Component.text("מחירים")).body(itemValues).build();
    }

    private List<DialogBody> getBodies(double multiplier){
        List<DialogBody> dialogBodies = new LinkedList<>();
        List<Map.Entry<ItemType, Integer>> entries = mainHandler().getMoneyHandler().getSorted();
        for (Map.Entry<ItemType, Integer> entry : entries) {
            int value = entry.getValue();
            ItemType itemType = entry.getKey();

            DialogBody dialogBody = getItemStackBody(itemType, value, multiplier);
            dialogBodies.add(dialogBody);
        }
        return dialogBodies;
    }

    private DialogBody getItemStackBody(ItemType itemType, int value, double userMultiplier) {
        ItemStack itemStack = itemType.createItemStack();

        TagResolver resolver = TagResolver.builder()
                .tag("item_display_name", Tag.selfClosingInserting(itemStack.effectiveName()))
                .tag("price", Tag.preProcessParsed(mainHandler().getMoneyHandler().format(value)))
                .tag("price_multiplied", Tag.preProcessParsed(mainHandler().getMoneyHandler().format(value * userMultiplier)))
                .tag("multiplier", Tag.preProcessParsed("x" + userMultiplier))
                .build();

        String s = """
                <gray>פריט: <item_display_name>
                <gray>שווי: <white><price>
                <gray>שווי לאחר הכפלה (<multiplier>): <cut_money_green:'<price_multiplied>'>
                """;

        PlainMessageDialogBody msg = DialogBody
                .plainMessage(
                        MiniMessage.miniMessage().deserialize(s, this.viewer, resolver, MiniPlaceholders.audienceGlobalPlaceholders()),
                        150
                );
        DialogBody dialogBody = DialogBody.item(itemStack).description(msg).build();
        return dialogBody;
    }
}
