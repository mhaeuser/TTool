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

import myutil.TraceManager;

/**
 * Class MdMethodMutation
 * Creation: 24/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 24/06/2022
 */
public class MdMethodMutation extends MethodMutation implements MdMutation {
    
    private boolean implementationChanged = false;
    private boolean returnParametersChanged = false;
    private boolean parametersChanged = false;

    public MdMethodMutation(String _blockName, String _methodName) {
        super(_blockName, _methodName);
    }

    public MdMethodMutation(String _name, String _blockName, boolean _imp) {
        this(_name, _blockName);
        setImplementationProvided(_imp);
    }

    @Override
    public void addReturnParameter(String _returnParameter) {
        returnParametersChanged = true;
        super.addReturnParameter(_returnParameter);
    }

    @Override
    public void setParameters(List<String[]> _parameters) {
        parametersChanged = true;
        super.setParameters(_parameters);
    }

    @Override
    public void addParameter(String[] _parameter) {
        parametersChanged = true;
        super.addParameter(_parameter);
    }

    @Override
    protected void setImplementationProvided(boolean _imp) {
        implementationChanged = true;
        super.setImplementationProvided(_imp);
    }

    public void apply(AvatarSpecification _avspec) {
        AvatarBlock block = getBlock(_avspec);
        AvatarMethod am = getElement(_avspec);

        if (am == null) {
            TraceManager.addDev("Unknown Method");
            return;
        }

        List<AvatarMethod> meth = block.getMethods();
        if (!meth.contains(am)) {
            TraceManager.addDev("Method is from a super-bloc");
            return;
        }

        if (implementationChanged) {
            am.setImplementationProvided(isImplementationProvided());
        }

        if (returnParametersChanged) {
            am.removeReturnAttributes();
            for (String s : getReturnParameters()) {
                AvatarAttribute aa = new AvatarAttribute("", AvatarType.getType(s), block, null);
                am.addReturnParameter(aa);
            }
        }

        if (parametersChanged) {
            am.removeAttributes();
            for (String[] s : getParameters()) {
                AvatarAttribute aa = new AvatarAttribute(s[0], AvatarType.getType(s[1]), block, null);
                am.addParameter(aa);
            }
        }
    }

    public static MdMethodMutation createFromString(String toParse) {
        String _returnParameter = "";

        List<String[]> _parameters = parseParameters(toParse);
        String[] tokens = toParse.split(" ");
        String _methodName = tokens[2];
        String _blockName = tokens[5];

        MdMethodMutation mutation = new MdMethodMutation(_blockName, _methodName);

        boolean _imp = false;

        switch (tokens[6].toUpperCase()) {
            case "TO":
                if (tokens[7].indexOf('(') == -1) _returnParameter = tokens[7];
                mutation.setParameters(_parameters);
                mutation.addReturnParameter(_returnParameter);
                if (tokens[tokens.length - 1].toUpperCase().equals("CODE")) {
                    _imp = (tokens[tokens.length - 2].toUpperCase().equals("WITH"));
                    mutation.setImplementationProvided(_imp);
                }
                break;

            case "WITH":
                _imp = true;
            case "WITHOUT":
                mutation.setImplementationProvided(_imp);
                break;

            default:
                break;
        }
        return mutation;
    }
}
