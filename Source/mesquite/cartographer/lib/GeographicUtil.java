package mesquite.cartographer.lib;

import mesquite.cont.lib.*;
import mesquite.lib.*;


public class GeographicUtil {
	
	public static double getGreatCircleDistance(boolean useKm, double longitude1, double longitude2, double latitude1, double latitude2){
		double radius;
		if (useKm)
			radius = 6371.0; //mean radius in km
		else 
			radius = 3958.76; //mean radius in miles
		double dist =MesquiteDouble.unassigned;

		double lambda1 = GeographicData.getPolarLongitude(longitude1);
		double lambda2 = GeographicData.getPolarLongitude(longitude2);
		double delta1 = GeographicData.getPolarLatitude(latitude1);
		double delta2 = GeographicData.getPolarLatitude(latitude2);

		if (MesquiteDouble.isCombinable(lambda1) && MesquiteDouble.isCombinable(lambda2) && MesquiteDouble.isCombinable(delta1) && MesquiteDouble.isCombinable(delta2)) {
			dist = Math.cos(delta1)*Math.cos(delta2)*Math.cos(lambda1-lambda2) + Math.sin(delta1)*Math.sin(delta2);
			dist = radius*Math.acos(dist);
		}
		return dist ;	

	}	
	
	public static double mod (double y, double x) {	
		int intPart= (int)( y/x);
		double value=y - x * ((int)(y/x));
		if ( value < 0) value = value + x;
		return value;
	}
	
	public static double radiansFromKilometers (double d) {	
		return (d/1.852)*Math.PI/(60*180.0);   //1.852 kilometers in a nautical mile
	}
	
	/** given the distance between two points in kilometers, and their lon/lats, calculates the long lat that is d kilometers on the great circle from point 1 toward point 2 */
	public static double[] getGreatCirclePoint(double d, double longitude1, double longitude2, double latitude1, double latitude2)  {

		if (d<0.00000001) 
			return new double[] {longitude1, latitude1};
		
		double lon1 = GeographicData.getPolarLongitude(longitude1);
		double lon2 = GeographicData.getPolarLongitude(longitude2);
		double lat1 = GeographicData.getPolarLatitude(latitude1);
		double lat2 = GeographicData.getPolarLatitude(latitude2);

		double tc;
		double radiansD = radiansFromKilometers(d);
		
		if (Math.cos(lat1)<0.000001) {
			if (lat1>0)
				tc = Math.PI;
			else
				tc = Math.PI*2;
		}
		else {
			tc = Math.atan2(Math.sin(lon1-lon2)*Math.cos(lat2), Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon1-lon2));
			tc = mod(tc, 2*Math.PI);
		}
		
		
		double lat =Math.asin(Math.sin(lat1)*Math.cos(radiansD)+Math.cos(lat1)*Math.sin(radiansD)*Math.cos(tc));
	    double dlon=Math.atan2(Math.sin(tc)*Math.sin(radiansD)*Math.cos(lat1),Math.cos(radiansD)-Math.sin(lat1)*Math.sin(lat));
	    double lon=mod( lon1-dlon +Math.PI,2*Math.PI )-Math.PI;

	    
	    lat = GeographicData.getRegularLatitude(lat);
	    lon = GeographicData.getRegularLongitude(lon);
	    
	    return new double[] {lon, lat};
	}
	

	/** given the lon/lats of two points, calculates the long lat that is halfway between them on the great circle route */
	public static double[] getGreatCircleMidPoint(double longitude1, double longitude2, double latitude1, double latitude2)  {
		double fullDistance = getGreatCircleDistance(true, longitude1,  longitude2,  latitude1,  latitude2);		
		return getGreatCirclePoint(fullDistance/2.0,  longitude1,  longitude2,  latitude1,  latitude2);
	}


}
