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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Plot {
    final AABB bounds;
    Optional<Player> owner = Optional.empty();
    final Location spawn;
    boolean started = false;
    long started_time_milis = -1;
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
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.SANDSTONE, 64), new ItemStack(Material.SANDSTONE, 64),new ItemStack(Material.SANDSTONE, 64));
        player.setHealth(20);
        player.setFoodLevel(20);
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
        public void onPressurePlatePress(PlayerInteractEvent e){
            if(e.getPlayer().equals(owner.get())){
                if(e.getAction() == Action.PHYSICAL){
                    if(e.getClickedBlock().getType() == Material.GOLD_PLATE){
                        if(started){
                            end();
                        }
                    }
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
                } else {
                    if(!started){
                        start();
                    }
                    Block block = e.getBlock();
                    placed_blocks.add(new Vector(block.getX(), block.getY(), block.getZ()));
                }
            }
        }
    }
    public void start(){
        started = true;
        started_time_milis = System.currentTimeMillis();
        owner.get().sendMessage("§e[BridgeTrainer] §aYou begun bridging, GO!");
    }
    public void end(){
        owner.get().sendMessage("§e[BridgeTrainer] §aGood job! You took " + (System.currentTimeMillis() - started_time_milis)/1000 + "s to complete the bridge!");
        spawn_player(owner.get());
        reset_plot();
    }
    /**
     * This is called when the player looses
     * e.g. when the player falls into the void
     */
    private void player_lost(Player player){
        spawn_player(player);
        if(started){
            owner.get().sendMessage("§e[BridgeTrainer] §cYou fell! §aYou took " + (System.currentTimeMillis() - started_time_milis)/1000 + "s!");
        }
        reset_plot();
    }
    public void reset_plot(){
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
        started = false;
        started_time_milis = -1;
    }
}
