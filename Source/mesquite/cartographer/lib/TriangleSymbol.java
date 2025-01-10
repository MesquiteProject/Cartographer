/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cartographer.lib;import java.awt.*;import java.awt.geom.AffineTransform;import java.awt.geom.Path2D;import mesquite.lib.*;import mesquite.lib.ui.ExtensibleDialog;import mesquite.lib.ui.FillableMesquiteSymbol;import mesquite.lib.ui.MesquiteSymbol;/* ======================================================================== */public class TriangleSymbol extends FillableMesquiteSymbol  {	int direction;	public static final int UP = 0;	public static final int DOWN = 1;	Checkbox upBox;	public TriangleSymbol() {	}	/*.................................................................................................................*/	/**gets the name of the symbol*/	public String getName(){		return "Triangle";	}	/*.................................................................................................................*/	/**gets whether the symbol is drawn via a Polygon*/	public boolean getIsPolygon(){		return true;	}	/*.................................................................................................................*/	public int getDirection(){		return direction;	}	/*.................................................................................................................*/	public void setDirection(int direction){		this.direction = direction;	}	/*.................................................................................................................*/	public void  setToCloned(MesquiteSymbol cloned){		super.setToCloned(cloned);		setDirection(((TriangleSymbol)cloned).getDirection());	}	/*.................................................................................................................*/	public  MesquiteSymbol  cloneMethod(){		TriangleSymbol newSymbol = new TriangleSymbol();		newSymbol.setToCloned(this);		return  newSymbol;	}	/*.................................................................................................................*/	/**gets the NEXUS commands to specify the options specific to this tool*/	public String getExtraNexusOptions(){		if (direction==UP)			return " DIRECTION=UP ";		else			return " DIRECTION=DOWN ";	}	/*.................................................................................................................*/	public void addDialogElements(ExtensibleDialog dialog, boolean includeSize){		super.addDialogElements(dialog, includeSize);		upBox = dialog.addCheckBox("point up",direction==UP);	}	/*.................................................................................................................*/	public void getDialogOptions(){		super.getDialogOptions();		if (upBox.getState())			direction=UP;		else			direction=DOWN;	}	/*.................................................................................................................*/	public void processSubcommand(String token, Parser subcommands){		super.processSubcommand(token, subcommands);		if (token.equalsIgnoreCase("DIRECTION")){			token = subcommands.getNextToken(); //=			token = subcommands.getNextToken(); //direction			if (token.equalsIgnoreCase("UP"))				setDirection(UP);			else if (token.equalsIgnoreCase("DOWN"))				setDirection(DOWN);		}	}	/*.................................................................................................................*/	/**gets the Polygon*/	public Path2D.Double getPolygon(int maxSize){		int symSize = getSize();		if (maxSize<symSize && maxSize>0)			symSize = maxSize;		int doubleSize = symSize + symSize;		Path2D.Double triangle=new Path2D.Double();		triangle.reset();		if (getDirection()==UP) {			triangle.moveTo(0, doubleSize);  //lower left			triangle.lineTo(symSize, 0);  //point			triangle.lineTo(doubleSize, doubleSize); //lower right			triangle.lineTo(0, doubleSize);  //lower left		}		else if (getDirection()==DOWN) {			triangle.moveTo(0, 0); //upper right			triangle.lineTo(doubleSize, 0);  //upper left			triangle.lineTo(symSize, doubleSize);  //point			triangle.lineTo(0, 0); //upper right		}		AffineTransform polyTransform = new AffineTransform();		polyTransform.translate(-symSize, -symSize);		triangle.transform(polyTransform);	    return triangle;	}}