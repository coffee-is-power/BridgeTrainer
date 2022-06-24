package space.tiagodinis33.bridgetrainer;

import java.util.Objects;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Plot {
    AABB bounds;
    Optional<Player> owner = Optional.empty();
    Location spawn;
    final Plot self = this;
    public Plot(AABB bounds, Location spawn) {
        this(bounds, spawn, null);
    }
    public Plot(AABB bounds, Location spawn, Player owner) {
        Objects.requireNonNull(bounds);
        Objects.requireNonNull(spawn);
        self.bounds = bounds;
        self.spawn = spawn;
        self.owner = Optional.ofNullable(owner);
        bounds.correct_bounds();
    }
    public void spawn_player(Player player){
        self.owner = Optional.of(player);
        player.teleport(self.spawn);
    }
    public class EventListener implements Listener{
        @EventHandler
        public void onMove(PlayerMoveEvent e){
            if(e.getPlayer().equals(owner.orElse(null))){
                if(e.getPlayer().getLocation().getY() < bounds.min_y){
                    player_lost(e.getPlayer());
                }
            }
        }
        @EventHandler
        public void onBlockPlace(BlockPlaceEvent e) {
            if(e.getPlayer().equals(owner.orElse(null))){
                bounds.correct_bounds();
                double x = e.getBlock().getX();
                double y = e.getBlock().getY();
                double z = e.getBlock().getZ();
                if(!bounds.point_is_inside_aabb(x, y, z)){
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("§c[BridgeTrainer] You cannot place blocks out of bounds");
                }
            }
        }
    }
    /**
     * This is called when the player looses
     * e.g. when the player falls into the void
     */
    private void player_lost(Player player){
        spawn_player(player);
    }
}
