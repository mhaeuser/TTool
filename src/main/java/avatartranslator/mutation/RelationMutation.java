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
 * Class RelationMutation
 * Creation: 29/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 29/06/2022
 */
public abstract class RelationMutation extends AvatarMutation implements UnnamedElementMutation {

    private String name = "";
    private int nameType = UNDEFINED_TYPE;
    
    private String block1, block2;

    private boolean blocking = false, asynchronous = false, ams = false, isPrivate, isBroadcast = false, isLossy;
    private boolean blockingSet = false, asynchronousSet = false, amsSet = false, isPrivateSet = false, isBroadcastSet = false, isLossySet = false;

    private int sizeOfFIFO = 1024; // -1 means infinite
    private boolean sizeOfFIFOSet = false;

    private int id = 0;//DG
    private boolean idSet = false;

    public String getName() {
        return name;
    }

    public boolean isNameSet() {
        return nameType != UNDEFINED_TYPE;
    }

    public int getNameType() {
        return nameType;
    }

    public void setName(String _name) {
        name = _name;
        nameType = NAME_TYPE;
    }

    public void setUUID(String _uuid) {
        name = _uuid;
        nameType = UUID_TYPE;
    }

    public String getBlock1() {
        return block1;
    }

    public AvatarBlock getBlock1(AvatarSpecification _avspec) {
        return getBlock(_avspec, getBlock1());
    }

    public String getBlock2() {
        return block2;
    }

    public AvatarBlock getBlock2(AvatarSpecification _avspec) {
        return getBlock(_avspec, getBlock2());
    }

    public void setBlocks(String _block1, String _block2) {
        block1 = _block1;
        block2 = _block2;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public boolean blockingSet() {
        return blockingSet;
    }

    public void setBlocking(boolean b) {
        blocking = b;
        blockingSet = true;
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public boolean asynchronousSet() {
        return asynchronousSet;
    }

    public void setAsynchronous(boolean b) {
        asynchronous = b;
        asynchronousSet = true;
    }

    public boolean isAMS() {
        return ams;
    }

    public boolean AMSSet() {
        return amsSet;
    }

    public void setAMS(boolean b) {
        ams = b;
        amsSet = true;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean privateSet() {
        return isPrivateSet;
    }

    public void setPrivate(boolean b) {
        isPrivate = b;
        isPrivateSet = true;
    }

    public boolean isBroadcast() {
        return isBroadcast;
    }

    public boolean broadcastSet() {
        return isBroadcastSet;
    }

    public void setBroadcast(boolean b) {
        isBroadcast = b;
        isBroadcastSet = true;
    }

    public boolean isLossy() {
        return isLossy;
    }

    public boolean lossySet() {
        return isLossySet;
    }

    public void setLossy(boolean b) {
        isLossy = b;
        isLossySet = true;
    }

    public int getSizeOfFIFO() {
        return sizeOfFIFO;
    }

    public boolean sizeOfFIFOSet() {
        return sizeOfFIFOSet;
    }

    public void setSizeOfFIFO(int _sizeOfFIFO) {
        sizeOfFIFO = _sizeOfFIFO;
        sizeOfFIFOSet = true;
    }

    public int getId() {
        return id;
    }

    public boolean idSet() {
        return idSet;
    }

    public void setId(int _id) {
        id = _id;
        idSet = true;
    }

    public AvatarRelation getElementFromName(AvatarSpecification _avspec, String _name) {
        List<AvatarRelation> relations = _avspec.getRelations();
        for (AvatarRelation rel : relations) {
            if (rel.getName().equals(_name)) return rel;
        }
        return null;
    }

    public AvatarRelation getElementFromUUID(AvatarSpecification _avspec, String _uuid) {
        List<AvatarRelation> relations = _avspec.getRelations();
        for (AvatarRelation rel : relations) {
            UUID relUUID = rel.getUUID();
            UUID uuid = UUID.fromString(_uuid);
            if (relUUID != null) {
                if (relUUID.equals(uuid)) return rel;
            }
        }
        return null;
    }

    public AvatarRelation getElement(AvatarSpecification _avspec, int _type, String _name) {
        if (_type == NAME_TYPE) return getElementFromName(_avspec, _name);
        if (_type == UUID_TYPE) return getElementFromUUID(_avspec, _name);
        return null;
    }

    public AvatarRelation getElement(AvatarSpecification _avspec) {
        if(!isNameSet()) {
            List<AvatarRelation> relations = _avspec.getRelations();
            for (AvatarRelation relation : relations) {
                boolean flag = relation.getBlock1().getName().equals(this.getBlock1());

                flag = flag && relation.getBlock2().getName().equals(this.getBlock2());

                if (flag && blockingSet()) flag = (relation.isBlocking() == this.isBlocking());

                if (flag && asynchronousSet()) flag = (relation.isAsynchronous() == this.isAsynchronous());

                if (flag && AMSSet()) flag = (relation.isAMS() == this.isAMS());

                if (flag && privateSet()) flag = (relation.isPrivate() == this.isPrivate());

                if (flag && broadcastSet()) flag = (relation.isBroadcast() == this.isBroadcast());

                if (flag && lossySet()) flag = (relation.isLossy() == this.isLossy());

                if (flag && sizeOfFIFOSet()) flag = (relation.getSizeOfFIFO() == this.getSizeOfFIFO());

                if (flag && idSet()) flag = (relation.getId() == this.getId());

                if (flag) return relation;
            }
            return null;
        }
        return getElement(_avspec, getNameType(), getName());
    }
}