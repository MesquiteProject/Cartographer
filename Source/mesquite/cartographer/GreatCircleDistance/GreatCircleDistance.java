/* Mesquite source code.  Copyright 1997-2009 W. Maddison and D. Maddison.
Version 2.7, August 2009.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */
package mesquite.cartographer.GreatCircleDistance;
/*~~  */


import java.awt.Checkbox;

import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.distance.lib.*;
import mesquite.cont.lib.*;
import mesquite.cartographer.lib.*;

/* ======================================================================== */
public class GreatCircleDistance extends GeoTaxaDistFromMatrix {
	MesquiteBoolean calcMiles, calcKilometers;

	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		calcKilometers= new MesquiteBoolean(true);
		calcMiles= new MesquiteBoolean(false);
		MesquiteSubmenuSpec mss = addSubmenu(null, "Distance Units");
		addCheckMenuItemToSubmenu( null, mss,"Kilometers", makeCommand("setCalcKilometers",  this), calcKilometers);
		addCheckMenuItemToSubmenu( null, mss,"Miles", makeCommand("setCalcMiles",  this), calcMiles);
		return true;
	}	 
	public TaxaDistance getTaxaDistance(Taxa taxa, MCharactersDistribution observedStates){
		if (observedStates==null) {
			MesquiteMessage.warnProgrammer("Observed states null in "+ getName());
			return null;
		}
		GreatCircleTaxDist TD = new GreatCircleTaxDist( this,taxa, observedStates);
		return TD;
	}
	/*.................................................................................................................*/
	public boolean useKilometers() {
		return calcKilometers.getValue();
	}
	/*.................................................................................................................*/
	public String getName() {
		return "Great Circle Distance";  

	}

	public boolean optionsAdded() {
		return true;
	}
	RadioButtons radios;
	public void addOptions(ExtensibleDialog dialog) {
		super.addOptions(dialog);
		String[] labels =  {"kilometers", "miles"};
		int defaultValue= 0;
		if (calcKilometers.getValue())
			defaultValue = 0;
		else	if (calcMiles.getValue())
			defaultValue = 2;
		radios = dialog.addRadioButtons(labels, defaultValue);

	}
	public void processOptions(ExtensibleDialog dialog) {
		super.processOptions(dialog);
		if (radios.getValue()==0) {
			calcKilometers.setValue(true);
			calcMiles.setValue(false);
		}
		else if (radios.getValue()==1) {
			calcKilometers.setValue(false);
			calcMiles.setValue(true);
		}
	}

	/*.................................................................................................................*/
	public Snapshot getSnapshot(MesquiteFile file) {
		Snapshot temp = new Snapshot();
		temp.addLine("setCalcKilometers " + calcKilometers.toOffOnString());
		temp.addLine("setCalcMiles " + calcMiles.toOffOnString());
		return temp;
	}
	MesquiteInteger pos = new MesquiteInteger();
	/*.................................................................................................................*/
	public Object doCommand(String commandName, String arguments, CommandChecker checker) {
		if (checker.compare(this.getClass(), "Sets whether or not kilometers should be used for Great Circle Distance calculations.", "[on or off]", commandName, "setCalcKilometers")) {
			boolean current = calcKilometers.getValue();
			calcKilometers.toggleValue(parser.getFirstToken(arguments));
			if (current!=calcKilometers.getValue()) {
				if (calcKilometers.getValue())
					calcMiles.setValue(false);
				parametersChanged();
			}
		}
		else if (checker.compare(this.getClass(), "Sets whether or not miles should be used for Great Circle Distance calculations.", "[on or off]", commandName, "setCalcMiles")) {
			boolean current = calcMiles.getValue();
			calcMiles.toggleValue(parser.getFirstToken(arguments));
			if (current!=calcMiles.getValue()) {
				if (calcMiles.getValue())
					calcKilometers.setValue(false);
				parametersChanged();
			}

		}
		else
			return super.doCommand(commandName, arguments, checker);
		return null;
	}
	/*.................................................................................................................*/

	/** returns an explanation of what the module does.*/
	public String getExplanation() {
		return "Geographic distance from a character matrix." ;
	}

	public boolean requestPrimaryChoice(){
		return true;
	}
	public boolean isPrerelease(){
		return false;
	}
	/*.................................................................................................................*/
	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer
	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.
	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/
	public int getVersionOfFirstRelease(){
		return -100;  
	}
	/*.................................................................................................................*/
	public boolean showCitation(){
		return false;
	}
}



class GreatCircleTaxDist extends GeoTaxaDistance {
	GeographicData gData;
	GreatCircleDistance gcdModule;

	public GreatCircleTaxDist(MesquiteModule ownerModule, Taxa taxa, MCharactersDistribution observedStates){
		super(ownerModule, taxa, observedStates);
		gData = (GeographicData)observedStates.getParentData();
		gcdModule = (GreatCircleDistance)ownerModule;

	}

	/** Great Circle Distance as calculated using the formulae from 			
	 * http://mathworld.wolfram.com/GreatCircle.html

	 */

	public double getDistance(int taxon1,int  taxon2){
		if (taxon1>=0 && taxon1<getNumTaxa() && taxon2>=0 && taxon2<getNumTaxa() && gData!=null) {
			return GeographicUtil.getGreatCircleDistance(gcdModule.useKilometers(), gData.getState(GeographicData.getLongitudeCharacter(),taxon1,0),gData.getState(GeographicData.getLongitudeCharacter(),taxon2,0),gData.getState(GeographicData.getLatitudeCharacter(),taxon1,0), gData.getState(GeographicData.getLatitudeCharacter(),taxon2,0));
		}		
		else
			return MesquiteDouble.unassigned;
	}	


	public boolean isSymmetrical() {
		return true;
	}




}




