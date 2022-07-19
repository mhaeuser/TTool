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
//import myutil.TraceManager;

/**
 * Class RandomMutation
 * Creation: 27/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 27/06/2022
 */

public abstract class RandomMutation extends UnnamedStateMachineElementMutation {

    protected RandomMutation(String _blockName, String _attributeName) {
        super(_blockName);
        setAttributeName(_attributeName);
    }

    protected RandomMutation(String _blockName, String _name, int _nameType) {
        super(_blockName, _name, _nameType);
    }

    protected RandomMutation(String _blockName, String _name, int _nameType, String _attributeName) {
        super(_blockName, _name, _nameType);
        setAttributeName(_attributeName);
    }

    private String attributeName;

    private String minValue;
    private String maxValue;

    private boolean valuesSet = false;

    private int functionId = 0;
    private String extraAttribute1 = "";
    private String extraAttribute2 = "";

    private boolean functionSet = false;

    protected String getAttributeName() {
        return attributeName;
    }

    private void setAttributeName(String _variable) {
        attributeName = _variable;
    }

    protected String getMinValue() {
        return minValue;
    }

    protected String getMaxValue() {
        return maxValue;
    }

    protected boolean areValuesSet() {
        return valuesSet;
    }

    public void setValues(String[] _minMaxValues) {
        setValues(_minMaxValues[0], _minMaxValues[1]);
    }

    public void setValues(String _minValue, String _maxValue) {
        minValue = _minValue;
        maxValue = _maxValue;
        valuesSet = true;
    }

    protected int getFunctionId() {
        return functionId;
    }

    protected String getExtraAttribute1() {
        return extraAttribute1;
    }

    protected String getExtraAttribute2() {
        return extraAttribute2;
    }

    protected boolean isFunctionSet() {
        return functionSet;
    }

    public void setFunction(String _law, String[] _extras) {
        switch (_extras.length) {
            case 0:
                setFunction(_law);
                break;
            case 1:
                setFunction(_law, _extras[0]);
                break;
            default:
                setFunction(_law, _extras[0], _extras[1]);
        }
    }

    public void setFunction(String _law) {
        int law = MutationParser.indexOf(AvatarTransition.DISTRIBUTION_LAWS, _law);
        if (law == -1) law = MutationParser.indexOf(AvatarTransition.DISTRIBUTION_LAWS_SHORT, _law);
        if (law == -1) law = 0;
        functionId = law;
        functionSet = true;
    }

    public void setFunction(String _functionId, String _extraAttribute) {
        setFunction(_functionId);
        extraAttribute1 = _extraAttribute;
    }

    public void setFunction(String _functionId, String _extraAttribute1, String _extraAttribute2) {
        setFunction(_functionId, _extraAttribute1);
        extraAttribute2 = _extraAttribute2;
    }

    @Override
    public AvatarRandom getElement(AvatarSpecification _avspec) throws ApplyMutationException {
        if (!isNameSet()) {
            AvatarStateMachine asm = getAvatarStateMachine(_avspec);
            List<AvatarStateMachineElement> elts =  asm.getListOfElements();
            for (AvatarStateMachineElement elt : elts) {
                if (elt instanceof AvatarRandom) {
                    AvatarRandom rnd = (AvatarRandom)elt;
                    boolean flag = rnd.getVariable().equals(this.getAttributeName());
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
        AvatarStateMachineElement element = super.getElement(_avspec);
        if (element == null) {
            throw new ApplyMutationException("No random named " + getName() + "in block " + getBlockName());
        }
        if (element instanceof AvatarRandom) {
            return (AvatarRandom)element;
        }
        throw new ApplyMutationException("Element " + getName() + " in block " + getBlockName() + " is not a random");
    }

    public static String[] parseMinMax(int index, String[] tokens) throws ParseMutationException {

        index = MutationParser.indexOf(index, tokens, "(");

        if (tokens.length <= index + 3) {
            throw new ParseMutationException("min max", "after (min, max)");
        }

        String[] out = {tokens[index + 1], tokens[index + 3]};
        return out;
    }

    public static String[] parseMinMax(String[] tokens) throws ParseMutationException {
        return parseMinMax(0, tokens);
    }
    

    public static String parseLaw(int index, String[] tokens) {
    
        index = MutationParser.indexOf(index, tokens, "(");
    
        if (tokens.length <= index + 5 || tokens[index+5].toUpperCase().equals("TO")) return "";
        return tokens[index + 5];
    }

    public static String parseLaw(String[] tokens) {
        return parseLaw(0, tokens);
    }


    public static String[] parseExtras(String[] tokens) {
        return parseExtras(0, tokens);
    }

    public static String[] parseExtras(int index, String[] tokens) {

        String[] out0 = {};
        String[] out1 = {""};
        String[] out2 = {"", ""};
        String[] out;

        index = MutationParser.indexOf(index, tokens, ")");

        index = MutationParser.indexOf(index, tokens, "(");

        if (index == -1) return out0;

        int indexEnd = MutationParser.indexOf(index, tokens, ")");
        if (indexEnd == -1) return out0;
        switch (indexEnd - index) {
            case 1:
                out = out0;
                break;
            case 2:
                out = out1;
                out[0] = tokens[index + 1];
                break;
            default:
                out = out2;
                out[0] = tokens[index + 1];
                out[1] = tokens[index + 3];
                break;
        }
        return out;
    }

    public static RandomMutation createFromString(String toParse) throws ParseMutationException {
        switch (MutationParser.findMutationToken(toParse)) {
            case "ADD":
                return AddRandomMutation.createFromString(toParse);
            case "RM":
            case "REMOVE":
                return RmRandomMutation.createFromString(toParse);
            case "MD":
            case "MODIFY":
                return MdRandomMutation.createFromString(toParse);
            default:
                break;
        }
        return null;
    }
}
