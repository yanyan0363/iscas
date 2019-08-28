package arcgis;

public class Test {

	public static void main(String[] args) {
		double b1 = 38.7067;
		double l1 = 142.6899;
		double b2 = 38.6985;
		double l2 = 142.7442;
//		double dis = GisHelper.calDisByBL(l1,b1,l2,b2);
		double dis = GisHelper.calDisByBLProjTo3857(l1,b1,l2,b2);
		System.out.println("dis:"+dis);
		System.out.println(1.7367-1.7179);
		double b3 = 38.1;
		double l3 = 142.6;
//		dis = GisHelper.calDisByBL(l1, b1, l3, b3);
		dis = GisHelper.calDisByBLProjTo3857(l1, b1, l3, b3);
		System.out.println(dis);
	}
}
