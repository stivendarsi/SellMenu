package me.stivendarsi.sellMenu.menu;

import io.github.miniplaceholders.api.MiniPlaceholders;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import static me.stivendarsi.sellMenu.SellMenu.mainHandler;

public class DialogCache {
    private Component textTemplate;
    private CachedItemStack cachedItemStack;

    public DialogCache(CachedItemStack cachedItemStack) {

        TagResolver resolver = TagResolver.builder()
                .tag("item_display_name", Tag.selfClosingInserting(cachedItemStack.itemStack().effectiveName()))
                .tag("price", Tag.preProcessParsed(mainHandler().getMoneyHandler().format(cachedItemStack.value())))
                .build();


        String template = """
                 <cut_tigeril_gold:'פריט'><gray>:</gray> <item_display_name>
                 <cut_tigeril_gold:'שווי'><gray>:</gray> <cut_money_green:'<price>'>
                 <cut_tigeril_gold:'לאחר הכפלה'> <gray>(x<sell_user_multiplier>):</gray> <cut_money_green:'<sell_user_value:%s>'>
                """.formatted(cachedItemStack.value());
   //     textTemplate = MiniMessage.miniMessage().deserialize(template, viewer, resolver, MiniPlaceholders.audienceGlobalPlaceholders());



        //   return DialogBody.item(cachedItemStack.itemStack()).description(msg).build();
    }

//    private DialogBody getDialog(Player viewer) {
//        Component msg = MiniMessage.miniMessage().deserialize("<cut_tigeril_gold:'לאחר הכפלה'> <gray>(x<sell_user_multiplier>):</gray> <cut_money_green:'<sell_user_value:%s>'>", viewer, MiniPlaceholders.audienceGlobalPlaceholders());
//        Component finalText = this.textTemplate.append(msg);
//        return DialogBody.item(cachedItemStack.itemStack()).description(DialogBody.plainMessage(finalText, 170));
//    }
}
