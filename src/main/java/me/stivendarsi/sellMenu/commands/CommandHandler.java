package me.stivendarsi.sellMenu.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.stivendarsi.orbit.Orbit;
import me.stivendarsi.sellMenu.SellMenu;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class CommandHandler {
    public static void register(LifecycleEventManager<@NotNull Plugin> manager) {
        Permission sellAdmin = new Permission("sell.admin");
        SellMenu.sellMenuInstance().getServer().getPluginManager().addPermission(sellAdmin);
        manager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            Commands commands = event.registrar();
            commands.register(Commands.literal("worth").executes(SellCommands::openValueBrowser)
                            .then(Commands.literal("hand").executes(SellCommands::getItemValue))
                    .build());
            commands.register(Commands.literal("sell").executes(SellCommands::open)

                    .then(Commands.literal("get-multiplier").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission(sellAdmin))
                            .then(Commands.argument("player_name", word())
                                    .then(Commands.argument("orbit_identifier", word()).suggests(CommandHandler::getSuggestion)
                                            .executes(SellCommands::getMultiplier)
                                    )
                            )
                    )
                    .then(Commands.literal("set-multiplier").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission(sellAdmin))
                            .then(Commands.argument("player_name", word())
                                    .then(Commands.argument("orbit_identifier", string()).suggests(CommandHandler::getSuggestion)
                                            .then(Commands.argument("multiplier", doubleArg(1))
                                                    .then(Commands.argument("set_if_higher", bool())
                                                            .executes(SellCommands::modifyMultiplier)
                                                    )
                                            )
                                    )
                            )
                    )
                    .then(Commands.literal("reload").executes(SellCommands::reload).requires(ctx -> ctx.getSender().isOp()))
                    .build());
        });
    }

    private static CompletableFuture<Suggestions> getSuggestion(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (String orbitIdentifier : Orbit.mainHandler().orbitHandler().getOrbitIdentifiers()) {
            builder.suggest(orbitIdentifier);
        }
        return builder.buildFuture();
    }
}
