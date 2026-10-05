package MMA76230501C;
import java.util.Arrays;

//
// Internal / External Cylinder
//

public class Cylinder {

    // Internal / External Cylinder
    //  -1 - internal
    //   1 - external
    protected int intExt;

    // CMM
    protected double zDSafe;     // safe distance on Z
    protected double xyDSafe;    // safe distance on XY
    protected String probe;      // Using probe
    protected int mainA;         // Using angle A of probe

    // Program
    protected String objPref;
    protected int dimNum;        // - 1 from start

    // Cylinder
    public static  record Point(double x, double y, double z) {
    }

    public static  Point center;      // Center of cylinder

    // Slices of cylinder
    public static  record CSlice(double z, double d, double hT, double lT) {
    }

    // z - top of cylinder on Z
    // d - diameter
    // lT and hT - tolerances low and high
    public static CSlice[] slices;

    // Direction of movements
    protected record MoveDir(int b) {
    }

    protected final MoveDir[] moveDirs = {
            new MoveDir(0),
            new MoveDir(90),
            new MoveDir(180),
            new MoveDir(-90)
    };


    protected Cylinder(
            int intExt,
            double zDSafe,
            double xyDSafe,
            String probe,
            int mainA,
            String objPref,
            int dimNum,
            Point center,
            CSlice[] slices) {

        this.intExt = intExt;
        this.zDSafe = zDSafe;
        this.xyDSafe = xyDSafe;
        this.probe = probe;
        this.mainA = mainA;
        this.objPref = objPref;
        this.dimNum = dimNum;
        this.center = center;
        this.slices = slices;
    }


    protected void changeProbe(int a, int b) {

        if (a == 0)
            b = 0;

        System.out.println(
                String.format(
                        "SNSLCT/SA(" + probe + ")",
                        a / 1.0,
                        b / 1.0));
    }


    protected void goTo(Point p) {

        System.out.println(
                String.format(
                        "GOTO/%.3f, %.3f, %.3f",
                        p.x() + center.x(),
                        p.y() + center.y(),
                        p.z() + center.z()));
    }


    protected void goToZ(double z) {

        System.out.println(
                String.format(
                        "GOTO/ZAXIS,%.3f",
                        z + center.z()));
    }


    protected void goToXY(double x, double y) {

        System.out.println(
                String.format(
                        "GOTO/XAXIS,%.3f,YAXIS,%.3f, ",
                        x + center.x(),
                        y + center.y()));
    }


    protected void outputTol(String obj, String tol) {

        System.out.println(
                String.format(
                        "OUTPUT/FA(%s),TA(%s)",
                        obj,
                        tol));
    }


    protected String getPointName(CSlice slice, MoveDir dir) {

        return String.format(
                "P_%s_%d_%.3f",
                objPref,
                dir.b(),
                slice.z());
    }


    protected String getCirclName(CSlice slice) {

        return String.format(
                "C_%s_%.3f",
                objPref,
                slice.z());
    }


    //
    //
    // MAIN
    //
    //

    public void run() {

        System.out.println("SNSET/APPRCH,5");
        System.out.println("SNSET/RETRCT,5");
        System.out.println("SNSET/SEARCH,5");
        System.out.println("SNSET/CLRSRF,5");
        System.out.println("SNSET/DEPTH,0");


        System.out.println(
                String.format(
                        "RECALL/SA(" + probe + ")",
                        0.0,
                        0.0));

        for (MoveDir dir : moveDirs) {

            System.out.println(
                    String.format(
                            "RECALL/SA(" + probe + ")",
                            mainA / 1.0,
                            dir.b() / 1.0));
        }


        double minR = Arrays.stream(slices)
                .mapToDouble(CSlice::d)
                .min()
                .orElseThrow() / 2;

        double maxR = Arrays.stream(slices)
                .mapToDouble(CSlice::d)
                .max()
                .orElseThrow() / 2;


        goToZ(zDSafe);

        changeProbe(0, 0);

        goTo(new Point(0, 0, zDSafe));


        for (MoveDir dir : moveDirs) {

            System.out.println("$$ ");
            System.out.println("$$ " + dir.b());
            System.out.println("$$ ");

            changeProbe(mainA, dir.b());

            // System.out.println (dir.b+" "+Math.sin(Math.toRadians(dir.b))+
            // " "+Math.cos(Math.toRadians(dir.b)));

            double xCos = Math.cos(Math.toRadians(dir.b()));
            double xSin = Math.sin(Math.toRadians(dir.b()));


            for (CSlice slice : slices) {

                String xPN = getPointName(slice, dir);

                double px =
                        xCos * (slice.d() / 2 + intExt * xyDSafe);

                double py =
                        xSin * (slice.d() / 2 + intExt * xyDSafe);


                goToXY(
                        -1 * intExt * px,
                        -1 * intExt * py);

                goToZ(slice.z());


                px = xCos * (slice.d() / 2);
                py = xSin * (slice.d() / 2);


                System.out.println(
                        String.format(
                                "F(%s)=FEAT/POINT,CART,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f",
                                xPN,
                                -1 * intExt * px,
                                -1 * intExt * py,
                                slice.z(),
                                -1 * xCos,
                                -1 * xSin,
                                0.0));


                System.out.println(
                        String.format(
                                "MEAS/POINT,F(%s),1",
                                xPN));

                System.out.println("ENDMES");
            }


            if (intExt == -1)

                goToXY(
                        xCos * (minR - xyDSafe),
                        xSin * (minR - xyDSafe));

            else

                goToXY(
                        -1 * xCos * (maxR + xyDSafe),
                        -1 * xSin * (maxR + xyDSafe));


            goToZ(zDSafe);
        }


        // Report

        System.out.println("$$ ");
        System.out.println("$$  CALCULATION ");
        System.out.println("$$ ");


        for (CSlice slice : slices) {

            String cName = getCirclName(slice);


            System.out.println(
                    String.format(
                            "F(%s)=FEAT/CIRCLE,INNER,CART,%.3f,%.3f,%.3f,0,0,-1,%.3f",
                            cName,
                            0.0,
                            0.0,
                            slice.z(),
                            slice.d()));


            String makeC =
                    "CONST/CIRCLE,F(" + cName + "),BF";


            for (MoveDir dir : moveDirs) {

                makeC =
                        makeC
                                + ",FA("
                                + getPointName(slice, dir)
                                + ")";
            }


            System.out.println(makeC);


            String t1Name =
                    String.format(
                            "%s_DIAM_%.3f_%.3f",
                            objPref,
                            slice.z(),
                            slice.d());


            String t2Name =
                    String.format(
                            "%s_CIRC_%.3f_%.3f",
                            objPref,
                            slice.z(),
                            slice.d());


            System.out.println(
                    String.format(
                            "T(%s)=TOL/DIAM,%.3f,%.3f",
                            t1Name,
                            slice.lT(),
                            slice.hT()));


            System.out.println(
                    String.format(
                            "T(%s)=TOL/CIRLTY,0.2",
                            t2Name));


            System.out.println(
                    String.format(
                            "TEXT/OUTFIL,'Dim. %d_1'",
                            ++dimNum));


            outputTol(cName, t1Name);


            System.out.println(
                    String.format(
                            "TEXT/OUTFIL,'Dim. %d_2'",
                            dimNum));


            outputTol(cName, t2Name);
        }
    }
}