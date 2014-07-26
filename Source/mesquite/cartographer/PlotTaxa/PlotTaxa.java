/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.PlotTaxa;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.cartographer.lib.*;//import mesquite.rhetenor.*;/* ======================================================================== */public class PlotTaxa extends DrawTaxa {	TaxonLocsPlot taxaLocsTask;	Vector drawings;	int spotSize = 4;	public MesquiteBoolean showSpots;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		taxaLocsTask= (TaxonLocsPlot)hireEmployee(TaxonLocsPlot.class, "Method to choose taxon locations");		if (taxaLocsTask == null)			return sorry(getName() + " couldn't start because no taxon location plotter module was obtained.");		drawings = new Vector(); 		showSpots = new MesquiteBoolean(true);		addCheckMenuItem(null, "Show Standard Taxon Spots", makeCommand("toggleShowSpots",  this), showSpots);		addMenuItem( "Spot Size...", makeCommand("setSpotDiameter",  this)); 		return true; 	 }  	 	/*.................................................................................................................*/   	 public boolean showCitation(){   	 	return true;   	 }	/*.................................................................................................................*/	public boolean isSubstantive(){		return true;	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;	}   	 public Dimension getPreferredSize(){   	 	if (taxaLocsTask == null)   	 		return null;   	 		   	 	return taxaLocsTask.getPreferredSize();   	 } 	public void employeeQuit(MesquiteModule m){ 			iQuit(); 	}	/*.................................................................................................................*/	public   TaxaDrawing createTaxaDrawing(TaxaDisplay taxaDisplay, int numTaxa) {		PlotTaxaDrawing taxaDrawing =  new PlotTaxaDrawing (taxaDisplay, numTaxa, this, spotSize);		drawings.addElement(taxaDrawing);		return taxaDrawing;	}  	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine("setNodeLocs " , taxaLocsTask);  	 	temp.addLine("setSpotDiameter " + spotSize);  	 	temp.addLine("toggleShowSpots " + showSpots.toOffOnString()); 	 	return temp;  	 }	MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets diameter of spots at taxa", "[diameter]", commandName, "setSpotDiameter")) {			int newDiameter = MesquiteInteger.fromFirstToken(arguments, pos);			if (!MesquiteInteger.isCombinable(newDiameter))				newDiameter = MesquiteInteger.queryInteger(containerOfModule(), "Set spot diameter", "Spot Diameter:", spotSize, 0, 100);			if (newDiameter>-1 && newDiameter<100 && newDiameter!=spotSize) {				Enumeration e = drawings.elements();				while (e.hasMoreElements()) {					Object obj = e.nextElement();					PlotTaxaDrawing taxaDrawing = (PlotTaxaDrawing)obj;					spotSize = newDiameter;					taxaDrawing.spotsize=newDiameter;					parametersChanged();				}			}		}		else if (checker.compare(this.getClass(), "Sets whether or not standard spots are shown", "[on = show;  off]", commandName, "toggleShowSpots")) {			showSpots.toggleValue(parser.getFirstToken(arguments));			parametersChanged();			Enumeration e = drawings.elements();			while (e.hasMoreElements()) {				Object obj = e.nextElement();				PlotTaxaDrawing taxaDrawing = (PlotTaxaDrawing)obj;				taxaDrawing.taxaDisplay.repaint();			}		}		else if (checker.compare(this.getClass(), "Sets the module that calculates the taxon locations", "[name of module]", commandName, "setNodeLocs")) {			TaxonLocsPlot temp= (TaxonLocsPlot)replaceEmployee(TaxonLocsPlot.class, arguments, "Method choose taxon locations", taxaLocsTask);			if (temp!=null) {				taxaLocsTask=temp;				parametersChanged();				Enumeration e = drawings.elements();				while (e.hasMoreElements()) {					Object obj = e.nextElement();					PlotTaxaDrawing taxaDrawing = (PlotTaxaDrawing)obj;					taxaDrawing.taxaDisplay.repaint();				}			}			return temp;		}		else {			return  super.doCommand(commandName, arguments, checker);		}		return null;	}	/*.................................................................................................................*/    	 public String getName() {		return "Plot Taxa";   	 }   	 	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Draws taxa plotted in a two dimensional space." ;   	 }	/*.................................................................................................................*/   	 }/* ======================================================================== */class PlotTaxaDrawing extends TaxaDrawing  {	public PlotTaxa ownerModule;	public int edgewidth = 4;	public int spotsize = 6;	int oldNumTaxa = 0; 	public static final int inset=1;	private boolean ready=false;//	private int foundBranch;	private NameReference colorNameRef;	public PlotTaxaDrawing (TaxaDisplay taxaDisplay, int numTaxa, PlotTaxa ownerModule, int spotSize) {		super(taxaDisplay, numTaxa); //*DRM		this.spotsize = spotSize;		colorNameRef = NameReference.getNameReference("Color");		this.ownerModule = ownerModule;		this.taxaDisplay = taxaDisplay;		oldNumTaxa = numTaxa;		ready = true;	}		private int getSpotSize(int it){			return spotsize;	}		private int bumpUp(int i){		return i + (255-i)/4;	}	/*_________________________________________________*/	public   void drawTaxa(Taxa taxa, Graphics g) {	        	if (taxa.getNumTaxa()!=numTaxa)        			resetNumTaxa(taxa.getNumTaxa());//*DRM	        	g.setColor(taxaDisplay.branchColor);	       	 	drawSpots(taxa, g);  	   }		/*_________________________________________________*/	private   void drawSpots(Taxa taxa, Graphics g) {//*DRM			g.setColor(taxaDisplay.getBranchColor(node));						for (int it=0; it<taxa.getNumTaxa(); it++) {			drawSpot( g, it);		}	}	/*_________________________________________________*/	public   void recalculatePositions(Taxa taxa) {	        if (!ownerModule.isDoomed()) {	        	if (taxa.getNumTaxa()!=numTaxa)        			resetNumTaxa(taxa.getNumTaxa());			TaxonLocsPlot t = ownerModule.taxaLocsTask;			t.calculateTaxonLocs(taxaDisplay,  taxa,  taxaDisplay.getField()); 		}	}	/*_________________________________________________*/	private boolean inSpot(int it, int h, int v){		int s = getSpotSize(it);		if ((h-x[it])*(h-x[it]) + (v-y[it])*(v-y[it]) < s*s/4) //use radius			return true;		else			return false;	}	/*_________________________________________________*/	private void drawSpot(Graphics g, int it){		if (ownerModule.showSpots.getValue() && MesquiteInteger.isCombinable(x[it]) && MesquiteInteger.isCombinable(y[it])) {			int s = getSpotSize(it);			g.fillOval( x[it]- s/2, y[it]- s/2, s, s);		}	}	/*_________________________________________________*/	private void highlightSpot(Graphics g, int it){		if (MesquiteInteger.isCombinable(x[it]) && MesquiteInteger.isCombinable(y[it])) {			int s = getSpotSize(it);			for (int diam = s + 12; diam> s + 8; diam --)				g.drawOval( x[it]- (int)((double)diam/2 + 0.5), y[it]- (int)((double)diam/2 + 0.5), diam, diam);		}	}	/*_________________________________________________*/	private void fillSpot(Graphics g, int it){		if (MesquiteInteger.isCombinable(x[it]) && MesquiteInteger.isCombinable(y[it])) {			int s = getSpotSize(it);			g.fillOval( x[it]- s/2 + 2, y[it]- s/2 + 2, s - 4, s - 4);		}	}	/*_________________________________________________*/	public  void fillTerminalBox(Taxa taxa, int it, Graphics g) {	}	/*_________________________________________________*/	public  void fillTerminalBoxWithColors(Taxa taxa, int it, ColorDistribution colors, Graphics g){	}	/*_________________________________________________*/	public  int findTerminalBox(Taxa taxa, int x, int y){		return -1;	}	/*_________________________________________________*/	public void reorient(int orientation) {//*DRM		taxaDisplay.setOrientation(orientation);		taxaDisplay.pleaseUpdate(true);	}	/*_________________________________________________*/	public void setEdgeWidth(int edw) {		edgewidth = edw;	}}	