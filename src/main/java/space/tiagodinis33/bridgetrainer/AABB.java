package space.tiagodinis33.bridgetrainer;

import org.bukkit.entity.Player;

public class AABB {
    int min_x;
    int min_y;
    int min_z;
    int max_x;
    int max_y;
    int max_z;
    public AABB(
        int min_x,
        int min_y,
        int min_z,
        int max_x,
        int max_y,
        int max_z
    ){
        // Max
        this.max_x = max_x;
        this.max_y = max_y;
        this.max_z = max_z;
        // Min
        this.min_x = min_x;
        this.min_y = min_y;
        this.min_z = min_z;
    }
    /**
     * Swaps min_x/y/z and max_x/y/z if min is greater that max
     */
    void correct_bounds(){
        this.min_x = Math.min(this.max_x, this.min_x);
        this.min_y = Math.min(this.max_y, this.min_y);
        this.min_z = Math.min(this.max_z, this.min_z);
        this.max_x = Math.max(this.max_x, this.min_x);
        this.max_y = Math.max(this.max_y, this.min_y);
        this.max_z = Math.max(this.max_z, this.min_z);
    }
    /** 
     * Checks if a point is inside this AABB
     * The aabb bounds must be valid, use {@link AABB#correct_bounds()} if you're not sure
    */
    boolean point_is_inside_aabb(double x, double y, double z) {
        return x >= min_x && x <= max_x &&
               y >= min_y && y <= max_y &&
               z >= min_z && z <= max_z;
    }
}
