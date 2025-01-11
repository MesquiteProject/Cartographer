/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. �.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.BasicTaxaWindowCoord;/*~~  */import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.lib.taxa.*;import mesquite.lib.ui.*;/** Coordinates the display of the basic Taxa Windows (BasicTaxaWindowMaker actually makes the window) */public class BasicTaxaWindowCoord extends FileInit {	ListableVector taxaWindows;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) { 		getFileCoordinator().addMenuItem(MesquiteTrunk.treesMenu, "New Taxa Map", makeCommand("makeTaxaWindow",  this)); 		taxaWindows = new ListableVector(); 		MesquiteSubmenuSpec mms = getFileCoordinator().addSubmenu(MesquiteTrunk.treesMenu, "Current Taxa Maps", makeCommand("showTaxaWindow",  this)); 		mms.setList(taxaWindows); 		 		return true;  	 }  	   	 String taxaRef(Taxa d, boolean internal){  	 	if (internal)  	 		return getProject().getTaxaReferenceInternal(d);  	 	else  	 		return getProject().getTaxaReferenceExternal(d);  	 }	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return false;   	 }	/*.................................................................................................................*/   	 public boolean isPrerelease(){   	 	return false;   	 }	/*.................................................................................................................*/  	 public void employeeQuit(MesquiteModule m){  	 	if (m instanceof TaxaWindowMaker) {  	 		for (int i=0; i<taxaWindows.size(); i++){  	 			MesquiteWindow w = (MesquiteWindow)taxaWindows.elementAt(i);  	 			if (w.getOwnerModule() == m || w.getOwnerModule() == null) {  	 				taxaWindows.removeElement(w, false);  	 				resetAllMenuBars();  	 				return;  	 			}  	 		}  	 		resetAllMenuBars();  	 	}  	 }	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();		for (int i = 0; i<getNumberOfEmployees(); i++) {			Object e=getEmployeeVector().elementAt(i);			if (e instanceof TaxaWindowMaker) {				TaxaWindowMaker dwm = (TaxaWindowMaker)e;				Taxa d = (Taxa)dwm.doCommand("getTaxa", null, CommandChecker.defaultChecker);				if (d != null) {					temp.addLine("makeTaxaWindow " + taxaRef(d, false) + " ", dwm ); 				}			}		}  	 	return temp;  	 }  	 MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Requests that a taxa map be made", "[number of taxa block]", commandName, "makeTaxaWindow")) {	   	 	Taxa taxa = null;		   	if (!StringUtil.blank(arguments)) //rearranged to attempt to get some taxa to be used		   		taxa =  getProject().getTaxa(checker.getFile(), parser.getFirstToken(arguments));	   	 	if (taxa == null){	   	 		int numTaxas = getProject().getNumberTaxas(checker.getFile());	   	 		//if only one taxa block, use it	   	 		if (numTaxas<=0){	   	 			return null;	   	 		}	   	 		else if (numTaxas==1){		   	 		taxa =  getProject().getTaxa(checker.getFile(), 0);	   	 		}	   	 		else {		   	 		taxa =  getProject().chooseTaxa(containerOfModule(), "For which block of taxa do you want to show taxa in the taxa map?");	   	 			//else, query user	   	 		}	   	 	}   	 		if (taxa==null)   	 			return null;			TaxaWindowMaker taxaWindowTask= (TaxaWindowMaker)hireCompatibleEmployee(TaxaWindowMaker.class, taxa, null);			if (taxaWindowTask !=null){	   	 		taxaWindowTask.doCommand("makeTaxaWindow", getProject().getTaxaReferenceInternal(taxa), checker); 				MesquiteWindow btw = taxaWindowTask.getModuleWindow(); 				if (btw!=null) { 					taxaWindows.addElement(btw, false); 					resetAllMenuBars();					return taxaWindowTask;				}					else return null;   	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Shows an existing taxa map", "[number of taxa map as employee of coordinator]", commandName, "showTaxaWindow")) {    	 		pos.setValue(0);    	 		int which = MesquiteInteger.fromString(arguments, pos);    	 		if ((which == 0 || MesquiteInteger.isPositive(which)) && which<taxaWindows.size()) {    	 			MesquiteWindow win = (MesquiteWindow)taxaWindows.elementAt(which);    	 			win.show();    	 		}    	 			    	 	}    	 	else    	 		return  super.doCommand(commandName, arguments, checker);		return null;   	 }	/*.................................................................................................................*/	/**Returns command to hire employee if clonable*/	public String getClonableEmployeeCommand(MesquiteModule employee){		if (employee!=null && employee.getEmployer()==this) {			if (employee.getHiredAs()==TaxaWindowMaker.class) {				Taxa d = (Taxa)employee.doCommand("getTaxa", null, CommandChecker.defaultChecker);				if (d != null) {					return ("makeTaxaWindow " + taxaRef(d, true) + "  " + StringUtil.tokenize(employee.getName()) + ";");//quote				}			}		}		return null;	}	/*.................................................................................................................*/    	 public String getName() {		return "Taxa Map Coordinator";   	 }   	 	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Coordinates the creation of basic taxa maps." ;   	 }   	 }	