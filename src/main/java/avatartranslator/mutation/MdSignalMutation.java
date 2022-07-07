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

import myutil.TraceManager;

/**
 * Class MdSignalMutation
 * Creation: 24/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 24/06/2022
 */
public class MdSignalMutation extends SignalMutation implements MdMutation {

    private boolean inoutChanged = false;
    private boolean parametersChanged = false;

    public MdSignalMutation(String _blockName, String _signalName) {
        super(_blockName, _signalName);
    }

    public MdSignalMutation(String _blockName, String _signalName, int _inout) {
        this(_blockName, _signalName);
        setInOut(_inout);
    }

    @Override
    public void setParameters(List<String[]> _parameters) {
        parametersChanged = true;
        super.setParameters(_parameters);
    }


    @Override
    public void addParameter(String[] _parameter) {
        parametersChanged = true;
        super.addParameter(_parameter);
    }

    @Override
    protected void setInOut(int _inout) {
        inoutChanged = true;
        super.setInOut(_inout);
    }

    public void apply(AvatarSpecification _avspec) {
        
        AvatarBlock block = getBlock(_avspec);
        AvatarSignal as = getElement(_avspec);

        if (as == null) {
            TraceManager.addDev("Unknown Signal");
            return;
        }

        List<AvatarSignal> sign = block.getSignals();
        if(!sign.contains(as)) {
            TraceManager.addDev("Signal is from a super-bloc");
            return;

        }

        if (parametersChanged) {
            as.removeAttributes();
            for (String[] s : getParameters()) {
                AvatarAttribute aa = new AvatarAttribute(s[1], AvatarType.getType(s[0]), block, null);
                as.addParameter(aa);
            }
        }

        if (inoutChanged) {
            as.setInOut(getInOut());
        }
    }

    public static MdSignalMutation createFromString(String toParse) {
        MdSignalMutation mutation;

        String[] tokens = toParse.split(" ");
        String _signalName = tokens[2];
        String _blockName = tokens[5];

        if (tokens[6].toUpperCase().equals("TO")) {
            int _inout = 0;
            switch (tokens[7].toUpperCase()) {
                case "IN":
                case "INPUT":
                    _inout = AvatarSignal.IN;
                    break;
                case "OUT":
                case "OUTPUT":
                    _inout = AvatarSignal.OUT;
                    break;
                default:
                    break;
            }
            mutation = new MdSignalMutation(_blockName, _signalName, _inout);
        } else {
            mutation = new MdSignalMutation(_blockName, _signalName);
        }
        if (tokens[tokens.length -1 ].contains(")")) {
            mutation.setParameters(parseParameters(toParse));
        }
        return mutation;
    }
    
}
