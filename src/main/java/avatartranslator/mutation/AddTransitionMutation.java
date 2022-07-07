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

/**
 * Class AddTransitionMutation
 * Creation: 27/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 27/06/2022
 */

public class AddTransitionMutation extends TransitionMutation implements AddMutation {

    boolean isGraphical = false;

    public AddTransitionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType) {
        super(_blockName, _fromString, _fromType, _toString, _toType);
    }

    public AddTransitionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType, String _transitionName) {
        super(_blockName, _fromString, _fromType, _toString, _toType, _transitionName, NAME_TYPE);
    }
    
    //todo : add Graphical referenceObject
    public AvatarTransition createElement(AvatarSpecification _avspec) {
        AvatarBlock block = getBlock(_avspec);
        AvatarTransition trans = new AvatarTransition(block, getName(), null);
        if (isProbabilitySet()) {
            trans.setProbability(getProbability());
        }
        if (isGuardSet()) {
            trans.setGuard(getGuard());
        }
        if (areDelaysSet()) {
            trans.setDelays(getMinDelay(), getMaxDelay());
        }
        if (isDelayDistributionLawSet()) {
            trans.setDistributionLaw(getDelayDistributionLaw(), getDelayExtra1(), getDelayExtra2());
        }
        if (areComputesSet()) {
            trans.setComputes(getMinCompute(), getMaxCompute());
        }
        List<String> actions = getActions();
        for (String action : actions) {
            trans.addAction(action);
        }
        return trans;
    }

    public void apply(AvatarSpecification _avspec) {
        AvatarStateMachine asm = getAvatarStateMachine(_avspec);
        AvatarTransition trans = createElement(_avspec);
        AvatarStateMachineElement fromElement = getFromElement(_avspec);
        AvatarStateMachineElement toElement = getToElement(_avspec);

        asm.addElement(trans);
        fromElement.addNext(trans);
        trans.addNext(toElement);
    }

    public static AddTransitionMutation createFromString(String toParse) {

        AddTransitionMutation mutation = null;

        String[] tokens = MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "BLOCK");

        String _blockName = tokens[index + 1];

        index = MutationParser.indexOf(tokens, "FROM");
        String _fromString = tokens[index + 1];
        int _fromType = MutationParser.isUUID(_fromString) ? UUID_TYPE : NAME_TYPE;

        index = MutationParser.indexOf(tokens, "TO");
        String _toString = tokens[index + 1];
        int _toType = MutationParser.isUUID(_toString) ? UUID_TYPE : NAME_TYPE;


        index = MutationParser.indexOf(tokens, "TRANSITION");
        if (index + 1 != MutationParser.indexOf(tokens, "FROM")) {
            String _transitionName = tokens[index + 1];
            mutation = new AddTransitionMutation(_blockName, _fromString, _fromType, _toString, _toType, _transitionName);
        } else {
            mutation = new AddTransitionMutation(_blockName, _fromString, _fromType, _toString, _toType);
        }

        if (toParse.contains("[")) {
            String _guard = parseGuard(toParse);
            mutation.setGuard(_guard);
        }

        if (MutationParser.isTokenIn(tokens, "AFTER")) {
            String[] _minMaxDelay = parseMinMax(toParse);
            mutation.setDelays(_minMaxDelay);

            String _law = parseLaw(toParse);
            String[] _extras = parseExtras(toParse);

            switch (_extras.length) {
                case 0:
                    mutation.setDelayDistributionLaw(_law);
                    break;
                case 1:
                    mutation.setDelayDistributionLaw(_law, _extras[0]);
                    break;
                default:
                    mutation.setDelayDistributionLaw(_law, _extras[0], _extras[1]);
            }
        }

        if (toParse.contains("\"")) {
            String[] _actions = parseActions(toParse);

            mutation.setActions(_actions);
        }

        if (MutationParser.isTokenIn(tokens, "PROBABILITY")) {
            String _probability = parseProbability(toParse);
            mutation.setProbability(_probability);
        }

        if (MutationParser.isTokenIn(tokens, "COMPUTES")) {
            String[] _minMaxComputes = parseComputes(toParse);
            mutation.setComputes(_minMaxComputes);
        }
        
        return mutation;
    }

}
