/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.lib;/*~~  */import java.awt.*;import mesquite.lib.*;import mesquite.cont.lib.*;/* abstract class for projections that use only a standard longitude */public abstract class CalibratedStand1Proj extends CalibratedStandProj {		public MesquiteNumber standardLongitude = new MesquiteNumber();   // lambda-0	protected double lambda0 = 0.0;		/*.................................................................................................................*/  	public boolean parametersSpecified(){  		return (standardLongitude.isCombinable());  	}	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {  		addMenuItem("Projection Parameters...", makeCommand("projectionParameters",  this));  		return super.startJob(arguments, condition, hiredByName);    	 }  	 	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = super.getSnapshot(file);	 	temp.addLine("projectionParameters '" + standardLongitude.toString() + "' ");	 	return temp;  	 }	/*.................................................................................................................*/	public String getProjectionParameters(){		String s = "";		s += "\t\t\t<standardLongitude>" + standardLongitude.toString() + "</standardLongitude>\n";		return s;	}	/*.................................................................................................................*/   	public void processProjectionParameters(boolean duringOptimization){		super.processProjectionParameters(duringOptimization);		lambda0 = GeographicData.getPolarLongitude(standardLongitude.getDoubleValue());   	}	/*.................................................................................................................*/	public boolean readProjectionParameters(String contents){		Parser subParser = new Parser();		subParser.setString(contents);		MesquiteString nextTag = new MesquiteString();		String subTagContent = subParser.getNextXMLTaggedContent(nextTag);		while (!StringUtil.blank(nextTag.getValue())) {			if ("standardLongitude".equalsIgnoreCase(nextTag.getValue())) {// here's the signed longitude, stored in subTagContent				standardLongitude.setValue(MesquiteDouble.fromString(subTagContent));			}			subTagContent = subParser.getNextXMLTaggedContent(nextTag);		}		return true;	}	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {     	 	if (checker.compare(this.getClass(), "Sets the parameters of the map", "[top bottom left right]", commandName, "projectionParameters")) {    	 		if (StringUtil.blank(arguments) && !MesquiteThread.isScripting()) {    	 			if (queryStandards())     	 				if (standardLongitude.isCombinable()) {    	 					processProjectionParameters(false);						parametersChanged(); 					}    	 		}    	 		else {	     	 		standardLongitude.setValue(MesquiteDouble.fromString(parser.getNextToken()));	 	 		if (!standardLongitude.isCombinable())	    	 			if (!MesquiteThread.isScripting())	    	 				queryStandards();	 	 		if (!standardLongitude.isCombinable())	    	 			return null;	    	 		else  {					processProjectionParameters(false);	    	 			parametersChanged(); 	    	 		}    	 		}    	 	} 	 	else       	 		return  super.doCommand(commandName, arguments, checker);    	 	return null;    	 }	/*.................................................................................................................*/	public boolean queryStandards() {		MesquiteInteger buttonPressed = new MesquiteInteger(1);		ExtensibleDialog queryDialog = new ExtensibleDialog(containerOfModule(), "Projection Parameters",  buttonPressed);		queryDialog.addLabel("Parameters of Projection", Label.CENTER);		DoubleField  longField = queryDialog.addDoubleField("Standard longitude:", standardLongitude.getDoubleValue(),8, -180.0, 180.0);		String s = getStringDescribingLatLongs();		queryDialog.appendToHelpString(s);		queryDialog.completeAndShowDialog(true);					boolean ok = (queryDialog.query()==0);		MesquiteBoolean success = new MesquiteBoolean(true);				if (ok) {			standardLongitude.setValue(longField.getValue(success));  		}				queryDialog.dispose();   		 		if (!success.getValue()) {			discreetAlert("Value is out of bounds; it will be reset to its previous value.");		}   		return ok;	}	/*.................................................................................................................*/   	public String getParameters() { 		return "";   	} 	/*.................................................................................................................*/	public double evaluate(MesquiteDouble param, Object param2){			return 0;	}	/*.................................................................................................................*/	public double evaluate(double[] x, Object param){		if (x == null || x.length != 1)			return 0;		if (x[0]>180.0 || x[0]<-180.0)			return MesquiteDouble.veryLargeNumber;		standardLongitude.setValue(x[0]);  		processProjectionParameters(true);		double score = getMismatch();		reportOptimizationToUser(score);		return score;	}	/*.................................................................................................................*/    	 public void setParametersToCalibrationPoint(int j){		if (calibrations[j]!=null) {			standardLongitude.setValue(calibrations[j].getLongitude().getDoubleValue()); 		}    	 }	/*.................................................................................................................*/    	 public void setParametersToTwoCalibrationPoints(int i, int j){		if (calibrations[j]!=null) {			standardLongitude.setValue(calibrations[j].getLongitude().getDoubleValue()); 		}    	 }	/*.................................................................................................................*/    	 public int getNumParameters (){    	 	return 1;    	 }	/*.................................................................................................................*/		double  standardLongitudeStored; 	/*.................................................................................................................*/    	 public void storeParameters() {		standardLongitudeStored =standardLongitude.getDoubleValue();      	 }	/*.................................................................................................................*/    	 public void recoverStoredParameters() {		standardLongitude.setValue(standardLongitudeStored);      	 }	/*.................................................................................................................*/    	 public void setParametersIfUnassigned(boolean setEvenIfAssigned) {		if (!standardLongitude.isCombinable() || setEvenIfAssigned)			standardLongitude.setValue(RandomBetween.getDoubleStatic(-180.0,180.0));     	 }	/*.................................................................................................................*/    	 public double[] getParamArray() {    	 	double[] x = new double[1];    	 	x[0] = standardLongitude.getDoubleValue();    	 	return x;    	 }   	 }