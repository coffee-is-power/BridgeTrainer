package space.tiagodinis33.bridgetrainer;

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
}
