package MMA76230501C;

import java.util.Arrays;


//
// Internal / External Cylinder 
//


public final class S2_EXT_old {

	static // Internal / External Cylinder
    //  -1 - internal
	//   1 - external  
	
	int intExt = 1;
	
	
	// CMM

	static final Double zDSafe = 100.0; // safe distance on Z
	static final Double xyDSafe = 5.0; // safe distance on XY
	static String probe = "A%.1f_B%.1f_D2X50_TP20"; // Using probe
	static final Integer mainA = 45; // Using angle A of probe

	// Program

	static String objPref = "EXT";
	static int dimNum = 199;  // - 1 from start
	
	// Cylinder

	record point(double x, double y, double z) {
	}

	static final point center = new point(0.0, 0.0, 0.0); // Center of cylinder

	// Slices of cylinder

	record cSlice(double z, double d, double hT, double lT) {
	}

	// z - top of cylinder on Z
	// d - diameter
	// lT and hT - tolerances low and high

	static final cSlice[] cSlices = { 
			new cSlice(-1.0, 188.9, 0.2, -0.2), 
			new cSlice(-3.0, 188.9, 0.2, -0.2),
			new cSlice(-70.0, 183.8, 0.2, -0.2), 
			new cSlice(-90.0, 183.8, 0.2, -0.2), 
			new cSlice(-110.0, 183.8, 0.2, -0.2),
			new cSlice(-120.0, 183.8, 0.2, -0.2),
			new cSlice(-135.0, 183.8, 0.2, -0.2),
			new cSlice(-145.0, 183.8, 0.2, -0.2),
			new cSlice(-170.0, 183.8, 0.2, -0.2),
			new cSlice(-180.0, 183.8, 0.2, -0.2)};

	// Direction of movements

	final record moveDir(int b) {
	};

	static moveDir[] moveDirs = { new moveDir(0), new moveDir(90), new moveDir(180),
			new moveDir(-90) };

	
	static void changeProbe(Integer a, Integer b) {
		if (a == 0)
			b = 0;
		System.out.println(String.format("SNSLCT/SA(" + probe + ")", a / 1.0, b / 1.0));
	}

	static void goTo(point p) {
		System.out.println(String.format("GOTO/%.3f, %.3f, %.3f", p.x + center.x, p.y + center.y, p.z + center.z));
	}

	static void goToZ(Double z) {
		System.out.println(String.format("GOTO/ZAXIS,%.3f", z + center.z));
	}

	static void goToXY(Double x, Double y) {
		System.out.println(String.format("GOTO/XAXIS,%.3f,YAXIS,%.3f, ", x + center.x, y + center.y));
	}

	static void outputTol(String obj, String tol) {
		System.out.println(String.format("OUTPUT/FA(%s),TA(%s)", obj, tol));
	}

	static String getPointName(cSlice slice, moveDir dir) {
		return (String.format("P_%s_%d_%.3f", objPref, dir.b(), slice.z()));
	}

	static String getCirclName(cSlice slice) {
		return (String.format("C_%s_%.3f", objPref, slice.z()));
	}

	//
	//
	// MAIN
	//
	//

	public static void main(String[] args) {

		System.out.println("SNSET/APPRCH,5");
		System.out.println("SNSET/RETRCT,5");
		System.out.println("SNSET/SEARCH,5");
		System.out.println("SNSET/CLRSRF,5");
		System.out.println("SNSET/DEPTH,0");


		
		
		
		System.out.println(String.format("RECALL/SA(" + probe + ")", 0.0, 0.0));
		for (moveDir dir : moveDirs) {
			System.out.println(String.format("RECALL/SA(" + probe + ")", mainA / 1.0, dir.b() / 1.0));
		}

		double minR = Arrays.stream(cSlices).mapToDouble(cSlice::d).min().orElseThrow() / 2;
		double maxR = Arrays.stream(cSlices).mapToDouble(cSlice::d).max().orElseThrow() / 2;
 
		
		goToZ(zDSafe);
		changeProbe(0, 0);
		goTo(new point(0, 0, zDSafe));

		for (moveDir dir : moveDirs) {
			System.out.println("$$ ");
			System.out.println("$$ " + dir.b());
			System.out.println("$$ ");

			changeProbe(mainA, dir.b());
			// System.out.println (dir.b+" "+Math.sin(Math.toRadians(dir.b))+"
			// "+Math.cos(Math.toRadians(dir.b)));

			
			Double xCos = Math.cos(Math.toRadians(dir.b));
			Double xSin = Math.sin(Math.toRadians(dir.b));

			
			for (cSlice slice : cSlices) {

				String xPN = getPointName(slice, dir);

				Double px = xCos * (slice.d() / 2 + intExt * xyDSafe);
				Double py = xSin * (slice.d() / 2 + intExt * xyDSafe);
				goToXY(-1*intExt*px, -1*intExt*py);
				goToZ(slice.z());

				px = xCos * (slice.d() / 2);
				py = xSin * (slice.d() / 2);

				System.out.println(String.format("F(%s)=FEAT/POINT,CART,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f", xPN, -1*intExt*px, -1*intExt*py,
						slice.z(), -1 * xCos, -1 * xSin, 0.0));
				System.out.println(String.format("MEAS/POINT,F(%s),1", xPN));
				System.out.println("ENDMES");

				
				
			}

			if (intExt == -1) 
			    goToXY( xCos * (minR - xyDSafe),  xSin * (minR - xyDSafe));
			else 
			    goToXY( -1 * xCos * (maxR + xyDSafe),  -1 * xSin * (maxR + xyDSafe));
				
			goToZ(zDSafe);
						
		}

// Report

		System.out.println("$$ ");
		System.out.println("$$  CALCULATION ") ;
		System.out.println("$$ ");
		
		
		for (cSlice slice : cSlices) {

		    String cName = getCirclName(slice);
			
		    
		    System.out.println(String.format("F(%s)=FEAT/CIRCLE,INNER,CART,%.3f,%.3f,%.3f,0,0,-1,%.3f", cName, 0.0, 0.0, slice.z(), slice.d() ));
			String makeC = "CONST/CIRCLE,F(" +cName  + "),BF";
			for (moveDir dir : moveDirs) {
				makeC = makeC + ",FA(" + getPointName(slice, dir) + ")";
			}
			System.out.println(makeC);

			String t1Name = String.format("%s_DIAM_%.3f_%.3f", objPref, slice.z(), slice.d());
			String t2Name = String.format("%s_CIRC_%.3f_%.3f", objPref, slice.z(), slice.d());
			
			System.out.println(String.format("T(%s)=TOL/DIAM,%.3f,%.3f", t1Name, slice.lT(), slice.hT()));
			System.out.println(String.format("T(%s)=TOL/CIRLTY,0.2", t2Name));
			System.out.println(String.format("TEXT/OUTFIL,'Dim. %d_1'", ++dimNum));
			outputTol(cName, t1Name);
			System.out.println(String.format("TEXT/OUTFIL,'Dim. %d_2'", dimNum));
			outputTol(cName, t2Name);

		}

	}
}

