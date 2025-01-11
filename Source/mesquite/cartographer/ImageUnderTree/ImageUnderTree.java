/* Mesquite source code.  Copyright 1997-2003 D. Maddison and W. Maddison. Version 1.0, September 2003.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.ImageUnderTree;/*~~  */import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class ImageUnderTree extends TreeDisplayAssistantD {	public MesquiteBoolean imageDim;	ImageTreeDisplayBkgndExtra bkgndImageExtra;	Image bkgndImage;	String imagePath;	public boolean startJob(String arguments, Object condition, boolean hiredByName){		addMenuItem("Choose Background Image...", makeCommand("setImage", this));		addMenuItem("Remove Background Image", makeCommand("offImage", this));		imageDim = new MesquiteBoolean(true);		//addCheckMenuItem(null, "Dim Image", MesquiteModule.makeCommand("dimImage",  this), imageDim);		return true;	} 	public boolean getUserChooseable(){ 		return true;   	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return false;	}	/*.................................................................................................................*/	public Image getImage(){		return bkgndImage;	}  	 	/*.................................................................................................................*/	public   TreeDisplayExtra createTreeDisplayExtra(TreeDisplay treeDisplay) {		MesquiteString imagePathMS = new MesquiteString();		bkgndImage = MesquiteImage.loadImage("Choose background image:", imagePath, treeDisplay,  imagePathMS);		imagePath = imagePathMS.getValue();		bkgndImageExtra = new ImageTreeDisplayBkgndExtra(this, treeDisplay, bkgndImage); //TODO: should remember all of these		bkgndImageExtra.setPlacement(TreeDisplayExtra.BELOW);		return bkgndImageExtra;	}  	 	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();   	 	temp.addLine("setImage " + StringUtil.tokenize(imagePath));		//temp.addLine("dimImage " + imageDim.toOffOnString());   	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Removes image from treewindow background", null, commandName, "offImage")) {			iQuit();			resetContainingMenuBar();    	 	}    	 	else if (checker.compare(this.getClass(), "Chooses new image for the treewindow background.", null, commandName, "setImage")) {			String path = parser.getFirstToken(arguments);			MesquiteString ms = new MesquiteString();    	 		if (bkgndImageExtra!=null) {      	 			if (bkgndImage!=null)    	 				bkgndImage = MesquiteImage.loadImage("Choose background image:", null, bkgndImageExtra.getTreeDisplay(),  ms);    	 			else    	 			    	 bkgndImage = MesquiteImage.loadImage("Choose background image:", path, bkgndImageExtra.getTreeDisplay(),  ms);    	 			bkgndImageExtra.getTreeDisplay().pleaseUpdate(false);    	 		}    	 		else {    	 		    	 bkgndImage = MesquiteImage.loadImage("Choose background image:", path, null,  ms);    	 		    	 			}			if (ms!=null)				imagePath = ms.getValue();			    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether image should be dimmed or not.", "[on or off]", commandName, "dimImage")) {    	 		imageDim.toggleValue(parser.getFirstToken(arguments));    	 		if (bkgndImageExtra!=null)    	 			bkgndImageExtra.getTreeDisplay().pleaseUpdate(false);    	 	}    	 	else    	 		return  super.doCommand(commandName, arguments, checker);		return null;   	 }	/*.................................................................................................................*/    	 public String getNameForMenuItem() {		return "Add Background Image...";   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Add Background Image";   	 }   	 	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Sets an image to be the background image under a tree." ;   	 }   	 public void endJob(){	 	if (bkgndImageExtra !=null)	 		bkgndImageExtra.turnOff(); //should do all   	 	super.endJob();   	 }}/* ======================================================================== */class ImageTreeDisplayBkgndExtra extends TreeDisplayBkgdExtra {	public ImageUnderTree ownerModule;		public ImageTreeDisplayBkgndExtra (ImageUnderTree ownerModule, TreeDisplay treeDisplay,	Image bkgndImage) {		super(ownerModule, treeDisplay);		this.ownerModule = ownerModule;	}	/*_________________________________________________*/	public   void drawImage(TreeDisplay treeDisplay, Tree tree, int drawnRoot, Graphics g) {	        if (MesquiteTree.OK(tree)) {	        	g.drawImage(ownerModule.getImage(),0,0,Color.white, null);	       	 }	   }	/*.................................................................................................................*/	public   void drawOnTree(Tree tree, int drawnRoot, Graphics g) {		drawImage(treeDisplay, tree, drawnRoot, g);	}	public   void printOnTree(Tree tree, int drawnRoot, Graphics g) {		drawOnTree(tree, drawnRoot, g);	}	public   void setTree(Tree tree) {	}}	