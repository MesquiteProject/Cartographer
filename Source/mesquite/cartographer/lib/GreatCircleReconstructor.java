package mesquite.cartographer.lib;

import mesquite.lib.*;
import mesquite.cont.lib.*;
import mesquite.lib.taxa.*;
import mesquite.lib.tree.*;
import mesquite.lib.ui.*;

/*=========================================================================*/
/** This class reconstructs ancestral states by taking the great circle midpoint on a downpass if there are two daughters,
 * and the centroid if there are more than two, using GeographciData and a tree.
This operates on a simple double[][] instead of a MContinuousDistribution.*/
public class GreatCircleReconstructor {
		boolean rootedMode = true;
		double[][] observedStates;
		double[][] reconstructedStates;
		Tree tree;
		double[][] downA;
		int item = 0;
		int alreadyWarnedZeroLength = 0;
		int numChars = 0;
		int numItems = 1;
		boolean reconstructed = false;
		boolean[] deleted;
		
		public void reconstruct(Tree tree, double[][] observedStates,boolean weighted,  boolean rootedMode, boolean[] deletedTaxa){
			//NOTE: rootedMode == false not yet supported
			if (!rootedMode)
				MesquiteMessage.warnProgrammer("SquaredReconstructor error: rootedMode == false not yet supported");
			if (observedStates!=null && tree!=null && !(tree.nodeIsPolytomous(tree.getRoot()) && !rootedMode)){
				this.deleted = deletedTaxa;
				this.observedStates = observedStates;
				this.rootedMode = rootedMode;
				this.tree = tree;
				numChars = observedStates.length;
				if (numChars<2) {
					reconstructed = false;
					return;
				}
				numItems =1;
				doReconstruct();
	 		}
	 		else
	 			reconstructed = false;
		}
		
		void doReconstruct(){
			
			if (downA==null || downA.length!=numChars || downA[0]==null || downA[0].length != tree.getNumNodeSpaces()){
				downA=  new double[numChars][tree.getNumNodeSpaces()];
	 		}
	 		Double2DArray.zeroArray(downA);
			for (item = 0; item<numItems; item++) { //NOTE THIS relies on the variable item!  reentrancy problems could arise...
		 		if (statesLegal(tree, tree.getRoot(deleted))){
					greatCircleReconstruct(tree);
				}
			}
			reconstructed = true;
		}
		

		/*.................................................................................................................*/
		private double[] greatCircleDown(Tree tree, int node) {
			double[] nodeValue = new double[numChars];
			if (tree.nodeIsInternal(node)) { 
				int numDaughters = tree.numberOfDaughtersOfNode(node);
				double[][] daughterValues = new double[numChars][numDaughters];
				double[] singleDaughter = new double[numChars];
				Double2DArray.zeroArray(daughterValues);
				int count = 0;
				for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter)){
					singleDaughter= greatCircleDown(tree, daughter);
					for (int ic=0; ic<numChars; ic++)
						daughterValues[ic][count]=singleDaughter[ic];
					count++;
				}

				if (tree.nodeExists(node)) {
					if (numDaughters==2){ // use greatCircleMidpoint
						nodeValue = GeographicUtil.getGreatCircleMidPoint(daughterValues[0][0], daughterValues[0][1],daughterValues[1][0],daughterValues[1][1]);
					}
					else {
						DoubleArray.zeroArray(nodeValue);
						for (int ic=0; ic<numChars; ic++){
							for (int d=0; d<numDaughters; d++) {
								nodeValue[ic]+=daughterValues[ic][d];
							}
							nodeValue[ic] /= numDaughters;
						}
					}

					for (int ic=0; ic<numChars; ic++)
						downA[ic][node]=nodeValue[ic];
				}
				
			} else {
				for (int ic=0; ic<numChars; ic++){
					nodeValue[ic] = downA[ic][node];
				}
			}
			
			return nodeValue;
		}

		/*.................................................................................................................*/
		private void assignTerminals(Tree tree) {
			for (int it=0; it<tree.getTaxa().getNumTaxa(); it++) {
				if (tree.taxonInTree(it)) {
					int n = tree.nodeOfTaxonNumber(it);
					for (int ic=0; ic<numChars; ic++)
						downA[ic][n]=observedStates[ic][it];
				}
			}
		}

		void greatCircleReconstruct (Tree tree) {
			assignTerminals(tree);
			greatCircleDown(tree, tree.getRoot());
		}
	
		
		
		public double[][] getReconstructedStates(int item){
			
			if (reconstructed) {
				return downA;
			}
			return null;
		}
		
		
		
		public boolean statesLegal(Tree tree, int node) {
			if (tree.nodeIsTerminal(node)) {
				for (int ic = 0; ic<numChars; ic++)
					if (!ContinuousState.isCombinable(getObservedState(item, ic, tree.taxonNumberOfNode(node))))
						return false;
				return true;
			}
			for (int d = tree.firstDaughterOfNode(node, deleted); tree.nodeExists(d); d = tree.nextSisterOfNode(d, deleted))
				if (!statesLegal(tree, d))
					return false;
			return true;
		}
		 double getObservedState(int item, int ic, int it){
			 if (ic>=0 && it>= 0 && ic<observedStates.length && it< observedStates[0].length)
				return observedStates[ic][it];
			return 0;
		}
	   	
			
	}
