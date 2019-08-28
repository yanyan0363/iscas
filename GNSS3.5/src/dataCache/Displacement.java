package dataCache;

import java.util.Date;

import beans.Loc;
import mathUtil.DoubleUtil;
import utils.Config;

public class Displacement {
	public DataCache myCache = null ; 
	public double xDisplacement = 0 ;//东西向
	public double yDisplacement = 0 ;//南北向
	public double zDisplacement = 0 ;//地心向,即垂直方向位移
	public double displacement2D = 0 ;//水平方向的位移距离，即东西向和南北向位移的平方开根号
//	public double zDisToLastDis = 0;//地心向位移量，相对于上一个时间点的z位移量,不小于0
//	public Loc loc;
	public Date time = null ;//位移时刻，数据产生的时刻
	public Date localTime = null;//接收到数据时刻的本机时间
	private boolean isHandled = false;
	protected boolean isDispExist = false;//当前disp数据是否存在,在DispWithMEMS(Date time)初始化时，置为false
	private double PGD = 0;//表示ENU合位移
	
	/**
	 * 获取ENU合位移
	 * @return
	 */
	public double getPGD() {
		if (PGD == 0) {
			PGD = Math.sqrt(DoubleUtil.add(DoubleUtil.add(DoubleUtil.mul(xDisplacement, xDisplacement), DoubleUtil.mul(yDisplacement, yDisplacement)), DoubleUtil.mul(zDisplacement, zDisplacement)));
			return PGD;
		}else {
			return PGD;
		}
	}
	public Displacement() {
		// TODO Auto-generated constructor stub
	}

	public void dispose(){
//		System.out.println("no dispose run in class Displacement");
		this.myCache = null;
		this.time = null;
		this.localTime = null;
		this.xDisplacement = 0;
		this.yDisplacement = 0;
		this.zDisplacement = 0;
		this.displacement2D = 0;
		this.PGD = 0;
	}
	/*public Displacement(DataCache c , double xDisplacement, double yDisplacement, double zDisplacement, double zDisToLastDis, Date t, Loc loc, Date localNow){
		this.myCache = c ;
		this.xDisplacement = xDisplacement;
		this.yDisplacement = yDisplacement;
		this.zDisplacement =  zDisplacement; 
		this.zDisToLastDis = zDisToLastDis;
		this.displacement2D = Math.sqrt(xDisplacement*xDisplacement+yDisplacement*yDisplacement) ;
		this.time = t; 
		this.loc = loc;
		this.localTime = localNow;
		this.isDispExist = true;
//		if(c!= null )
//			c.addDisplacement(this) ;	
	}*/
	/**
	 * 当前位移数据是否已经被处理
	 * @return
	 */
	public boolean isHandled() {
		return isHandled;
	}

	/**
	 * 判断当前位移量是否满足地震事件
	 * @param maxZDis 表示历史数据中的最大z位移量
	 * @return
	 */
//	public boolean isEvtDisplacement(double maxZDis){
//		isHandled = true;
//		if (Math.abs(zDisplacement) >= Config.minVDis ) {
//		 
//			return true;
//		}
//		double h = Math.abs(zDisplacement) ;
//		double zmax = Math.abs(maxZDis) ;
//		if(zmax==0)
//			return false ;
//		 if(h/zmax>=Config.minVRelDis)
//		{
//		 
//			return true ;
//		} 
//		return false;
//	}
	
	public boolean isDispExist() {
		return isDispExist;
	}

	public void setDispExist(boolean isDispExist) {
		this.isDispExist = isDispExist;
	}

	@Override
	public String toString() {
		return time+", "+xDisplacement+", "+yDisplacement+", "+zDisplacement+", "+displacement2D;
	}
}
