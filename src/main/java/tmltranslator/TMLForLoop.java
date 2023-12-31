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




package tmltranslator;

import myutil.*;

import java.util.Objects;

/**
 * Class TMLForLoop
 * Creation: 23/11/2005
 * @version 1.0 23/11/2005
 * @author Ludovic APVRILLE
 */
public class TMLForLoop extends TMLActivityElement {
    //next #0 -> inside the loop
    //next #1 -> after the loop
    
    private String init = "", condition="", increment="";

    private boolean isInfinite;
    
    public TMLForLoop(String _name, Object _referenceObject) {
         super(_name, _referenceObject);   
    }
    
    public void setInit(String _init) { init = _init; }
    public void setCondition(String _condition) { condition = _condition; }
    public void setIncrement(String _increment) { increment = _increment; }
    
    public String getInit() { return init;}
    public String getCondition() { return condition;}
    public String getIncrement() { return increment;}

    public void setInfinite(boolean b) {
	isInfinite = b;
    }

    public boolean isInfinite() {
	return isInfinite;
    }

    public String customExtraToXML() {
	    return " init=\"" + Conversion.transformToXMLString(init) + "\" condition=\"" + Conversion.transformToXMLString(condition) + "\" increment=\"" + Conversion.transformToXMLString(increment) + "\" isInfinite=\"" + isInfinite + "\" ";
    }

    public boolean equalSpec(Object o) {
        if (!(o instanceof TMLForLoop)) return false;
        if(!super.equalSpec(o)) return false;
        TMLForLoop tmlForLoop = (TMLForLoop) o;
        return Objects.equals(init, tmlForLoop.getInit()) &&
                Objects.equals(condition, tmlForLoop.getCondition()) &&
                Objects.equals(increment, tmlForLoop.getIncrement()) &&
                isInfinite == tmlForLoop.isInfinite();
    }

    public  TMLForLoop deepClone(TMLModeling tmlm) throws TMLCheckingError {
        TMLForLoop newElt = new TMLForLoop(getName(), getReferenceObject());
        fillValues(newElt, tmlm);
        return newElt;
    }

    public void fillValues(TMLForLoop newElt, TMLModeling tmlm) throws TMLCheckingError {
        super.fillValues(newElt, tmlm);
        newElt.init = init;
        newElt.condition = condition;
        newElt.increment = increment;
        newElt.isInfinite = isInfinite;
    }


}
