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
 * Class RmActionMutation
 * Creation: 28/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 28/06/2022
 */

public class RmActionMutation extends ActionMutation implements RmMutation {

    public RmActionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType, String _actionString) {
        super(_blockName, _fromString, _fromType, _toString, _toType, _actionString);
    }

    public RmActionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType, int _index) {
        super(_blockName, _fromString, _fromType, _toString, _toType, _index);
    }

    public RmActionMutation(String _blockName, String _transitionString, int _transitionType, String _actionString) {
        super(_blockName, _transitionString, _transitionType, _actionString);
    }

    public RmActionMutation(String _blockName, String _transitionString, int _transitionType, int _index) {
        super(_blockName, _transitionString, _transitionType, _index);
    }

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarTransition transition = getElement(_avspec);

        List<AvatarAction> actions = transition.getActions();

        if(getIndex() != -1) {
            actions.remove(getIndex());
            return;
        }

        AvatarAction action = createAction(_avspec);
        for(AvatarAction tmp_action : actions) {
            if(action.toString().equals(tmp_action.toString())) {
                actions.remove(tmp_action);
                return;
            }
        }
    }

    public static RmActionMutation createFromString(String toParse) throws ParseMutationException {

        RmActionMutation mutation = null;

        String[] tokens = MutationParser.tokenise(toParse);

        String _fromString = null;
        int _fromType = -1;

        String _toString = null;
        int _toType = -1;

        String _transitionString = null;
        int _transitionType = -1;

        int _index = -1;

        int index = MutationParser.indexOf(tokens, "IN");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("block name", "in blockName");
        }
        String _blockName = tokens[index + 1];

        index = MutationParser.indexOf(tokens, "FROM");
        if (index != -1) {
            _fromString = tokens[index + 1];
            _fromType = MutationParser.UUIDType(_fromString);

            index = MutationParser.indexOf(tokens, "TO");
            if (index == -1 || tokens.length == index+1) {
                throw new ParseMutationException("to element name", "to toElementName");
            }
            _toString = tokens[index + 1];
            _toType = MutationParser.UUIDType(_toString);
        } else {
            index = MutationParser.indexOf(tokens, "TRANSITION");
            if (tokens.length == index + 1) {
                throw new ParseMutationException("transition description", "transition transitionName] or [from fromElement to toElement");
            }
            _transitionString = tokens[index + 1];
            _transitionType = MutationParser.UUIDType(_transitionString);
        }

        index = MutationParser.indexOf(tokens, "AT");
        if (index == -1 || tokens.length == index + 1) {
            throw new ParseMutationException("action index", "at actionIndex");
        }
        _index = Integer.parseInt(tokens[index + 1]);

        if (_transitionString == null) {
            mutation = new RmActionMutation(_blockName, _fromString, _fromType, _toString, _toType, _index);
        } else {
            mutation = new RmActionMutation(_blockName, _transitionString, _transitionType, _index);
        }

        return mutation;
    }
}