/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.lib;/*~~  */import java.awt.*;import mesquite.lib.*;import mesquite.cont.lib.*;public abstract class CalibratedStand4Proj extends CalibratedStandProj {		public MesquiteNumber originLongitude = new MesquiteNumber();   // lambda-0	public MesquiteNumber originLatitude = new MesquiteNumber();   // phi-0	public MesquiteNumber standardLatitude1 = new MesquiteNumber();   // phi-1	public MesquiteNumber standardLatitude2 = new MesquiteNumber();   // phi-2	protected double phi1 = 0.0, phi2 = 0.0, phi0=0.0;  // standard latitudes	protected double lambda0 = 0.0;   // standard longitude		/*.................................................................................................................*/  	public boolean parametersSpecified(){  		return (originLongitude.isCombinable() && originLatitude.isCombinable() && standardLatitude1.isCombinable() && standardLatitude1.isCombinable());  	}	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {  		addMenuItem("Projection Parameters...", makeCommand("projectionParameters",  this));  		return super.startJob(arguments, condition, hiredByName);  	 }  	 	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = super.getSnapshot(file);	 	temp.addLine("projectionParameters '" + originLatitude.toString() + "' '" + originLongitude.toString()+ "' '" + standardLatitude1.toString()+ "' '" + standardLatitude2.toString() + "' ");	 	return temp;  	 }	/*.................................................................................................................*/	public String getProjectionParameters(){		String s = "";		s += "\t\t\t<standardLatitude1>" + standardLatitude1.toString() + "</standardLatitude1>\n";		s += "\t\t\t<standardLatitude2>" + standardLatitude2.toString() + "</standardLatitude2>\n";		s += "\t\t\t<originLongitude>" + originLongitude.toString() + "</originLongitude>\n";		s += "\t\t\t<originLatitude>" + originLatitude.toString() + "</originLatitude>\n";		return s;	}	/*.................................................................................................................*/	public boolean readProjectionParameters(String contents){		Parser subParser = new Parser();		subParser.setString(contents);		MesquiteString nextTag = new MesquiteString();		String subTagContent = subParser.getNextXMLTaggedContent(nextTag);		while (!StringUtil.blank(nextTag.getValue())) {			if ("standardLatitude1".equalsIgnoreCase(nextTag.getValue())) {// here's the signed longitude, stored in subTagContent				standardLatitude1.setValue(MesquiteDouble.fromString(subTagContent));			}			else if ("standardLatitude2".equalsIgnoreCase(nextTag.getValue())) {// here's the signed latitude, stored in subTagContent				standardLatitude2.setValue(MesquiteDouble.fromString(subTagContent));			}			else if ("originLongitude".equalsIgnoreCase(nextTag.getValue())) {// here's the signed longitude, stored in subTagContent				originLongitude.setValue(MesquiteDouble.fromString(subTagContent));			}			else if ("originLatitude".equalsIgnoreCase(nextTag.getValue())) {// here's the signed latitude, stored in subTagContent				originLatitude.setValue(MesquiteDouble.fromString(subTagContent));			}			subTagContent = subParser.getNextXMLTaggedContent(nextTag);		}		return true;	}	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {     	 	if (checker.compare(this.getClass(), "Sets the parameters of the map", "[top bottom left right]", commandName, "projectionParameters")) {    	 		if (StringUtil.blank(arguments) && !MesquiteThread.isScripting()) {    	 			if (queryStandards())     	 				if ((standardLatitude1.isCombinable() &&standardLatitude2.isCombinable() &&originLatitude.isCombinable() && originLongitude.isCombinable())) {						processProjectionParameters(false);						parametersChanged(); 					}    	 		}    	 		else {	    	 		originLatitude.setValue(MesquiteDouble.fromString(parser.getFirstToken(arguments)));	     	 		originLongitude.setValue(MesquiteDouble.fromString(parser.getNextToken()));	     	 		standardLatitude1.setValue(MesquiteDouble.fromString(parser.getNextToken()));	     	 		standardLatitude2.setValue(MesquiteDouble.fromString(parser.getNextToken()));    	 			if (!(standardLatitude1.isCombinable() &&standardLatitude2.isCombinable() &&originLatitude.isCombinable() && originLongitude.isCombinable()))	    	 			if (!MesquiteThread.isScripting())	    	 				queryStandards();    	 			if (!(standardLatitude1.isCombinable() &&standardLatitude2.isCombinable() &&originLatitude.isCombinable() && originLongitude.isCombinable()))	    	 			return null;	    	 		else  {					processProjectionParameters(false);	    	 			parametersChanged(); 	    	 		}    	 		}    	 	} 	 	else       	 		return  super.doCommand(commandName, arguments, checker);    	 	return null;    	 }	/*.................................................................................................................*/	public boolean queryStandards() {		MesquiteInteger buttonPressed = new MesquiteInteger(1);		ExtensibleDialog queryDialog = new ExtensibleDialog(containerOfModule(), "Projection Parameters",  buttonPressed);		queryDialog.addLabel("Parameters of Projection", Label.CENTER);		DoubleField latField1 = queryDialog.addDoubleField("Standard latitude 1:", standardLatitude1.getDoubleValue(),8,-90.0, 90.0);		DoubleField latField2 = queryDialog.addDoubleField("Standard latitude 2:", standardLatitude2.getDoubleValue(),8,-90.0, 90.0);		DoubleField latField = queryDialog.addDoubleField("Coordinate origin latitude:", originLatitude.getDoubleValue(),8,-90.0, 90.0);		DoubleField  longField = queryDialog.addDoubleField("Coordinate origin longitude:", originLongitude.getDoubleValue(),8,-180.0, 180.0);		String s = getStringDescribingLatLongs();		queryDialog.appendToHelpString(s);		queryDialog.completeAndShowDialog(true);					boolean ok = (queryDialog.query()==0);		MesquiteBoolean success = new MesquiteBoolean(true);				if (ok) {			standardLatitude1.setValue(latField1.getValue(success));  			standardLatitude2.setValue(latField2.getValue(success));  			originLatitude.setValue(latField.getValue(success));  			originLongitude.setValue(longField.getValue(success));  		}				queryDialog.dispose();   		 		if (!success.getValue()) {			discreetAlert("Some values are out of bounds; these values will be reset to their previous values.");		}   		return ok;	}	/*_________________________________________________*/	/**   */   	public void processProjectionParameters(boolean duringOptimization){		super.processProjectionParameters(duringOptimization);		phi0 = GeographicData.getPolarLatitude(originLatitude.getDoubleValue());		phi1 = GeographicData.getPolarLatitude(standardLatitude1.getDoubleValue());		phi2 = GeographicData.getPolarLatitude(standardLatitude2.getDoubleValue());		lambda0 = GeographicData.getPolarLongitude(originLongitude.getDoubleValue());   	}	/*.................................................................................................................*/	public double evaluate(MesquiteDouble param, Object param2){			return 0;	}	/*.................................................................................................................*/	public double evaluate(double[] x, Object param){		if (x == null || x.length != 4) {			return 0;		}		if (x[0]>90.0 || x[0]<-90.0)			return MesquiteDouble.veryLargeNumber;		if (x[1]>90.0 || x[1]<-90.0)			return MesquiteDouble.veryLargeNumber;		if (x[2]>90.0 || x[2]<-90.0)			return MesquiteDouble.veryLargeNumber;		if (x[3]>180.0 || x[3]<-180.0)			return MesquiteDouble.veryLargeNumber;		standardLatitude1.setValue(x[0]);  		standardLatitude2.setValue(x[1]);  		originLatitude.setValue(x[2]);  		originLongitude.setValue(x[3]); 		processProjectionParameters(true);		double score = getMismatch();		reportOptimizationToUser(score);		return score;	}	/*.................................................................................................................*/    	 public void setParametersToCalibrationPoint(int j){		if (calibrations[j]!=null) {			standardLatitude1.setValue(calibrations[j].getLatitude().getDoubleValue());  			standardLatitude2.setValue(calibrations[j].getLatitude().getDoubleValue());  			originLatitude.setValue(calibrations[j].getLatitude().getDoubleValue());  			originLongitude.setValue(calibrations[j].getLongitude().getDoubleValue()); 		}    	 }	/*.................................................................................................................*/    	 public void setParametersToTwoCalibrationPoints(int i, int j){ 		if (calibrations[i]!=null) {			standardLatitude1.setValue(calibrations[i].getLatitude().getDoubleValue());  			originLatitude.setValue(calibrations[i].getLatitude().getDoubleValue());  		} 		if (calibrations[j]!=null) {			standardLatitude2.setValue(calibrations[j].getLatitude().getDoubleValue());  			originLongitude.setValue(calibrations[j].getLongitude().getDoubleValue()); 		}   	 }	/*.................................................................................................................*/    	 public int getNumParameters (){    	 	return 4;    	 }	/*.................................................................................................................*/		double standardLatitude1Stored, standardLatitude2Stored, originLatitudeStored, originLongitudeStored; 	/*.................................................................................................................*/    	 public void storeParameters() {		standardLatitude1Stored = standardLatitude1.getDoubleValue();  		standardLatitude2Stored = standardLatitude2.getDoubleValue();  		originLatitudeStored = originLatitude.getDoubleValue();  		originLongitudeStored =originLongitude.getDoubleValue();      	 }	/*.................................................................................................................*/    	 public void recoverStoredParameters() {		standardLatitude1.setValue(standardLatitude1Stored);  		standardLatitude2.setValue(standardLatitude2Stored);  		originLatitude.setValue(originLatitudeStored);  		originLongitude.setValue(originLongitudeStored);      	 }	/*.................................................................................................................*/    	 public void setParametersIfUnassigned(boolean setEvenIfAssigned) {		if (!standardLatitude1.isCombinable() || setEvenIfAssigned)			standardLatitude1.setValue(RandomBetween.getDoubleStatic(-90.0,90.0));  		if (!standardLatitude2.isCombinable() || setEvenIfAssigned)			standardLatitude2.setValue(RandomBetween.getDoubleStatic(-90.0,90.0));  		if (!originLatitude.isCombinable() || setEvenIfAssigned)			originLatitude.setValue(RandomBetween.getDoubleStatic(-90.0,90.0));  		if (!originLongitude.isCombinable() || setEvenIfAssigned)			originLongitude.setValue(RandomBetween.getDoubleStatic(-180.0,180.0));      	 }	/*.................................................................................................................*/    	 public double[] getParamArray() {    	 	double[] x = new double[4];    	 	x[0] = standardLatitude1.getDoubleValue();    	 	x[1] = standardLatitude2.getDoubleValue();    	 	x[2] = originLatitude.getDoubleValue();    	 	x[3] = originLongitude.getDoubleValue();    	 	return x;    	 }	/*.................................................................................................................*/   	public String getParameters() { 		return "";   	}   	 }