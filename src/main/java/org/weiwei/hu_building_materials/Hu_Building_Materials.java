package org.weiwei.hu_building_materials;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.weiwei.hu_building_materials.listener.MenuListener;
import org.weiwei.hu_building_materials.command.Command;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import uilt.Config;

public final class Hu_Building_Materials extends JavaPlugin {

    @Getter
    private static Hu_Building_Materials instance = null;

    @Getter
    private static boolean coinsEngine;

    @Override
    public void onEnable() {
        instance = this;
        Config.loadConfig();

        getCommand("build_shop").setExecutor(new Command());

        getServer().getPluginManager().registerEvents(new MenuListener(), this);


        if(Bukkit.getPluginManager().isPluginEnabled("CoinsEngine")) {
            coinsEngine = true;
        }else {
            Hu_Building_Materials.getInstance().getLogger().warning("CoinsEngine is not enabled!");
        }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
