package space.tiagodinis33.bridgetrainer;

import java.util.Objects;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    }
    public void spawn_player(Player player){
        self.owner = Optional.of(player);
        player.teleport(self.spawn);
    }
}
