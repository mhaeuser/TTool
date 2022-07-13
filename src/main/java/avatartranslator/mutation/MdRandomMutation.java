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

import avatartranslator.*;

/**
 * Class MdRandomMutation
 * Creation: 28/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 28/06/2022
 */

public class MdRandomMutation extends RandomMutation implements MdMutation {

    private RandomMutation current;

    private boolean attributeNameChange = false;

    public MdRandomMutation(String _blockName, String _attributeName) {
        super(_blockName, _attributeName);
        current = new NoneRandomMutation(_blockName, _attributeName);
    }

    public MdRandomMutation(String _blockName, String _attributeName, String _newAttributeName) {
        super(_blockName, _newAttributeName);
        current = new NoneRandomMutation(_blockName, _attributeName);
        attributeNameChange = true;
    }
    
    public MdRandomMutation(String _blockName, String _name, int _nameType) {
        super(_blockName, _name, _nameType);
        current = new NoneRandomMutation(_blockName, _name, _nameType);
    }

    public MdRandomMutation(String _blockName, String _name, int _nameType, String _newAttributeName) {
        super(_blockName, _newAttributeName);
        current = new NoneRandomMutation(_blockName, _name, _nameType);
        attributeNameChange = true;
    }

    public void setCurrentValues(String[] _minMaxValues) {
        current.setValues(_minMaxValues);
    }

    public void setCurrentValues(String _minValue, String _maxValue) {
        current.setValues(_minValue, _maxValue);
    }

    public void setCurrentFunction(String _law, String[] _extras) {
        current.setFunction(_law, _extras);
    }

    public void setCurrentFunction(String _law) {
        current.setFunction(_law);
    }

    public void setCurrentFunction(String _law, String _extraAttribute) {
        current.setFunction(_law, _extraAttribute);
    }

    public void setCurrentFunction(String _law, String _extraAttribute1, String _extraAttribute2) {
        current.setFunction(_law, _extraAttribute1, _extraAttribute2);
    }

    @Override
    public AvatarRandom getElement(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarRandom random = null;
        try {
            random = current.getElement(_avspec);
        } catch (Exception e) {
        }
        if (random == null) random = super.getElement(_avspec);
        return random;
    }

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarRandom rand = current.getElement(_avspec);

        if(rand == null) {
            throw new ApplyMutationException("no such random in block " + getBlockName());
        }

        if (attributeNameChange)
            rand.setVariable(this.getAttributeName());

        if (areValuesSet())
            rand.setValues(this.getMinValue(), this.getMaxValue());

        if (isFunctionSet()) {
            rand.setFunctionId(this.getFunctionId());
            rand.setExtraAttribute1(this.getExtraAttribute1());
            rand.setExtraAttribute2(this.getExtraAttribute2());
        }
    }

    public static MdRandomMutation createFromString(String toParse) throws ParseMutationException {

        MdRandomMutation mutation = null;
        String[] tokens = MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "IN");
        String _blockName = tokens[index + 1];

        String _attributeName = null;
        String[] _currentValues = null;
        String _currentLaw = null;
        String[] _currentExtras = null;
        String _newAttributeName = null;
        String[] _newValues = null;
        String _newLaw = null;
        String[] _newExtras = null;

        String _name = null;
        int _nameType = -1;

        int toIndex = MutationParser.indexOf(tokens, "TO");
        if (toIndex == -1) {
            throw new ParseMutationException("new options", "to newOptions");
        }

        index = MutationParser.indexOf(tokens, "RANDOM");
        if (tokens.length == index + 1) {
            throw new ParseMutationException("random description", "random randomName] or [random with attributeName");
        }

        if (MutationParser.isToken(tokens[index+1])) {
            index = MutationParser.indexOf(tokens, "WITH");
            if (tokens.length == index + 1 || index == -1) {
                throw new ParseMutationException("random description", "random randomName] or [random with attributeName");
            }
            _attributeName = tokens[index + 1];
            index = MutationParser.indexOf(tokens, "(");
            if (index != -1 && index < toIndex) {
                _currentValues = parseMinMax(tokens);
                _currentLaw = parseLaw(tokens);
                _currentExtras = parseExtras(tokens);
            }
        } else {
            _name = tokens[index + 1];
            _nameType = MutationParser.UUIDType(_name);
        }

        index = toIndex;
        if (tokens.length > index + 1 && !MutationParser.isToken(tokens[index + 1])) {
            _newAttributeName = tokens[index + 1];
            if (_name == null) {
                mutation = new MdRandomMutation(_blockName, _attributeName, _newAttributeName);
            } else {
                mutation = new MdRandomMutation(_blockName, _name, _nameType, _newAttributeName);
            }
        } else {
            if (_name == null) {
                mutation = new MdRandomMutation(_blockName, _attributeName);
            } else {
                mutation = new MdRandomMutation(_blockName, _name, _nameType);
            }
        }

        if (_currentValues != null) {
            mutation.setCurrentValues(_currentValues);
            mutation.setCurrentFunction(_currentLaw, _currentExtras);
        }

        index = MutationParser.indexOf(toIndex, tokens, "(");
        if (index != -1) {
            _newValues = parseMinMax(tokens);
            _newLaw = parseLaw(tokens);
            _newExtras = parseExtras(tokens);
            mutation.setValues(_newValues);
            mutation.setFunction(_newLaw, _newExtras);
        }

        return mutation;
    }
}
