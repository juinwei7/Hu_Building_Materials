package org.weiwei.hu_building_materials;

import org.bukkit.plugin.java.JavaPlugin;
import org.weiwei.hu_building_materials.Listener.menu_Listener;
import org.weiwei.hu_building_materials.command.command;
import uilt.Config;

public final class Hu_Building_Materials extends JavaPlugin {


    public static Hu_Building_Materials instance = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Config.loadConfig();

        getCommand("build_shop").setExecutor(new command());

        getServer().getPluginManager().registerEvents(new menu_Listener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
