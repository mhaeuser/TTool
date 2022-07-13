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
 * Class ConnectionMutation
 * Creation: 29/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 29/06/2022
 */
public abstract class ConnectionMutation extends RelationMutation {

    protected ConnectionMutation(String _block1, String _block2, String _signal1, String _signal2) {
        super(_block1, _block2);
        setSignals(_signal1, _signal2);
    }

    protected ConnectionMutation(String _relationString, int _relationType, String _signal1, String _signal2) {
        super(_relationString, _relationType);
        setSignals(_signal1, _signal2);
    }

    private String signal1;
    private String signal2;

    protected AvatarSignal getSignal(AvatarSpecification _avspec, String _signal) throws ApplyMutationException {
        AvatarBlock block = getBlock1(_avspec);
        AvatarSignal signal = block.getSignalByName(_signal);
        if (signal == null) {
            block = getBlock2(_avspec);
            signal = block.getSignalByName(_signal);
        }
        if (signal == null) {
            throw new ApplyMutationException("no signal named " + _signal + " in blocks " + getBlock1() + " and " + getBlock2());
        }
        return signal;
    }

    protected String getSignal1() {
        return signal1;
    }

    protected AvatarSignal getSignal1(AvatarSpecification _avspec) throws ApplyMutationException {
        return getSignal(_avspec, getSignal1());
    }

    protected String getSignal2() {
        return signal2;
    }

    protected AvatarSignal getSignal2(AvatarSpecification _avspec) throws ApplyMutationException {
        return getSignal(_avspec, getSignal2());
    }

    private void setSignals(String _signal1, String _signal2) {
        signal1 = _signal1;
        signal2 = _signal2;
    }

    public static ConnectionMutation createFromString(String toParse) throws ParseMutationException {
        switch (MutationParser.findMutationToken(toParse)) {
            case "ADD":
                return AddConnectionMutation.createFromString(toParse);
            case "RM":
            case "REMOVE":
                return RmConnectionMutation.createFromString(toParse);
            default:
                break;
        }
        return null;
    }

}