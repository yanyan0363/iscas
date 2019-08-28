package event;

import java.util.List;

public class ButterWorthFilter {
	//100Hz [b,a]=butter(2,0.0015,'high'); % 0.0015=0.075/（100/2）
	private static Double[] a = {1.000000000000000, -1.993335700135204, 0.993357832954142};
    private static Double[] b = {0.996673383272337, -1.993346766544673, 0.996673383272337};
//    public static Double[] highpass(Double[] signal) {
//    	Double[] in = new Double[b.length];
//    	Double[] out = new Double[a.length - 1];
//    	in[0] = signal;
//    	double y = 0.0;
//    	for (int j = 0; j < b.length; j++) {
//    		if (in[j] != null) {
////    			y += b[j] * in[j];
//    			y = DoubleUtil.add(y, DoubleUtil.mul(b[j], in[j]));
//    		}
//    	}
//    	for (int j = 0; j < a.length - 1; j++) {
//    		if (out[j] != null) {
////    			y -= a[j + 1] * out[j];
//    			y = DoubleUtil.sub(y, DoubleUtil.mul(a[j+1], out[j])); 
//    		}
//    	}
//        return y;
//    	return filter(signal, a, b);
//    }
    private static Double[] in;
    private static Double[] out;
    private static Double[] outData;

    public static Double[] highpassFilter(List<Double> signal) {
        in = new Double[b.length];
        out = new Double[a.length - 1];
        outData = new Double[signal.size()];
        for (int i = 0; i < signal.size(); i++) {
            System.arraycopy(in, 0, in, 1, in.length - 1);  //in[1]=in[0],in[2]=in[1]...
            in[0] = signal.get(i);
            //calculate y based on a and b coefficients
            //and in and out.
            Double y = 0.0;
            for (int j = 0; j < b.length; j++) {
                if (in[j] != null) {
                    y += b[j] * in[j];
                }
            }
            for (int j = 0; j < a.length - 1; j++) {
                if (out[j] != null) {
                    y -= a[j + 1] * out[j];
                }
            }
            //shift the out array
            System.arraycopy(out, 0, out, 1, out.length - 1);
            out[0] = y;
            outData[i] = y;
        }
        for (int i = 0; i < in.length; i++) {
			in[i] = null;
		}
        for (int i = 0; i < out.length; i++) {
			out[i] = null;
		}
        in = null;
        out = null;
        return outData;
    }
}
