package me.stivendarsi.sellMenu;

import io.github.miniplaceholders.api.Expansion;
import me.stivendarsi.orbit.Orbit;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.sellMenu.commands.CommandHandler;
import me.stivendarsi.sellMenu.menu.MenuEventHandler;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.luckperms.api.LuckPerms;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SellMenu extends JavaPlugin {
    private static SellMenu plugin;
    private static LuckPerms luckPerms;

    private static MainHandler mainHandler;

    public static MainHandler mainHandler() {
        return mainHandler;
    }

    public static LuckPerms luckPerms() {
        return luckPerms;
    }

    public static SellMenu sellMenuInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        reloadConfig();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }

        mainHandler = new MainHandler();

        mainHandler.load();

        CommandHandler.register(getLifecycleManager());

        getServer().getPluginManager().registerEvents(new MenuEventHandler(), this);


        Expansion expansion = Expansion.builder("sell")
                .audiencePlaceholder("user_value", (audience, queue, ctx) -> {
                    if (!(audience instanceof Player player)) return Tag.preProcessParsed("");
                    int value = NumberUtils.toInt(queue.popOr("Null value").value(), -1);

                    OrbitData currentOrbit = Orbit.mainHandler().orbitHandler().getCurrentOrbit();
                    if (currentOrbit == null) throw new RuntimeException("Null orbit");

                    double multiplier = mainHandler().getMoneyHandler().getUserMultiplier(player.getUniqueId(), currentOrbit.identifier());
                    return Tag.preProcessParsed(mainHandler().getMoneyHandler().format(multiplier * value));

                })
                .audiencePlaceholder("user_multiplier", (audience, queue, ctx) -> {
                    if (!(audience instanceof Player player)) return Tag.preProcessParsed("");

                    OrbitData currentOrbit = Orbit.mainHandler().orbitHandler().getCurrentOrbit();
                    if (currentOrbit == null) throw new RuntimeException("Null orbit");

                    double multiplier = mainHandler().getMoneyHandler().getUserMultiplier(player.getUniqueId(), currentOrbit.identifier());

                    return Tag.preProcessParsed(String.valueOf(multiplier));

                })
                .build();

        expansion.register();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
