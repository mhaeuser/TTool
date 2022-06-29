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
 * Class MdRelationMutation
 * Creation: 29/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 29/06/2022
 */
public class MdRelationMutation extends RelationMutation implements MdMutation {
    
    private RelationMutation current;
    
    public MdRelationMutation(String _block1, String _block2) {
        setBlocks(_block1, _block2);
        current = new RmRelationMutation(_block1, _block2);
    }

    @Override
    public void setName(String _name) {
        current.setName(_name);
        super.setName(_name);
    }

    @Override
    public void setUUID(String _uuid) {
        current.setUUID(_uuid);
        super.setUUID(_uuid);
    }

    public void setCurrentBlocking(boolean b) {
        current.setBlocking(b);
    }

    public void setCurrentAsynchronous(boolean b) {
        current.setAsynchronous(b);
    }

    public void setCurrentAMS(boolean b) {
        current.setAMS(b);
    }

    public void setCurrentPrivate(boolean b) {
        current.setPrivate(b);
    }

    public void setCurrentBroadcast(boolean b) {
        current.setBroadcast(b);
    }

    public void setCurrentLossy(boolean b) {
        current.setLossy(b);
    }

    public void setCurrentSizeOfFIFO(int _sizeOfFIFO) {
        current.setSizeOfFIFO(_sizeOfFIFO);
    }

    public void setCurrentId(int _id) {
        current.setId(_id);
    }

    public void apply(AvatarSpecification _avspec) {
        AvatarRelation relation = current.getElement(_avspec);

        if (this.blockingSet()) relation.setBlocking(this.isBlocking());

        if (this.asynchronousSet()) relation.setAsynchronous(this.isAsynchronous());

        if (this.AMSSet()) relation.setAMS(this.isAMS());

        if (this.privateSet()) relation.setPrivate(this.isPrivate());

        if (this.broadcastSet()) relation.setBroadcast(this.isBroadcast());

        if (this.lossySet()) relation.setLossy(this.isLossy());

        if (this.sizeOfFIFOSet()) relation.setSizeOfFIFO(this.getSizeOfFIFO());

        if (this.idSet()) relation.setId(this.getId());
    }
    
}