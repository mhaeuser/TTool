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

package ui.avatarcd;

import ui.CDElement;
import ui.TGComponentManager;
import ui.TGConnectingPoint;
import ui.TGConnectingPointWidthHeight;
import ui.ucd.UCDUseCase;

/**
 * Class AvatarCDConnectingPoint
 * Definition of connecting points on which attribute connectors can be connected
 * Creation: 31/08/2011
 * @version 1.0 31/08/2011
 * @author Ludovic APVRILLE
 */
public class AvatarCDConnectingPoint extends TGConnectingPointWidthHeight {
    
    public AvatarCDConnectingPoint(CDElement _container, int _x, int _y, boolean _in, boolean _out, double _w, double _h) {
        super(_container, _x, _y, _in, _out, _w, _h);
    }
    
    @Override
    public boolean isCompatibleWith(int type) {
        if (type == TGComponentManager.ACD_COMPOSITION_CONNECTOR) {
            return true;
        }

        if (type == TGComponentManager.ACD_AGGREGATION_CONNECTOR) {
            return true;
        }

        if (type == TGComponentManager.CONNECTOR_SPECIA_CD) {
            return true;
        }

        if (type == TGComponentManager.ACD_ASSOCIATION_CONNECTOR_ARROW) {
            return true;
        }

        return type == TGComponentManager.ACD_ASSOCIATION_CONNECTOR;

    }

    public boolean isCompatibleWith(int type, TGConnectingPoint outPoint) {



        if ((outPoint != null) && (type == TGComponentManager.CONNECTOR_SPECIA_CD)) {
            if ((outPoint.getFather() instanceof AvatarCDActorBox) || (outPoint.getFather() instanceof AvatarCDActorStickman)) {
                if (!((getFather() instanceof AvatarCDActorBox) || (getFather() instanceof AvatarCDActorStickman))) {
                    return false;
                }
            }

            if ((outPoint.getFather() instanceof AvatarCDBlock)) {
                if (!((getFather() instanceof AvatarCDBlock))) {
                    return false;
                }
            }
        }

         if ((outPoint != null) &&
                 ((type == TGComponentManager.ACD_COMPOSITION_CONNECTOR) || (type == TGComponentManager.ACD_AGGREGATION_CONNECTOR))) {
             if ((getFather() instanceof AvatarCDActorBox) || (getFather() instanceof AvatarCDActorStickman)) {
                 return false;
             }
        }

        return isCompatibleWith(type);
    }
}
