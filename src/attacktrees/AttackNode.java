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
 * Class AttackNode
 * Creation: 10/04/2015
 * @version 1.0 10/04/2015
 * @author Ludovic APVRILLE
 * @see
 */

package attacktrees;

import java.util.*;


public abstract class AttackNode { 
    private Attack resultingAttack; // If no resulting attack -> error!
    private ArrayList<Attack> inputAttacks;
    private ArrayList<Integer> inputValues;
    private String name;
    protected String type = "";
    protected Object referenceObject;
    
    public AttackNode(String _name, Object _referenceObject) {
	name = _name;
	referenceObject = _referenceObject;
	inputAttacks = new ArrayList<Attack>();
	inputValues = new ArrayList<Integer>();
    }

    public String getName() { return name;}
    
    public void setResultingAttack(Attack _attack) {
	resultingAttack = _attack;
    }

    public Attack getResultingAttack() {
	return resultingAttack;
    }

    public void addInputAttack(Attack _attack, Integer _val) {
	inputAttacks.add(_attack);
	inputValues.add(_val);
    }

    public String toString() {
	String ret = name + "/" + type + " Incoming attacks: ";
	for (Attack att: inputAttacks) {
	    ret += att.getName() + " ";
	}

	if (resultingAttack == null) {
	    ret += " No resulting attack";
	} else {
	    ret += " Resulting attack:" + resultingAttack.getName();
	}
	
	return ret;
    }
    
}
