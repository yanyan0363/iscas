package dataCache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import baseObject.BaseStation;
import beans.MEMSData;
import mainFrame.GNSSFrame;
import mathUtil.DoubleUtil;
import metaData.StaticMetaData;
import utils.Config;

public class DataCache {
	public BaseStation myStation = null ;
	public ArrayList<DispWithMEMS> myDataList = new ArrayList<>() ;//GNSS数据
	private long timeWindow = Config.timeWindow;
	public Date triggerTime = null;
	public Date ST = null;
	
	public DataCache(BaseStation s){
		this.myStation = s ;
		if(s!= null)
			s.myDataCache = this ;
	}
//	Logger logger = Logger.getLogger(DataCache.class);

 
	/**
	 * 是否触发地震(每次正序计算)
	 * 并初始化triggerTime
	 * @return
	 */
	public boolean isTriggeredAsc() {
		int size = myDataList.size();
		if (size <= 0 || size < Config.ltw+2 ) {
			return false;
		}
		int i = (int)Math.ceil(Config.ltw);
		while (i < size) {
			DispWithMEMS dispWithMEMS = myDataList.get(i);
			List<MEMSData> memsDataList = dispWithMEMS.memsDataList;
			if (memsDataList == null || memsDataList.size() <= 0) {
				i++;
				continue;
			}
			for (int j = 0; j < memsDataList.size(); j++) {
				MEMSData memsData = memsDataList.get(j);
				if (memsData.isTriggerCalculated()) {
					continue;
				}else {//计算是否触发
					memsData.setTriggerCalculated(true);
					boolean ifSLTA = isSLTATriggered(memsData, i, j);
					if (ifSLTA) {//P波初动
						System.out.println("P波初动时刻: " + this.myStation.ID+"  "+StaticMetaData.formatMs.format(memsData.time));
						return AICTrigger(memsData.time,i); 
					}
				}
			}
			i++;
		}
		return false;
	}
	/**
	 * 是否触发地震(每次倒序计算)
	 * 并初始化triggerTime
	 * @return
	 */
	public boolean isTriggeredDesc() {
//		int S = (int)(Config.MEMSHz * Config.stw);//短窗口
//		int L = (int)(Config.MEMSHz * Config.ltw);//长窗口
//		double dSNR = Math.pow(10, (Config.SNR/10));
//		double thresh = (dSNR+1)/(dSNR*Config.stw/Config.ltw+1);//阈值
		//忽略SMMNX台站的触发，临时措施
//		if (this.myStation.ID.equalsIgnoreCase("SMMNX")) {
//			return false;
//		}
		long t1 = System.nanoTime()/1000000;
		synchronized (myDataList) {
			int disIdx = myDataList.size() - 1;
			if (disIdx < 0 || disIdx < Config.ltw ) {
				return false;
			}
			DispWithMEMS lastMems = myDataList.get(disIdx);
			int memsIdx = lastMems.memsDataList.size() - 1;
			while (memsIdx == -1) {
				disIdx--;
				if (disIdx >= 0) {
					lastMems = myDataList.get(disIdx);
					memsIdx = lastMems.memsDataList.size() - 1;
				}else {
					lastMems = null;
					return false;
				}
			}
			if (disIdx < Config.ltw) {
				lastMems = null;
				return false;
			}
			int curLSize = Config.MEMSHz * disIdx + memsIdx + 1;
			if (curLSize < StaticMetaData.L) {
				lastMems = null;
				return false;
			}
			while (disIdx >= Config.ltw) {
				if (memsIdx < 0) {
					disIdx--;
					if (disIdx>=0) {
						lastMems = myDataList.get(disIdx);
						memsIdx = lastMems.memsDataList.size() - 1;
					}
					continue;
				}
//				DispWithMEMS lastDis = (DispWithMEMS)this.myDataList.get(disIdx);
				MEMSData lastMemsData = null;
				if (lastMems.memsDataList.size() > memsIdx) {
					try {
						lastMemsData = lastMems.memsDataList.get(memsIdx);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (lastMemsData == null) {
					memsIdx--;
					continue;
				}
				if (lastMemsData.isTriggerCalculated()) {//若当前时刻的MEMS数据已经计算过是否触发地震，则结束当前计算
					break;
				}else {
					lastMemsData.setTriggerCalculated(true);
				}
				boolean ifSLTA = isSLTATriggered(lastMemsData, disIdx, memsIdx);
				if (!ifSLTA) {
					if (memsIdx > 0) {
						memsIdx--;
					}else {
						disIdx--;
						while (disIdx >= 0) {
							lastMems = myDataList.get(disIdx);
							memsIdx = lastMems.memsDataList.size() - 1;
							if (memsIdx >= 0) {
								break;
							}else {
								disIdx--;
							}
						}
					}
					continue;
				}else{//P波初动
					DispWithMEMS curMems = myDataList.get(disIdx);
//					logger.info("varS:"+varS+", varL:"+varL+", SLTA: " + SLTA);
//					logger.info("P波初动时刻--: " + this.myStation.ID+"  "+StaticMetaData.formatMs.format(curMems.memsDataList.get(memsIdx).time));
					
					System.out.println("P波初动时刻00: " + this.myStation.ID+"  "+StaticMetaData.formatMs.format(curMems.memsDataList.get(memsIdx).time));
					//begin...
					//向前推移，是否存在未计算触发，且P波初动的MEMSData，若存在，则更新curDate和disIdx
					MEMSData curMEMSData = curMems.memsDataList.get(memsIdx);
					int remDis = disIdx;
					Date curDate = curMEMSData.getTime();
					while(disIdx>=Config.ltw){
						memsIdx--;
						if(memsIdx >= 0){
							MEMSData tmpMEMSData = curMems.memsDataList.get(memsIdx);
							if (tmpMEMSData.isTriggerCalculated()) {//若当前时刻的MEMS数据已经计算过是否触发地震，则结束当前计算
								break;
							}else {
								tmpMEMSData.setTriggerCalculated(true);
								boolean tmpSLTA = isSLTATriggered(tmpMEMSData, disIdx, memsIdx);
								if(tmpSLTA && tmpMEMSData.getTime().before(curDate)){
									curDate = tmpMEMSData.getTime();
									remDis = disIdx;
									continue;
								}
							}
						}else {
							disIdx--;
							if (disIdx >= Config.ltw) {
								curMems = this.myDataList.get(disIdx);
								memsIdx = curMems.memsDataList.size();
								continue;
							}else {
								disIdx++;
								break;
							}
						}
					}
					curMems = null;
					curMEMSData = null;
					curDate = null;
					//end...
//					Date curDate = curMEMSData.getTime();
					System.out.println("P波初动时刻11: " + this.myStation.ID+"  "+StaticMetaData.formatMs.format(curDate));
//					return AICTrigger(curDate,disIdx); 
					lastMems = null;
					return AICTrigger(curDate,remDis); 
				}
			}
			lastMems = null;
		}
//		System.out.println("if trigger: false " + this.myStation.ID);
		long t2 = System.nanoTime()/1000000;
		System.out.println("                                          isTriggeredDesc -- "+(t2-t1)+"ms");
		return false;
	}
	/**
	 * 获取P波触发后3s内的垂直向加速度列表
	 * @return
	 */
	public List<Double> getUDListAfterTriggerIn3s() {
		System.out.println("getUDListAfterTriggerIn3s triggerTime:"+StaticMetaData.formatMs.format(triggerTime));
		long triggerTLong = triggerTime.getTime();
		long endLong = triggerTLong + 3000;//不包含此值
		int k = 0;
//		boolean flag = false;//用于标记是否存在后三秒数据
		while (k < 3) {//等待至触发后3s数据存在,最多等3s
			int size = this.myDataList.size();
			if (size <= 0 || this.myDataList.get(size -1).time.getTime() < endLong) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				k++;
				continue;
			}else {
//				flag = true;
				break;
			}
		}
//		if (!flag) {//后三秒数据不足，返回0值
//			return 0;
//		}
		List<Double> udList = new ArrayList<>();
		for (int i = 0; i < myDataList.size(); i++) {
			if (myDataList.get(i).time.getTime() + 1000 < triggerTLong) {
				continue;
			}else if (myDataList.get(i).time.getTime() > endLong) {
				break;
			}
			DispWithMEMS dispWithMEMS = myDataList.get(i);
			List<MEMSData> memsDatas = dispWithMEMS.memsDataList;
			for (int j = 0; j < memsDatas.size(); j++) {
				long curLong = memsDatas.get(j).time.getTime();
				if (curLong>=triggerTLong && curLong < endLong) {
					udList.add(memsDatas.get(j).accH);
//					System.out.println(StaticMetaData.formatMs.format(memsDatas.get(j).time));
				}else if (curLong > endLong) {
					break;
				}
			}
		}
		return udList;
	}
	/**
	 * AIC计算触发时刻
	 * @param SLTriggerTime SL方法计算的触发时刻,即P波初动时刻
	 * @param disIdx SL触发时刻所属的DispWithMEMS的Idx
	 * @return
	 */
	private boolean AICTrigger(Date SLTriggerTime, int disIdx) {
		System.out.println(StaticMetaData.formatMs.format(this.myDataList.get(disIdx).time));
		List<MEMSData> memsDataList = new ArrayList<>();
		//前2.5s数据
		Date t1 = new Date(SLTriggerTime.getTime()-2500);
		//后0.5s数据
		Date t2 = new Date(SLTriggerTime.getTime()+500);
		//now 当前时刻
//		Date t2 = new Date(SLTriggerTime.getTime());
		int startDisIdx = disIdx - 3;
		if (startDisIdx < 0) {
			startDisIdx = 0;
		}
		int endDisIdx = disIdx + 1;
		if (endDisIdx > this.myDataList.size()-1) {
			endDisIdx = this.myDataList.size()-1;
		}
		while (startDisIdx <= endDisIdx) {
			DispWithMEMS mems = this.myDataList.get(startDisIdx);
			List<MEMSData> memsDatas = mems.memsDataList;
			for (int i = 0; i < memsDatas.size(); i++) {
				if (t1.after(memsDatas.get(i).getTime())) {
					continue;
				}else if (memsDatas.get(i).getTime().after(t1) && !memsDatas.get(i).getTime().after(t2)) {
					memsDataList.add(memsDatas.get(i));
				}else {
					break;
				}
			}
			startDisIdx++;
		}
		if (memsDataList.size() < 3) {
			System.out.println("AICTrigger memsDataList.size() < 3, memsDataList:: " + memsDataList.size());
			return false;
		}
	    triggerTime = AIC(memsDataList);//P波触发时刻
	    if(triggerTime == null ){
	    	return false ;
	    }
		System.out.println("P波触发时刻: " + this.myStation.ID+"  " + StaticMetaData.formatMs.format(triggerTime));
		System.out.println(this.myStation.ID+"    " + StaticMetaData.formatMs.format(triggerTime));
		t1 = null;
		t2 = null;
		return true;
	}
	/**
	 * P波初动
	 * @param lastMemsData
	 * @param disIdx
	 * @param memsIdx
	 * @return true表示P波初动
	 */
	private boolean isSLTATriggered(MEMSData lastMemsData, int disIdx, int memsIdx){
		//S短窗口
//		double varS = varUD(S, disIdx, memsIdx);
		Date ts = new Date(lastMemsData.time.getTime()-(long)(Config.stw * 1000));
		double varS = varUD(StaticMetaData.S, disIdx, memsIdx, ts);
		if (varS == 0) {
			return false;
		}
		//L长窗口
//		double varL = varUD(L, disIdx, memsIdx);
		Date tl = new Date(lastMemsData.time.getTime()-(long)(Config.ltw * 1000));
		double varL = varUD(StaticMetaData.L, disIdx, memsIdx, tl);
		if (varL == 0) {
			return false;
		}
		double SLTA = 0;
		SLTA = varS/varL;
		boolean res = SLTA > StaticMetaData.thresh;
//		System.out.println("varS:"+varS+", varL:"+varL+", SLTA: " + SLTA+", "+StaticMetaData.formatMs.format(lastMemsData.time));
//		if (res) {
//			System.out.println("P波初动 varS:"+varS+", varL:"+varL+", SLTA: " + SLTA);
//		}
		ts = null;
		tl = null;
		return res;
	}
	/**
	 * 获取AIC值最小的时刻
	 * @param memsDataList
	 * @return
	 */
	private Date AIC(List<MEMSData> memsDataList) {
		int N = memsDataList.size();
//		System.out.println("AIC memsDataList.size():"+N);
		int minK = 2;
		double minTmp = 0;
		for (int k = 2; k < N-1; k++) {//舍去首尾，因为lg0
			double tmp = (k)*Math.log10(var(memsDataList,0,k-1))+(N-1-k)*Math.log10(var(memsDataList, k, N-1));
//			System.out.println("tmp "+k+":"+tmp);
			if (tmp==Double.NEGATIVE_INFINITY) {//负无穷值去掉
				System.out.println("K NEGATIVE_INFINITY :" + k +", time:"+StaticMetaData.formatMs.format(memsDataList.get(k-1).getTime()));
				continue;
			}
			if (k==2) {
//				minK = k;
				minTmp = tmp;
			}else {
				if (tmp < minTmp) {
					minTmp = tmp;
					minK = k;
				}
			}
//			System.out.println("time:"+StaticMetaData.formatMs.format(memsDataList.get(k-1).getTime())+", aic:"+tmp);
		}
		if(minK-1<0 || minK>memsDataList.size()){
			System.out.println("AIC异常，P波到时计算异常，返回null");
			return null ;
		}
		return memsDataList.get(minK-1).getTime();
	}
	/**
	 * 计算方差
	 * @param X 数据数组
	 * @param startIdx 开始计算索引（包含）
	 * @param endIdx 结束计算索引（包含）
	 * @return
	 */
	private double var(List<MEMSData> memsDataList, int startIdx, int endIdx) {
		double sum = 0;
		for (int i = startIdx; i <= endIdx; i++) {
			sum+=memsDataList.get(i).getAccH();
//			sum = DoubleUtil.add(sum, memsDataList.get(i).getAccH());
		}
		double avg = sum/(endIdx-startIdx+1);
//		double avg = DoubleUtil.div(sum, (double)(endIdx-startIdx+1), 6);
		sum = 0;
		for (int i = startIdx; i <= endIdx; i++) {
			sum+=Math.pow((memsDataList.get(i).getAccH()-avg), 2);
//			sum = DoubleUtil.add(sum, Math.pow(DoubleUtil.sub(memsDataList.get(i).getAccH(), avg), 2));
		}
		return sum/(endIdx-startIdx+1);
//		return DoubleUtil.div(sum, (double)(endIdx-startIdx+1), 12);
	}
	/**
	 * 计算方差
	 * @param X 数据数组
	 * @param startIdx 开始计算索引（包含）
	 * @param endIdx 结束计算索引（包含）
	 * @return
	 */
	private double var(double[] X, int startIdx, int endIdx) {
		double sum = 0;
		for (int i = startIdx; i <= endIdx; i++) {
			sum+=X[i];
		}
		double avg = sum/(endIdx-startIdx+1);
		sum = 0;
		for (int i = startIdx; i <= endIdx; i++) {
			sum+=Math.pow((X[i]-avg), 2);
		}
		return sum/(endIdx-startIdx+1);
	}
	/**
	 * 
	 * @param maxTW 时间窗口长度
	 * @param startDisIdx 起始Dis的Idx，循环往复往前计算
	 * @param startMEMSIdx 起始MEMSIdx，循环往复往前计算
	 * @param t0 起始时刻，所有MEMSData的时刻不得早于t0
	 * @return UD方向的方差
	 */
	private double varUD(int maxTW, int startDisIdx, int startMEMSIdx, Date t0) {
		List<MEMSData> memsDataList = new ArrayList<>();
		int lastDisIdx = startDisIdx;
		while (memsDataList.size() < maxTW && lastDisIdx >= 0) {
			DispWithMEMS displacement = this.myDataList.get(lastDisIdx);
			int lastMemsIdx = 0;
			if (memsDataList.size() == 0) {
				if (displacement.memsDataList.size() > startMEMSIdx) {
					lastMemsIdx = startMEMSIdx;
				}else {
					lastMemsIdx = displacement.memsDataList.size() - 1;
				}
			}else if (displacement.memsDataList.size() > 0) {
				lastMemsIdx = displacement.memsDataList.size() - 1;
			}else {
				lastDisIdx--;
				continue;
			}
			if (lastMemsIdx < 0) {
				lastDisIdx--;
				continue;
			}
			MEMSData memsData = displacement.memsDataList.get(lastMemsIdx);
			if (memsData.time.before(t0)) {
				break;
			}
			int size = maxTW - memsDataList.size();
			List<MEMSData> tmpList = new ArrayList<>();
			List<MEMSData> memsDatas = displacement.memsDataList;
			synchronized (memsDatas) {
				if (lastMemsIdx+1 > size) {
//					for (int i = lastMemsIdx+1-size; i < lastMemsIdx+1; i++) {
//						tmpList.add(displacement.memsDataList.get(i));
//					}
					for (int i = 0; i < lastMemsIdx+1; i++) {
						if (t0.before(memsDatas.get(i).getTime())) {
							tmpList.add(memsDatas.get(i));
						}
					}
				}else if (lastMemsIdx+1 <= size) {
					for (int i = 0; i < lastMemsIdx+1; i++) {
						tmpList.add(memsDatas.get(i));
					}
				}
			}
			memsDataList.addAll(tmpList);//顺序无关
			lastDisIdx--;
			while (tmpList != null && tmpList.size() > 0) {
				tmpList.remove(0);
			}
			tmpList = null;
		}
		int size = memsDataList.size();
		double Avg = 0.0;
		for (MEMSData memsData : memsDataList) {
			double vhf= memsData.getAccH();
//		    Avg = DoubleUtil.add(Avg, vhf);
			Avg += vhf;
		}
		if (size == 0) {
			return 0;
		}
//		Avg = DoubleUtil.div(Avg, (double)size, 2);
		Avg = Avg/size;
		double var = 0.0;
		for (MEMSData memsData : memsDataList) {
			double v = Math.pow(memsData.getAccH()-Avg, 2);
			var+= v ;
//			double dd = DoubleUtil.sub(memsData.getAccH(), Avg);
//			double v = DoubleUtil.mul(dd, dd);
//			var = DoubleUtil.add(var, v);
		}
		var = var/size;
//		var = DoubleUtil.div(var, (double)size, 4);
		while (memsDataList != null && memsDataList.size() > 0) {
			memsDataList.remove(0);
		}
		memsDataList = null;
		if (var < Config.MEMS_Min_0) {
			System.out.println(var);
			return 0;
		}else {
			return var;
		}
	}
	/**
	 * 
	 * @param tw 时间窗口长度
	 * @param startDisIdx 起始Dis的Idx，循环往复往前计算
	 * @param startMEMSIdx 起始MEMSIdx，循环往复往前计算
	 * @return UD方向的方差
	 */
	private double varUD(int tw, int startDisIdx, int startMEMSIdx) {
		List<MEMSData> memsDataList = new ArrayList<>();
		int lastDisIdx = startDisIdx;
		while (memsDataList.size() < tw && lastDisIdx >= 0) {
			DispWithMEMS displacement = this.myDataList.get(lastDisIdx);
			int lastMemsIdx = 0;
			if (memsDataList.size() == 0) {
				lastMemsIdx = startMEMSIdx;
			}else if (displacement.memsDataList.size() > 0) {
				lastMemsIdx = displacement.memsDataList.size() - 1;
			}else {
				lastDisIdx--;
				continue;
			}
			int size = tw - memsDataList.size();
			List<MEMSData> tmpList = new ArrayList<>();
			synchronized (displacement) {
				if (lastMemsIdx+1 > size) {
					for (int i = lastMemsIdx+1-size; i < lastMemsIdx+1; i++) {
						tmpList.add(displacement.memsDataList.get(i));
					}
//					Collections.copy(tmpList, displacement.memsDataList.subList(lastMemsIdx+1-size, lastMemsIdx+1));
				}else if (lastMemsIdx+1 <= size) {
					for (int i = 0; i < lastMemsIdx+1; i++) {
						tmpList.add(displacement.memsDataList.get(i));
					}
//					Collections.copy(tmpList, displacement.memsDataList.subList(0, lastMemsIdx+1));
				}
			}
			memsDataList.addAll(tmpList);//顺序无关
			lastDisIdx--;
			tmpList.clear();
			tmpList = null;
		}
		int size = memsDataList.size();
		System.out.println("MEMS size:" + size);
		double Avg = 0.0;
		for (MEMSData memsData : memsDataList) {
			Avg += memsData.getAccH();
		}
		Avg = Avg/size;
		double var = 0.0;
		for (MEMSData memsData : memsDataList) {
			var+=Math.pow(memsData.getAccH()-Avg, 2);
		}
		var = var/size;
		memsDataList.clear();
		memsDataList = null;
		return var;
	}
	public boolean insertMEMSData(MEMSData memsData) {
		DispWithMEMS displacement = (DispWithMEMS)getDispWithMEMSByTime(memsData.time);
		synchronized(displacement){
			List<MEMSData> list = displacement.memsDataList;
			if (list.size() == 0) {
//				this.calENH(displacement, memsData);
				return list.add(memsData);
			}
			Date tmpT = list.get(list.size()-1).time;
			if (memsData.time.after(tmpT)) {
//				memsData.calENH(list.get(list.size()-1));//计算ENH
//				this.calENH(memsData, list.get(list.size() - 1));
				tmpT = null;
				return list.add(memsData);
			}
			if (memsData.time.before(list.get(0).time)) {
//				this.calENH(displacement, memsData);
				list.add(0, memsData);
				tmpT = null;
				return true;
			}
			if (memsData.time.before(tmpT)) {
				tmpT = memsData.time;
				for (int i = list.size()-1; i > 0; i--) {
					if (tmpT.before(list.get(i).time) && tmpT.after(list.get(i-1).time)) {
						list.add(i, memsData);
//						memsData.calENH(list.get(i-1));//计算ENH
//						this.calENH(memsData, list.get(i-1));
						tmpT = null;
						return true;
					}
				}
			}
		}
		return true;
	}
//	private void calENH(MEMSData memsData, MEMSData lastMems) {
//		memsData.calENH(lastMems);
//	}
//	private void calENH(DispWithMEMS displacement, MEMSData memsData) {
//		if (displacement.isDispExist) {
//			memsData.calENH(displacement.xDisplacement, displacement.yDisplacement, displacement.zDisplacement, displacement.time);
//		}else {
//			for (int i = this.myDataList.size()-1; i >= 0; i--) {
//				Displacement tmpDisp = this.myDataList.get(i);
//				try {
//					if (tmpDisp != null && displacement != null && tmpDisp.equals(displacement)) {
//						if (i>0) {
//							tmpDisp = this.myDataList.get(i-1);
//							if (tmpDisp.isDispExist) {
//								memsData.calENH(tmpDisp.xDisplacement, tmpDisp.yDisplacement, tmpDisp.zDisplacement, tmpDisp.time);
//								break;
//							}else {
//								boolean flag = false;
//								i--;
//								while (i>=0) {
//									tmpDisp = this.myDataList.get(i);
//									if (tmpDisp.isDispExist) {
//										memsData.calENH(tmpDisp.xDisplacement, tmpDisp.yDisplacement, tmpDisp.zDisplacement, tmpDisp.time);
//										flag = true;
//										break;
//									}
//									i--;
//								}
//								if (flag) {
//									break;
//								}else {
//									memsData.calENH(0, 0, 0, tmpDisp.time);
//									break;
//								}
//							}
//						}else{
//							memsData.calENH(0, 0, 0, tmpDisp.time);
//							break;
//						}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
////		System.out.println("after calENH(displacement): isDispExist-" + displacement.isDispExist + ", " + displacement.xDisplacement+", " + displacement.yDisplacement+", " + displacement.zDisplacement);
//	}
//	public Displacement getDispWithMEMSByTime(Date time) {
//		//首先，将time中的ms置为0值
//		Calendar tmp = Calendar.getInstance();
//		tmp.setTime(time);
//		tmp.set(Calendar.MILLISECOND, 0);
//		time = tmp.getTime();
//		List<DispWithMEMS> dispList = this.myDataList;
//		DispWithMEMS displacement = new DispWithMEMS(this, time);
//		dispList.add(displacement);
//		return displacement;
//	}
	public Displacement getDispWithMEMSByTime(Date time) {
		//首先，将time中的ms置为0值
				Calendar tmp = Calendar.getInstance();
				tmp.setTime(time);
				tmp.set(Calendar.MILLISECOND, 0);
				time = tmp.getTime();
				List<DispWithMEMS> dispList = this.myDataList;
				if (dispList == null || dispList.size() == 0) {
					DispWithMEMS displacement = new DispWithMEMS(this, time);
					dispList.add(displacement);
					tmp = null;
					time = null;
					return displacement;
				}
//				synchronized (dispList) {
					int size = dispList.size();
					int cmp = dispList.get(size-1).time.compareTo(time);
					if (cmp == 0) {
						return dispList.get(size-1);
					}else if (cmp < 0) {
						DispWithMEMS displacement = new DispWithMEMS(this, time);
						dispList.add(displacement);
						tmp = null;
						time = null;
						return displacement;
					}
					cmp = dispList.get(0).time.compareTo(time);
					if (cmp == 0) {
						tmp = null;
						time = null;
						return dispList.get(0);
					}else if (cmp > 0) {
						DispWithMEMS displacement = new DispWithMEMS(this, time);
						dispList.add(0, displacement);
						tmp = null;
						time = null;
						return displacement;
					}
					for (int i = size-2; i >= 0; i--) {
						Displacement displacement = dispList.get(i);
						int cmp2 = displacement.time.compareTo(time);
						if (cmp2 == 0) {
							tmp = null;
							time = null;
							return displacement;
						}else if (cmp2 > 0) {
							continue;
						}else if (cmp2 < 0) {
							DispWithMEMS displ = new DispWithMEMS(this, time);
							dispList.add(i+1, displ);
							tmp = null;
							time = null;
							return displ;
						}
					}
//				}
				tmp = null;
				time = null;
				return null;
	}
//	public void dispose(){
//		 
//	}
	public Displacement getLastData(){
		if(this.myDataList== null || this.myDataList.size()<=0 )
			return null ;
		Displacement displacement = null;
		synchronized (myDataList) {
			displacement = this.myDataList.get(this.myDataList.size()-1) ;
		}
		return displacement;
	}
	
//	public Displacement getDisplacementInstance(){
//		return new Displacement(null, 0,0,0,0,null,null,new Date()) ;
//	}
	public Displacement getDispWithMEMSInstance(Date time) {
		DispWithMEMS displacement = new DispWithMEMS(this, time);
		addDispWithMEMS(displacement);
		return displacement;
	}
//	public displacement getDisplacementInstance(DataCache c , double x, double y, double z,Date t){
//		return new displacement(c, x,y,z,t) ;
//	}
	/**
	 * 计算当前time，zDis的相对于上一时刻的地心向z位移量zDisToLastDis
	 * @param time 待计算zDisToLastDis的位移时间
	 * @param zDis 相对于原始位置的z位移
	 * @return
	 */
	public double calZDisToLastDis(Date time, double zDis){
		double zDisToLastDis = 0;
		Displacement last = this.getLastData();
		if (last == null) {
			return zDis;
		}
		if (last.time.before(time) || this.myDataList.size() == 1) {
 		   return Math.abs(zDis - last.zDisplacement);
		}else if (last.time.after(time)){
			for(int i = 0; i < this.myDataList.size()-1 ; i++){
			     if(this.myDataList.get(i).time.before(time) && this.myDataList.get(i+1).time.after(time)){
			    	 return Math.abs(this.myDataList.get(i).zDisplacement-zDis); 
			     }
			}
		}
		return zDis;
	}
	public boolean isCurTimeDisExist(Date time){
		Displacement last = this.getLastData();
//		System.out.println("last:"+last+"  this:"+time.toLocaleString());
		if (last == null || last.time.before(time)) {
			return false;
		}else if (last.time.compareTo(time) == 0) {
			if (last.isDispExist) {
				return true;
			}else {
				return false;
			}
		}else{
			for(int i = 0; i < this.myDataList.size() ; i++){
				Displacement d1 = this.myDataList.get(i) ;
				if(d1.time.compareTo(time) == 0){
					if (d1.isDispExist) {
						return true;
					}else {
						return false;
					}
				}
				if (d1.time.compareTo(time) > 0) {
					return false;
				}
			}
		}
//		System.out.println(myStation.ID+"::"+time.toLocaleString()+" "+ last.time.toLocaleString());
		return false;
	}
	/**
	 * 获取time时刻的位移对象，
	 * 以Displacement.isDispExist判断位移数据是否存在
	 * @param time
	 * @return
	 */
//	public Displacement getCurTimeDis(Date time){
//		Displacement last = this.getLastData();
//		if (last == null || last.time.before(time)) {
//			//(last == null || last.time.before(time) || (last.time.compareTo(time) == 0 && !last.isDispExist))
//			return getDispWithMEMSInstance(time);
//		}else if (last.time.compareTo(time) == 0) {//last.time.compareTo(time) == 0 && last.isDispExist
//			return last;
//		}else{
//			for(int i = 0; i < this.myDataList.size() ; i++){
//				Displacement d1 = this.myDataList.get(i) ;
//				if(d1.time.compareTo(time) == 0){
//					if (d1.isDispExist) {
//						return d1;
//					}else {
//						return getDispWithMEMSInstance(time);
//					}
//				}
//				if (d1.time.compareTo(time) > 0) {
//					return getDispWithMEMSInstance(time);
//				}
//			}
//		}
//		return getDispWithMEMSInstance(time);
//	}
	public void addDispWithMEMS(DispWithMEMS d){
		if(d == null )
			return ; 
		if(this.myDataList == null )
			this.myDataList = new ArrayList<>();
		d.myCache = this ;
	    insertNewData(d) ;
	}
	
	public void insertNewData(DispWithMEMS d){
		if (d==null||d.time==null) {
			System.out.println(d);
			return;
		}
		long t = d.time.getTime() ;
		synchronized (myDataList) {
			for(int i = myDataList.size()-1; i > 0; i--){
				DispWithMEMS d1 = myDataList.get(i) ;
				long t1 = d1.time.getTime() ;
				if (t1 == t) {
					return;
				}else if(t1 < t){
					this.myDataList.add(i+1, d);
					return ; 
				}
			}
			this.myDataList.add(d) ;
		}
		return ; 
	}
	/**
	 * 根据接收到的位移数据的本机时间，维护时间窗口
	 */
	public void maintainTimeWindow(){
		 if(this.myStation.isActive)
			 return ;//当前台站已经处于激活状态，队列长度一直增量
		 if(maintainMyStationsWindow()){
			 return;
		 }
		 maintainMyDataListTimeWindow();
//		 maintainMEMSDataListTimeWindow();
		 maintainMyStationsWindow();
//		 System.out.println(this.myStation.ID + " maintainTimeWindow结束。");
	}
	/**
	 * 维护myDataList的时间窗口
	 */
	synchronized private void maintainMyDataListTimeWindow(){
		synchronized (myDataList) {
//			System.out.println("maintainMyDataListTimeWindow before -- "+myStation.ID+" -- "+myDataList.size());
			if (myDataList.size() <= 0) {
				return;
			}
			Displacement d = myDataList.get(myDataList.size()-1) ;
			long t2 = d.localTime.getTime() ;
//			long t3 = new Date().getTime();
			long t3 = System.nanoTime()/1000000;
			while(true){
				if(myDataList.size() <=0 )
					break ; 
				DispWithMEMS d1 = myDataList.get(0) ;
				long t1 = d1.localTime.getTime() ;
				if((t2-t1)/1000 > timeWindow || (t3 - t1)/1000 > timeWindow){
					myDataList.remove(0) ;
					d1.dispose();
					d1 = null ;
				}
				else break ;
			}
			System.gc();
//			System.out.println("maintainMyDataListTimeWindow after -- "+myDataList.size());
//			System.out.println("maintainMyDataListTimeWindow 耗时  -- "+(System.nanoTime()/1000000-t3)+"ms");
			t2 = 0;
			t3 = 0;
		}
	}
	/**
	 * 当前myDataList为空时，返回true，表示已清除相应的station；
	 * 否则，返回false，表示需要继续维护myDataList和memsDataList的时间窗口。
	 * @return
	 */
	private boolean maintainMyStationsWindow(){
		//若当前数据缓存为空，则清除GNSSFrame中myStations的相应记录,返回
		 if ((this.myDataList == null || this.myDataList.size() <= 0)) {
			 synchronized (GNSSFrame.myStations) {
				 if (GNSSFrame.myStations.containsKey(this.myStation.ID)) {
					 GNSSFrame.myStations.remove(this.myStation.ID);
				 }
				 return true;
			}
		 }
		 return false;
	}

}
