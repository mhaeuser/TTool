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
//import myutil.TraceManager;

/**
 * Class AddAttributeMutation
 * Creation: 23/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 23/06/2022
 */
public class AddAttributeMutation extends AttributeMutation implements AddMutation {

    public AddAttributeMutation(String _blockName, String _attributeName, String _attributeType) {
        super(_blockName, _attributeName, _attributeType);
    }

    public AddAttributeMutation(String _blockName, String _attributeName, String _attributeType, String _initialValue) {
        super(_blockName, _attributeName, _attributeType, _initialValue);
    }

    public AvatarAttribute createElement(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarType type = getType();

        AvatarBlock block = getBlock(_avspec);
        if (block == null) {
            throw new MissingBlockException(getBlockName());
        }

        for (AvatarAttribute aa : block.getAttributes()) {
            if (aa.getName() == this.getName()) {
                throw new ApplyMutationException("Attribute " + this.getName() + " alrady exists in block " + getBlockName());
            }
        }

        AvatarAttribute aa = new AvatarAttribute(getName(), type, block, null);
        if (hasInitialValue()) aa.setInitialValue(getInitialValue());
        return aa;
    }

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarAttribute aa = createElement(_avspec);
        AvatarBlock block = getBlock(_avspec);
        block.addAttribute(aa);
    }

    public static AddAttributeMutation createFromString(String toParse) throws ParseMutationException {

        //TraceManager.addDev("AddAttribute");

        AddAttributeMutation mutation = null;
        String[] tokens = MutationParser.tokenise(toParse);

        //TraceManager.addDev(MutationParser.tokensToString(tokens));

        int index = MutationParser.indexOf(tokens, "ATTRIBUTE");
        if (tokens.length <= index + 2) {
            throw new ParseMutationException("Missing attribute arguments [add attribute attributeType attributeName]");
        }
        String _attributeType = tokens[index + 1];
        String _attributeName = tokens[index + 2];

        index = MutationParser.indexOf(tokens, "IN");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("Missing block name [in blockName]");
        }
        String _blockName = tokens[index + 1];

        index = MutationParser.indexOf(tokens,  "=");

        if (index != -1) {
            if (tokens.length == index + 1) {
                throw new ParseMutationException("Missing attribute initial value [ = initialValue]");
            }
            String _initialValue = tokens[index + 1];
            mutation = new AddAttributeMutation(_blockName, _attributeName, _attributeType, _initialValue);
        } else {
            mutation = new AddAttributeMutation(_blockName, _attributeName, _attributeType);
        }
        
        return mutation;
    }
}