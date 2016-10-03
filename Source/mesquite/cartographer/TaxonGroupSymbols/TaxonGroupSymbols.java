/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.cartographer.TaxonGroupSymbols;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class TaxonGroupSymbols extends TreeDisplayAssistantDI {	public Vector extras;	public boolean first = true;	int size = 6;	MesquiteBoolean showSymbols, groupColors, combineColors;	boolean showSymbolsDefault = false;	boolean combineColorsDefault = false;	boolean groupColorsDefault = true;	MesquiteMenuItemSpec showItem, colorChoiceItem, combineColorsItem;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName){		extras = new Vector();		showSymbols = new MesquiteBoolean(showSymbolsDefault);		combineColors = new MesquiteBoolean(combineColorsDefault);		groupColors = new MesquiteBoolean(groupColorsDefault);		showItem = addCheckMenuItem(null, "Show Taxon Group Symbols", makeCommand("showSymbols", this), showSymbols);		colorChoiceItem = addCheckMenuItem(null,"Use Group Colors for Symbols", makeCommand("setColors", this), groupColors);			combineColorsItem = addCheckMenuItem(null,"Darker Symbols where Overlap", makeCommand("combineColors", this), combineColors);		//addItemToSubmenu("Set Symbol Size...", makeCommand("setSize", this));		return true;	} 	/*.................................................................................................................*/	public void resetMenus(){		if (showSymbols.getValue()) { //symbols are being shown			if (colorChoiceItem == null) { //menus need redoing				deleteMenuItem(showItem);				showItem = addCheckMenuItem(null, "Show Taxon Group Symbols", makeCommand("showSymbols", this), showSymbols);				colorChoiceItem = addCheckMenuItem(null,"Use Group Colors for Symbols", makeCommand("setColors", this), groupColors);					combineColorsItem = addCheckMenuItem(null,"Darker Symbols where Overlap", makeCommand("combineColors", this), combineColors);				resetContainingMenuBar();			}		}		else {			if (colorChoiceItem != null) { //menus need redoing				deleteMenuItem(colorChoiceItem);				colorChoiceItem = null;				deleteMenuItem(combineColorsItem);				combineColorsItem = null;				resetContainingMenuBar();			}		}	}	/*.................................................................................................................*/	public boolean canHireMoreThanOnce(){		return false;	}	/*.................................................................................................................*/	public   TreeDisplayExtra createTreeDisplayExtra(TreeDisplay treeDisplay) {		TaxonSymbolsExtra newPj = new TaxonSymbolsExtra(this, treeDisplay);		extras.addElement(newPj);		return newPj;	}	/*.................................................................................................................*/	public boolean getGroupColors() {		return groupColors.getValue();	}	/*.................................................................................................................*/	public String getName() {		return "Taxon Group Symbols (Tree Window)";	}	/*.................................................................................................................*/	public String getNameForMenuItem() {		return "Show Taxon Group Symbols";	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) {		Snapshot temp = new Snapshot();		if (showSymbols.getValue() != showSymbolsDefault || combineColors.getValue() != combineColorsDefault || groupColors.getValue() != groupColorsDefault){			temp.addLine("showSymbols " + showSymbols.toOffOnString());			temp.addLine("combineColors " + combineColors.toOffOnString());			temp.addLine("setColors " + groupColors.toOffOnString());		}		return temp;	}	MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets whether or not taxon symbols are shown", "[on; off]", commandName, "showSymbols")) {			showSymbols.toggleValue(parser.getFirstToken(arguments));			resetMenus();			parametersChanged();		}		else if (checker.compare(this.getClass(), "Sets whether or not symbols are darkened where they overlap", "[on; off]", commandName, "combineColors")) {			combineColors.toggleValue(parser.getFirstToken(arguments));			resetMenus();			parametersChanged();		}		else if (checker.compare(this.getClass(), "Sets whether or not taxon symbols are shown in the taxon's group colors", "[on; off]", commandName, "setColors")) {			groupColors.toggleValue(parser.getFirstToken(arguments));			resetMenus();			parametersChanged();		}		else			return  super.doCommand(commandName, arguments, checker);		return null;	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Controls whether taxon symbols are drawn at terminal nodes." ;	}	public boolean isSubstantive(){		return false;	}   	 	public void endJob(){		if (extras !=null){			Enumeration e = extras.elements();			while (e.hasMoreElements()) {				Object obj = e.nextElement();				if (obj instanceof TreeDisplayExtra) {					TreeDisplayExtra tCO = (TreeDisplayExtra)obj;					tCO.turnOff();				}			}		}		super.endJob();	}}/* ======================================================================== */class TaxonSymbolsExtra extends TreeDisplayExtra  {	TaxonGroupSymbols shapesModule;	TaxaPartition currentPartition=null;	TaxaGroup tg;	Polygon upTriangle, square, diamond, downTriangle, star;	String currentShape = "squareOpen";	int size = 10;	MesquiteSymbol defaultSymbol = new CircleSymbol();	public TaxonSymbolsExtra (TaxonGroupSymbols ownerModule, TreeDisplay treeDisplay) {		super(ownerModule, treeDisplay);		shapesModule = ownerModule;	}	void update(){		treeDisplay.pleaseUpdate();	}	/*.................................................................................................................*/	public   void drawOnTree(Tree tree, int node, Graphics g) {		if (currentPartition==null)			currentPartition = (TaxaPartition)tree.getTaxa().getCurrentSpecsSet(TaxaPartition.class);		if (!shapesModule.showSymbols.getValue())			return;		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))			drawOnTree(tree, d, g);		if (tree.nodeIsTerminal(node)){			int it = tree.taxonNumberOfNode(node);			int x = (int)treeDisplay.getTreeDrawing().getX(node);			int y = (int)treeDisplay.getTreeDrawing().getY(node);			if (MesquiteInteger.isCombinable(x) && MesquiteInteger.isCombinable(y)) {				TaxaGroup tg = currentPartition.getTaxaGroup(it);				MesquiteSymbol symbol;				if (tg==null)					symbol = defaultSymbol;				else					symbol = tg.getSymbol();				if (symbol==null)					symbol = defaultSymbol;		 		Composite composite = ColorDistribution.getComposite(g);				if (symbol instanceof FillableMesquiteSymbol) {					Color color;					if (shapesModule.combineColors.getValue())						ColorDistribution.setTransparentGraphics(g);					if (shapesModule.getGroupColors()&& tg!=null){						color = tg.getColor();					}					else						color = Color.black;					if (tree.anySelected() && !tree.getSelected(node))						color = ColorDistribution.brighter(color, ColorDistribution.dimmingConstant);					((FillableMesquiteSymbol)symbol).setFillColor(color);				}				symbol.drawSymbol(g,x,y);				ColorDistribution.setComposite(g,composite);					}		}	}	/*.................................................................................................................*/	public   void printOnTree(Tree tree, int drawnRoot, Graphics g) {		drawOnTree(tree, drawnRoot, g); 	}	/*.................................................................................................................*/	public   void setTree(Tree tree) {	}	Color getColorForTaxon(Taxa taxa, int it, Tree tree, int node){		if ((!taxa.getSelected(it) && taxa.anySelected()) || (!tree.getSelected(node) && tree.anySelected())) {			return Color.gray;		}		else {			if (currentPartition!=null) {				tg = currentPartition.getTaxaGroup(it);				if (tg!=null){					Color cT = tg.getColor();					if (cT!=null){						return cT;					}				}			}			return Color.black;		}	}	MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/	public void turnOff() {		shapesModule.extras.removeElement(this);		super.turnOff();	}}