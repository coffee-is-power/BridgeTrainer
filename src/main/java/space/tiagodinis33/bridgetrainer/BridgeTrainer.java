package space.tiagodinis33.bridgetrainer;

import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class BridgeTrainer extends JavaPlugin implements Listener {
    List<Plot> plots = null;
    @Override
    public void onEnable() {
        plots = ConfigParser.getPlots(this);
        for(Player player : Bukkit.getOnlinePlayers()){
            Optional<Plot> plot_opt = request_plot();
            if(!plot_opt.isPresent()){
                player.kickPlayer("§cSorry but all plots are busy.");
            } else {
                plot_opt.get().spawn_player(player);
            }
        }
        plots.forEach((pl) -> {
            Bukkit.getPluginManager().registerEvents(pl.new EventListener(), BridgeTrainer.this);
        });
        Bukkit.getPluginManager().registerEvents(this, BridgeTrainer.this);

    }
    private Optional<Plot> request_plot(){
        return plots.stream().filter(plot -> !plot.owner.isPresent()).findFirst();
    }
    private Optional<Plot> get_plot_of(Player player){
        return plots.stream().filter(plot -> plot.owner.isPresent()).filter((plot) -> plot.owner.get().equals(player)).findFirst();
    }
    @EventHandler
    public void onDamage(EntityDamageEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    public void onFallingBlockCollision(EntityChangeBlockEvent e){
        if(e.getEntity() instanceof FallingBlock) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        Optional<Plot> plot_opt = request_plot();
        if(!plot_opt.isPresent()){
            player.kickPlayer("§cSorry but all plots are busy.");
        } else {
            plot_opt.get().spawn_player(player);
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Optional<Plot> plot_opt = get_plot_of(e.getPlayer());
        plot_opt.ifPresent((plot) -> {
            plot.owner = Optional.empty();
            plot.reset_plot();
        });
    }
    @Override
    public void onDisable() {
        
    }
}
