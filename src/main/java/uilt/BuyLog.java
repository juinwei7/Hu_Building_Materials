package uilt;

import org.bukkit.Bukkit;
import org.weiwei.hu_building_materials.Hu_Building_Materials;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class BuyLog {

    private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter LINE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void log(String message,String type) {
        Hu_Building_Materials plugin = Hu_Building_Materials.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> writeLog(plugin, message, type));
    }

    private static synchronized void writeLog(Hu_Building_Materials plugin, String message, String type) {
        LocalDateTime now = LocalDateTime.now();
        Path directory = plugin.getDataFolder().toPath().resolve(type);
        Path file = directory.resolve(FILE_FORMAT.format(now) + ".log");
        String line = "[" + LINE_FORMAT.format(now) + "] " + message + System.lineSeparator();

        try {
            Files.createDirectories(directory);
            Files.writeString(file, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "無法寫入交易紀錄 " + file, exception);
        }
    }
}
