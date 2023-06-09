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

import myutil.Conversion;
import myutil.TraceManager;
import translator.JKeyword;
import translator.RTLOTOSKeyword;
import translator.UPPAALKeyword;

/**
   * Class AvatarSignal
   * Signals in Avatar ...
   * Creation: 20/05/2010
   * @version 1.0 20/05/2010
   * @author Ludovic APVRILLE
 */
public class AvatarSignal extends AvatarMethod {

    // Signal type
    public final static int IN = 0;
    public final static int OUT = 1;

    private int inout;

    public AvatarSignal(String _name, int _inout, Object _referenceObject) {
        super(_name, _referenceObject);
        
        inout = _inout;
        name = _name;
    }

    public int getInOut() {
        return inout;
    }

    public String getSignalName() {
        return name;
    }

    public void setInOut(int _inout) {
        inout = _inout;
    }

    public boolean isOut() {
        return (inout == OUT);
    }

    public boolean isIn() {
        return (inout == IN);
    }

    public static boolean isAValidSignal(String _signal) {
        return AvatarTerm.isValidName (_signal);
    }

    @Override
    public String toString() {
        String ret = super.toString();
        if (isOut()) {
            return "out " + ret;
        }
        return "in " + ret;
    }

    @Override
    public String toBasicString() {
        String ret = super.toBasicString();
        if (isOut()) {
            return "out " + ret;
        }
        return "in " + ret;
    }

	public String minString(){
        int cpt = 0;
		String ret = getName() + "(";
        for(AvatarAttribute attribute: parameters) {
            if (cpt != 0) {
                ret += ",";
            }
            cpt ++;
            ret += attribute.getName();
        }

        ret += ")";
        return ret;
	}

	public int getNbParams(){
        int cpt = 0;
	
        for(AvatarAttribute attribute: parameters) {           
           cpt ++;            
        }
        return cpt;
	}

    //DG 13.06.
    /* public int getCumulSizeParams(){
        int cumul = 0;
	
        for(AvatarAttribute attribute: parameters) { 
	    AvatarType type = attribute.getType();
	    cumul += 4;  //hack          
        }
        return cumul;
	}*/
    //fin DG


	public AvatarSignal advancedClone(AvatarStateMachineOwner _block) {
		AvatarSignal as = new AvatarSignal(getName(), getInOut(), getReferenceObject());
		setAdvancedClone(as, _block);
		return as;
	}


	public boolean isCompatibleWith(AvatarSignal _as) {
		if (getInOut() == _as.getInOut()) {
			return false;
		}

		return super.isCompatibleWith(_as);
	}

    public String[] getNameExceptions() { return null;}

    // A signal must be of the form: "out/in id(type id0, type id1, ...)"
    // Or 'returntype id(type id0, type id1, ...)'
    // Returns null in case the method is not valid
    public static AvatarSignal isAValidSignalThenCreate(String _signal, AvatarBlock _block) {


        String signal, tmp, id;
        String rt = "";

        if (_signal == null) {
            return null;
        }

        signal = _signal.trim();

        int inout = IN;

        if (signal.startsWith("in ")) {
            signal = signal.substring(3).trim();
        } else if (signal.startsWith("out ")) {
            inout = OUT;
            signal = signal.substring(4).trim();
        }

        // Must replace all "more than one space" by only one space
        signal = Conversion.replaceAllString(signal, "\t", " ");
        signal = Conversion.replaceAllString(signal, "  ", " ");
        //TraceManager.addDev("Signal=" + signal);

        if (signal.length() == 0) {
            return null;
        }

        // Check for name
        int indexName = signal.indexOf('(');
        String signalName = signal;
        if (indexName != -1) {
            signalName = signal.substring(0, indexName).trim();
            signal = signal.substring(indexName);
        }

        //TraceManager.addDev("SignalName: " + signalName + " signal:" + signal);

        if (!isAValidId(signalName, true, true,true, false)) {
            TraceManager.addDev("Unvalid id of signal " + signalName);
            return null;
        }


        AvatarSignal as = new AvatarSignal(signalName, inout, null);

        int indexBeg = signal.indexOf('(');
        int indexEnd = signal.indexOf(')');

        if ((indexBeg == -1) || (indexEnd == -1)) {
            TraceManager.addDev("No parenthesis in signal=" + signal);
            return as;
        }

        // Check parenthesis order
        if (indexBeg > indexEnd) {
            TraceManager.addDev(") before (");
            return as;
        }


        // Between parenthesis: parameters of the form: String space String comma
        // We replace double space by spaces and then spaces by commas
        tmp = signal.substring(indexBeg + 1, indexEnd).trim();
        //TraceManager.addDev("valid signal: tmp=" + tmp);

        // no parameter?
        if (tmp.length() == 0) {
            return as;
        }

        // Has parameters...
        tmp = Conversion.replaceAllString(tmp, "  ", " ");
        tmp = Conversion.replaceAllString(tmp, " ,", ",");
        tmp = Conversion.replaceAllString(tmp, ", ", ",");
        tmp = Conversion.replaceAllChar(tmp, ' ', ",");


        String splitted[] = tmp.split(",");
        int size = splitted.length / 2;
        // TraceManager.addDev("Nb of parameters=" + size);
        int i;



        try {
            for (i = 0; i < splitted.length; i = i + 2) {
                if (splitted[i].length() == 0) {
                    return null;
                }
                if (splitted[i + 1].length() == 0) {
                    return null;
                }
                if (!isAValidId(splitted[i], false, false,false, true)) {
                    TraceManager.addDev("Unvalid type: " + splitted[i]);
                    return null;
                }
                if (!isAValidId(splitted[i + 1], true, true,true, false)) {
                    TraceManager.addDev("Unvalid id of parameter " + splitted[i + 1]);
                    return null;
                }
                //TraceManager.addDev("Adding parameter: " + splitted[i] + " " + splitted[i+1]);
                AvatarAttribute aa = new AvatarAttribute(splitted[i + 1], AvatarType.getType(splitted[i]), _block, null);
                as.addParameter(aa);


            }
        } catch (Exception e) {
            TraceManager.addDev("AvatarSignal Exception:" + e.getMessage());
            return null;
        }

        //TraceManager.addDev("Returning method");

        return as;
    }

    public static boolean isAValidId(String id, boolean checkKeyword, boolean checkUPPAALKeyword, boolean checkJavaKeyword, boolean checkTypes) {
        // test whether _id is a word

        if ((id == null) || (id.length() < 1)) {
            return false;
        }

        String lowerid = id.toLowerCase();
        boolean b1, b2, b3, b4, b5, b6;
        b1 = (id.substring(0, 1)).matches("[a-zA-Z]");
        b2 = id.matches("\\w*");
        if (checkKeyword) {
            b3 = !RTLOTOSKeyword.isAKeyword(lowerid);
        } else {
            b3 = true;
        }

        if (checkKeyword) {
            b6 = !UPPAALKeyword.isAKeyword(lowerid);
        } else {
            b6 = true;
        }

        if (checkJavaKeyword) {
            b5 = !JKeyword.isAKeyword(lowerid);
        } else {
            b5 = true;
        }

        if (checkTypes) {
            b4 = ( lowerid.equals("int") || lowerid.equals("bool") );
        } else {
            b4 = true;
        }

        return (b1 && b2 && b3 && b4 && b5 && b6);
    }

    
}
