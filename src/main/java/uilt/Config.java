package uilt;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

import static org.weiwei.hu_building_materials.Hu_Building_Materials.instance;

public class Config {

    public static final String PREFIX = "PREFIX";

    public static final String PERMISSIONS_USE = "PERMISSIONS.USE";

    public static final String ITEM_LORE = "ITEM_LORE";

    public static final String BMB_GUINAME = "BUILDING_MATERIAL_BLOCK.GUINAME";
    public static final String BMB_BLOCK = "BUILDING_MATERIAL_BLOCK.BLOCK";

    public static final String DB_GUINAME = "DYED_BLOCK.GUINAME";
    public static final String DB_BLOCK = "DYED_BLOCK.BLOCK";

    public static final String OTH_GUINAME = "OTH_BLOCK.GUINAME";
    public static final String OTH_BLOCK = "OTH_BLOCK.BLOCK";


    public static final String MEG_YES_DOWN = "MESSAGE.YES_DOWN";
    public static final String MEG_NO_DOWN = "MESSAGE.NO_DOWN";
    public static final String MEG_INV_NOSLOT = "MESSAGE.INV_NOSLOT";
    public static final String MEG_COIN_ERROR = "MESSAGE.COIN_ERROR";


    @Getter
    private static YamlConfiguration config;

    @Getter
    private static List<String> ITEM_LORE_List;

    @Getter
    private static List<String> BMB_BLOCK_List;
    @Getter
    private static List<String> DB_BLOCK_List;
    @Getter
    private static List<String> OTH_BLOCK_List;

    public static void loadConfig() {
        File file = new File(instance.getDataFolder(), "Config.yml");
        if (!file.exists()) {
            instance.getLogger().info("Create Config.yml");
            instance.saveResource("Config.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(file);

        BMB_BLOCK_List = config.getStringList(BMB_BLOCK);
        DB_BLOCK_List = config.getStringList(DB_BLOCK);
        OTH_BLOCK_List = config.getStringList(OTH_BLOCK);
        ITEM_LORE_List = config.getStringList(ITEM_LORE);
    }

    public static List<String> TYPE_INT_getlist(String MAT_TYPE,String TYPE_INT){
        List<String> newlist;
        newlist = config.getStringList(MAT_TYPE + "." + TYPE_INT);
        return newlist;
    }
    public static double vip_discount(String VIP){
        return config.getDouble("PERMISSIONS.VIP." + VIP);
    }
}
