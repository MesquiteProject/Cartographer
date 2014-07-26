/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.RectangularProjection;/*~~  */import mesquite.lib.*;import mesquite.cartographer.lib.*;public class RectangularProjection extends CalibratedMapProjection {			/*.................................................................................................................*/  	public boolean parametersSpecified(){  		return true;  	}	/*.................................................................................................................*/   	public boolean convertToUnscaledProjectionCoordinates(double longitude, double latitude, MesquiteNumber x, MesquiteNumber y){   		if (longitude < 0) {   			x.setValue(360.0 + longitude);   		}   		else   			x.setValue(longitude);   		y.setValue(latitude);   		return true;   	}	/*.................................................................................................................*/   	public boolean convertFromProjectionCoordinates(double x, double y, MesquiteNumber longitude, MesquiteNumber latitude){   		if ( x > 180)   			longitude.setValue(x-360.0);   		else   			longitude.setValue(x);   		latitude.setValue(y);   		return true;   	}	/*.................................................................................................................*/  	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer  	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.  	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/     	public int getVersionOfFirstRelease(){     		return -100;     }	/*.................................................................................................................*/    	 public String getName() {		return "Equirectangular Projection";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Converts to and from a Equirectangular map projection.";   	 }   	public boolean isPrerelease(){   		return false;   	}	/*.................................................................................................................*/   	 public boolean showCitation(){   	 	return false;   	 }   	 }