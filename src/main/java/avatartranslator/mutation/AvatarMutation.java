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
 * Class AvatarMutation
 * Creation: 23/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 23/06/2022
 */
public abstract class AvatarMutation {

    protected AvatarMutation() {
        super();
    }

    protected AvatarBlock getBlock(AvatarSpecification _avspec, String _blockName) {
        return _avspec.getBlockWithName(_blockName);
    }

    public abstract void apply(AvatarSpecification _avspec);

    public static AvatarMutation createFromString(String toParse) {
        toParse = toParse.trim();
        String[] tokens = toParse.toUpperCase().split(" ");
        if(tokens.length < 2) return null;
        switch (tokens[0]) {
            case "ATTACH":
                return AttachParentMutation.createFromString(toParse);
            case "DETACH":
                return DetachParentMutation.createFromString(toParse);
            case "NAME":
                return NameMutation.createFromString(toParse);
            default:
                break;
        }
        switch (tokens[1]) {
            case "BLOCK":
                return BlockElementMutation.createFromString(toParse);
            case "ATTRIBUTE":
                return AttributeMutation.createFromString(toParse);
            case "METHOD":
                return MethodMutation.createFromString(toParse);
            case "SIGNAL":
            case "INPUT":
            case "OUTPUT":
                return SignalMutation.createFromString(toParse);
            case "STATE":
                return StateMutation.createFromString(toParse);
            case "ACTION":
                if(tokens[2].equals("ON")) return ActionOnSignalMutation.createFromString(toParse);
                return ActionMutation.createFromString(toParse);
            case "RANDOM":
                return RandomMutation.createFromString(toParse);
            case "SET":
                return SetTimerMutation.createFromString(toParse);
            case "RESET":
                return ResetTimerMutation.createFromString(toParse);
            case "EXPIRE":
                return ExpireTimerMutation.createFromString(toParse);
            case "TRANSITION":
                return TransitionMutation.createFromString(toParse);
            default:
                break;
        }
        return null;
    }
}