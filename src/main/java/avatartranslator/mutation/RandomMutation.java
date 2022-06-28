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

package avatartranslator.mutation;

import java.util.List;

import avatartranslator.*;

/**
 * Class RandomMutation
 * Creation: 27/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 27/06/2022
 */

public abstract class RandomMutation extends StateMachineElementMutation {
    
    private String name = "";
    private int nameType = UNDEFINED_TYPE;

    private String variable;

    private String minValue;
    private String maxValue;

    private boolean valuesSet = false;

    private int functionId = 0;
    private String extraAttribute1 = "";
    private String extraAttribute2 = "";

    private boolean functionSet = false;

    public String getName() {
        return name;
    }

    public boolean isNameSet() {
        return nameType != UNDEFINED_TYPE;
    }

    public int getNameType() {
        return nameType;
    }

    public void setName(String _name) {
        name = _name;
        nameType = NAME_TYPE;
    }

    public void setUUID(String _uuid) {
        name = _uuid;
        nameType = UUID_TYPE;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String _variable) {
        variable = _variable;
    }

    public String getMinValue() {
        return minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public boolean areValuesSet() {
        return valuesSet;
    }

    public void setValues(String _minValue, String _maxValue) {
        minValue = _minValue;
        maxValue = _maxValue;
        valuesSet = true;
    }

    public int getFunctionId() {
        return functionId;
    }

    public String getExtraAttribute1() {
        return extraAttribute1;
    }

    public String getExtraAttribute2() {
        return extraAttribute2;
    }

    public boolean isFunctionSet() {
        return functionSet;
    }

    public void setFunction(int _functionId) {
        functionId = _functionId;
        functionSet = true;
    }

    public void setFunction(int _functionId, String _extraAttribute) {
        setFunction(_functionId);
        extraAttribute1 = _extraAttribute;
    }

    public void setFunction(int _functionId, String _extraAttribute1, String _extraAttribute2) {
        setFunction(_functionId, _extraAttribute1);
        extraAttribute2 = _extraAttribute2;
    }

    public AvatarRandom getElement(AvatarSpecification _avspec) {
        if (!isNameSet()) {
            AvatarStateMachine asm = getAvatarStateMachine(_avspec);
            List<AvatarStateMachineElement> elts =  asm.getListOfElements();
            for (AvatarStateMachineElement elt : elts) {
                if (elt instanceof AvatarRandom) {
                    AvatarRandom rnd = (AvatarRandom)elt;
                    boolean flag = rnd.getVariable().equals(this.getName());
                    if (this.areValuesSet()) {
                        if (this.getMinValue().equals(rnd.getMinValue())) {
                            flag = this.getMaxValue().equals(rnd.getMaxValue());
                        } else flag = false;
                    }
                    if (flag && this.isFunctionSet()) {
                        if (this.getFunctionId() == rnd.getFunctionId()) {
                            if (rnd.getExtraAttribute1().equals(this.getExtraAttribute1())) {
                                flag = rnd.getExtraAttribute2().equals(this.getExtraAttribute2());
                            } else flag = false;
                        } else flag = false;
                    }
                    if (flag) return rnd;
                }
            }
            return null;
        }
        AvatarStateMachineElement element = getElement(_avspec, nameType, name);
        if (element != null && element instanceof AvatarRandom) return (AvatarRandom)element;
        return null;
    }

}
