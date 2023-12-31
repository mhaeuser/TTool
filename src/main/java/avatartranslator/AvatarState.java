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

package avatartranslator;

/**
 * Class AvatarState
 * Creation: 20/05/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 20/05/2010
 */
public class AvatarState extends AvatarStateElement {
    private String entryCode;
    private boolean hidden; // i.e, not present in the original state machine
    public int stateID; // to be used by code generator to more efficiently keep track of states.
    private boolean checkLatency;
    private boolean commit = false; // Not used currently

    public AvatarState(String _name, Object _referenceObject, AvatarStateMachineOwner _block) {
        super(_name, _referenceObject, _block);

        //TraceManager.addDev("CREATING state with name = "+ _name);
    }

    public AvatarState(String _name, Object _referenceObject, AvatarStateMachineOwner _block, boolean _isCheckable, boolean _isChecked) {
        super(_name, _referenceObject, _block, _isCheckable, _isChecked);
        //TraceManager.addDev("CREATING state with name = "+ _name);
    }

    public AvatarStateMachineElement basicCloneMe(AvatarStateMachineOwner _block) {
        AvatarState as = new AvatarState(getName(), getReferenceObject(), _block, isCheckable(), isChecked());
        as.setAsVerifiable(canBeVerified());
        as.addEntryCode(getEntryCode());
        return as;
    }

    public void setHidden(boolean _hidden) {
        hidden = _hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setCommit(boolean _commit) {
        commit = _commit;
    }

    public boolean isCommit() {
        return commit;
    }

    public String getNiceName() {
        return "State " + getName();
    }

    public boolean getCheckLatency() {
        return checkLatency;
    }

    public void setCheckLatency(boolean b) {
        checkLatency = b;
    }

    public int hasEmptyTransitionsOnItself(AvatarStateMachine _asm) {
        AvatarTransition at;
        int cpt = 0;

        for (AvatarStateMachineElement asme : nexts) {
            if (asme instanceof AvatarTransition) {
                at = (AvatarTransition) asme;
                if (at.isEmpty()) {
                    if (at.getNext(0) == this) {
                        cpt++;
                    }
                }
            }
        }

        return cpt;
    }

    public void addEntryCode(String _code) {
        if (_code == null) {
            return;
        }
        if (entryCode == null) {
            entryCode = _code;
            return;
        }
        entryCode += _code + "\n";
    }

    public String getEntryCode() {
        if (entryCode == null) {
            return "";
        }
        return entryCode;
    }

    public void translate(AvatarTranslator translator, Object arg) {
        translator.translateState(this, arg);
    }
}
