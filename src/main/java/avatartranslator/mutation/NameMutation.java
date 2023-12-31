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
import java.util.UUID;

import avatartranslator.*;

/**
 * Class NameMutation
 * Creation: 06/07/2022
 *
 * @author Léon FRENOT
 * @version 1.0 06/07/2022
 */

public class NameMutation extends AvatarMutation {

    public NameMutation(String _uuid, String _name, String _blockName) throws ParseMutationException {
        super();

        try {
            uuid = UUID.fromString(_uuid);
        } catch (Exception e) {
            throw new ParseMutationException("uuid is not a valid UUID");
        }
        name = _name;
        blockName = _blockName;
    }

    public NameMutation(String _uuid, String _name) throws ParseMutationException {
        this(_uuid, _name, null);
    }

    private UUID uuid;
    private String name;
    private String blockName;

    public void apply(AvatarSpecification _avspec) throws ApplyMutationException {
        if (blockName == null) {
            List<AvatarRelation> elts = _avspec.getRelations();
            for(AvatarRelation elt : elts) {
                if(elt.getUUID().equals(uuid)) {
                    elt.setName(name);
                    return;
                }
            }
            for (AvatarBlock block : _avspec.getListOfBlocks()) {
                AvatarStateMachine asm = block.getStateMachine();
                List<AvatarStateMachineElement> asmElts = asm.getListOfElements();
                for(AvatarStateMachineElement elt : asmElts) {
                    if(elt.getUUID() != null && elt.getUUID().equals(uuid)) {
                        elt.setName(name);
                        return;
                    }
            }
            }
        } else {
            AvatarBlock block = getBlock(_avspec, blockName);

            if (block == null) {
                throw new MissingBlockException("Block", blockName);
            }

            AvatarStateMachine asm = block.getStateMachine();
            List<AvatarStateMachineElement> elts = asm.getListOfElements();
            for(AvatarStateMachineElement elt : elts) {
                if(elt.getUUID() != null && elt.getUUID().equals(uuid)) {
                    elt.setName(name);
                    return;
                }
            }
        }
    }

    public static NameMutation createFromString(String toParse) throws ParseMutationException {
        String[] tokens = MutationParser.tokenise(toParse);

        if (tokens.length < 3) {
            throw new ParseMutationException("Missing arguments [name uuid name (in blockName)]");
        }
        
        String _uuid = tokens[1];
        String _name = tokens[2];

        int index = MutationParser.indexOf(tokens, "IN");
        if (index != -1) {
            if (tokens.length == index) {
                throw new ParseMutationException("Missing block name [in blockName]");
            }
            String _blockName = tokens[index + 1];
            return new NameMutation(_uuid, _name, _blockName);
        }
        return new NameMutation(_uuid, _name);
    }
}