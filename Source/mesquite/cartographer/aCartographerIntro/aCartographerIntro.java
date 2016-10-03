/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.aCartographerIntro;import mesquite.lib.MesquiteTrunk;import mesquite.lib.duties.*;/* ======================================================================== */public class aCartographerIntro extends PackageIntro {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) { 		return true;  	 }  	 public Class getDutyClass(){  	 	return aCartographerIntro.class;  	 }	/*.................................................................................................................*/    	 public String getExplanation() {		return "Cartographer is a package of Mesquite modules providing tools for plotting data in geographic space.";   	 }   	/*.................................................................................................................*/    	 public String getName() {		return "Cartographer Package";   	 }	/*.................................................................................................................*/	/** Returns the name of the package of modules (e.g., "Basic Mesquite Package", "Rhetenor")*/ 	public String getPackageName(){ 		return "Cartographer Package"; 	}	/*.................................................................................................................*/	/** Returns citation for a package of modules*/ 	public String getPackageCitation(){ 		return "Maddison, D.R.,  & W.P. Maddison. 2016. Cartographer: A Mesquite package for plotting geographic data. Version " + getPackageVersion() +"  http://mesquiteproject.org/packages/cartographer"; 	}	/*.................................................................................................................*/	/** Returns whether there is a splash banner*/	public boolean hasSplash(){ 		return true; 	}	/*.................................................................................................................*/	/** returns the URL of the notices file for this module so that it can phone home and check for messages */	/*.................................................................................................................*/	public String  getHomePhoneNumber(){ 		if (MesquiteTrunk.debugMode)			return "http://mesquiteproject.org/packages/cartographer/noticesDev.xml";		else if (!isPrerelease()) 			return "http://mesquiteproject.org/packages/cartographer/notices.xml";		else			return "http://mesquiteproject.org/packages/cartographer/noticesPrerelease.xml";	}	/*.................................................................................................................*/	public String getPackageURL(){		return "http://mesquiteproject.org/packages/cartographer";  	}	/*.................................................................................................................*/	/** Returns version for a package of modules*/	public String getPackageVersion(){		return "1.41+";	}	/*.................................................................................................................*/	/** Returns version for a package of modules as an integer*/	public int getPackageVersionInt(){		return 141;	}	public String getPackageDateReleased(){		return "3 October 2016";	}	/*.................................................................................................................*/	/** Returns build number for a package of modules as an integer*/	public int getPackageBuildNumber(){		return 22;	}	/* release dates:	v1.0   5 May 2006	v1.2  19 September 2007	v1.3   8 June 2008	v1.31 1 January 2009	v1.4	19 August 2014	v1.41 29 August 2014	 * */		/*.................................................................................................................*/	public int getVersionOfFirstRelease(){		return 260;  	}}