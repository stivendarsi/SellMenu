package me.stivendarsi.sellMenu.menu;

import io.github.miniplaceholders.api.MiniPlaceholders;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.Orbit;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.stivendarsi.sellMenu.SellMenu.mainHandler;

@SuppressWarnings("UnstableApiUsage")
public class WorthBrowser {

    private final UUID userUUID;
    private static final String template = """
                <cut_tigeril_gold:'פריט'><gray>:</gray> <item_display_name>
                <cut_tigeril_gold:'שווי'><gray>:</gray> <cut_money_green:'<price>'>
                <cut_tigeril_gold:'לאחר הכפלה'> <gray>(x<multiplier>):</gray> <cut_money_green:'<price_multiplied>'>
                """;
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public WorthBrowser(Player viewer) {
        this.userUUID = viewer.getUniqueId();
    }

    public Dialog getDialog(String nameRegex) {
        return Dialog.create(builder -> {
            List<ActionButton> actionButtons = new ArrayList<>();
            ActionButton searchButton = ActionButton.builder(MiniMessage.miniMessage().deserialize("חפש"))
                    .action(DialogAction.customClick((response, audience) -> {
                        String prefix = response.getText("sell_search");
                        audience.showDialog(getDialog(prefix));
                    }, ClickCallback.Options.builder().build()))
                    .build();

            actionButtons.add(searchButton);

            ActionButton exist = ActionButton.builder(MiniMessage.miniMessage().deserialize("<red>יציאה</red>")).build();

            builder.empty().type(DialogType.multiAction(actionButtons).exitAction(exist).build())
                    .base(getBase(nameRegex));
        });
    }

    private DialogBase getBase(String nameRegex) {
        OrbitData orbitData = Orbit.mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) throw new RuntimeException("Null orbit");

        double multiplier = mainHandler().getMoneyHandler().getUserMultiplier(userUUID, orbitData.identifier());

        List<DialogBody> itemValues = getBodies(multiplier, nameRegex);

        List<DialogInput> dialogInputs = new ArrayList<>();

        DialogInput search = DialogInput.text("sell_search", Constants.color("<gray>חיפוש</gray>")).initial(nameRegex).build();
        dialogInputs.add(search);

        return DialogBase.builder(Component.text("מחירים")).inputs(dialogInputs).body(itemValues).afterAction(DialogBase.DialogAfterAction.NONE).pause(false).build();
    }

    private List<DialogBody> getBodies(double multiplier, String prefix) {
        List<DialogBody> dialogBodies = new LinkedList<>();
        List<Map.Entry<ItemStack, Integer>> entries = mainHandler().getMoneyHandler().getSorted();

        Player viewer = Bukkit.getPlayer(userUUID);
        if (viewer == null) return dialogBodies;

        for (Map.Entry<ItemStack, Integer> entry : entries) {
            int value = entry.getValue();
            ItemStack itemStack = entry.getKey();
            String name = itemStack.getType().asItemType().key().value().toLowerCase(Locale.ROOT);

            if (name.startsWith(prefix)) {
                DialogBody dialogBody = getItemStackBody(viewer, itemStack, value, multiplier);
                dialogBodies.add(dialogBody);
            }
        }
        return dialogBodies;
    }

    private DialogBody getItemStackBody(Player viewer, ItemStack itemStack, int value, double userMultiplier) {
        TagResolver resolver = TagResolver.builder()
                .tag("item_display_name", Tag.selfClosingInserting(itemStack.effectiveName()))
                .tag("price", Tag.preProcessParsed(mainHandler().getMoneyHandler().format(value)))
                .tag("price_multiplied", Tag.preProcessParsed(mainHandler().getMoneyHandler().format(value * userMultiplier)))
                .tag("multiplier", Tag.preProcessParsed(String.valueOf(userMultiplier)))
                .build();


        PlainMessageDialogBody msg = DialogBody.plainMessage(
                        mm.deserialize(template, viewer, resolver, MiniPlaceholders.audienceGlobalPlaceholders()),
                        170
        );

        return DialogBody.item(itemStack).description(msg).build();
    }
}
