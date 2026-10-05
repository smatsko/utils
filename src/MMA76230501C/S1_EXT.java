package MMA76230501C;
import MMA76230501C.Cylinder.CSlice;
import MMA76230501C.Cylinder.Point;

public final class S1_EXT extends Cylinder {

    public S1_EXT() {
        super(
            1,                              // 1 - external, -1 - internal  

            // CMM
            250.0,                          // safe distance on Z
            5.0,                            // safe distance on XY
            "A%.1f_B%.1f_D2X50_TP20",       // Using probe
            45,                             // Using angle A of probe

            // Program
            "EXT",                          // Prefix in objects   
            199,                            // - 1 from start

            // Cylinder
            new Point(0.0, 0.0, 0.0),       // Center of cylinder

            // Slices of cylinder
            new CSlice[] {
            		new CSlice(-1.0, 188.9, 0.2, -0.2), 
        			new CSlice(-3.0, 188.9, 0.2, -0.2),
        			new CSlice(-70.0, 183.8, 0.2, -0.2), 
        			new CSlice(-90.0, 183.8, 0.2, -0.2), 
        			new CSlice(-110.0, 183.8, 0.2, -0.2),
        			new CSlice(-120.0, 183.8, 0.2, -0.2),
        			new CSlice(-135.0, 183.8, 0.2, -0.2),
        			new CSlice(-145.0, 183.8, 0.2, -0.2),
        			new CSlice(-170.0, 183.8, 0.2, -0.2),
        			new CSlice(-180.0, 183.8, 0.2, -0.2)            		
            }
        );
    }

    public static void main(String[] args) {
        S1_EXT cylinder = new S1_EXT();
        cylinder.run();
    }
}