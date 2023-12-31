/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */




package req.ebrdd;

import myutil.Conversion;

import java.util.ArrayList;

/**
 * Class EBRDDLoop
 * Creation: 22/09/2009
 * @version 1.0 22/09/2009
 * @author Ludovic APVRILLE
 */
public class EBRDDChoice extends EBRDDComponent {
    private ArrayList<String> guards;
    
    public EBRDDChoice(String _name, Object _referenceObject) {
		super(_name, _referenceObject);
        nbNext = -1;
        guards = new ArrayList<String>();
    }
    
    public void addGuard(String _g) {
        guards.add(_g);
    }
    
    public int getNbGuard() {
        return guards.size();
    }
    
    public String getGuard(int i) {
        if (i < getNbGuard()) {
            return guards.get(i);
        } else {
            return null;
        }
    }
	
	
    
    public void setGuardAt(int _i, String _g) {
        guards.set(_i, _g);
    }
    
    public boolean hasMoreThanOneElse() {
        int cpt = 0;
        String guard;
        for(int i=0; i<getNbGuard(); i++) {
            guard = getGuard(i);
            guard = Conversion.replaceAllChar(guard, '[', " ");
            guard = Conversion.replaceAllChar(guard, ']', " ");
            guard = guard.trim();
            if (guard.compareTo("else") == 0) {
                cpt ++;
            }
        }
        
        return (cpt > 1);
    }
	
    
    public int getElseGuard() {
        //int cpt = 0;
        String guard;
        for(int i=0; i<getNbGuard(); i++) {
            guard = getGuard(i);
            guard = Conversion.replaceAllChar(guard, '[', " ");
            guard = Conversion.replaceAllChar(guard, ']', " ");
            guard = guard.trim();
            if (guard.compareTo("else") == 0) {
                return i;
            }
        }
        
        return -1;
    }
    
    public String getValueOfElse() {
        String g = "";
        int cpt = 0;
        String guard;
        
        for(int i=0; i<getNbGuard(); i++) {
            guard = getGuard(i);
            guard = Conversion.replaceAllChar(guard, '[', " ");
            guard = Conversion.replaceAllChar(guard, ']', " ");
            guard = guard.trim();
            if ((!(guard.compareTo("else") == 0)) && (!(guard.compareTo("after") == 0))) {
                guard = getGuard(i);
                guard = Conversion.replaceAllChar(guard, '[', "(");
                guard = Conversion.replaceAllChar(guard, ']', ")");
                guard = "(" + guard + ")";
                if (cpt == 0) {
                    g = guard;
                } else {
                    g = g + " or " +guard;
                }
                cpt ++;
            }
        }
        
        return "[not(" + g + ")]"; 
    }
	
	 public void orderGuards() {
        int index;
        String guard;
        EBRDDComponent next;
        
        // Put else at the end
        index = getElseGuard();
        if ((index > -1) && (index != (getNbGuard() - 1))) {
            next = getNextElement(index);
            guard = getGuard(index);
            guards.remove(index);
			removeNext(index);
            addNext(next);
            addGuard(guard); 
        }
    }
    
}