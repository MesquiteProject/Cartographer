/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison.  Version 1.3, June 2008. Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code.  The commenting leaves much to be desired. Please approach this source code with the spirit of helping out. Perhaps with your help we can be more than a few, and make Mesquite better. Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY. Mesquite's web site is http://mesquiteproject.org This source code and its compiled class files are free and modifiable under the terms of  GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.cartographer.BasicTaxaDrawCoordinator;/* ~~ */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/** Coordinates the drawing of taxa in windows (e.g., used in the Taxa Window) */public class BasicTaxaDrawCoordinator extends DrawTaxaCoordinator {	public DrawTaxa taxaDrawTask;	private DrawNamesTaxaDisplay namesTask;	MesquiteString taxaDrawName, bgColorName;	public Color bgColor = Color.white;	boolean suppression = false;	MesquiteCommand tdC;	double rescaleValue = 1.0;	static String defaultDrawer = null;	// MesquiteBoolean showNodeNumbers;	/* ................................................................................................................. */	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		loadPreferences();		// addMenuItem("-", null);		makeMenu("Display");		if (defaultDrawer != null) {			taxaDrawTask = (DrawTaxa) hireNamedEmployee(DrawTaxa.class, defaultDrawer);			if (taxaDrawTask == null)				taxaDrawTask = (DrawTaxa) hireEmployee(DrawTaxa.class, null);		}		else			taxaDrawTask = (DrawTaxa) hireEmployee(DrawTaxa.class, null);		if (taxaDrawTask == null)			return sorry(getName() + " couldn't start because no taxa drawing module was obtained");		setAutoSaveMacros(true);		taxaDrawName = new MesquiteString(taxaDrawTask.getName());		bgColorName = new MesquiteString("White");		namesTask = (DrawNamesTaxaDisplay) hireEmployee(DrawNamesTaxaDisplay.class, null);		// TODO: if choice of namesTask, use setHriingCommand		tdC = makeCommand("setTaxaDrawer", this);		taxaDrawTask.setHiringCommand(tdC);		MesquiteSubmenuSpec mmis = addSubmenu(null, "Form", tdC);		addMenuItem("Set Current Form as Default", makeCommand("setFormToDefault", this));		mmis.setList(DrawTaxa.class);		mmis.setSelected(taxaDrawName);		mmis = addSubmenu(null, "Background Color", makeCommand("setBackground", this));		mmis.setList(ColorDistribution.standardColorNames);		mmis.setSelected(bgColorName);		return true;	}	public Dimension getPreferredSize() {		return taxaDrawTask.getPreferredSize();	}	/* ................................................................................................................. */	public String preparePreferencesForXML () {		StringBuffer buffer = new StringBuffer();		StringUtil.appendXMLTag(buffer, 2, "defaultDrawer", defaultDrawer);  		return buffer.toString();	}	/* ................................................................................................................. */	public void processSingleXMLPreference (String tag, String content) {		if ("defaultDrawer".equalsIgnoreCase(tag))			defaultDrawer = StringUtil.cleanXMLEscapeCharacters(content);	}		/* ................................................................................................................. */	public void processPreferencesFromFile(String[] prefs) {		if (prefs != null && prefs.length > 0) {			defaultDrawer = prefs[0];		}	}	/* ................................................................................................................. */	public Snapshot getSnapshot(MesquiteFile file) {		Snapshot temp = new Snapshot();		temp.addLine("suppress");		temp.addLine("setTaxaDrawer ", taxaDrawTask);		if (bgColor != null) {			String bName = ColorDistribution.getStandardColorName(bgColor);			if (bName != null)				temp.addLine("setBackground " + StringUtil.tokenize(bName));// quote		}		temp.addLine("desuppress");		return temp;	}	public DrawNamesTaxaDisplay getNamesTask() {		return namesTask;	}	/* ................................................................................................................. */	public TaxaDisplay createOneTaxaDisplay(Taxa taxa, MesquiteWindow window) {		taxaDisplay = new BasicTaxaDisplay(this, taxa);		taxaDisplay.setTaxaDrawing(taxaDrawTask.createTaxaDrawing(taxaDisplay, taxa.getNumTaxa()));		taxaDisplay.setDrawTaxonNames(namesTask);		taxaDisplay.suppressDrawing(suppression);		return taxaDisplay;	}	/* ................................................................................................................. */	public TaxaDisplay[] createTaxaDisplays(int numDisplays, Taxa taxa, MesquiteWindow window) {		taxaDisplays = new BasicTaxaDisplay[numDisplays];		this.numDisplays = numDisplays;		for (int i = 0; i < numDisplays; i++) {			taxaDisplays[i] = new BasicTaxaDisplay(this, taxa);			taxaDisplays[i].setDrawTaxonNames(namesTask);			taxaDisplays[i].setTaxaDrawing(taxaDrawTask.createTaxaDrawing(taxaDisplays[i], taxa.getNumTaxa()));			taxaDisplays[i].suppressDrawing(suppression);		}		return taxaDisplays;	}	/* ................................................................................................................. */	public TaxaDisplay[] createTaxaDisplays(int numDisplays, Taxa[] taxas, MesquiteWindow window) {		taxaDisplays = new BasicTaxaDisplay[numDisplays];		this.numDisplays = numDisplays;		for (int i = 0; i < numDisplays; i++) {			taxaDisplays[i] = new BasicTaxaDisplay(this, taxas[i]);			taxaDisplays[i].setTaxaDrawing(taxaDrawTask.createTaxaDrawing(taxaDisplays[i], taxas[i].getNumTaxa()));			taxaDisplays[i].suppressDrawing(suppression);		}		return taxaDisplays;	}	long progress = 0;	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets the current taxa window form to be the default", null, commandName, "setFormToDefault")) {			defaultDrawer = " #" + MesquiteModule.getShortClassName(taxaDrawTask.getClass());			storePreferences();		}		else if (checker.compare(this.getClass(), "Sets the module to be used to draw the taxa", "[name of taxa draw module]", commandName, "setTaxaDrawer")) {			incrementMenuResetSuppression();			DrawTaxa temp = null;			if (taxaDisplay != null) {				boolean vis = true;				while (taxaDisplay.getDrawingInProcess()) {					;				}				vis = taxaDisplay.isVisible();				taxaDisplay.setVisible(false);				taxaDisplay.suppressDrawing(true);				taxaDisplay.getTaxaDrawing().dispose();				taxaDisplay.setTaxaDrawing(null);				temp = (DrawTaxa) replaceEmployee(DrawTaxa.class, arguments, "Form of Taxa?", taxaDrawTask);				if (temp != null) {					taxaDrawTask = temp;					taxaDrawName.setValue(taxaDrawTask.getName());					taxaDrawTask.setHiringCommand(tdC);				}				taxaDisplay.setTaxaDrawing(taxaDrawTask.createTaxaDrawing(taxaDisplay, taxaDisplay.getTaxa().getNumTaxa()));				taxaDisplay.suppressDrawing(suppression);				if (!suppression)					taxaDisplay.pleaseUpdate(true);				taxaDisplay.setVisible(vis);			}			decrementMenuResetSuppression();			if (temp == null)				return null;			else {				if (!MesquiteThread.isScripting())					parametersChanged();				return taxaDrawTask;			}		}		else if (checker.compare(this.getClass(), "Suppresses taxa drawing", null, commandName, "suppress")) {			suppression = true;			if (taxaDisplay != null) {				taxaDisplay.suppressDrawing(suppression);			}		}		else if (checker.compare(this.getClass(), "Removes suppression of taxa drawing", null, commandName, "desuppress")) {			suppression = false;			if (taxaDisplay != null) {				taxaDisplay.suppressDrawing(suppression);				taxaDisplay.pleaseUpdate(true);			}		}		else if (checker.compare(this.getClass(), "Sets background color of taxa display", "[name of color]", commandName, "setBackground")) {			String token = ParseUtil.getFirstToken(arguments, stringPos);			Color bc = ColorDistribution.getStandardColor(token);			if (bc == null)				return null;			bgColor = bc;			bgColorName.setValue(token);			if (taxaDisplay != null) {				while (taxaDisplay.getDrawingInProcess())					;				taxaDisplay.setBackground(bc);				Container c = taxaDisplay.getParent();				if (c != null)					c.setBackground(bc);				namesTask.invalidateNames(taxaDisplay);				if (!suppression)					taxaDisplay.repaintAll();			}			if (!MesquiteThread.isScripting())				parametersChanged();		}		else if (checker.compare(this.getClass(), "Returns the taxa drawing module in use", null, commandName, "getTaxaDrawer")) {			return taxaDrawTask;		}		else			return  super.doCommand(commandName, arguments, checker);		return null;	}		public double getRescaleValue() {		return rescaleValue;	}	public void setRescaleValue() {		rescaleValue = taxaDrawTask.getRescaleValue();	}	public void endJob() {		taxaDrawTask = null;		namesTask = null;		if (taxaDisplay != null) {			taxaDisplay = null;		}		super.endJob();	}	/* ................................................................................................................. */	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification) {		if (MesquiteThread.isScripting())			return;		if (taxaDisplay != null) {			((BasicTaxaDisplay) taxaDisplay).pleaseUpdate(true);		}	}	/* ................................................................................................................. */	public String getName() {		return "Basic Taxa Draw Coordinator";	}	/* ................................................................................................................. */	public String getNameForMenuItem() {		return "Taxa Drawing";	}	/* ................................................................................................................. */	/** returns an explanation of what the module does. */	public String getExplanation() {		return "Coordinates the drawing of taxa by maintaining the basic TaxaDisplay and by hiring a DrawTaxa module.";	}}/* ======================================================================== */class BasicTaxaDisplay extends TaxaDisplay {	boolean showPixels = false;// for debugging	BasicTaxaDrawCoordinator ownerDrawModule;	public BasicTaxaDisplay(BasicTaxaDrawCoordinator ownerModule, Taxa taxa) {		super(ownerModule, taxa);		ownerDrawModule = ownerModule;		// suppress = true;		setBackground(Color.white);	}	public void setTaxa(Taxa taxa) {		if (ownerModule.isDoomed())			return;		boolean wasNull = (this.taxa == null);		super.setTaxa(taxa);// here ask for nodelocs to be calculated		if (wasNull)			repaint();	}	/**/	public void forceRepaint() {		if (ownerModule.isDoomed())			return;		repaintsPending = 0;		repaint();	}	static int cr = 0;	public void repaint(boolean resetTaxa) { // TODO: this whole system needs revamping.		if (ownerModule.isDoomed())			return;		repaintRequests++;		if (repaintRequests > 1000) {			repaintRequests = 0;			MesquiteMessage.warnProgrammer("more than 1000 repaint requests in Taxa Display");			MesquiteMessage.printStackTrace("more than 1000 repaint requests in Taxa Display");		}		if (taxa != null && resetTaxa) {			recalculatePositions();		}		super.repaint();	}	public void repaint() {		if (ownerModule == null || ownerModule.isDoomed())			return;		repaintRequests++;		if (repaintRequests > 1000) {			repaintRequests = 0;			MesquiteMessage.warnProgrammer("more than 1000 repaint requests in Taxa Display");			MesquiteMessage.printStackTrace("more than 1000 repaint requests in Taxa Display");		}		super.repaint();	}	/* _________________________________________________ */	long repaintRequests = 0;	int retry = 0;	/* _________________________________________________ */	public void paint(Graphics g) {		if (ownerModule.isDoomed())			return;		if (MesquiteWindow.checkDoomed(this))			return;		ownerDrawModule.setRescaleValue();		setDrawingInProcess(true);		int initialPending = repaintsPending;		which = 0;		if (bailOut(initialPending))			return;		if (getParent().getBackground() != getBackground())			getParent().setBackground(getBackground());		if (bailOut(initialPending))			return;		if (getFieldWidth() == 0 || getFieldHeight() == 0)			setFieldSize(getBounds().width, getBounds().height);		if (bailOut(initialPending))			return;		if (getTaxaDrawing() != null && taxa != null)			getTaxaDrawing().recalculatePositions(taxa);		if (bailOut(initialPending))			return;		super.paint(g);		if (bailOut(initialPending))			return;		if (getTaxaDrawing() == null) {			repaint();		}		else if (suppress) {			if (retry > 500)				System.out.println("Error: retried " + retry + " times to draw taxa; remains suppressed");			else {				retry++;				repaint();			}		}		int stage = 0;		try {			if (taxa == null || taxa.isDoomed()) {				setDrawingInProcess(false);				MesquiteWindow.uncheckDoomed(this);				return;			}			if (bailOut(initialPending))				return;			retry = 0;			if (showPixels) {				for (int h = 0; h < getFieldWidth() && h < getFieldHeight(); h += 50) {					g.setColor(Color.red);					g.drawString(Integer.toString(h), h, h);				}			}			if (bailOut(initialPending))				return;			drawAllBackgroundExtras(taxa, g);			stage = 1;			if (bailOut(initialPending))				return;			getTaxaDrawing().drawTaxa(taxa, g);			stage = 2;			if (bailOut(initialPending))				return;			drawAllExtras(taxa, g);			if (bailOut(initialPending))				return;			stage = 3;			if (!suppressNames && ownerModule != null && ((DrawTaxaCoordinator) ownerModule).getNamesTask() != null)				((DrawTaxaCoordinator) ownerModule).getNamesTask().drawNames(this, taxa, g);			stage = 4;		} catch (Throwable e) {			MesquiteMessage.println("Error or Exception in taxa drawing (stage " + stage + ")");			MesquiteFile.throwableToLog(this, e);		}		setDrawingInProcess(false);		if (bailOut(initialPending))			return;		else if (!isVisible())			repaint();		else			repaintsPending = 0;		repaintRequests = 0;		MesquiteWindow.uncheckDoomed(this);		setInvalid(false);	}	public void update(Graphics g) {		super.update(g);	}	private int which = 0;	private boolean bailOut(int initialPending) {		which++;		if (repaintsPending > initialPending) {			setDrawingInProcess(false);			repaintsPending = 0;			repaint();			return true;		}		return false;	}	/* _________________________________________________ */	private void showNodeLocations(Taxa taxa, Graphics g, int N) {		g.setColor(Color.red);		for (int it = 0; it < taxa.getNumTaxa(); it++) {			GraphicsUtil.fillOval(g,getTaxaDrawing().x[it], getTaxaDrawing().y[it], 4, 4,false);		}		g.setColor(Color.black);	}	/* _________________________________________________ */	public void print(Graphics g) {		if (getFieldWidth() == 0 || getFieldHeight() == 0)			setFieldSize(getBounds().width, getBounds().height);		// super.paint(g);		if (taxa == null)			MesquiteMessage.warnProgrammer("taxa NULL in taxa draw coord");		else if (!suppress) {			repaintsPending = 0;			/* NEEDS TO DRAW BACKGROUND EXTRAS */			printAllBackgroundExtras(taxa, g);			getTaxaDrawing().drawTaxa(taxa, g); // OTHER ROOTS			printAllExtras(taxa, g);			if (!suppressNames && ownerModule != null && ((DrawTaxaCoordinator) ownerModule).getNamesTask() != null)				((DrawTaxaCoordinator) ownerModule).getNamesTask().drawNames(this, taxa, g);			printComponents(g);		}		else			MesquiteMessage.warnProgrammer("taxa drawing suppressed");	}	/* _________________________________________________ */	public boolean pointInTaxon(int it, int x, int y) {		if (extras != null) {			Enumeration e = extras.elements();			while (e.hasMoreElements()) {				Object obj = e.nextElement();				TaxaDisplayExtra ex = (TaxaDisplayExtra) obj;				if (!(ex instanceof TaxaDisplayBkgdExtra)) {					if (ownerModule == null || ownerModule.isDoomed())						return false;					if (ex.pointInTaxon(taxa, it, x, y))						return true;				}			}		}		return getTaxaDrawing().locationNearTaxon(it, 4, x, y);	}	/* _________________________________________________ */	public boolean taxonInRectangle(int it, int x1, int y1, int x2, int y2) {		if (extras != null) {			Enumeration e = extras.elements();			while (e.hasMoreElements()) {				Object obj = e.nextElement();				TaxaDisplayExtra ex = (TaxaDisplayExtra) obj;				if (!(ex instanceof TaxaDisplayBkgdExtra)) {					if (ownerModule == null || ownerModule.isDoomed())						return false;					if (ex.taxonInRectangle(taxa, it, x1, y1, x2, y2))						return true;				}			}		}		return false; // DRM need equivalent of location near taxon	}	/* _________________________________________________ */	public void fillTaxon(Graphics g, int M) {		if (extras != null) {			Enumeration e = extras.elements();			while (e.hasMoreElements()) {				Object obj = e.nextElement();				TaxaDisplayExtra ex = (TaxaDisplayExtra) obj;				if (!(ex instanceof TaxaDisplayBkgdExtra)) {					if (ownerModule == null || ownerModule.isDoomed())						return;					ex.fillTaxon(taxa, g, M);				}			}		}		((DrawTaxaCoordinator) ownerModule).getNamesTask().fillTaxon(g, M);	}	/* _________________________________________________ */	private boolean responseOK() {		return (!getDrawingInProcess() && ownerModule != null && (ownerModule.getEmployer() instanceof TaxaDisplayActive));	}	int xDown = MesquiteInteger.unassigned;	int yDown = MesquiteInteger.unassigned;	DragRectangle dragRectangle;	/* _________________________________________________ */	public void mouseMoved(int modifiers, int x, int y, MesquiteTool tool) {		if (MesquiteWindow.checkDoomed(this))			return;		if (responseOK()) {			Graphics g = getGraphics();			boolean dummy = ((TaxaDisplayActive) ownerModule.getEmployer()).mouseMoveInTaxaDisplay(modifiers, x, y, this, g);			if (g != null)				g.dispose();		}		MesquiteWindow.uncheckDoomed(this);		super.mouseMoved(modifiers, x, y, tool);	}	/* _________________________________________________ */	public void mouseDown(int modifiers, int clickCount, long when, int x, int y, MesquiteTool tool) {		if (MesquiteWindow.checkDoomed(this))			return;		boolean somethingTouched = false;		if (responseOK()) {			Graphics g = getGraphics();			somethingTouched = ((TaxaDisplayActive) ownerModule.getEmployer()).mouseDownInTaxaDisplay(modifiers, x, y, this, g);			xDown = x;			yDown = y;			if (tool.isArrowTool())				dragRectangle = new DragRectangle(getGraphics(), x, y);			if (g != null)				g.dispose();		}		if (!somethingTouched)			super.panelTouched(modifiers, x, y, true);		MesquiteWindow.uncheckDoomed(this);	}	/* _________________________________________________ */	public void mouseDrag(int modifiers, int x, int y, MesquiteTool tool) {		if (MesquiteWindow.checkDoomed(this))			return;		if (responseOK()) {			Graphics g = getGraphics();			boolean dummy = ((TaxaDisplayActive) ownerModule.getEmployer()).mouseDragInTaxaDisplay(modifiers, x, y, this, g);			if (dragRectangle != null && tool.isArrowTool())				dragRectangle.drawRectangleDrag(x, y);			if (g != null)				g.dispose();		}		MesquiteWindow.uncheckDoomed(this);		super.mouseDrag(modifiers, x, y, tool);	}	/* ---------------------------------- */	/** selects all taxa within the pixel rectangle indicated */	private void selectPointsWithin(int x1, int y1, int x2, int y2, int modifiers) {		if (!MesquiteInteger.isCombinable(x1) || !MesquiteInteger.isCombinable(y1)) {			return;		}		else if (!MesquiteInteger.isCombinable(x2) || !MesquiteInteger.isCombinable(y2)) {			return;		}		for (int it = 0; it < taxa.getNumTaxa(); it++) {			boolean in = taxonInRectangle(it, MesquiteInteger.minimum(x1, x2), MesquiteInteger.minimum(y1, y2), MesquiteInteger.maximum(x1, x2), MesquiteInteger.maximum(y1, y2));			taxa.setSelected(it, in);		}		taxa.notifyListeners(this, new Notification(MesquiteListener.SELECTION_CHANGED));	}	/* _________________________________________________ */	public void mouseUp(int modifiers, int x, int y, MesquiteTool tool) {		if (MesquiteWindow.checkDoomed(this))			return;		if (responseOK()) {			Graphics g = getGraphics();			boolean dummy = ((TaxaDisplayActive) ownerModule.getEmployer()).mouseUpInTaxaDisplay(modifiers, x, y, this, g);			if (dragRectangle != null && tool.isArrowTool()) {				dragRectangle.drawRectangleUpDown();				dragRectangle.dispose();				dragRectangle = null;				selectPointsWithin(xDown, yDown, x, y, modifiers);				xDown = MesquiteInteger.unassigned;				yDown = MesquiteInteger.unassigned;			}			if (g != null)				g.dispose();		}		MesquiteWindow.uncheckDoomed(this);		super.mouseUp(modifiers, x, y, tool);	}}