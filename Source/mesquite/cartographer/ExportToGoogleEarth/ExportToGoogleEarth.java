/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. 
Version 1.3, June 2008.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */
package mesquite.cartographer.ExportToGoogleEarth;

import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import mesquite.cartographer.lib.GeographicUtil;
import mesquite.cartographer.lib.GreatCircleReconstructor;
import mesquite.cont.lib.GeographicData;
import mesquite.cont.lib.GeographicState;
import mesquite.lib.Arguments;
import mesquite.lib.ExporterDialog;
import mesquite.lib.IntegerField;
import mesquite.lib.MesquiteBoolean;
import mesquite.lib.MesquiteDouble;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteMessage;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteStringBuffer;
import mesquite.lib.Parser;
import mesquite.lib.StringUtil;
import mesquite.lib.duties.FileInterpreterI;
import mesquite.lib.duties.OneTreeSource;
import mesquite.lib.taxa.Taxa;
import mesquite.lib.tree.MesquiteTree;
import mesquite.lib.tree.Tree;
import mesquite.lib.ui.ProgressIndicator;
import mesquite.lib.ui.SingleLineTextField;



public class ExportToGoogleEarth extends FileInterpreterI implements ItemListener {
	boolean includeTree = false;
	boolean squareTree = true;
	boolean showSelected = false;
	boolean terminalsOnGround = true;
	boolean phylogram = false;
	boolean extrudeIcons = false;
	boolean avoidThroughEarth = true;
	boolean greatCircleBigSteps = false;
	int maxHeight=2000000;
	int unselectedBranchWidth = 3;
	int selectedBranchWidth = 4;

