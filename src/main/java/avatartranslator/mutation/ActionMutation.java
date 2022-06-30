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
 * Class ActionMutation
 * Creation: 28/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 28/06/2022
 */

public abstract class ActionMutation extends TransitionMutation {

    public ActionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType, String _actionString) {
        super(_blockName, _fromString, _fromType, _toString, _toType);
        setActionString(_actionString);
    }

    public ActionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType, int _index) {
        super(_blockName, _fromString, _fromType, _toString, _toType);
        setIndex(_index);
    }

    public ActionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType, String _actionString, int _index) {
        this(_blockName, _fromString, _fromType, _toString, _toType, _actionString);
        setIndex(_index);
    }

    public ActionMutation(String _blockName, String _transitionString, int _transitionType, String _actionString) {
        super(_blockName, _transitionString, _transitionType);
        setActionString(_actionString);
    }

    public ActionMutation(String _blockName, String _transitionString, int _transitionType, int _index) {
        super(_blockName, _transitionString, _transitionType);
        setIndex(_index);
    }

    public ActionMutation(String _blockName, String _transitionString, int _transitionType, String _actionString, int _index) {
        this(_blockName, _transitionString, _transitionType, _actionString);
        setIndex(_index);
    }

    private String actionString;

    private int index = -1;

    protected String getActionString() {
        return actionString;
    }

    protected int getIndex() {
        return index;
    }

    protected int getIndex(AvatarSpecification _avspec) {
        if (index == -1)
            index = getIndexFromString(_avspec);
        return index;
    }

    protected int getIndexFromString(AvatarSpecification _avspec) {
        AvatarTransition trans = super.getElement(_avspec);
        AvatarBlock block = getBlock(_avspec);
        int len = trans.getNbOfAction();
        for (int i = 0; i < len; i++) {
            if (trans.getAction(i).toString().equals(AvatarTerm.createFromString(block, getActionString()).toString())) {
                return i;
            }
        }
        return -1;
    }

    private void setActionString(String _actionString) {
        actionString = _actionString;
    }

    private void setIndex(int _index) {
        index = _index;
    }

    public AvatarAction createAction(AvatarSpecification _avspec) {
        AvatarBlock block = getBlock(_avspec);
        AvatarAction action = AvatarTerm.createActionFromString(block, getActionString());
        return action;
    }
    
}
