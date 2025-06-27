/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. 
Version 1.3, June 2008.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
*/
package mesquite.cartographer.URLBingMaps; 

import mesquite.cartographer.lib.URLLongLatServer;
import mesquite.lib.StringUtil;

/* ======================================================================== */
public class URLBingMaps extends URLLongLatServer { 
	
	/*.................................................................................................................*/
	String getZoom(String zoom){
	     if (!StringUtil.blank(zoom))
			return zoom;
		return "14";
	}
	/*.................................................................................................................*/
    	 public String getURL(double latitude, double longitude, String zoom){
    	 	return "http://bing.com/maps/default.aspx?cp="+ latitude + "~"+longitude+"&lvl=" + getZoom("14");
   	}
    /*.................................................................................................................*/
    	 public  boolean isPrerelease(){
    	 	return false;
    	 }
   /*.................................................................................................................*/
    	 public  String getName(){
    	 	return "Bing Maps";
    	 }
	/*.................................................................................................................*/
    	 public  String getExplanation(){
    	 	return "Provides URL for Bing Maps";
    	 }
       	 /*.................................................................................................................*/
    	 /** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer
    	  * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.
    	  * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/
    	 public int getVersionOfFirstRelease(){
    		 return -141;  
    	 }
}

