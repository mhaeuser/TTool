/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class SysmlsecMethodologyConnectingPoint
 * Definition of connecting points on which connectors between elements of sysmlsecmethodo may be connected
 * Creation: 26/01/2016
 * @version 1.0 26/01/2016
 * @author Ludovic APVRILLE
 * @see
 */

package ui.sysmlsecmethodology;

//import java.awt.*;

import ui.CDElement;
import ui.TGComponentManager;
import ui.TGConnectingPointWidthHeight;

public class SysmlsecMethodologyConnectingPoint extends  TGConnectingPointWidthHeight {

    public SysmlsecMethodologyConnectingPoint(CDElement _container, int _x, int _y, boolean _in, boolean _out, double _w, double _h, int _orientation) {
        super(_container, _x, _y, _in, _out, _w, _h);
        orientation = _orientation;
    }

    public boolean isCompatibleWith(int type) {

        if (type == TGComponentManager.SYSMLSEC_METHODOLOGY_CONNECTOR) {
            return true;
        }


        return false;
    }


}