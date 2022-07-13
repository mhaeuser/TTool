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
import myutil.TraceManager;

/**
 * Class RmTransitionMutation
 * Creation: 27/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 27/06/2022
 */
public class RmTransitionMutation extends TransitionMutation implements RmMutation {
    
    public RmTransitionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType) {
        super(_blockName, _fromString, _fromType, _toString, _toType);
    }

    public RmTransitionMutation(String _blockName, String _transitionString, int _transitionType) {
        super(_blockName, _transitionString, _transitionType);
    }

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarTransition trans = getElement(_avspec);
        if (trans == null) {
            throw new ApplyMutationException("no such transition in block " + getBlockName());
        }
        AvatarStateMachine asm = getAvatarStateMachine(_avspec);
        AvatarStateMachineElement fromElement = getFromElement(_avspec);
        if (fromElement == null) {
            throw new ApplyMutationException("From element " + getFrom() + " doesn't exist in block " + getBlockName());
        }

        fromElement.removeNext(trans);
        asm.removeElement(trans);
    }

    public static RmTransitionMutation createFromString(String toParse) throws ParseMutationException {
        
        String[] tokens = MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "IN");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("block name", "in blockName");
        }
        String _blockName = tokens[index + 1];
        
        int index0 = MutationParser.indexOf(tokens, "TRANSITION");
        if (tokens.length == index0 + 1) {
            throw new ParseMutationException("transition description", "transition transitionDescription");
        }

        if (!MutationParser.isToken(tokens[index0 + 1])) {
            String _transitionString = tokens[index0 + 1];
            int _transitionType = MutationParser.UUIDType(_transitionString);
            return new RmTransitionMutation(_blockName, _transitionString, _transitionType);
        }

        index = MutationParser.indexOf(index, tokens, "FROM");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("from element name", "from fromElementName");
        }
        String _fromString = tokens[index + 1];
        int _fromType = MutationParser.UUIDType(_fromString);

        index = MutationParser.indexOf(tokens, "TO");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("to element name", "to toElementName");
        }
        String _toString = tokens[index + 1];
        int _toType = MutationParser.UUIDType(_toString);

        RmTransitionMutation mutation = new RmTransitionMutation(_blockName, _fromString, _fromType, _toString, _toType);

        if (toParse.contains("[")) {
            String _guard = parseGuard(toParse);
            mutation.setGuard(_guard);
        }

        if (MutationParser.isTokenIn(tokens, "AFTER")) {
            String[] _minMaxDelay = parseMinMax(toParse);
            mutation.setDelays(_minMaxDelay);

            String _law = parseLaw(toParse);
            String[] _extras = parseExtras(toParse);

            mutation.setDelayDistributionLaw(_law, _extras);
        }

        if (toParse.contains("\"")) {
            String[] _actions = parseActions(toParse);

            mutation.setActions(_actions);
        }

        if (MutationParser.isTokenIn(tokens, "PROBABILITY")) {
            TraceManager.addDev("setCurrentProbability");
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