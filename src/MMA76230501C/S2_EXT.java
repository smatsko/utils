package MMA76230501C;
import MMA76230501C.Cylinder.CSlice;
import MMA76230501C.Cylinder.Point;

public final class S2_EXT extends Cylinder {

    public S2_EXT() {
        super(
            1,                              // 1 - external, -1 - internal  

            // CMM
            100.0,                          // safe distance on Z
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
            		new CSlice(-1.0, 183.8, 0.2, -0.2), 
        			new CSlice(-3.0, 183.8, 0.2, -0.2),
        			new CSlice(-30.0, 183.8, 0.2, -0.2), 
        			new CSlice(-60.0, 183.8, 0.2, -0.2), 
        			new CSlice(-87.0, 183.8, 0.2, -0.2),
        			new CSlice(-90.5, 183.8, 0.2, -0.2),
        			new CSlice(-94.5, 183.8, 0.2, -0.2),
        			new CSlice(-98.5, 183.8, 0.2, -0.2)        	            		
            }
        );
    }

    public static void main(String[] args) {
        S2_EXT cylinder = new S2_EXT();
        cylinder.run();
    }
}