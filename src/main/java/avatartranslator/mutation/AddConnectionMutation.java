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
 * Class AddAssociationMutation
 * Creation: 29/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 29/06/2022
 */
public class AddConnectionMutation extends ConnectionMutation implements AddMutation{

    public AddConnectionMutation(String _block1, String _block2, String _signal1, String _signal2) {
        super(_block1, _block2, _signal1, _signal2);
    }

    public AddConnectionMutation(String _relationString, int _relationType, String _signal1, String _signal2) {
        super(_relationString, _relationType, _signal1, _signal2);
    }

    public AvatarElement createElement(AvatarSpecification _avspec) {
        return null;
    }

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarRelation relation = getElement(_avspec);
        if (relation == null) {
            throw new ApplyMutationException("no such relation");
        }
        if (relation.getBlock1() == getBlock2(_avspec) && relation.getBlock2() == getBlock1(_avspec)){
            relation.addSignals(getSignal2(_avspec), getSignal1(_avspec));
        }
        else {
            relation.addSignals(getSignal1(_avspec), getSignal2(_avspec));
        }
    }

    public static AddConnectionMutation createFromString(String toParse) throws ParseMutationException {

        AddConnectionMutation mutation = null;
        String[] tokens = MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "LINK");
        if (index != -1 && index + 1 < tokens.length && !MutationParser.isToken(tokens[index + 1])) {
            String _relationString = tokens[index + 1];
            int _relationType = MutationParser.UUIDType(_relationString);
            index = MutationParser.indexOf(tokens, "FROM");
            if (tokens.length == index + 1 || index == -1) {
                throw new ParseMutationException("from output signal name", "from fromOutputSignalName");
            }
            String _signal1 = tokens[index + 1];
            index = MutationParser.indexOf(tokens, "TO");
            if (tokens.length == index + 1 || index == -1) {
                throw new ParseMutationException("to input signal name", "to toInputSignalName");
            }
            String _signal2 = tokens[index + 1];
            mutation = new AddConnectionMutation(_relationString, _relationType, _signal1, _signal2);
            return mutation;
        }
        
        index = MutationParser.indexOf(tokens, "FROM");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("from output signal name", "from fromOutputSignalName in fromBlockName");
        }
        String _signal1 = tokens[index + 1];
        index = MutationParser.indexOf(index, tokens, "IN");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("from block name", "from fromOutputSignalName in fromBlockName");
        }
        String _block1 = tokens[index + 1];
        index = MutationParser.indexOf(index, tokens, "TO");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("to input signal name", "to toInputSignalName in toBlockName");
        }
        String _signal2 = tokens[index + 1];
        index = MutationParser.indexOf(index, tokens, "IN");
        if (tokens.length == index + 1 || index == -1) {
            throw new ParseMutationException("to block name", "to toInputSignalName in toBlockName");
        }
        String _block2 = tokens[index + 1];

        mutation = new AddConnectionMutation(_block1, _block2, _signal1, _signal2);

        boolean b;

        switch (MutationParser.findPublicToken(tokens)) {
            case "PUBLIC":
                mutation.setPrivate(false);
                break;
            case "PRIVATE":
                mutation.setPrivate(true);
                break;
            default:
                break;
        }

        switch (MutationParser.findSynchToken(tokens)) {
            case "SYNCHRONOUS":
            case "SYNCH":
                mutation.setAsynchronous(false);
                break;
            case "ASYNCHRONOUS":
            case "ASYNCH":
                mutation.setAsynchronous(true);
                break;
            default:
                break;
        }

        if (MutationParser.isTokenIn(tokens, "AMS")) {
            b = !tokens[index - 1].equals("NO");
            mutation.setAMS(b);
        }

        if (MutationParser.isTokenIn(tokens, "LOSSY")) {
            b = !tokens[index - 1].equals("NO");
            mutation.setLossy(b);
        }

        if (MutationParser.isTokenIn(tokens, "BLOCKING")) {
            b = !tokens[index - 1].equals("NO");
            mutation.setBlocking(b);
        }

        if (MutationParser.isTokenIn(tokens, "BROADCAST")) {
            b = !tokens[index - 1].equals("NO");
            mutation.setBroadcast(b);
        }

        index = MutationParser.indexOf(tokens, "MAXFIFO");
        if (index != -1 && tokens.length > index + 2) {
            mutation.setSizeOfFIFO(tokens[index + 2]);
        }

        return mutation;
    }

}