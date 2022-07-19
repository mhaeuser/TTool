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
 * Class AddMethodMutation
 * Creation: 24/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 24/06/2022
 */
public class AddMethodMutation extends MethodMutation implements AddMutation {

    public AddMethodMutation(String _blockName, String _methodName, List<String> _returnParameters, List<String[]> _parameters, boolean _imp) {
        super(_blockName, _methodName, _returnParameters, _parameters, _imp);
    }

    public AddMethodMutation(String _blockName, String _methodName, List<String> _returnParameters, List<String[]> _parameters) {
        super(_blockName, _methodName, _returnParameters, _parameters);
    }

    public AddMethodMutation(String _blockName, String _methodName, boolean _imp) {
        super(_blockName, _methodName, _imp);
    }

    public AddMethodMutation(String _blockName, String _methodName) {
        super(_blockName, _methodName);
    }

    public AvatarMethod createElement(AvatarSpecification _avspec) throws ApplyMutationException {

        AvatarBlock block = getBlock(_avspec);
        if (block == null) {
            throw new MissingBlockException(getBlockName());
        }

        for (AvatarMethod am : block.getMethods()) {
            if (am.getName().equals(this.getMethodName())) {
                throw new ApplyMutationException("Method " + getMethodName() + " already exists in block " + getBlockName());
            }
        }

        AvatarMethod am = new AvatarMethod(getMethodName(), null);
        for (String s : getReturnParameters()) {
            AvatarAttribute aa = new AvatarAttribute("", AvatarType.getType(s), block, null);
            am.addReturnParameter(aa);
        }
        for (String[] s : getParameters()) {
            AvatarAttribute aa = new AvatarAttribute(s[1], AvatarType.getType(s[0]), block, null);
            am.addParameter(aa);
        }
        am.setImplementationProvided(isImplementationProvided());
        return am;
    }

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarMethod am = createElement(_avspec);
        AvatarBlock block = getBlock(_avspec);
        block.addMethod(am);
    }

    public static AddMethodMutation createFromString(String toParse) throws ParseMutationException {
        String[] tokens = MutationParser.tokenise(toParse);
        
        List<String> _returnParameters = parseReturnParameters(toParse, "METHOD");

        String _methodName = parseMethodName(toParse);

        List<String[]> _parameters = parseParameters(toParse, _methodName.toUpperCase());

        /*for (String[] parameter : _parameters) {
            TraceManager.addDev(MutationParser.tokensToString(parameter));
        }*/

        int index = MutationParser.indexOf(tokens, "IN");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("block name", "in blockName");
        }
        String _blockName = tokens[index + 1];
        
        if (MutationParser.isTokenIn(tokens, "CODE")) {
            boolean _imp = MutationParser.isTokenIn(tokens, "WITH");
            return new AddMethodMutation(_blockName, _methodName, _returnParameters, _parameters, _imp);
        } else {
            return new AddMethodMutation(_blockName, _methodName, _returnParameters, _parameters);
        }
    }
}