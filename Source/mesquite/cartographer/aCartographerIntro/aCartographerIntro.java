/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.aCartographerIntro;import mesquite.lib.MesquiteTrunk;import mesquite.lib.duties.*;/* ======================================================================== */public class aCartographerIntro extends PackageIntro {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) { 		return true;  	 }  	 public Class getDutyClass(){  	 	return aCartographerIntro.class;  	 }	/*.................................................................................................................*/    	 public String getExplanation() {		return "Cartographer is a package of Mesquite modules providing tools for plotting data in geographic space.";   	 }   	/*.................................................................................................................*/    	 public String getName() {		return "Cartographer Package";   	 }	/*.................................................................................................................*/	/** Returns the name of the package of modules (e.g., "Basic Mesquite Package", "Rhetenor")*/ 	public String getPackageName(){ 		return "Cartographer Package"; 	}	/*.................................................................................................................*/	/** Returns citation for a package of modules*/ 	public String getPackageCitation(){ 		return "Maddison, D.R.,  & W.P. Maddison. 2023. Cartographer: A Mesquite package for plotting geographic data. Version " + getPackageVersion() +"  http://mesquiteproject.org/packages/cartographer"; 	}	/*.................................................................................................................*/	/** Returns whether there is a splash banner*/	public boolean hasSplash(){ 		return true; 	}	/*.................................................................................................................*/	public String getManualPath(){		return getPackagePath() +"docs/index.html";  	}	/*.................................................................................................................*/	/** returns the URL of the notices file for this module so that it can phone home and check for messages */	/*.................................................................................................................*/	public String  getHomePhoneNumber(){ 		if (!isPrerelease()) 			return "http://cartographer.mesquiteproject.org/noticesAndUpdates/notices.xml";		else if (MesquiteTrunk.debugMode)			return "http://cartographer.mesquiteproject.org/noticesAndUpdates/noticesDebug.xml";		else			return "http://cartographer.mesquiteproject.org/noticesAndUpdates/notices.xml";		/*		versions 1.50 and 1.51: 		if (!isPrerelease()) 			return "https://raw.githubusercontent.com/MesquiteProject/Cartographer/master/noticesAndUpdates/notices.xml";		else if (MesquiteTrunk.debugMode)			return "https://raw.githubusercontent.com/MesquiteProject/Cartographer/development/noticesAndUpdates/noticesDebug.xml";		else			return "https://raw.githubusercontent.com/MesquiteProject/Cartographer/development/noticesAndUpdates/notices.xml";*//* 		versions 1.40 and before		 if (MesquiteTrunk.debugMode)			return "http://mesquiteproject.org/packages/cartographer/noticesDev.xml";		else if (!isPrerelease()) 			return "http://mesquiteproject.org/packages/cartographer/notices.xml";		else			return "http://mesquiteproject.org/packages/cartographer/noticesPrerelease.xml";*/		}	/*.................................................................................................................*/	public String getPackageURL(){		return "http://cartographer.mesquiteproject.org";  	}	/*.................................................................................................................*/	/** Returns the repository path on the repository server*/	public String getRepositoryPath(){		return "MesquiteProject/Cartographer";	}	/*.................................................................................................................*/	/** Returns the repository URL*/	public String getRepositoryFullURL(){		return "https://github.com/MesquiteProject/Cartographer";	}	/** Returns the URL for the release package*/	public String getReleaseURL(){		return "https://github.com/MesquiteProject/Cartographer/releases/download/1.60/Cartographer.1.60.zip";	}	/*.................................................................................................................*/	/** Returns the tag for the release tag*/	public String getReleaseTag(){		return "1.60";	}	/*.................................................................................................................*/	/** Returns version for a package of modules*/	public String getPackageVersion(){		return "1.60+";	}	/*.................................................................................................................*/	/** Returns version for a package of modules as an integer*/	public int getPackageVersionInt(){		return 160;	}	public String getPackageDateReleased(){		return "26 Sept 2024";	}	/*.................................................................................................................*/	/** Returns build number for a package of modules as an integer*/	public int getPackageBuildNumber(){		return 38;	}	/* release dates:	v1.0   5 May 2006	v1.2  19 September 2007	v1.3   8 June 2008	v1.31 1 January 2009	v1.4	19 August 2014	v1.41 29 August 2014	v1.50 1 January 2017 (build 30)	v1.52 5 May 2018 (build 35)	v1.60 9 April 2023 (build 36)	 * */	/*.................................................................................................................*/	/** returns whether this module is a prerelease version.  This returns "TRUE" here, forcing modules to override to claim they are not prerelease */	public boolean isPrerelease(){		return true;  	}	/*.................................................................................................................*/	public int getVersionOfFirstRelease(){		return 260;  	}}