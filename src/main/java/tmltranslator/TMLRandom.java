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


import java.util.Objects;

/**
 * Class TMLRandom
 * Creation: 10/06/2008
 * @version 1.0 10/06/2008
 * @author Ludovic APVRILLE
 */
public class TMLRandom extends TMLActivityElement {
    public static final int EQUIPROBABLE = 0;

    private String variable, minValue, maxValue;
    private int functionId = EQUIPROBABLE;

    public TMLRandom(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
    }

    public void setVariable(String _variable) { variable = _variable; }
    public void setMinValue(String _minValue) { minValue = _minValue; }
    public void setMaxValue(String _maxValue) { maxValue = _maxValue; }
    public void setFunctionId(int _functionId) { functionId = _functionId; }

    public String getVariable() { return variable;}
    public String getMinValue() { return minValue;}
    public String getMaxValue() { return maxValue;}
    public int getFunctionId()  { return functionId;}

    public String customExtraToXML() {
	return " politics=\"" + functionId + "\" variable=\"" + variable + "\" minValue=\"" + minValue + "\" maxValue=\"" + maxValue + "\" ";
    }

    public boolean equalSpec(Object o) {
        if (!(o instanceof TMLRandom)) return false;
        if (!super.equalSpec(o)) return false;

        TMLRandom tmlRandom = (TMLRandom) o;
        return Objects.equals(variable, tmlRandom.getVariable()) &&
                Objects.equals(minValue, tmlRandom.getMinValue()) &&
                Objects.equals(maxValue, tmlRandom.getMaxValue()) &&
                functionId == tmlRandom.getFunctionId();
    }

    public  TMLRandom deepClone(TMLModeling tmlm) throws TMLCheckingError {
        TMLRandom newElt = new TMLRandom(getName(), getReferenceObject());
        fillValues(newElt, tmlm);
        return newElt;
    }

    public void fillValues(TMLRandom newElt, TMLModeling tmlm) throws TMLCheckingError {
        super.fillValues(newElt, tmlm);
        newElt.setFunctionId(getFunctionId());
        newElt.setVariable(getVariable());
        newElt.setMinValue(getMinValue());
        newElt.setMaxValue(getMaxValue());
    }

}
