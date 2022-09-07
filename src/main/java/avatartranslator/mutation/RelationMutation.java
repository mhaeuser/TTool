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

    protected RelationMutation(String _block1, String _block2) {
        super();
        setBlocks(_block1, _block2);
    }
    protected RelationMutation(String _block1, String _block2, String _name) {
        this(_block1, _block2);
        setName(_name, NAME_TYPE);
    }

    protected RelationMutation(String _relationString, int _relationType) {
        super();
        setName(_relationString, _relationType);
    }

    private String relationString = "";
    private int relationStringType = UNDEFINED_TYPE;
    
    private String block1, block2;

    private boolean blocking = false, asynchronous = false, ams = false, isPrivate, isBroadcast = false, isLossy;
    private boolean blockingSet = false, asynchronousSet = false, amsSet = false, isPrivateSet = false, isBroadcastSet = false, isLossySet = false;

    private int sizeOfFIFO = 1024; // -1 means infinite
    private boolean sizeOfFIFOSet = false;

    private int id = 0;//DG
    private boolean idSet = false;

    protected String getName() {
        return relationString;
    }

    protected boolean isNameSet() {
        return relationStringType != UNDEFINED_TYPE;
    }

    protected int getRelationStringType() {
        return relationStringType;
    }

    private void setName(String _relationString, int _relationType) {
        relationString = _relationString;
        relationStringType = _relationType;
    }

    protected String getBlock1() {
        return block1;
    }

    protected AvatarBlock getBlock1(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarBlock block = getBlock(_avspec, getBlock1());
        if (block == null) {
            throw new MissingBlockException("block 1", getBlock1());
        }
        return block;
    }

    protected String getBlock2() {
        return block2;
    }

    protected AvatarBlock getBlock2(AvatarSpecification _avspec) throws ApplyMutationException {
        AvatarBlock block = getBlock(_avspec, getBlock2());
        if (block == null) {
            throw new MissingBlockException("block 2", getBlock2());
        }
        return block;
    }

    private void setBlocks(String _block1, String _block2) {
        block1 = _block1;
        block2 = _block2;
    }

    protected boolean isBlocking() {
        return blocking;
    }

    protected boolean blockingSet() {
        return blockingSet;
    }

    public void setBlocking(boolean b) {
        blocking = b;
        blockingSet = true;
    }

    protected boolean isAsynchronous() {
        return asynchronous;
    }

    protected boolean asynchronousSet() {
        return asynchronousSet;
    }

    public void setAsynchronous(boolean b) {
        asynchronous = b;
        asynchronousSet = true;
    }

    protected boolean isAMS() {
        return ams;
    }

    protected boolean AMSSet() {
        return amsSet;
    }

    public void setAMS(boolean b) {
        ams = b;
        amsSet = true;
    }

    protected boolean isPrivate() {
        return isPrivate;
    }

    protected boolean privateSet() {
        return isPrivateSet;
    }

    public void setPrivate(boolean b) {
        isPrivate = b;
        isPrivateSet = true;
    }

    protected boolean isBroadcast() {
        return isBroadcast;
    }

    protected boolean broadcastSet() {
        return isBroadcastSet;
    }

    public void setBroadcast(boolean b) {
        isBroadcast = b;
        isBroadcastSet = true;
    }

    protected boolean isLossy() {
        return isLossy;
    }

    protected boolean lossySet() {
        return isLossySet;
    }

    public void setLossy(boolean b) {
        isLossy = b;
        isLossySet = true;
    }

    protected int getSizeOfFIFO() {
        return sizeOfFIFO;
    }

    protected boolean sizeOfFIFOSet() {
        return sizeOfFIFOSet;
    }

    public void setSizeOfFIFO(String _sizeOfFIFO) {
        setSizeOfFIFO(Integer.parseInt(_sizeOfFIFO));
    }

    public void setSizeOfFIFO(int _sizeOfFIFO) {
        sizeOfFIFO = _sizeOfFIFO;
        sizeOfFIFOSet = true;
    }

    protected int getId() {
        return id;
    }

    protected boolean idSet() {
        return idSet;
    }

    public void setId(int _id) {
        id = _id;
        idSet = true;
    }

    private AvatarRelation getElementFromName(AvatarSpecification _avspec, String _name) {
        List<AvatarRelation> relations = _avspec.getRelations();
        for (AvatarRelation rel : relations) {
            if (rel.getName().equals(_name)) return rel;
        }
        return null;
    }

    private AvatarRelation getElementFromUUID(AvatarSpecification _avspec, String _uuid) {
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

                flag = flag || (relation.getBlock1().getName().equals(this.getBlock2()) && relation.getBlock2().getName().equals(this.getBlock1()));

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
        return getElement(_avspec, getRelationStringType(), getName());
    }

    public static RelationMutation createFromString(String toParse) throws ParseMutationException {
        switch (MutationParser.findMutationToken(toParse)) {
        case "ADD":
            return AddRelationMutation.createFromString(toParse);
        case "RM":
        case "REMOVE":
            return RmRelationMutation.createFromString(toParse);
        case "MD":
        case "MODIFY":
            return MdRelationMutation.createFromString(toParse);
        default:
            break;
        }
        return null;
    }
}