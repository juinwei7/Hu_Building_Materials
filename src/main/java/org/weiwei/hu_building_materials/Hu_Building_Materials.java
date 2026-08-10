package org.weiwei.hu_building_materials;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.weiwei.hu_building_materials.listener.MenuListener;
import org.weiwei.hu_building_materials.command.Command;
import uilt.Config;

public final class Hu_Building_Materials extends JavaPlugin {

    @Getter
    private static Hu_Building_Materials instance = null;

    @Override
    public void onEnable() {
        instance = this;
        if (!Config.loadConfig()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("build_shop").setExecutor(new Command());

        getServer().getPluginManager().registerEvents(new MenuListener(), this);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
