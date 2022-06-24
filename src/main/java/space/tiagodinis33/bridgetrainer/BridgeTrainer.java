package space.tiagodinis33.bridgetrainer;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Sets;

public final class BridgeTrainer extends JavaPlugin {
    Plot plot = new Plot(
        new AABB(100, 245, 96, 106, 256, 131), 
        new Location(Bukkit.getWorld("world"), 103, 251, 102)
    );
    HashSet<Plot> plots = Sets.newHashSet(plot);
    @Override
    public void onEnable() {
        plot.spawn_player(Bukkit.getPlayer("XtiagodinisX"));
        plots.forEach((pl) -> {
            Bukkit.getPluginManager().registerEvents(pl.new EventListener(), BridgeTrainer.this);
        });
    }

    @Override
    public void onDisable() {
        
    }
}
