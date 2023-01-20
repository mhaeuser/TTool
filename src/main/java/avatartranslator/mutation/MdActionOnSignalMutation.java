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
import java.util.ArrayList;

/**
 * Class MdActionOnSignalMutation
 * Creation: 28/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 28/06/2022
 */

public class MdActionOnSignalMutation extends ActionOnSignalMutation implements MdMutation {

    private ActionOnSignalMutation current;

    private boolean signalNameChanged = false;

    public MdActionOnSignalMutation(String _blockName, String _signalName) {
        super(_blockName, _signalName);
        current = new NoneActionOnSignalMutation(_blockName, _signalName);
    }

    public MdActionOnSignalMutation(String _blockName, String _signalName, String _newSignalName) {
        super(_blockName, _newSignalName);
        current = new NoneActionOnSignalMutation(_blockName, _signalName);
        signalNameChanged = true;
    }
    
    public MdActionOnSignalMutation(String _blockName, String _name, int _nameType) {
        super(_blockName, _name, _nameType);
        current = new NoneActionOnSignalMutation(_blockName, _name, _nameType);
    }

    public MdActionOnSignalMutation(String _blockName, String _name, int _nameType, String _newSignalName) {
        super(_blockName, _newSignalName);
        current = new NoneActionOnSignalMutation(_blockName, _name, _nameType);
        signalNameChanged = true;
    }

    public void noCurrentValues() {
        current.noValues();
    }

    public void addCurrentValue(String _value) {
        current.addValue(_value);
    }

    public void setCurrentCheckLatency(boolean b) {
        current.setCheckLatency(b);
    }

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarActionOnSignal aaos = current.getElement(_avspec);

        if(aaos == null) {
            throw new ApplyMutationException("no such action on signal in block " + getBlockName());
        }

        if(signalNameChanged) {
            AvatarSignal signal = getSignal(_avspec, this.getSignalName());
            if (signal == null) {
                throw new ApplyMutationException("no signal named " + getSignalName() + " in block " + getBlockName());
            }
            aaos.setSignal(signal);
        }

        if (isCheckLatencySet())
            aaos.setCheckLatency(this.getCheckLatency());
        
        if (areValuesSet()) {
            aaos.removeAllValues();
            for (String s : this.getValues())
                aaos.addValue(s);
        }
    }

    public static MdActionOnSignalMutation createFromString(String toParse) throws ParseMutationException {

        MdActionOnSignalMutation mutation = null;
        String[] tokens = MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "IN");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("block name", "in blockName");
        }
        String _blockName = tokens[index + 1];

        String _name = null;
        int _nameType = -1;

        String _signalName = null;

        String _newSignalName = null;

        String[] _values = null;

        index = MutationParser.indexOf(tokens, "SIGNAL");
        if (tokens.length > index + 1 && !MutationParser.isToken(tokens[index + 1])) {
            _name = tokens[index + 1];
            _nameType = MutationParser.UUIDType(_name);
        } else {
            index = MutationParser.indexOf(tokens, "WITH");
            _signalName = tokens[index + 1];
        }

        index = MutationParser.indexOf(tokens, "TO");
        if (tokens.length > index + 1 && !MutationParser.isToken(tokens[index + 1])) {
            _newSignalName = tokens[index + 1];
            _values = parseValues(toParse.substring(toParse.toLowerCase().indexOf(" to ")));

            if (_name == null) {
                mutation = new MdActionOnSignalMutation(_blockName, _signalName, _newSignalName);
            } else {
                mutation = new MdActionOnSignalMutation(_blockName, _name, _nameType, _newSignalName);
            }
            //Provisional workaround for parsing actions on signal with several parameters
            int i = 0;
            ArrayList<String> buffer = new ArrayList<String>();
            for (String value : _values){
                if (i%2 == 0){
                    buffer.add(value);
                }
                i++;
            }
        mutation.setValues(buffer.toArray(new String[buffer.size()]));

        } else {
            if (_name == null) {
                mutation = new MdActionOnSignalMutation(_blockName, _signalName);
            } else {
                mutation = new MdActionOnSignalMutation(_blockName, _name, _nameType);
            }
        }

        int index0 = MutationParser.indexOf(tokens, "CHECK");
        if (tokens.length > index + 1 && index0 != -1) {
            if (index0 < index) {
                if (tokens[index0 - 1].toUpperCase().equals("NO")) {
                    mutation.setCurrentCheckLatency(false);
                } else {
                    mutation.setCurrentCheckLatency(true);
                }
                index0 = MutationParser.indexOf(index0 + 1, tokens, "CHECK");
                if (index0 != -1) {
                    if (tokens[index0 - 1].toUpperCase().equals("NO")) {
                        mutation.setCheckLatency(false);
                    } else {
                        mutation.setCheckLatency(true);
                    }
                }
            } else {
                if (tokens[index0 - 1].toUpperCase().equals("NO")) {
                    mutation.setCheckLatency(false);
                } else {
                    mutation.setCheckLatency(true);
                }
            }
        }
        return mutation;        
    }
}
