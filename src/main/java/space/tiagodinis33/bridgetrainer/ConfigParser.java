package space.tiagodinis33.bridgetrainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigParser {
    public static List<Plot> getPlots(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        Configuration config = plugin.getConfig();
        return new ArrayList<Plot>(config.getMapList("plots").stream().map(map -> {
            AABB bounds = getAABBFromMap((Map<?, ?>)map.get("bounds"));
            Map<?, ?> spawn = (Map<?, ?>) map.get("spawn");
            return new Plot(bounds, new Location(Bukkit.getWorld((String) map.get("world")), (Integer) spawn.get("x"), (Integer) spawn.get("y"), (Integer) spawn.get("z")));
        }).collect(Collectors.toList()));
    }
    public static AABB getAABBFromMap(Map<?, ?> map) {
        Map<?, ?> min = (Map<?, ?>) map.get("min");
        Map<?, ?> max = (Map<?, ?>) map.get("max");
        return new AABB(
            (Integer) min.get("x"),
            (Integer) min.get("y"),
            (Integer) min.get("z"),
            (Integer) max.get("x"),
            (Integer) max.get("y"),
            (Integer) max.get("z")
        );
    }
}
