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

package avatartranslator;

import myutil.NameChecker;
import myutil.TraceManager;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
   * Class AvatarElement
   * Creation: 20/05/2010
   * @version 1.0 20/05/2010
   * @author Ludovic APVRILLE
 */
public class AvatarElement implements NameChecker.NamedElement {

    private static int ID=0;

    protected String name;
    protected Object referenceObject;
    protected Vector<Object> otherReferenceObjects;
    private int myID;
    protected boolean isNew;

    public AvatarElement(String _name, Object _referenceObject) {
        myID=++ID;
        name = _name;
        referenceObject = _referenceObject;
    }

    public void addReferenceObjectFrom(AvatarElement _elt) {
    	addReferenceObject(_elt.getReferenceObject());
    	Vector<Object> others = _elt.getReferenceObjects();
    	if (others != null) {
    		for(Object o: others) {
    			addReferenceObject(o);
    		}
    	}

    }

    public void clearReferenceObject() {
        referenceObject = null;
    }

    public void setReferenceObject(Object _referenceObject) {
        referenceObject = _referenceObject;
    }

    public void addReferenceObject(Object _ref) {
        if (otherReferenceObjects == null) {
            otherReferenceObjects = new Vector<Object>();
        }
        otherReferenceObjects.add(_ref);
    }

    public boolean hasReferenceObject(Object _ref) {
        if (referenceObject == _ref) {
            return true;
        }

        if (otherReferenceObjects != null) {
            for(Object obj: otherReferenceObjects) {
                if (obj == _ref) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setName(String _name) {
        name = _name;
    }

    public String getName() {
        return name;
    }

    public String[] getNameExceptions() { return null;}
    public myutil.NameChecker.NamedElement[] getSubNamedElements() { return null;}

    public Object getReferenceObject() {
        return referenceObject;
    }

    public Vector<Object> getReferenceObjects() {
    	return otherReferenceObjects;
    }

    public int getID(){
        return myID;
    }

    public UUID getUUID() {
        if (referenceObject != null) {
            if (referenceObject instanceof ElementWithUUID) {
                return ((ElementWithUUID)referenceObject).getUUID();
            }
        }
        return null;
    }

    public static void resetID() {
        TraceManager.addDev("Reset AvatarID");
        ID = 0;
    }

    @Override
    public String toString() {
    	return getName();
    }

    public void cloneLinkToReferenceObjects(AvatarElement ae) {
    	if (otherReferenceObjects == null) {
    		return;
    	}
    	for(Object o: otherReferenceObjects) {
    		ae.addReferenceObject(o);
    	}
    }

    public boolean isNew() {
        if (referenceObject == null) {
            return false;
        }
        if (referenceObject instanceof ElementWithNew) {
            return ((ElementWithNew)(referenceObject)).isNew();
        }
        return false;
    }
}
