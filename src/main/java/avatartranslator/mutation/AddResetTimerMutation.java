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

/**
 * Class AddResetTimerMutation
 * Creation: 28/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 28/06/2022
 */
public class AddResetTimerMutation extends ResetTimerMutation implements AddMutation {

    public AddResetTimerMutation(String _blockName, String _timerName) {
        super(_blockName, _timerName);
    }

    public AddResetTimerMutation(String _blockName, String _name, String _timerName) {
        super(_blockName, _name, NAME_TYPE, _timerName);
    }

    public AvatarResetTimer createElement(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarResetTimer elt = new AvatarResetTimer(getName(), null, getAvatarStateMachine(_avspec).getOwner());
        AvatarAttribute timer = getAttribute(_avspec, getTimerName());
        if (timer == null) {
            throw new ApplyMutationException("No timer named " + getTimerName() + " in block " + getBlockName());
        }
        elt.setTimer(timer);
        return elt;
    }

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarResetTimer elt = createElement(_avspec);
        AvatarStateMachine asm = getAvatarStateMachine(_avspec);
        asm.addElement(elt);
    }

    public static AddResetTimerMutation createFromString(String toParse) throws ParseMutationException {

        AddResetTimerMutation mutation = null;
        String[] tokens = MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "IN");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("block name", "in blockName");
        }
        String _blockName = tokens[index + 1];

        index = MutationParser.indexOf(tokens, "WITH");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("timer name", "with timerName");
        }
        String _timerName = tokens[index + 1];

        index = MutationParser.indexOf(tokens, "TIMER");
        if (tokens.length > index + 1 && MutationParser.isToken(tokens[index+1])) {
            mutation = new AddResetTimerMutation(_blockName, _timerName);
        } else {
            if (tokens.length == index + 1) {
                throw new ParseMutationException("reset timer name", "reset timer resetTimerName");
            }
            String _name = tokens[index + 1];
            mutation = new AddResetTimerMutation(_blockName, _name, _timerName);
        }

        return mutation;
    }

}
