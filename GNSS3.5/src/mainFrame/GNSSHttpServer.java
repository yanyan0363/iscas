package mainFrame;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import http.GetAccessTokenTest;
import http.GetSDStartEndTest;
import servlet.AddStation;
import servlet.DeleteStation;
import servlet.GetCurStationFamily;
import servlet.GetEQEvents;
import servlet.GetEQImgsServlet;
import servlet.GetIndexNums;
import servlet.GetStDispVH;
import servlet.GetStLast2MinDisp;
import servlet.GetStLast2MinDispWithMEMS;
import servlet.GetStLastXMinDispWithMEMS;
import servlet.GetStLastXMinDispWithMEMSWithoutFitting;
import servlet.GetStationDispWithMEMS;
import servlet.GetStationDisplacement;
import servlet.GetStationMetaData;
import servlet.GetStationsDis2DZ;
import servlet.GetStsDisp2DZ;
import servlet.GetTimeLine;
import servlet.UpdateStation;
import utils.Config;

public class GNSSHttpServer {

	public void start() {
		Server server = new Server(Config.httpServerPort);
		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setContextPath("/GNSS");
		server.setHandler(contextHandler);
//		contextHandler.addServlet(new ServletHolder(new GetModeServlet()), "/getMode");
		contextHandler.addServlet(new ServletHolder(new GetStationDisplacement()), "/stationDisplacement");
		contextHandler.addServlet(new ServletHolder(new GetStLast2MinDisp()), "/stLast2MinDisp");
		contextHandler.addServlet(new ServletHolder(new GetStationDispWithMEMS()), "/stationDispWithMEMS");
		contextHandler.addServlet(new ServletHolder(new GetStLast2MinDispWithMEMS()), "/stLast2MinDispWithMEMS");
//		contextHandler.addServlet(new ServletHolder(new GetStLastXMinDispWithMEMS()), "/stLastXMinDispWithMEMS");
		contextHandler.addServlet(new ServletHolder(new GetStLastXMinDispWithMEMSWithoutFitting()), "/stLastXMinDispWithMEMSWithoutFitting");
		contextHandler.addServlet(new ServletHolder(new GetCurStationFamily()), "/getCurStationFamily");
//		contextHandler.addServlet(new ServletHolder(new ChangeMode()), "/changeMode");
//		contextHandler.addServlet(new ServletHolder(new AnalyseFile()), "/AnalyseFile");
		contextHandler.addServlet(new ServletHolder(new GetStationMetaData()), "/getStationMetaData");
		contextHandler.addServlet(new ServletHolder(new AddStation()), "/addStation");
		contextHandler.addServlet(new ServletHolder(new UpdateStation()), "/updateStation");
		contextHandler.addServlet(new ServletHolder(new DeleteStation()), "/deleteStation");
		contextHandler.addServlet(new ServletHolder(new GetStationsDis2DZ()), "/getStationsDis2DZ");
//		contextHandler.addServlet(new ServletHolder(new AbandonTask()), "/abandonTask");
		contextHandler.addServlet(new ServletHolder(new GetEQImgsServlet()), "/GetEQImgs");
		contextHandler.addServlet(new ServletHolder(new GetTimeLine()), "/getTimeLine");
		contextHandler.addServlet(new ServletHolder(new GetEQEvents()), "/monitorEQEvents");
		contextHandler.addServlet(new ServletHolder(new GetStsDisp2DZ()), "/stsDisp2DZ");
		contextHandler.addServlet(new ServletHolder(new GetStDispVH()), "/stDispVH");
//		contextHandler.addServlet(new ServletHolder(new GetAccessTokenTest()), "/getAccessTokenTest");
		contextHandler.addServlet(new ServletHolder(new GetSDStartEndTest()), "/getSDStartEndTest");
		contextHandler.addServlet(new ServletHolder(new GetIndexNums()), "/getIndexNums");
		
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
