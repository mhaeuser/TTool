/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class AvatarSpecification
 * Avatar specification
 * Creation: 13/12/2010
 * @version 1.0 13/12/2010
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator.directsimulation;

import java.util.*;

import avatartranslator.*;
import myutil.*;


public class AvatarSpecificationSimulation  {
	
	private static int MAX_TRANSACTION_IN_A_ROW = 1000; 
  
    private AvatarSpecification avspec;
	private long clockValue;
	private LinkedList<AvatarSimulationBlock> blocks;
	private LinkedList<AvatarActionOnSignal> asynchronousMessages;
	private LinkedList<AvatarSimulationTransaction> pendingTransactions;
	private LinkedList<AvatarSimulationTransaction> allTransactions;
	
    public AvatarSpecificationSimulation(AvatarSpecification _avspec) {
        avspec = _avspec;
    }
	
	public void initialize() {
		// Remove composite states
		avspec.removeCompositeStates();
		
		// Remove timers
		avspec.removeTimers();
		
		// Reinit clock
		clockValue = 0;
		
		// Reinit simulation 
		AvatarSimulationTransaction.reinit();
		
		// Create all simulation blocks
		blocks = new LinkedList<AvatarSimulationBlock>();
		for(AvatarBlock block: avspec.getListOfBlocks()) {
			AvatarSimulationBlock asb = new AvatarSimulationBlock(block);
			blocks.add(asb);
		}
		
		// Create all simulation asynchronous channels
		asynchronousMessages = new LinkedList<AvatarActionOnSignal>();
		
		// Create the structure for pending and executed transactions
		pendingTransactions = new LinkedList<AvatarSimulationTransaction>();
		allTransactions = new LinkedList<AvatarSimulationTransaction>();
	}
	
	public boolean isInDeadlock() {
		return true;
	}
	
	public void runSimulation() {
		int index[];
		LinkedList<AvatarSimulationTransaction> selectedTransactions;
		
		TraceManager.addDev("Simulation started at time: " + clockValue);
		boolean go = true;
		while(go == true) {
			gatherPendingTransactions();
			
			if (pendingTransactions.size() == 0) {
				go = false;
				TraceManager.addDev("No more pending transactions");
			} else {
				selectedTransactions = selectTransactions(pendingTransactions);
				
				if (selectedTransactions.size() == 0) {
					go = false;
					TraceManager.addDev("Deadlock: no transaction can be selected");
				} else {
					TraceManager.addDev("Nb of selected transactions: " + selectedTransactions.size());
				}
				
			}
		}
		
		TraceManager.addDev("Simulation finished at time: " + clockValue);
		
		printExecutedTransactions();
	}
	
	public void gatherPendingTransactions() {
		pendingTransactions.clear();
		// Gather all pending transactions from blocks
		for(AvatarSimulationBlock asb: blocks) {
			pendingTransactions.addAll(asb.getPendingTransactions(allTransactions, clockValue, MAX_TRANSACTION_IN_A_ROW));
		}
	}
	
	public LinkedList<AvatarSimulationTransaction> selectTransactions(LinkedList<AvatarSimulationTransaction> _pendingTransactions) {
		LinkedList<AvatarSimulationTransaction> ll = new LinkedList<AvatarSimulationTransaction>();
		return ll;
	}
	
	public void printExecutedTransactions() {
		for(AvatarSimulationTransaction ast: allTransactions) {
			TraceManager.addDev(ast.toString() + "\n");
		}
	}

}