package space.tiagodinis33.bridgetrainer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Plot {
    final AABB bounds;
    Optional<Player> owner = Optional.empty();
    final Location spawn;

    public Plot(AABB bounds, Location spawn) {
        this(bounds, spawn, null);
    }
    public Plot(AABB bounds, Location spawn, Player owner) {
        Objects.requireNonNull(bounds);
        Objects.requireNonNull(spawn);
        this.bounds = bounds;
        this.spawn = spawn;
        this.owner = Optional.ofNullable(owner);
        bounds.correct_bounds();
    }
    public void spawn_player(Player player){
        this.owner = Optional.of(player);
        player.teleport(this.spawn);
    }
    private final HashSet<Vector> placed_blocks = new HashSet<>();
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
        public void onFallingBlockCollision(EntityChangeBlockEvent e){
            if(e.getEntity() instanceof FallingBlock) {
                e.setCancelled(true);
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
                } else {
                    
                    Block block = e.getBlock();
                    placed_blocks.add(new Vector(block.getX(), block.getY(), block.getZ()));
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
        reset_plot();
    }
    private void reset_plot(){
        for(Vector block_pos : placed_blocks){
            Block block = spawn.getWorld().getBlockAt(block_pos.getBlockX(), block_pos.getBlockY(), block_pos.getBlockZ());
            Material old_material = block.getType();
            byte data = block.getData();
            block.setType(Material.AIR);
            FallingBlock falling_block = spawn.getWorld()
                .spawnFallingBlock(
                    new Location(
                        spawn.getWorld(),
                        block_pos.getX(), 
                        block_pos.getY(), 
                        block_pos.getZ()
                    ), 
                    old_material,
                    data
                );
            falling_block.setVelocity(new Vector(new Random().nextDouble() % 10, new Random().nextDouble() % 10, new Random().nextDouble() % 10));
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(BridgeTrainer.class), () -> {
                falling_block.remove();
            }, 20*3);
        }
        placed_blocks.clear();
    }
}
