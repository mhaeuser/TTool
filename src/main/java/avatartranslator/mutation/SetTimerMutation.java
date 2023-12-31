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
 * Class SetTimerMutation
 * Creation: 28/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 28/06/2022
 */
public abstract class SetTimerMutation extends TimerOperatorMutation {

    private String timerValue;

    protected SetTimerMutation(String _blockName, String _timerName, String _timerValue) {
        super(_blockName, _timerName);
        timerValue = _timerValue;
    }

    protected SetTimerMutation(String _blockName, String _name, int _nameType) {
        super(_blockName, _name, _nameType);
    }

    protected SetTimerMutation(String _blockName, String _name, int _nameType, String _timerValue) {
        super(_blockName, _name, _nameType);
        timerValue = _timerValue;
    }

    protected SetTimerMutation(String _blockName, String _name, int _nameType, String _timerName, String _timerValue) {
        super(_blockName, _name, _nameType, _timerName);
        timerValue = _timerValue;
    }

    protected String getTimerValue() {
        return timerValue;
    }

    @Override
    public AvatarSetTimer getElement(AvatarSpecification _avspec) throws ApplyMutationException {
        if (isNameSet())
            return (AvatarSetTimer)super.getElement(_avspec);
        AvatarStateMachine asm = getAvatarStateMachine(_avspec);
        List<AvatarStateMachineElement> elms = asm.getListOfElements();
        for (AvatarStateMachineElement elm : elms) {
            if (elm instanceof AvatarSetTimer) {
                AvatarSetTimer tmp = (AvatarSetTimer)elm;
                if (tmp.getTimer().getName().equals(this.getTimerName()) && tmp.getTimerValue().equals(this.getTimerValue())) {
                    return tmp;
                }
            }
        }
        return null;
    }

    public static SetTimerMutation createFromString(String toParse) throws ParseMutationException {
        switch (MutationParser.findMutationToken(toParse)) {
        case "ADD":
            return AddSetTimerMutation.createFromString(toParse);
        case "RM":
        case "REMOVE":
            return RmSetTimerMutation.createFromString(toParse);
        case "MD":
        case "MODIFY":
            return MdSetTimerMutation.createFromString(toParse);
        default:
            break;
        }
        return null;
    }
}
