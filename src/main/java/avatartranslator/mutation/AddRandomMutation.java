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
 * Class AddRandomMutation
 * Creation: 28/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 28/06/2022
 */

public class AddRandomMutation extends RandomMutation implements AddMutation {

    public AddRandomMutation(String _blockName, String _attributeName) {
        super(_blockName, _attributeName);
    }

    public AddRandomMutation(String _blockName, String _attributeName, String _name) {
        super(_blockName, _name, NAME_TYPE, _attributeName);
    }
    
    //todo : add Graphical referenceObject
    public AvatarRandom createElement(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarRandom rand = new AvatarRandom(getName(), null, getAvatarStateMachine(_avspec).getOwner());
        rand.setVariable(getAttributeName());
        if(areValuesSet()) rand.setValues(getMinValue(), getMaxValue());
        if(isFunctionSet()) {
            rand.setFunctionId(getFunctionId());
            rand.setExtraAttribute1(getExtraAttribute1());
            rand.setExtraAttribute2(getExtraAttribute2());
        }
        return rand;
    }

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarStateMachine asm = getAvatarStateMachine(_avspec);
        AvatarRandom rand = createElement(_avspec);
        asm.addElement(rand);
    }

    public static AddRandomMutation createFromString(String toParse) throws ParseMutationException {

        AddRandomMutation mutation = null;
        String[] tokens = MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "IN");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("block name", "in blockName");
        }
        String _blockName = tokens[index + 1];

        index = MutationParser.indexOf(tokens, "WITH");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("attibute name", "with attributeName");
        }
        String _attributeName = tokens[index + 1];

        String[] _values = parseMinMax(tokens);
        String _law = parseLaw(tokens);
        String[] _extras = parseExtras(tokens);

        index = MutationParser.indexOf(tokens, "RANDOM");
        if (tokens.length == index + 1 || MutationParser.isToken(tokens[index+1])) {
            mutation = new AddRandomMutation(_blockName, _attributeName);
        } else {
            String _name = tokens[index + 1];
            mutation = new AddRandomMutation(_blockName, _attributeName, _name);
        }

        mutation.setValues(_values);
        
        mutation.setFunction(_law, _extras);

        return mutation;
        
    }
}
