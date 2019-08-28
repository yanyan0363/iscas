package mainFrame;

public class FrameRunner {

	public static GNSSFrame iFrame = new GNSSFrame() ;
	public static void startUp(){
		if (iFrame.init()) {
			System.out.println("系统初始化完成，正在启动");
			iFrame.startUp() ;
			System.out.println("系统启动成功");
		}else {
			System.out.println("系统初始化失败");
		}
	}

	public static void main(String[] args) {
		FrameRunner.startUp();
	}
}
