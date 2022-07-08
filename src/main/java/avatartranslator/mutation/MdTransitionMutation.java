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
 * Class MdTransitionMutation
 * Creation: 27/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 27/06/2022
 */
public class MdTransitionMutation extends TransitionMutation implements MdMutation {
    
    private TransitionMutation current;
    
    public MdTransitionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType) {
        super(_blockName, _fromString, _fromType, _toString, _toType);
        current = new NoneTransitionMutation(_blockName, _fromString, _fromType, _toString, _toType);
    }

    public MdTransitionMutation(String _blockName, String _transitionString, int _transitionType) {
        super(_blockName, _transitionString, _transitionType);
        current = new NoneTransitionMutation(_blockName, _transitionString, _transitionType);
    }

    public MdTransitionMutation(String _blockName, String _currentFromString, int _currentFromType, String _currentToString, int _currentToType, String _newFromString, int _newFromType, String _newToString, int _newToType) {
        super(_blockName, _newFromString, _newFromType, _newToString, _newToType);
        current = new NoneTransitionMutation(_blockName, _currentFromString, _currentFromType, _currentToString, _currentToType);
    }

    public MdTransitionMutation(String _blockName, String _transitionString, int _transitionType, String _newFromString, int _newFromType, String _newToString, int _newToType) {
        super(_blockName, _newFromString, _newFromType, _newToString, _newToType);
        current = new NoneTransitionMutation(_blockName, _transitionString, _transitionType);
    }

    public void setCurrentProbability(String _probability) {
        current.setProbability(_probability);
    }

    public void setCurrentProbablility(double _probability) {
        current.setProbability(_probability);
    }

    public void setCurrentGuard(String _guard) {
        current.setGuard(_guard);
    }

    public void setCurrentDelays(String[] _minMaxDelays) {
        current.setDelays(_minMaxDelays);
    }

    public void setCurrentDelays(String _minDelay, String _maxDelay) {
        current.setDelays(_minDelay, _maxDelay);
    }

    public void setCurrentDelayExtra(String _delayExtra) {
        current.setDelayExtra(_delayExtra);
    }

    public void setCurrentDelayExtras(String _delayExtra1, String _delayExtra2) {
        current.setDelayExtras(_delayExtra1, _delayExtra2);
    }

    public void setCurrentDelayDistributionLaw(String _law) {
        current.setDelayDistributionLaw(_law);
    }

    public void setCurrentDelayDistributionLaw(String _law, String _delayExtra1) {
        current.setDelayDistributionLaw(_law, _delayExtra1);
    }

    public void setCurrentDelayDistributionLaw(String _law, String _delayExtra1, String _delayExtra2) {
        current.setDelayDistributionLaw(_law, _delayExtra1, _delayExtra2);
    }

    public void setCurrentComputes(String[] _minMaxComputes) {
        current.setComputes(_minMaxComputes);
    }

    public void setCurrentComputes(String _minCompute, String _maxCompute) {
        current.setComputes(_minCompute, _maxCompute);
    }

    public void setCurrentActions(String[] _actions) {
        current.setActions(_actions);
    }

    public void addCurrentAction(String _action) {
        current.addAction(_action);
    }

    public void setCurrentNoActions() {
        current.setNoActions();
    }

    @Override
    public AvatarTransition getElement(AvatarSpecification _avspec) {
        try {
            return current.getElement(_avspec);
        } catch (Exception e) {
            return super.getElement(_avspec);
        }
    }

    public void apply(AvatarSpecification _avspec) {
        AvatarTransition trans = current.getElement(_avspec);
        
        if (this.isFromSet()) {
            AvatarStateMachineElement currentFromElement = current.getFromElement(_avspec);
            AvatarStateMachineElement newFromElement = this.getFromElement(_avspec);
            currentFromElement.removeNext(trans);
            newFromElement.addNext(trans);
        }

        if (this.isToSet()) {
            AvatarStateMachineElement newToElement = this.getToElement(_avspec);
            trans.removeAllNexts();
            trans.addNext(newToElement);
        }

        if (this.isProbabilitySet()) {
            trans.setProbability(this.getProbability());
        }
        if (this.isGuardSet()) {
            trans.setGuard(this.getGuard());
        }
        if (this.areDelaysSet()) {
            trans.setDelays(this.getMinDelay(), this.getMaxDelay());
        }
        if (this.isDelayDistributionLawSet()) {
            trans.setDistributionLaw(this.getDelayDistributionLaw(), this.getDelayExtra1(), this.getDelayExtra2());
        }
        if (this.areComputesSet()) {
            trans.setComputes(this.getMinCompute(), this.getMaxCompute());
        }
        if (this.areActionsSet()) {
            trans.removeAllActions();
            List<String> actions = this.getActions();
            for (String action : actions) {
                trans.addAction(action);
            }
        }
    }

    public static MdTransitionMutation createFromString(String toParse) {

        MdTransitionMutation mutation = null;

        String[] tokens = MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "IN");
        String _blockName = tokens[index + 1];
        
        int index0 = MutationParser.indexOf(tokens, "TRANSITION");

        if (MutationParser.isToken(tokens[index0 + 1])) {
            index = MutationParser.indexOf(index, tokens, "FROM");
            String _fromString = tokens[index + 1];
            int _fromType = MutationParser.UUIDType(_fromString);

            index0 = MutationParser.indexOf(tokens, "TO");
            String _toString = tokens[index0 + 1];
            int _toType = MutationParser.UUIDType(_toString);

            mutation = new MdTransitionMutation(_blockName, _fromString, _fromType, _toString, _toType);
        } else {
            String _transitionString = tokens[index0 + 1];
            int _transitionType = MutationParser.UUIDType(_transitionString);
            mutation = new MdTransitionMutation(_blockName, _transitionString, _transitionType);
        }

        if (toParse.contains("[")) {
            String _guard = parseGuard(toParse);
            mutation.setCurrentGuard(_guard);
        }

        if (MutationParser.isTokenIn(tokens, "AFTER")) {
            String[] _minMaxDelay = parseMinMax(toParse);
            mutation.setCurrentDelays(_minMaxDelay);

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

            mutation.setCurrentActions(_actions);
        }

        if (MutationParser.isTokenIn(tokens, "PROBABILITY")) {
            String _probability = parseProbability(toParse);
            mutation.setCurrentProbability(_probability);
        }

        if (MutationParser.isTokenIn(tokens, "COMPUTES")) {
            String[] _minMaxComputes = parseComputes(toParse);
            mutation.setCurrentComputes(_minMaxComputes);
        }

        index0 = MutationParser.indexOf(index0 + 1, tokens, "TO");
        String newParse = MutationParser.concatenate(index0, tokens);

        if (newParse.contains("[")) {
            String _guard = parseGuard(newParse);
            mutation.setGuard(_guard);
        }

        if (MutationParser.isTokenIn(tokens, "AFTER")) {
            String[] _minMaxDelay = parseMinMax(newParse);
            mutation.setDelays(_minMaxDelay);

            String _law = parseLaw(newParse);
            String[] _extras = parseExtras(newParse);

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

        if (newParse.contains("\"")) {
            String[] _actions = parseActions(newParse);

            mutation.setActions(_actions);
        }

        if (MutationParser.isTokenIn(tokens, "PROBABILITY")) {
            String _probability = parseProbability(newParse);
            mutation.setProbability(_probability);
        }

        if (MutationParser.isTokenIn(tokens, "COMPUTES")) {
            String[] _minMaxComputes = parseComputes(newParse);
            mutation.setComputes(_minMaxComputes);
        }

        return mutation;
    }
}
