/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.BasicDrawNamesTaxaDisplay;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/** Draws the taxon names in a taxa drawing */public class BasicDrawNamesTaxaDisplay extends DrawNamesTaxaDisplay {	TaxaDisplay taxaDisplay;	TaxaDrawing taxaDrawing;	TaxonPolygon[] namePolys;	Taxa taxa;	Graphics gL;	Font currentFont = null;	String myFont = null;	int myFontSize = -1;	FontMetrics fm;	int rise;	int descent;	int oldNumTaxa=0;	MesquiteString fontSizeName, fontName;	MesquiteBoolean colorPartition, shadePartition;	MesquiteBoolean showTaxonNames;	MesquiteString fontColorName;	Color fontColor=Color.black;	Color fontColorLight = Color.gray;	int longestString = 0;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		currentFont = MesquiteWindow.defaultFont; 		fontName = new MesquiteString(MesquiteWindow.defaultFont.getName());		fontSizeName = new MesquiteString(Integer.toString(MesquiteWindow.defaultFont.getSize()));		MesquiteSubmenuSpec namesMenu = addSubmenu(null, "Names");		MesquiteSubmenuSpec msf = addSubmenu(null, "Font", makeCommand("setFont", this), MesquiteSubmenu.getFontList());		msf.setList(MesquiteSubmenu.getFontList());		msf.setDocumentItems(false);		msf.setSelected(fontName);		MesquiteSubmenuSpec mss = addSubmenu(null, "Font Size", makeCommand("setFontSize", this), MesquiteSubmenu.getFontSizeList());		mss.setList(MesquiteSubmenu.getFontSizeList());		mss.setDocumentItems(false);		mss.setSelected(fontSizeName);		fontColorName = new MesquiteString("Black");		MesquiteSubmenuSpec mmis = addSubmenu(null, "Default Font Color", makeCommand("setColor",  this));		mmis.setList(ColorDistribution.standardColorNames);		mmis.setSelected(fontColorName);		//MesquiteSubmenuSpec mssNames = addSubmenu(null, "Names");		colorPartition = new MesquiteBoolean(true);		addCheckMenuItemToSubmenu(null, namesMenu, "Color by Taxon Group", makeCommand("toggleColorPartition", this), colorPartition);		shadePartition = new MesquiteBoolean(false);		addCheckMenuItemToSubmenu(null, namesMenu, "Background Color by Taxon Group", makeCommand("toggleShadePartition", this), shadePartition);		showTaxonNames = new MesquiteBoolean(true);		addCheckMenuItemToSubmenu(null, namesMenu, "Show Taxon Names", makeCommand("toggleShowNames", this), showTaxonNames);				return true;  	 }  	    	public void endJob(){		taxaDisplay = null;		taxaDrawing = null;		super.endJob();   	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();		if (myFont!=null)			temp.addLine("setFont " + myFont);  //TODO: this causes problem since charts come before taxa window		if (myFontSize>0)			temp.addLine("setFontSize " + myFontSize);  //TODO: this causes problem since charts come before taxa window		temp.addLine("setColor " + ParseUtil.tokenize(fontColorName.toString()));  //TODO: this causes problem since charts come before taxa window		temp.addLine("toggleColorPartition " + colorPartition.toOffOnString());		temp.addLine("toggleShadePartition " + shadePartition.toOffOnString());		temp.addLine("toggleShowNames " + showTaxonNames.toOffOnString());  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Toggles whether taxon names are colored according to their group in the current taxa partition", "[on or off]", commandName, "toggleColorPartition")) {    	 		boolean current = colorPartition.getValue();    	 		colorPartition.toggleValue(parser.getFirstToken(arguments));    	 		if (current!=colorPartition.getValue())	    	 		parametersChanged();    	 	}    	 	else if (checker.compare(this.getClass(), "Toggles whether taxon names are given a background color according to their group in the current taxa partition", "[on or off]", commandName, "toggleShadePartition")) {    	 		boolean current = shadePartition.getValue();    	 		shadePartition.toggleValue(parser.getFirstToken(arguments));    	 		if (current!=shadePartition.getValue())	    	 		parametersChanged();    	 	}    	 	else if (checker.compare(this.getClass(), "Toggles whether names of terminal taxa are shown", "[on or off]", commandName, "toggleShowNames")) {    	 		boolean current = showTaxonNames.getValue();    	 		showTaxonNames.toggleValue(parser.getFirstToken(arguments));    	 		if (current!=showTaxonNames.getValue())	    	 		parametersChanged();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the font used for the taxon names", "[name of font]", commandName, "setFont")) {    	 		String t = parser.getFirstToken(arguments);    	 		if (currentFont==null){    	 			myFont = t; 				fontName.setValue(t);   	 		}    	 		else {	    	 		Font fontToSet = new Font (t, currentFont.getStyle(), currentFont.getSize());	    	 		if (fontToSet!= null) {    	 				myFont = t;    	 				fontName.setValue(t);	    	 			currentFont = fontToSet;	    	 			parametersChanged();	    	 		}    	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the font size used for the taxon names", "[size of font]", commandName, "setFontSize")) {    	 		int fontSize = MesquiteInteger.fromString(arguments);    	 		if (currentFont==null){	    	 		if (MesquiteThread.isScripting() && !MesquiteInteger.isPositive(fontSize))	    	 			fontSize = MesquiteInteger.queryInteger(containerOfModule(), "Font Size", "Font Size", 12);	    	 		if (MesquiteInteger.isPositive(fontSize)) {	    	 			myFontSize = fontSize;    	 				fontSizeName.setValue(Integer.toString(fontSize));    	 			}    	 		}    	 		else {	    	 		if (MesquiteThread.isScripting() && !MesquiteInteger.isPositive(fontSize))	    	 			fontSize = MesquiteInteger.queryInteger(containerOfModule(), "Font Size", "Font Size", currentFont.getSize());	    	 		if (MesquiteInteger.isPositive(fontSize)) {	    	 			myFontSize = fontSize;		    	 		Font fontToSet = new Font (currentFont.getName(), currentFont.getStyle(), fontSize);		    	 		if (fontToSet!= null) {		    	 			currentFont = fontToSet;    	 					fontSizeName.setValue(Integer.toString(fontSize));			    	 		parametersChanged();		    	 		}	    	 		}    	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets color of taxon names", "[name of color]", commandName, "setColor")) {    	 		String token = ParseUtil.getFirstToken(arguments, stringPos);    	 		Color bc = ColorDistribution.getStandardColor(token);			if (bc == null)				return null;			fontColor = bc;			fontColorLight = ColorDistribution.brighter(bc, ColorDistribution.dimmingConstant);			fontColorName.setValue(token);			parametersChanged();		}    	 	else     	 		return  super.doCommand(commandName, arguments, checker);		return null;   	 }   	public Font getFont(){   		return currentFont;   	}   	public void invalidateNames(TaxaDisplay taxaDisplay){   	}	/*.................................................................................................................*/	private void drawNamesOnTaxa(Taxa taxa, TaxaDisplay taxaDisplay, int it, TaxaPartition partitions) {		if (!showTaxonNames.getValue())			return;		Color bgColor = null;		if (it<0) {			//MesquiteMessage.warnProgrammer("error: negative taxon number found in DrawTaxonNames " + taxonNumber + "  taxa: " + taxa.writeTaxa());			return;		}		else if (it>=taxa.getNumTaxa()) {			//MesquiteMessage.warnProgrammer("error: taxon number too high found in DrawTaxonNames " + taxonNumber + "  taxa: " + taxa.writeTaxa());			return;		}		if (it>= namePolys.length) {			//MesquiteMessage.warnProgrammer("error: taxon number " + taxonNumber + " / name boxes " + nameBoxes.length);			return;		}		int horiz=taxaDrawing.x[it];		int vert=taxaDrawing.y[it];		int lengthString;		String s=taxa.getName(it);		if (s== null)			return;		Taxon taxon = taxa.getTaxon(it);		if (taxon== null)			return;		boolean selected = taxa.getSelected(it);		//check all extras to see if they want to add anything		boolean underlined = false;		Color taxonColor;		if (!taxa.anySelected() || taxa.getSelected(it))			taxonColor = fontColor;		else			taxonColor = fontColorLight;					if (partitions!=null && (colorPartition.getValue() || shadePartition.getValue())){			TaxaGroup mi = (TaxaGroup)partitions.getProperty(it);			if (mi!=null) {				if (colorPartition.getValue() && mi.getColor() != null)					taxonColor = mi.getColor();				if (shadePartition.getValue()){					bgColor =mi.getColor();				}			}		}		ListableVector extras = taxaDisplay.getExtras();		if (extras!=null){			Enumeration e = extras.elements();			while (e.hasMoreElements()) {				Object obj = e.nextElement();				TaxaDisplayExtra ex = (TaxaDisplayExtra)obj;	 			if (ex.getTaxonUnderlined(taxon))	 				underlined = true;	 			Color tc = ex.getTaxonColor(taxon);	 			if (tc!=null) {	 				taxonColor = tc;	 			}	 			String es = ex.getTaxonStringAddition(taxon);	 			if (!StringUtil.blank(es))	 				s+= es;		 	}	 	}	 			gL.setColor(taxonColor); 		lengthString = fm.stringWidth(s); //what to do if underlined?		int centeringOffset = 0;		if (taxaDisplay.centerNames)			centeringOffset = (longestString-lengthString)/2;				horiz += centeringOffset;		setBounds(namePolys[it], horiz, vert-rise/2, lengthString, rise+descent);		if (bgColor!=null) {			gL.setColor(bgColor);			gL.fillRect(horiz, vert-rise/2, lengthString, rise+descent);			gL.setColor(taxonColor);		}		gL.drawString(s, horiz, vert+rise/2);		if (underlined){			Rectangle b =namePolys[it].getB();			gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);			//gL.fillPolygon(namePolys[it]);		}		gL.setColor(Color.black);		if (selected){			gL.setXORMode(Color.white);			gL.fillPolygon(namePolys[it]);			gL.setPaintMode();		}	}	/*.................................................................................................................*/		void setBounds(TaxonPolygon poly, int x, int y, int w, int h){			poly.getBounds();			poly.setB(x,y,w,h);			//int[] xs = poly.xpoints;			//int[] ys = poly.ypoints;			//if (true || xs == null || xs.length !=4 || ys == null || ys.length !=4){				poly.npoints=0;				poly.addPoint(x, y);				poly.addPoint(x+w, y);				poly.addPoint(x+w, y+h);				poly.addPoint(x, y+h);				poly.npoints=4;			/*}			else {				xs[0] = x;				xs[1] = x+w;				xs[2] = x+w;				xs[3] = x;				ys[0] = y;				ys[1] = y;				ys[2] = y+h;				ys[3] = y+h;			}*/	}	/*.................................................................................................................*/	public void drawNames(TaxaDisplay taxaDisplay,  Taxa taxa, Graphics g) {		if (taxaDisplay==null)			return; // alert("taxa display null in draw taxon names");		if (taxa==null)			return; // alert("taxa null in draw taxon names");		if (g==null)			return; // alert("graphics null in draw taxon names");		int totalNumTaxa = taxa.getNumTaxa();		if (namePolys==null) {			namePolys = new TaxonPolygon[totalNumTaxa];			oldNumTaxa=totalNumTaxa;			for (int i = 0; i<totalNumTaxa; i++) {				namePolys[i] = new TaxonPolygon();				namePolys[i].xpoints = new int[4];				namePolys[i].ypoints = new int[4];				namePolys[i].npoints=4;			}		}		else if (oldNumTaxa<totalNumTaxa) {			for (int i = 0; i<oldNumTaxa; i++)				namePolys[i]=null;			namePolys = new TaxonPolygon[totalNumTaxa];			for (int i = 0; i<totalNumTaxa; i++) {				namePolys[i] = new TaxonPolygon();				namePolys[i].xpoints = new int[4];				namePolys[i].ypoints = new int[4];				namePolys[i].npoints=4;			}			oldNumTaxa=totalNumTaxa;		}		this.taxaDisplay =taxaDisplay;		this.taxaDrawing =taxaDisplay.getTaxaDrawing();		this.taxa =taxa;		this.gL =g;		if (taxaDrawing==null)			alert("node displays null in draw taxon names");		try{			if (currentFont ==null) {				currentFont = g.getFont();				if (myFont==null)					myFont = currentFont.getName();				if (myFontSize<=0)					myFontSize = currentFont.getSize();	    	 		Font fontToSet = new Font (myFont, currentFont.getStyle(), myFontSize);				if (fontToSet==null)					currentFont = g.getFont();				else					currentFont = fontToSet;			}			Font tempFont = g.getFont();			g.setFont(currentFont);			fm=g.getFontMetrics(currentFont);			rise= fm.getMaxAscent();			descent = fm.getMaxDescent();			TaxaPartition part = null;			if (colorPartition.getValue() || shadePartition.getValue())				part = (TaxaPartition)taxa.getCurrentSpecsSet(TaxaPartition.class);			if (taxaDisplay.centerNames) {				longestString = 0;				findLongestString(taxa);			}			for (int it = 0; it<taxa.getNumTaxa(); it++) 				 drawNamesOnTaxa(taxa, taxaDisplay, it, part);			g.setFont(tempFont);		}		catch (Exception e){			MesquiteMessage.warnProgrammer("Exception in draw taxon names");		}	}		/*.................................................................................................................*/	private void findLongestString(Taxa taxa) {		for (int it = 0; it<taxa.getNumTaxa(); it++) {			int lengthString = fm.stringWidth(taxa.getTaxonName(it)); 			if (lengthString>longestString)				longestString = lengthString;		}	}	/*.................................................................................................................*/	int foundTaxon;	/*.................................................................................................................*/	private void findNameOnTaxa(Taxa taxa, int x, int y) {		for (int it = 0; it<taxa.getNumTaxa(); it++) {			if (it>= namePolys.length) {				MesquiteMessage.warnProgrammer("error in draw taxon names: Name polys not big enough; taxon number " + it + " / name boxes " + namePolys.length);				return;			}			if (namePolys[it]!=null && namePolys[it].contains(x,y)) {				foundTaxon=it;			}		}				}	/*.................................................................................................................*/	public   int findTaxon(Taxa taxa, int x, int y) {   //finds taxon name box				foundTaxon=-1;		if (taxa!=null && namePolys!=null) {			if (taxa.isDoomed())				return -1;			findNameOnTaxa(taxa, x, y);		}		return foundTaxon; 	}		/*.................................................................................................................*/	public   void fillTaxon(Graphics g, int M) {  //fills taxon name box		if (showTaxonNames.getValue()) {			try {				if ((namePolys!=null) && (namePolys[M]!=null))					g.fillPolygon(namePolys[M]);			}			catch (ArrayIndexOutOfBoundsException e) {			alert("taxon flash out of getBounds");}		}	}	/*.................................................................................................................*/    	 public String getName() {		return "Basic Draw Names for Taxa Display";   	 }	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Draws taxon names on a taxa.  Chooses orientation of names according to orientation of taxa." ;   	 }   	 }class TaxonPolygon extends Polygon {	Rectangle b;		public void setB(int x, int y, int w, int h){		if (b == null)			b = new Rectangle(x, y, w, h);		else {			b.x = x;			b.y = y;			b.width = w;			b.height = h;		}	}		public Rectangle getB(){		return b;	}}