package MMA76230501C;
import MMA76230501C.Cylinder.CSlice;
import MMA76230501C.Cylinder.Point;

public final class S2_INT extends Cylinder {

    public S2_INT() {
        super(
            -1,                              // 1 - external, -1 - internal  

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
            		new CSlice(-1.0, 181.1, 0.2, -0.2), 
        			new CSlice(-3.0, 181.1, 0.2, -0.2),
        			new CSlice(-30.0, 181.1, 0.2, -0.2), 
        			new CSlice(-60.0, 181.1, 0.2, -0.2), 
        			new CSlice(-87.0, 181.1, 0.2, -0.2),
        			new CSlice(-90.5, 179.1, 0.2, -0.1),
        			new CSlice(-94.5, 179.1, 0.2, -0.1),
        			new CSlice(-98.5, 179.1, 0.2, -0.1)        	            		
            }
        );
    }

    public static void main(String[] args) {
        S2_INT cylinder = new S2_INT();
        cylinder.run();
    }
}