/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.TaxonGroupSymbolsTax;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;//import mesquite.cartographer.lib.*;/* ======================================================================== */public class TaxonGroupSymbolsTax extends TaxaDisplayAssistantDI {	public Vector extras;	public boolean first = true;	int defaultSymbolSize = 4;	MesquiteBoolean showSymbols, groupColors, combineColors;	MesquiteMenuItemSpec showItem, colorChoiceItem, combineColorsItem;		/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName){		extras = new Vector();		showSymbols = new MesquiteBoolean(false);		groupColors = new MesquiteBoolean(true);		combineColors = new MesquiteBoolean(false);		showItem = addCheckMenuItem(null, "Show Taxon Group Symbols", makeCommand("showSymbols", this), showSymbols);		colorChoiceItem = addCheckMenuItem(null,"Use Group Colors for Symbols", makeCommand("setColors", this), groupColors);			combineColorsItem = addCheckMenuItem(null,"Darker Symbols where Overlap", makeCommand("combineColors", this), combineColors); 		addMenuItem( "Size of Default Symbol...", makeCommand("setSymbolSize",  this));		//addItemToSubmenu("Set Symbol Size...", makeCommand("setSize", this)); 				return true;	} 	/*.................................................................................................................*	public void resetMenus(){		if (showSymbols.getValue()) { //symbols are being shown			if (colorChoiceItem == null) { //menus need redoing				deleteMenuItem(showItem);				showItem = addCheckMenuItem(null, "Show Taxon Group Symbols", makeCommand("showSymbols", this), showSymbols);				colorChoiceItem = addCheckMenuItem(null,"Use Group Colors for Symbols", makeCommand("setColors", this), groupColors);				resetContainingMenuBar();			}		}		else {			if (colorChoiceItem != null) { //menus need redoing				deleteMenuItem(colorChoiceItem);				colorChoiceItem = null;				resetContainingMenuBar();			}		}	}	/*.................................................................................................................*/	public boolean canHireMoreThanOnce(){		return false;	}	/*.................................................................................................................*/	public   TaxaDisplayExtra createTaxaDisplayExtra(TaxaDisplay taxaDisplay) {		TaxonSymbolsExtraTax newPj = new TaxonSymbolsExtraTax(this, taxaDisplay);		extras.addElement(newPj);		return newPj;	}	/*.................................................................................................................*/    	 public boolean getGroupColors() {		return groupColors.getValue();   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Taxon Group Symbols (Taxa Window)";   	 } 	/*.................................................................................................................*/    	 public String getNameForMenuItem() {		return "Show Taxon Group Symbols";   	 }  	 	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {  	 	Snapshot temp = new Snapshot();  	 	temp.addLine("showSymbols " + showSymbols.toOffOnString());  	 	temp.addLine("combineColors " + combineColors.toOffOnString());  		temp.addLine("setColors " + groupColors.toOffOnString());  	 	temp.addLine("setSymbolSize " + defaultSymbolSize);  	 	return temp;  	 }	MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Sets diameter of default symbol", "[diameter]", commandName, "setSymbolSize")) {			int newDiameter = MesquiteInteger.fromFirstToken(arguments, pos);			if (!MesquiteInteger.isCombinable(newDiameter))				newDiameter = MesquiteInteger.queryInteger(containerOfModule(), "Set Diameter of Default Symbols", "Diameter:", defaultSymbolSize, 0, 100);    	 			    	 		if (newDiameter>-1 && newDiameter<100 && newDiameter!=defaultSymbolSize) {				defaultSymbolSize = newDiameter;		    	 	parametersChanged();    	 		}    	 		    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether or not taxon symbols are shown", "[on; off]", commandName, "showSymbols")) {    	 		showSymbols.toggleValue(parser.getFirstToken(arguments));    	 		//resetMenus();			parametersChanged();    	 	}   	 	else if (checker.compare(this.getClass(), "Sets whether or not symbols are darkened where they overlap", "[on; off]", commandName, "combineColors")) {    	 		combineColors.toggleValue(parser.getFirstToken(arguments));			parametersChanged();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether or not taxon symbols are shown in the taxon's group colors", "[on; off]", commandName, "setColors")) {    	 		groupColors.toggleValue(parser.getFirstToken(arguments));    	 		//resetMenus();			parametersChanged();    	 	}      	 	else    	 		return  super.doCommand(commandName, arguments, checker);		return null;   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Controls whether taxon symbols are drawn at terminal nodes." ;   	 }	public boolean isSubstantive(){		return false;	}   	 	public void endJob(){		if (extras !=null){			Enumeration e = extras.elements();			while (e.hasMoreElements()) {				Object obj = e.nextElement();				if (obj instanceof TaxaDisplayExtra) {					TaxaDisplayExtra tCO = (TaxaDisplayExtra)obj;		 			tCO.turnOff();		 		}			} 		} 		super.endJob(); 	} 	}/* ======================================================================== */class TaxonSymbolsExtraTax extends TaxaDisplayExtra  {	TaxonGroupSymbolsTax shapesModule;	TaxaPartition currentPartition=null;	TaxaGroup tg;	Polygon upTriangle, square, diamond, downTriangle, star;	String currentShape = "squareOpen";	MesquiteSymbol defaultSymbol = new CircleSymbol();	public TaxonSymbolsExtraTax (TaxonGroupSymbolsTax ownerModule, TaxaDisplay taxaDisplay) {		super(ownerModule, taxaDisplay);		shapesModule = ownerModule;	}	void update(){		taxaDisplay.pleaseUpdate();	}	/**returns whether or not the taxon is in the bounds specified*/	public boolean taxonInRectangle(Taxa taxa, int it, int x1, int y1, int x2, int y2){		if (taxa==null || taxaDisplay==null)			return false;		if (currentPartition==null)			currentPartition = (TaxaPartition)taxa.getCurrentSpecsSet(TaxaPartition.class);		int symbolX = taxaDisplay.getTaxaDrawing().x[it];		int symbolY = taxaDisplay.getTaxaDrawing().y[it];		TaxaGroup tg=null;		if (currentPartition!=null)			tg = currentPartition.getTaxaGroup(it);					MesquiteSymbol symbol;		if (tg==null || !shapesModule.showSymbols.getValue()) {			symbol = defaultSymbol;			symbol.setSize(shapesModule.defaultSymbolSize);		}		else			 symbol = tg.getSymbol();		if (symbol==null)			symbol = defaultSymbol;		return symbol.inRect(symbolX, symbolY,x1,y1, x2,y2);	}	/*.................................................................................................................*/	public boolean pointInTaxon(Taxa taxa, int it, int x, int y) {		if (taxa==null || taxaDisplay==null)			return false;		if (currentPartition==null)			currentPartition = (TaxaPartition)taxa.getCurrentSpecsSet(TaxaPartition.class);		int symbolX = taxaDisplay.getTaxaDrawing().x[it];		int symbolY = taxaDisplay.getTaxaDrawing().y[it];		TaxaGroup tg=null;		if (currentPartition!=null)			tg = currentPartition.getTaxaGroup(it);					MesquiteSymbol symbol;		if (tg==null || !shapesModule.showSymbols.getValue()) {			symbol = defaultSymbol;			symbol.setSize(shapesModule.defaultSymbolSize);		}		else			 symbol = tg.getSymbol();		if (symbol==null)			symbol = defaultSymbol;		return symbol.inSymbol(symbolX, symbolY,x,y);	}	/*.................................................................................................................*/	public   void fillTaxon(Taxa taxa, Graphics g, int it) {		if (taxa==null || taxaDisplay==null)			return;		if (currentPartition==null)			currentPartition = (TaxaPartition)taxa.getCurrentSpecsSet(TaxaPartition.class);		int x = taxaDisplay.getTaxaDrawing().x[it];		int y = taxaDisplay.getTaxaDrawing().y[it];		TaxaGroup tg=null;		if (currentPartition!=null)			tg = currentPartition.getTaxaGroup(it);					MesquiteSymbol symbol;		if (tg==null || !shapesModule.showSymbols.getValue()) {			symbol = defaultSymbol;			symbol.setSize(shapesModule.defaultSymbolSize);		}		else			 symbol = tg.getSymbol();		if (symbol==null)			symbol = defaultSymbol;		symbol.fillSymbol(g,x,y);	}	/*.................................................................................................................*/	public boolean drawTaxon(TaxaGroup tg) {		return true;	}	/*.................................................................................................................*/	public   void drawOnTaxa(Taxa taxa, Graphics g) {		if (currentPartition==null)			currentPartition = (TaxaPartition)taxa.getCurrentSpecsSet(TaxaPartition.class);		for (int it = 0; it<taxa.getNumTaxa(); it++){			int x = taxaDisplay.getTaxaDrawing().x[it];			int y = taxaDisplay.getTaxaDrawing().y[it];			if (MesquiteInteger.isCombinable(x) && MesquiteInteger.isCombinable(y)) {				TaxaGroup tg=null;				if (currentPartition!=null)					tg = currentPartition.getTaxaGroup(it);				if (tg==null || tg.isVisible()){					MesquiteSymbol symbol;					if (tg==null || !shapesModule.showSymbols.getValue()) {						symbol = defaultSymbol;						symbol.setSize(shapesModule.defaultSymbolSize);					}					else						symbol = tg.getSymbol();					if (symbol==null)						symbol = defaultSymbol;					Composite composite = ColorDistribution.getComposite(g);					if (symbol instanceof FillableMesquiteSymbol) {						Color color;						if (shapesModule.combineColors.getValue())							ColorDistribution.setTransparentGraphics(g);						if (shapesModule.getGroupColors() && tg!=null){							color = tg.getColor();						}						else							color = Color.black;						if (taxa.anySelected() && !taxa.getSelected(it))							color = ColorDistribution.brighter(color, ColorDistribution.dimmingConstant);						((FillableMesquiteSymbol)symbol).setFillColor(color);					}					symbol.drawSymbol(g,x,y);					ColorDistribution.setComposite(g,composite);						}			}		}	}		/*.................................................................................................................*/	public   void printOnTaxa(Taxa taxa, Graphics g) {		drawOnTaxa(taxa, g); 	}	/*.................................................................................................................*/	public   void setTaxa(Taxa taxa) {	}	Color getColorForTaxon(Taxa taxa, int it){		if ((!taxa.getSelected(it) && taxa.anySelected())) {			return Color.gray;		}		else {			if (currentPartition!=null) {				tg = currentPartition.getTaxaGroup(it);				if (tg!=null){					Color cT = tg.getColor();					if (cT!=null){						return cT;					}				}			}			return Color.black;		}	}  	 MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/	public void turnOff() {		shapesModule.extras.removeElement(this);		super.turnOff();	}}