/* Mesquite.cartographer source code.  Copyright 2008-2009 D. Maddison and W. Maddison. Version 1.3, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.cartographer.lib;import java.awt.*;import java.awt.geom.Path2D;import mesquite.lib.*;import mesquite.lib.taxa.*;import mesquite.lib.tree.*;import mesquite.lib.ui.*;/* ======================================================================== */public class SquareSymbol extends FillableMesquiteSymbol  {	public SquareSymbol() {	}	/*.................................................................................................................*/	/**gets the name of the symbol*/	public String getName(){		return "Square";	}	/*.................................................................................................................*/	public  MesquiteSymbol  cloneMethod(){		SquareSymbol newSymbol = new SquareSymbol();		newSymbol.setToCloned(this);		return  newSymbol;	}	/*.................................................................................................................*/	/**gets whether the symbol is drawn via a Polygon*/	public boolean getIsPolygon(){		return true;	}	/*.................................................................................................................*/	/**gets the Polygon*/	public Path2D.Double getPolygon(int maxSize){		int symSize = getSize();		if (maxSize<symSize && maxSize>0)			symSize = maxSize;		Path2D.Double square =new Path2D.Double();		square.reset();		square.moveTo(-symSize, -symSize);		square.lineTo(-symSize, +symSize);		square.lineTo(+symSize, +symSize);		square.lineTo(+symSize, -symSize);		square.lineTo(-symSize, -symSize);		return square;	}}