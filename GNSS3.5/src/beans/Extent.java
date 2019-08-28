package beans;

import com.esri.arcgis.geometry.ISpatialReference;

public class Extent {

	public double minX = 0;
	public double minY = 0;
	public double maxX = 0;
	public double maxY = 0;
	public ISpatialReference spatialReference = null;
	
	public Extent(double minX, double minY, double maxX, double maxY, ISpatialReference spatialReference) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.spatialReference = spatialReference;
	}
	@Override
	public String toString() {
		
		return "extent: "+ this.minX+", " + this.minY + ", " + this.maxX + ", " + this.maxY;
	}
	public String toBBox() {
		return this.minX+","+this.minY+","+this.maxX+","+this.maxY;
	}
}
