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
import java.util.List;
import java.util.LinkedList;

/**
 * Class MethodMutation
 * Creation: 24/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 24/06/2022
 */

public abstract class MethodMutation extends BlockElementMutation {

    protected MethodMutation(String _blockName, String _methodName, List<String> _returnParameters, List<String[]> _parameters, boolean _imp) {
        this(_blockName, _methodName, _imp);
        setParameters(_parameters);
        setReturnParameters(_returnParameters);
    }

    protected MethodMutation(String _blockName, String _methodName, List<String> _returnParameters, List<String[]> _parameters) {
        this(_blockName, _methodName, _returnParameters, _parameters, false);
    }

    protected MethodMutation(String _blockName, String _methodName, boolean _imp) {
        super(_blockName);
        setMethodName(_methodName);
        initParameters();
        setImplementationProvided(_imp);
    }

    protected MethodMutation(String _blockName, String _methodName) {
        this(_blockName, _methodName, false);
    }

    private String methodName;

    private boolean implementationProvided;

    protected List<String> returnParameters;

    protected List<String[]> parameters;

    private void setMethodName(String _methodName) {
        methodName = _methodName;
    }

    protected String getMethodName() {
        return methodName;
    }

    private void initParameters() {
        returnParameters = new LinkedList<>();
        parameters = new LinkedList<>();
    }

    public void setReturnParameters(List<String> _returnParameters) {
        returnParameters = _returnParameters;
    }

    public void addReturnParameter(String _returnParameter) {
        returnParameters.add(_returnParameter);
    }

    protected List<String> getReturnParameters() {
        return returnParameters;
    }

    public void setParameters(List<String[]> _parameters) {
        parameters = _parameters;
    }

    public void addParameter(String[] _parameter) {
        parameters.add(_parameter);
    }

    public void addParameter(String _attributeType, String _attributeName) {
        String[] parameter = { _attributeType, _attributeName};
        addParameter(parameter);
    }

    protected List<String[]> getParameters() {
        return parameters;
    }

    protected void setImplementationProvided(boolean _imp) {
        implementationProvided = _imp;
    }

    protected boolean isImplementationProvided() {
        return implementationProvided;
    }

    public AvatarMethod getElement(AvatarSpecification _avspec) {
        return getMethod(_avspec, getMethodName());
    }

    protected static List<String> parseReturnParameters(String toParse, String token) {
        List<String> output = new LinkedList<>();
        String[] tokens =  MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, token);
        index++;
        if (tokens[index].equals("(")) {
            int endIndex = MutationParser.indexOf(index, tokens, ")");
            for (int i = index + 1; i < endIndex ; i += 2) {
                output.add(tokens[i]);
            }
        } else {
            output.add(tokens[index]);
        }
        return output;
    }

    protected static List<String[]> parseParameters(String toParse, int index) {
        List<String[]> output = new LinkedList<>();
        String[] tokens =  MutationParser.tokenise(toParse);

        index = MutationParser.indexOf(index, tokens, "(");

        if (toParse.indexOf('(') == -1) return output;

        int endIndex = MutationParser.indexOf(index, tokens, ")");
        
        for(int i = index + 1; i < endIndex; i += 3) {
            String[] tmp = {tokens[i], tokens[i+1]};
            output.add(tmp.clone());
        }

        return output;
    }



    protected static List<String[]> parseParameters(String toParse, String token) {
        
        String[] tokens =  MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, token);
        return parseParameters(toParse, index+1);
    }

    protected static String parseMethodName(String toParse) {

        String[] tokens =  MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "METHOD");
        index = MutationParser.indexOf(index+2, tokens, "(");

        return tokens[index - 1];

    }

    public static MethodMutation createFromString(String toParse) {
        switch (MutationParser.findMutationToken(toParse)) {
            case "ADD":
                return AddMethodMutation.createFromString(toParse);
            case "RM":
            case "REMOVE":
                return RmMethodMutation.createFromString(toParse);
            case "MD":
            case "MODIFY":
                return MdMethodMutation.createFromString(toParse);
            default:
                break;
        }
        return null;
    }
}