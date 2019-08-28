package test;

import arcgis.GisHelper;
import beans.BLH;
import beans.XYZ;

public class XYZToBLH {

	public static void main(String[] args) {
//		SMCLX
//		 -1191887.5212
//		 5447818.1830
//		 3087653.6967
//		ZBDLW
//		 -1261781.7365
//		 5448153.8253
//		 3060158.3408
//		HBDMB
//		 -1308587.9283
//		 5436644.3176
//		 3058710.6915
		 XYZ SMCLX = new XYZ(-1191887.5212, 5447818.1830, 3087653.6967);
		 XYZ ZBDLW = new XYZ(-1261781.7365, 5448153.8253, 3060158.3408);
		 XYZ HBDMB = new XYZ(-1308587.9283, 5436644.3176, 3058710.6915);
		 System.out.println("SMCLX:"+GisHelper.XYZtoBLH(SMCLX));
		 System.out.println("ZBDLW:"+GisHelper.XYZtoBLH(ZBDLW));
		 System.out.println("HBDMB:"+GisHelper.XYZtoBLH(HBDMB));
//		 SMCLX:BLH:29.1354467628281,102.34086674232826,1295.790129329
//		 ZBDLW:BLH:28.849948373689124,103.03969473960991,1685.731281258
//		 HBDMB:BLH:28.840443264275624,103.53352684581087,598.12175305
		 //SMTWX
		 XYZ xyz = GisHelper.BLHtoXYZ(new BLH(28.808612, 102.27843, 0));
		 System.out.println(xyz);

	}
}
