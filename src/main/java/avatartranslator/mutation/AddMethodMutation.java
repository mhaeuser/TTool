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
 * Class AddMethodMutation
 * Creation: 24/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 24/06/2022
 */
public class AddMethodMutation extends MethodMutation implements AddMutation {

    public AddMethodMutation(String _blockName, String _methodName, boolean _imp) {
        super(_blockName, _methodName, _imp);
    }

    public AddMethodMutation(String _blockName, String _methodName) {
        this(_blockName, _methodName, false);
    }

    public AvatarMethod createElement(AvatarSpecification _avspec) {
        AvatarBlock block = getBlock(_avspec);
        AvatarMethod am = new AvatarMethod(getMethodName(), null);
        for (String s : getReturnParameters()) {
            AvatarAttribute aa = new AvatarAttribute("", AvatarType.getType(s), block, null);
            am.addReturnParameter(aa);
        }
        for (String[] s : getParameters()) {
            AvatarAttribute aa = new AvatarAttribute(s[0], AvatarType.getType(s[1]), block, null);
            am.addParameter(aa);
        }
        am.setImplementationProvided(isImplementationProvided());
        return am;
    }

    public void apply(AvatarSpecification _avspec) {
        AvatarMethod am = createElement(_avspec);
        AvatarBlock block = getBlock(_avspec);
        block.addMethod(am);
    }

    public static AddMethodMutation createFromString(String toParse) {
        AddMethodMutation mutation = null;
        String _returnParameter;
        String _methodName;
        String _blockName;

        List<String[]> _parameters = parseParameters(toParse);
        String[] tokens = toParse.split(" ");

        boolean _imp = false;
        switch (tokens[tokens.length - 2].toUpperCase()) {
            case "WITH":
                _imp = true;
            case "WITHOUT":
                _blockName = tokens[tokens.length - 3];
                break;
            default:
                _blockName = tokens[tokens.length - 1];
                break;
        }

        String s = toParse.substring(toParse.indexOf(' ')+1);
        s = s.substring(s.indexOf(' '), s.indexOf('('));
        tokens = s.split(" ");
        if (tokens.length == 1) {
            _methodName = tokens[0];
            mutation = new AddMethodMutation(_blockName, _methodName, _imp);
        } else {
            _returnParameter = tokens[0];
            _methodName = tokens[1];
            mutation = new AddMethodMutation(_blockName, _methodName, _imp);
            mutation.addReturnParameter(_returnParameter);
        }

        mutation.setParameters(_parameters);

        return mutation;
    }
}