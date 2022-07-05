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
//import myutil.TraceManager;

import java.util.List;
import java.util.UUID;

/**
 * Class OperatorMutation
 * Creation: 30/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 30/06/2022
 */

public abstract class UnnamedStateMachineElementMutation extends StateMachineElementMutation implements UnnamedElementMutation {

    protected UnnamedStateMachineElementMutation(String _blockName) {
        super(_blockName);
    }

    protected UnnamedStateMachineElementMutation(String _blockName, String _name) {
        super(_blockName);
        setName(_name);
    }

    protected UnnamedStateMachineElementMutation(String _blockName, String _name, int _nameType) {
        super(_blockName);
        setName(_name, _nameType);
    }

    private String name = "";
    private int nameType = UNDEFINED_TYPE;

    protected boolean isUUID(String _name) {
        try {
            UUID.fromString(_name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected int UUIDType(String _name) {
        if(isUUID(_name)) {
            return UUID_TYPE;
        }
        return NAME_TYPE;
    }

    protected String getName() {
        return name;
    }

    protected boolean isNameSet() {
        return nameType != UNDEFINED_TYPE;
    }

    protected int getNameType() {
        return nameType;
    }

    private void setName(String _name) {
        name = _name;
        nameType = UUIDType(_name);
    }

    private void setName(String _name, int _nameType) {
        setName(_name);
        nameType = _nameType;
    }

    private AvatarStateMachineElement getElementFromName(AvatarSpecification _avspec, String _name) {
        AvatarStateMachine asm = getAvatarStateMachine(_avspec);
        List<AvatarStateMachineElement> elts = asm.getListOfElements();
        for (AvatarStateMachineElement elt : elts) {
            //TraceManager.addDev(elt.toString());
            if (elt.getName().equals(_name)) return elt;
        }
        return null;
    }

    private AvatarStateMachineElement getElementFromUUID(AvatarSpecification _avspec, String _uuid) {
        AvatarStateMachine asm = getAvatarStateMachine(_avspec);
        List<AvatarStateMachineElement> elts = asm.getListOfElements();
        for (AvatarStateMachineElement elt : elts) {
            UUID eltUUID = elt.getUUID();
            UUID uuid = UUID.fromString(_uuid);
            if (eltUUID != null) {
                if (eltUUID.equals(uuid)) return elt;
            }
        }
        return null;
    }

    public AvatarStateMachineElement getElement(AvatarSpecification _avspec, int _type, String _name) {
        if (_type == NAME_TYPE) return getElementFromName(_avspec, _name);
        if (_type == UUID_TYPE) return getElementFromUUID(_avspec, _name);
        return null;
    }

    public AvatarStateMachineElement getElement(AvatarSpecification _avspec) {
        return getElement(_avspec, getNameType(), getName());
    }

}