	String unselectedColor = "ff88ffff";
	String selectedColor ="ff00bbff";




	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		loadPreferences();
		return true;  //make this depend on taxa reader being found?)
	}

	public boolean isPrerelease(){
		return false;
	}
	public boolean isSubstantive(){
		return true;
	}

	/*.................................................................................................................*/
	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer
	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.
	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/
	public int getVersionOfFirstRelease(){
		return -120;  
	}

	/*.................................................................................................................*/
	public String preferredDataFileExtension() {
		return "kml";
	}
	/*.................................................................................................................*/
	public boolean canExportEver() {  
		return true;  //
	}
	/*.................................................................................................................*/
	public boolean canExportProject(MesquiteProject project) {  
		return (project.getNumberCharMatrices( GeographicState.class) > 0) ;
	}

	/*.................................................................................................................*/
	public boolean canExportData(Class dataClass) {  
		return (dataClass==GeographicState.class);
	}
	/*.................................................................................................................*/
	public boolean canImport() {  
		return false;
	}

	/*.................................................................................................................*/
	public void processSingleXMLPreference (String tag, String content) {
		if ("includeTree".equalsIgnoreCase(tag))
			includeTree = MesquiteBoolean.fromTrueFalseString(content);
		else if ("squareTree".equalsIgnoreCase(tag))
			squareTree = MesquiteBoolean.fromTrueFalseString(content);
		else if ("showSelected".equalsIgnoreCase(tag))
			showSelected = MesquiteBoolean.fromTrueFalseString(content);
		else if ("terminalsOnGround".equalsIgnoreCase(tag))
			terminalsOnGround = MesquiteBoolean.fromTrueFalseString(content);
		else if ("phylogram".equalsIgnoreCase(tag))
			phylogram = MesquiteBoolean.fromTrueFalseString(content);
		else if ("extrudeIcons".equalsIgnoreCase(tag))
			extrudeIcons = MesquiteBoolean.fromTrueFalseString(content);
		else if ("avoidThroughEarth".equalsIgnoreCase(tag))
			avoidThroughEarth = MesquiteBoolean.fromTrueFalseString(content);
		else if ("greatCircleBigSteps".equalsIgnoreCase(tag))
			greatCircleBigSteps = MesquiteBoolean.fromTrueFalseString(content);
		else if ("maxHeight".equalsIgnoreCase(tag))
			maxHeight = MesquiteInteger.fromString(content);
		else if ("unselectedBranchWidth".equalsIgnoreCase(tag))
			unselectedBranchWidth = MesquiteInteger.fromString(content);
		else if ("selectedBranchWidth".equalsIgnoreCase(tag))
			selectedBranchWidth = MesquiteInteger.fromString(content);

		else if ("unselectedColor".equalsIgnoreCase(tag))
			unselectedColor = StringUtil.cleanXMLEscapeCharacters(content);
		else if ("selectedColor".equalsIgnoreCase(tag))
			selectedColor = StringUtil.cleanXMLEscapeCharacters(content);	

	}
	/*.................................................................................................................*/
	public String preparePreferencesForXML () {
		StringBuffer buffer = new StringBuffer(200);
		StringUtil.appendXMLTag(buffer, 2, "includeTree", includeTree);  
		StringUtil.appendXMLTag(buffer, 2, "squareTree", squareTree);  
		//		StringUtil.appendXMLTag(buffer, 2, "useDefaultExecutablePath",  useDefaultExecutablePath);       //TODO4.01
		StringUtil.appendXMLTag(buffer, 2, "showSelected", showSelected);  
		StringUtil.appendXMLTag(buffer, 2, "terminalsOnGround", terminalsOnGround);  
		StringUtil.appendXMLTag(buffer, 2, "phylogram", phylogram);  
		StringUtil.appendXMLTag(buffer, 2, "extrudeIcons", extrudeIcons);  
		StringUtil.appendXMLTag(buffer, 2, "avoidThroughEarth", avoidThroughEarth);  
		StringUtil.appendXMLTag(buffer, 2, "greatCircleBigSteps", greatCircleBigSteps);  
		StringUtil.appendXMLTag(buffer, 2, "maxHeight", maxHeight);  
		StringUtil.appendXMLTag(buffer, 2, "unselectedBranchWidth", unselectedBranchWidth);  
		StringUtil.appendXMLTag(buffer, 2, "selectedBranchWidth", selectedBranchWidth);  
		StringUtil.appendXMLTag(buffer, 2, "unselectedColor", unselectedColor);  
		StringUtil.appendXMLTag(buffer, 2, "selectedColor", selectedColor);  
		return buffer.toString();
	}

	/* ============================  exporting ============================*/
	Checkbox includeTreeCheckbox;
	Checkbox squareTreeCheckbox;
	Checkbox phylogramCheckbox;
	Checkbox terminalsOnGroundCheckbox;
	Checkbox extrudeCheckbox;
	Checkbox showSelectedCheckbox;
	SingleLineTextField unselectedColorField;
	IntegerField unselectedWidthField;
	SingleLineTextField selectedColorField;
	IntegerField selectedWidthField;
	IntegerField rootHeightField;

	/*.................................................................................................................*/

	public boolean getExportOptions(boolean dataSelected, boolean taxaSelected){
		MesquiteInteger buttonPressed = new MesquiteInteger(1);
		ExporterDialog exportDialog = new ExporterDialog(this,containerOfModule(), "Export To Google Earth Options", buttonPressed);
		exportDialog.addLabel("Export taxon localities to Google Earth");
		String helpString = "If 'include tree' is checked, Mesquite will draw a tree above the Earth in the Google Earth file, in a manner inspired by Bill Piel's work, using the options you choose. \n";
		helpString += "The selected and unselected branch colors are each made up of exactly 8 hexadecimal digits, with the first 2 digits indicating transparency (with ff being fully opaque and 00 being fully transparent), ";
		helpString += "and the remaining 6 digits indicating the color, with ffffff being white, and 000000 being black. For more details touch on the Help Link button.";
		exportDialog.appendToHelpString(helpString);
		exportDialog.setHelpURL(getPackageCanonicalDocsPath()+"googleEarth.html");


		includeTreeCheckbox = exportDialog.addCheckBox("include a tree (a tree must be shown in a tree window)", includeTree);
		includeTreeCheckbox.addItemListener(this);
		squareTreeCheckbox = exportDialog.addCheckBox("square tree", squareTree);
		phylogramCheckbox = exportDialog.addCheckBox("display with branches proportional to lengths if branch lengths available", phylogram);
		terminalsOnGroundCheckbox = exportDialog.addCheckBox("place terminal taxa on ground", terminalsOnGround);
		extrudeCheckbox = exportDialog.addCheckBox("show connection from ground to any terminals above ground", extrudeIcons);
		showSelectedCheckbox = exportDialog.addCheckBox("highlight selected branches", showSelected);

		unselectedColorField = exportDialog.addTextField("Unselected branch color", unselectedColor, 12);
		unselectedWidthField = exportDialog.addIntegerField("Unselected branch width", unselectedBranchWidth, 6);
		selectedColorField = exportDialog.addTextField("Selected branch color", selectedColor, 12);
		selectedWidthField = exportDialog.addIntegerField("Selected branch width", selectedBranchWidth, 6);
		rootHeightField = exportDialog.addIntegerField("Height of root (in meters)", maxHeight, 12);

		checkEnabling();

		exportDialog.completeAndShowDialog(dataSelected, taxaSelected);

		boolean ok = (exportDialog.query(dataSelected, taxaSelected)==0);

		if (ok) {
			includeTree = includeTreeCheckbox.getState();
			squareTree = squareTreeCheckbox.getState();
			showSelected  = showSelectedCheckbox.getState();
			terminalsOnGround = terminalsOnGroundCheckbox.getState();
			unselectedColor = unselectedColorField.getText();
			selectedColor = selectedColorField.getText();
			maxHeight = rootHeightField.getValue();
			phylogram = phylogramCheckbox.getState();
			unselectedBranchWidth = unselectedWidthField.getValue();
			selectedBranchWidth = selectedWidthField.getValue();
			extrudeIcons = extrudeCheckbox.getState();
			storePreferences();
		}

		exportDialog.dispose();
		return ok;
	}	

	public void checkEnabling() {
		if (includeTreeCheckbox!=null){
			boolean treeIncluded = includeTreeCheckbox.getState(); 
			if (squareTreeCheckbox!=null)
				squareTreeCheckbox.setEnabled(treeIncluded);
			if (phylogramCheckbox!=null)
				phylogramCheckbox.setEnabled(treeIncluded);
			if (terminalsOnGroundCheckbox!=null)
				terminalsOnGroundCheckbox.setEnabled(treeIncluded);
			if (extrudeCheckbox!=null)
				extrudeCheckbox.setEnabled(treeIncluded);
			if (showSelectedCheckbox!=null)
				showSelectedCheckbox.setEnabled(treeIncluded);
			if (unselectedColorField!=null)
				unselectedColorField.setEnabled(treeIncluded);	
			if (unselectedWidthField!=null)
				unselectedWidthField.setEnabled(treeIncluded);
			if (selectedColorField!=null)
				selectedColorField.setEnabled(treeIncluded);
			if (selectedWidthField!=null)
				selectedWidthField.setEnabled(treeIncluded);
			if (rootHeightField!=null)
				rootHeightField.setEnabled(treeIncluded);
		}
	}
	public void itemStateChanged(ItemEvent e) {
		if (e.getItemSelectable()==includeTreeCheckbox && includeTreeCheckbox!=null){
			checkEnabling();
		}
	}

	/** Returns a String containing the URL for this module */
	public String getURLString(){
		return "googleEarth.html";
	}

	/** Returns whether or not the URL for this module is a relative reference from the PackageIntro directory */
	public boolean URLinPackageIntro(){
		return true;
	}


	public void readFile(MesquiteProject mf, MesquiteFile mNF, String arguments) {

	}

	/*.................................................................................................................*/
	/** writes to the buffer a series of straight lines representing the great circle between the two points */
	private void writeGreatCirclePath(StringBuffer outputBuffer, double ancLongitude, double longitude, double ancLatitude, double latitude, double startHeight, double endHeight) {
		long numPaths = Math.max(Math.round(Math.abs(ancLongitude-longitude)), Math.round(Math.abs(ancLatitude-latitude)));
		if (greatCircleBigSteps)
			numPaths = numPaths/5;
		double intervalLong = Math.abs(ancLongitude-longitude) / numPaths;
		double intervalLat = Math.abs(ancLatitude-latitude) / numPaths;
		if (ancLongitude<longitude)
			intervalLong=-intervalLong;
		if (ancLatitude<latitude)
			intervalLat=-intervalLat;
		double fullD = GeographicUtil.getGreatCircleDistance(true, longitude, ancLongitude, latitude, ancLatitude);
		double d = fullD/numPaths;
		double h = (endHeight-startHeight)/numPaths;
		double height = startHeight;

		for (long i=1; i<=numPaths; i++) {
			if (d*i<fullD) {
				double [] point = GeographicUtil.getGreatCirclePoint(d*i, longitude, ancLongitude, latitude, ancLatitude);
				height = startHeight+h*i;
				outputBuffer.append("\t\t\t\t"+point[0]+ "," + point[1] + ","+height+"\n"); 
			}
		}


	}

	ProgressIndicator progIndicator;
	int nodeCount = 0;
	/*.................................................................................................................*/
	private void writeNodeCoordinates(StringBuffer outputBuffer, Tree tree, int node, GeographicData data, double[][] reconstructed, double rootHeight, double depthUnit, boolean useBranchLengths) {
		nodeCount++;
		if (tree.nodeIsInternal(node)) { 
			for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter))
				writeNodeCoordinates(outputBuffer, tree, daughter, data, reconstructed, rootHeight, depthUnit, useBranchLengths);
		} 
		if (tree.getRoot()!=node && tree.nodeExists(node)) {
			if (progIndicator != null) {
				if (progIndicator.isAborted()) {
					progIndicator.goAway();
					return;
				}
				progIndicator.setText("Node " + nodeCount);
				progIndicator.setCurrentValue(nodeCount);
			}
			double longitude =  reconstructed[0][node];
			double latitude =  reconstructed[1][node];

			if (MesquiteDouble.isCombinable(longitude) && MesquiteDouble.isCombinable(latitude)) {


				double height;
				if (useBranchLengths) {
					if (terminalsOnGround)
						height = tree.tallestPathAboveNode(node,1.0)*depthUnit;
					else 
						height = (rootHeight-tree.distanceToRoot(node,true,1.0))*depthUnit;
				}
				else {
					height = tree.deepestPath(node)*1.0*depthUnit;
				}
				double ancLongitude =  reconstructed[0][tree.parentOfNode(node, 1)];
				double ancLatitude =  reconstructed[1][tree.parentOfNode(node, 1)];



				double ancHeight ;
				if (useBranchLengths) {
					if (terminalsOnGround)
						ancHeight =tree.tallestPathAboveNode(tree.parentOfNode(node, 1),1.0)*depthUnit;
					else
						ancHeight = (rootHeight-tree.distanceToRoot(tree.parentOfNode(node, 1),true,1.0))*depthUnit;
				}
				else {
					ancHeight = tree.deepestPath(tree.parentOfNode(node, 1))*1.0*depthUnit;
				}

				StringUtil.appendStartXMLTag(outputBuffer,1,"Placemark", true);
				if (showSelected && tree.getSelected(node))
					StringUtil.appendXMLTag(outputBuffer,2, "styleUrl", "#selectedLine");
				else
					StringUtil.appendXMLTag(outputBuffer,2, "styleUrl", "#unselectedLine");
				StringUtil.appendStartXMLTag(outputBuffer,2,"LineString", true);
				StringUtil.appendXMLTag(outputBuffer,3,"altitudeMode", "absolute");
				StringUtil.appendStartXMLTag(outputBuffer,3,"coordinates", true);
				outputBuffer.append("\t\t\t\t"+longitude + "," + latitude + ","+height+"\n"); //location of node
				if (squareTree) {
					outputBuffer.append("\t\t\t\t"+longitude + "," + latitude + ","+ancHeight+"\n"); // "elbow" of square tree branch
					if (avoidThroughEarth && (Math.abs(ancLongitude-longitude)>1.0 && Math.abs(ancLatitude-latitude)>1.0))
						writeGreatCirclePath(outputBuffer,  ancLongitude,  longitude,  ancLatitude,  latitude,  ancHeight, ancHeight);				
					outputBuffer.append("\t\t\t\t"+ancLongitude + "," + ancLatitude + ","+ancHeight+"\n");  // location of ancestral node
				}
				else {
					if (avoidThroughEarth && (Math.abs(ancLongitude-longitude)>1.0 && Math.abs(ancLatitude-latitude)>1.0))
						writeGreatCirclePath(outputBuffer,  ancLongitude,  longitude,  ancLatitude,  latitude,  height, ancHeight);				
					outputBuffer.append("\t\t\t\t"+ancLongitude + "," + ancLatitude + ","+ancHeight+"\n");  // location of ancestral node
				}

				StringUtil.appendEndXMLTag(outputBuffer,3,"coordinates");
				StringUtil.appendEndXMLTag(outputBuffer,2,"LineString");
				StringUtil.appendEndXMLTag(outputBuffer,1,"Placemark");
			}

		}
	}

	/*.................................................................................................................*/
	private void writeTreeCoordinates(StringBuffer outputBuffer, Tree tree, GeographicData data) {
		GreatCircleReconstructor reconstructor = new GreatCircleReconstructor();
		// ................  get raw long lats and put into arrays ........
		double[][] originalCoordinates = new double[2][tree.getNumNodeSpaces()];
		for (int it = 0; it<tree.getNumTaxa(); it++) {
			if (data.hasDataForTaxon(it)) {
				originalCoordinates[0][it] = data.getState(GeographicData.getLongitudeCharacter(),it,0);
				originalCoordinates[1][it] = data.getState(GeographicData.getLatitudeCharacter(),it,0);
			} else {
				originalCoordinates[0][it] = MesquiteDouble.unassigned;
				originalCoordinates[1][it] = MesquiteDouble.unassigned;
			}
		}				
		reconstructor.reconstruct(tree, originalCoordinates, false, true, null);  		
		double[][]  reconstructed  = reconstructor.getReconstructedStates(0);
		
		if (reconstructed==null) {
			MesquiteMessage.discreetNotifyUser("Locations of nodes in tree could not be reconstructed; this is possibly a result of some taxa having no latitude or longitude values. "
					+ "Tree will not be included.");
			return;
		}


		double depth;
		boolean useBranchLengths = tree.hasBranchLengths() && phylogram;
		if (useBranchLengths) {
			depth= tree.tallestPathAboveNode(tree.getRoot(), 1.0);
		} 
		else {
			depth= tree.deepestPath(tree.getRoot());
		}
		double unit = 1.0*maxHeight/depth;


		outputBuffer.append("\t<Style id=\"unselectedLine\">\n" );
		StringUtil.appendStartXMLTag(outputBuffer,2,"LineStyle", true);
		StringUtil.appendXMLTag(outputBuffer,3,"color", unselectedColor);
		StringUtil.appendXMLTag(outputBuffer,3,"width", ""+unselectedBranchWidth);
		StringUtil.appendEndXMLTag(outputBuffer,2,"LineStyle");
		StringUtil.appendEndXMLTag(outputBuffer,1,"Style");

		outputBuffer.append("\t<Style id=\"selectedLine\">\n" );
		StringUtil.appendStartXMLTag(outputBuffer,2,"LineStyle", true);
		StringUtil.appendXMLTag(outputBuffer,3,"color", selectedColor);
		StringUtil.appendXMLTag(outputBuffer,3,"width", ""+selectedBranchWidth);
		StringUtil.appendEndXMLTag(outputBuffer,2,"LineStyle");
		StringUtil.appendEndXMLTag(outputBuffer,1,"Style");

		outputBuffer.append("\t<Style id=\"thinGreyLine\">\n" );
		StringUtil.appendStartXMLTag(outputBuffer,2,"LineStyle", true);
		StringUtil.appendXMLTag(outputBuffer,3,"color", "ff0000");
		//		StringUtil.appendXMLTag(outputBuffer,3,"color", "b1b1b1");
		StringUtil.appendXMLTag(outputBuffer,3,"width", "8");
		StringUtil.appendEndXMLTag(outputBuffer,2,"LineStyle");
		StringUtil.appendEndXMLTag(outputBuffer,1,"Style");

		progIndicator = new ProgressIndicator(getProject(),getName(), "Exporting to Google Earth", tree.getNumNodeSpaces(), true);
		if (progIndicator!=null){
			progIndicator.setButtonMode(ProgressIndicator.OFFER_CONTINUE);
			progIndicator.setOfferContinueMessageString("Are you sure you want to stop the export?");
			progIndicator.start();
		}
		nodeCount=0;
		
		writeNodeCoordinates(outputBuffer,tree, tree.getRoot(), data, reconstructed, depth, unit, useBranchLengths);

		if (progIndicator!=null)
			progIndicator.goAway();

	}
	
	void removeEmptyTerminals(MesquiteTree tree, Taxa taxa, GeographicData data) {
		for (int it = 0; it<taxa.getNumTaxa(); it++){
			if (!data.hasDataForTaxon(it)){
				int node = tree.nodeOfTaxonNumber(it);
				if (node>0) {
					tree.deleteClade(node, false);
				}
			}
		}

	}
	/*.................................................................................................................*/
	public boolean exportFile(MesquiteFile file, String arguments) { //if file is null, consider whole project open to export
		Arguments args = new Arguments(new Parser(arguments), true);
		boolean usePrevious = args.parameterExists("usePrevious");
		GeographicData data = (GeographicData)getProject().chooseData(containerOfModule(), file, null, GeographicState.class, "Select data to export");
		if (data ==null) {
			showLogWindow(true);
			logln("WARNING: No geographic data available for export to a file to Google Earth.  The file will not be written.\n");
			return false;
		}

		if (!usePrevious){
			if (!getExportOptions(data.anySelected(), data.getTaxa().anySelected()))
				return false;
		}

		Taxa taxa = data.getTaxa();
		MesquiteTree tree = null;
		if (includeTree) {
			OneTreeSource treeTask = (OneTreeSource)hireEmployee(OneTreeSource.class, "Source of tree to be exported to Google Earth file");
			if (treeTask != null) {
				treeTask.initialize(taxa);
				MesquiteTree origtree = (MesquiteTree)treeTask.getTree(taxa);
				tree = origtree.cloneTree();
				removeEmptyTerminals(tree,taxa, data);
			}
			if (tree==null) {
				includeTree = false;
				MesquiteMessage.discreetNotifyUser("No tree is available to be included in the Google Earth file.  Make sure you have a tree window containing of tree for these taxa before you attempt to export the tree to Google Earth.");
			}
			fireEmployee(treeTask);
		}

		int numTaxa = taxa.getNumTaxa();
		int numChars = data.getNumChars();
		StringBuffer outputBuffer;
		if (includeTree)
			outputBuffer = new StringBuffer(numTaxa*(100+20 + numChars));
		else
			outputBuffer = new StringBuffer(numTaxa*(20 + numChars));
		StringUtil.appendStartOfXMLFile(outputBuffer);
		outputBuffer.append("<kml xmlns=\"http://earth.google.com/kml/2.0\">\n");
		StringUtil.appendStartXMLTag(outputBuffer,0,"Document", true);

		StringUtil.appendXMLTag(outputBuffer,1,"description", "Exported from Cartographer");
		StringUtil.appendXMLTag(outputBuffer,1,"name", "Localities");
		//		StringUtil.appendXMLTag(outputBuffer,1,"visibility", 0);
		StringUtil.appendXMLTag(outputBuffer,1,"open", 1);
		//		exportStyles(outputBuffer);

		for (int it = 0; it<numTaxa; it++){
			if ((!writeOnlySelectedTaxa || (taxa.getSelected(it))) && data.hasDataForTaxon(it)){
				StringUtil.appendStartXMLTag(outputBuffer,1,"Placemark", true);
				StringUtil.appendXMLTag(outputBuffer,2,"name", taxa.getTaxonName(it));
				//				StringUtil.appendXMLTag(outputBuffer,2,"styleUrl", "#cyan");

				StringUtil.appendStartXMLTag(outputBuffer,2,"LookAt", true);
				StringUtil.appendXMLTag(outputBuffer,3,"longitude", data.getState(GeographicData.getLongitudeCharacter(),it,0));
				StringUtil.appendXMLTag(outputBuffer,3,"latitude", data.getState(GeographicData.getLatitudeCharacter(),it,0));
				StringUtil.appendXMLTag(outputBuffer,3,"range", 5000);
				StringUtil.appendEndXMLTag(outputBuffer,2,"LookAt");

				//				StringUtil.appendXMLTag(outputBuffer,2,"visibility", 0);

				StringUtil.appendStartXMLTag(outputBuffer,2,"Point", true);
				double height = 0.0;
				if (includeTree && !terminalsOnGround && tree.hasBranchLengths() && phylogram) {
					double depth = tree.tallestPathAboveNode(tree.getRoot(), 1.0);
					double unit = 1.0*maxHeight/depth;
					height = (depth-tree.distanceToRoot(tree.nodeOfTaxonNumber(it),true,1.0))*unit;
					StringUtil.appendXMLTag(outputBuffer,3,"altitudeMode", "relativeToGround");
					if (extrudeIcons)
						StringUtil.appendXMLTag(outputBuffer,3,"extrude", "1");
				}
				else
					StringUtil.appendXMLTag(outputBuffer,3,"altitudeMode", "clampToGround");
				StringUtil.appendXMLTag(outputBuffer,3,"coordinates", ""+data.getState(GeographicData.getLongitudeCharacter(),it,0) + "," + data.getState(GeographicData.getLatitudeCharacter(),it,0)+ ","+height);
				StringUtil.appendEndXMLTag(outputBuffer,2,"Point");


				StringUtil.appendEndXMLTag(outputBuffer,1,"Placemark");
				outputBuffer.append(getLineEnding());
			}
		}

		if (includeTree) {
			writeTreeCoordinates(outputBuffer,tree, data);
			outputBuffer.append(getLineEnding());

		}

		StringUtil.appendEndXMLTag(outputBuffer,0,"Document");
		StringUtil.appendEndXMLTag(outputBuffer,0,"kml");

		MesquiteStringBuffer msb = new MesquiteStringBuffer();
		msb.append(outputBuffer.toString());
		saveExportedFileWithExtension(msb, arguments, "kml");
		return true;
	}

	public String getName() {
		return "Export To Google Earth";
	}
	public String getHTMLExplanation() {
		String  explanationString = "Exports a matrix of longitude-latitude data to a KML file that can then be opened by Google Earth. A tree can also be exported, as in the figure, below. <br> <img src=\"" + MesquiteFile.massageFilePathToURL(getPath() + "extrude.jpg");
		explanationString += "\"><br>";
		return explanationString;
	}
	public String getExplanation() {
		return "Exports a matrix of longitude-latitude data to a KML file that can then be opened by Google Earth. A tree can also be exported.";
	}

	/*.................................................................................................................*
	public void getSubfunctions(){
		String  explanationString = "Exports a matrix of longitude-latitude data to a KML file that can then be opened by Google Earth. A tree can also be exported, as in the figure, below. <br> <img src=\"" + MesquiteFile.massageFilePathToURL(getPath() + "extrude.jpg");
		explanationString += "\"><br>";
		registerSubfunction(new FunctionExplanation("Export to Google Earth", explanationString, null, null, getURLString(), URLinPackageIntro()));
		super.getSubfunctions();
	}



	/** code for David's stuff */
	/*.................................................................................................................*/
	private void exportStyles(StringBuffer outputBuffer) {
		outputBuffer.append("\t<Style id=\"cyanStar\">\n" );
		outputBuffer.append("\t\t<IconStyle>\n" );
		outputBuffer.append("\t\t\t<Icon>\n" );
		StringUtil.appendXMLTag(outputBuffer,4,"href", "http://bembidion.org/icons/cyanStar.png");
		StringUtil.appendEndXMLTag(outputBuffer,3,"Icon");
		StringUtil.appendEndXMLTag(outputBuffer,2,"IconStyle");
		StringUtil.appendEndXMLTag(outputBuffer,1,"Style");

		outputBuffer.append("\t<Style id=\"cyanStarHighlight\">\n" );
		outputBuffer.append("\t\t<IconStyle>\n" );
		outputBuffer.append("\t\t\t<Icon>\n" );
		StringUtil.appendXMLTag(outputBuffer,4,"href", "http://bembidion.org/icons/cyanStarHighlight.png");
		StringUtil.appendEndXMLTag(outputBuffer,3,"Icon");
		StringUtil.appendEndXMLTag(outputBuffer,2,"IconStyle");
		StringUtil.appendEndXMLTag(outputBuffer,1,"Style");

		outputBuffer.append("\t<StyleMap id=\"cyan\">\n" );
		outputBuffer.append("\t\t<Pair>\n" );
		StringUtil.appendXMLTag(outputBuffer,3,"key", "normal");
		outputBuffer.append("\t\t\t<styleUrl>#cyanStar</styleUrl>\n" );
		StringUtil.appendEndXMLTag(outputBuffer,2,"Pair");
		outputBuffer.append("\t\t<Pair>\n" );
		StringUtil.appendXMLTag(outputBuffer,3,"key", "highlight");
		outputBuffer.append("\t\t\t<styleUrl>#cyanStarHighlight</styleUrl>\n" );
		StringUtil.appendEndXMLTag(outputBuffer,2,"Pair");
		StringUtil.appendEndXMLTag(outputBuffer,1,"StyleMap");
	}

}
