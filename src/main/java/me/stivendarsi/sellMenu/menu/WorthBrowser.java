package me.stivendarsi.sellMenu.menu;

import com.nexomc.nexo.glyphs.GlyphTag;
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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static me.stivendarsi.sellMenu.SellMenu.mainHandler;

@SuppressWarnings("UnstableApiUsage")
public class WorthBrowser {

    private static final MiniMessage mm = MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build();

    private Map<CachedItemStack, DialogBody> dialogBodyMap;

    public WorthBrowser(Player viewer) {
        loadDialogBases(viewer);
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

            ActionButton exist = ActionButton.builder(MiniMessage.miniMessage().deserialize("<red>יציאה</red>")).action(DialogAction.staticAction(ClickEvent.callback(Audience::closeDialog))).build();

            builder.empty().type(DialogType.multiAction(actionButtons).exitAction(exist).build())
                    .base(getBase(nameRegex));
        });
    }

    private DialogBase getBase(String nameRegex) {
        OrbitData orbitData = Orbit.mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) throw new RuntimeException("Null orbit");
        List<DialogBody> dialogBodies = this.dialogBodyMap.entrySet().stream().filter(cache -> cache.getKey().name().startsWith(nameRegex)).map(Map.Entry::getValue).toList();


        List<DialogInput> dialogInputs = new ArrayList<>();

        DialogInput search = DialogInput.text("sell_search", Constants.color("<gray>חיפוש</gray>")).initial(nameRegex).build();
        dialogInputs.add(search);

        return DialogBase.builder(Component.text("מחירים")).inputs(dialogInputs).body(dialogBodies).afterAction(DialogBase.DialogAfterAction.NONE).pause(false).build();
    }


    private void loadDialogBases(Player viewer) {
        this.dialogBodyMap = new LinkedHashMap<>();

        for (Map.Entry<CachedItemStack, Integer> cache : mainHandler().getMoneyHandler().getSorted()) {
            DialogBody body = getItemStackBody(viewer, cache.getKey());
            this.dialogBodyMap.put(cache.getKey(), body);
        }
    }

    private DialogBody getItemStackBody(Player viewer, CachedItemStack cachedItemStack) {

        TagResolver resolver = TagResolver.builder()
                .tag("item_display_name", Tag.selfClosingInserting(cachedItemStack.itemStack().effectiveName()))
                .tag("price", Tag.preProcessParsed(mainHandler().getMoneyHandler().format(cachedItemStack.value())))
                .build();


        String template = """
                 <cut_tigeril_gold:'פריט'><gray>:</gray> <item_display_name>
                 <cut_tigeril_gold:'שווי'><gray>:</gray> <cut_money_green:'<price>'>
                 <cut_tigeril_gold:'לאחר הכפלה'> <gray>(x<sell_user_multiplier>):</gray> <cut_money_green:'<sell_user_value:%s>'>
                """.formatted(cachedItemStack.value());
        PlainMessageDialogBody msg =
                DialogBody.plainMessage(
                        mm.deserialize(template, viewer, resolver, MiniPlaceholders.audienceGlobalPlaceholders()),
                        170
                );


        return DialogBody.item(cachedItemStack.itemStack()).description(msg).build();
    }
}
