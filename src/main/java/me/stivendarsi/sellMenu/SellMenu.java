package me.stivendarsi.sellMenu;

import me.stivendarsi.sellMenu.commands.CommandHandler;
import me.stivendarsi.sellMenu.menu.MenuEventHandler;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SellMenu extends JavaPlugin {
    private static SellMenu plugin;
    private static LuckPerms luckPerms;

    private static MainHandler mainHandler;

    public static MainHandler mainHandler(){
        return mainHandler;
    }

    public static LuckPerms luckPerms(){
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

    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
