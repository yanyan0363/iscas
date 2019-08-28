package mainFrame;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class AnalysisTimerTaskRunner  {
	public long defaultTimeTick = 5 ;
	private Timer myTimer = new Timer();
	public AnalysisTimerTaskRunner( ) {
	 
	}
	
	public void run(){
		 AnalysisTimerTask task = new AnalysisTimerTask() ;
		 task.myTask = this ;
		 Date time = Calendar.getInstance().getTime() ;
		 myTimer.schedule(task, time, defaultTimeTick) ;
		 
	 }
	 
	public void timerCancel() {
		myTimer.cancel();
	}
	 
}
