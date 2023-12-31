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
 * Class MdSetTimerMutation
 * Creation: 28/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 28/06/2022
 */
public class MdSetTimerMutation extends SetTimerMutation implements MdMutation {

    private String newTimerName;
    private boolean newTimerNameSet = false;

    private String newTimerValue;
    private boolean newTimerValueSet = false;
    
    public MdSetTimerMutation(String _blockName, String _timerName, String _timerValue, String _newTimerValue) {
        super(_blockName, _timerName, _timerValue);
        setNewTimerValue(_newTimerValue);
    }

    public MdSetTimerMutation(String _blockName, String _name, int _nameType, String _newTimerValue) {
        super(_blockName, _name, _nameType);
        setNewTimerValue(_newTimerValue);
    }

    public MdSetTimerMutation(String _blockName, String _timerName, String _timerValue, String _newTimerName, String _newTimerValue) {
        super(_blockName, _timerName, _timerValue);
        setNewTimer(_newTimerName, _newTimerValue);
    }

    public MdSetTimerMutation(String _blockName, String _name, int _nameType, String _newTimerName, String _newTimerValue) {
        super(_blockName, _name, _nameType);
        setNewTimer(_newTimerName, _newTimerValue);
    }

    private void setNewTimerValue(String _newTimerValue) {
        //TraceManager.addDev("setNewTimerValue");
        newTimerValue = _newTimerValue;
        newTimerValueSet = true;
    }

    private void setNewTimer(String _newTimerName, String _newTimerValue) {
        setNewTimerValue(_newTimerValue);
        newTimerName = _newTimerName;
        newTimerNameSet = true;
    }

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarSetTimer elt = getElement(_avspec);
        if (elt == null) {
            throw new ApplyMutationException("no such set timer in block " + getBlockName());
        }

        if (newTimerNameSet) {
            AvatarAttribute newTimer = getAttribute(_avspec, newTimerName);
            if (newTimer == null) {
                throw new ApplyMutationException("No timer named " + newTimerName + " in block " + getBlockName());
            }
            elt.setTimer(newTimer);
        }
        
        if (newTimerValueSet) elt.setTimerValue(newTimerValue);
    }

    public static MdSetTimerMutation createFromString(String toParse) throws ParseMutationException {
        MdSetTimerMutation mutation = null;
        String[] tokens = MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "IN");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("block name", "in blockName");
        }
        String _blockName = tokens[index + 1];

        index = MutationParser.indexOf(tokens, "TO");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("new timer name", "to newTimerName at newInitialValue");
        }
        String _newTimerName = tokens[index + 1];

        index = MutationParser.indexOf(index, tokens, "AT");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("new initial value", "to newTimerName at newInitialValue");
        }
        String _newTimerValue = tokens[index + 1];

        index = MutationParser.indexOf(tokens, "TIMER");
        if (tokens.length == index + 1 || MutationParser.isToken(tokens[index+1])) {
            index = MutationParser.indexOf(tokens, "WITH");
            if (tokens.length == index + 1 || index == -1) {
                throw new ParseMutationException("timer name", "with timerName");
            }
            String _timerName = tokens[index + 1];

            index = MutationParser.indexOf(tokens, "AT");
            if (tokens.length == index + 1 || index == -1) {
                throw new ParseMutationException("initial value", "at initialValue");
            }
            String _timerValue = tokens[index + 1];
            mutation = new MdSetTimerMutation(_blockName, _timerName, _timerValue, _newTimerName, _newTimerValue);
        } else {
            if (tokens.length == index + 1) {
                throw new ParseMutationException("set timer name", "set timer setTimerName");
            }
            String _name = tokens[index + 1];
            int _nameType = MutationParser.UUIDType(_name);
            mutation = new MdSetTimerMutation(_blockName, _name, _nameType, _newTimerName, _newTimerValue);
        }
        return mutation;
    }
}
