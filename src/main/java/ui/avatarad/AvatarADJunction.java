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

package ui.avatarad;

import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.TGConnectingPoint;
import ui.util.IconManager;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Class AvatarADJunction
 * Junction between several activities, without any synchronization. To be used in avatar activity diagrams
 * Creation: 02/09/2011
 *
 * @author Ludovic APVRILLE
 * @version 1.1 09/02/2017
 */
public class AvatarADJunction extends AvatarADBasicComponent {

    protected int range = 10;

    public AvatarADJunction(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(30, 30);
        oldScaleFactor = tdp.getZoom();

        nbConnectingPoint = 22;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[1] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[2] = new AvatarADConnectingPoint(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[3] = new AvatarADConnectingPoint(this, 0, 0, false, true, 0.5, 1);
        connectingPoint[4] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[5] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[6] = new AvatarADConnectingPoint(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[7] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[8] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[9] = new AvatarADConnectingPoint(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[10] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[11] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[12] = new AvatarADConnectingPoint(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[13] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[14] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[15] = new AvatarADConnectingPoint(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[16] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[17] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[18] = new AvatarADConnectingPoint(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[19] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[20] = new AvatarADConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[21] = new AvatarADConnectingPoint(this, 0, 0, true, false, 1.0, 0.5);

        moveable = true;
        editable = false;
        removable = true;

        value = "";
        name = "junction";

        myImageIcon = IconManager.imgic212;
    }

    @Override
    public void internalDrawing(Graphics g) {
        //g.drawLine(x +width/2, y,  x+width/2, y + height);
        //g.drawLine(x, y + (height/2), x+width, y + (height/2));

        g.drawLine(x + width / 2, y, x + width / 2, y + height / 2 - range);
        g.drawLine(x + width / 2, y + height / 2 + range, x + width / 2, y + height);
        g.drawLine(x, y + (height / 2), x + width / 2 - range, y + (height / 2));
        g.drawLine(x + width / 2 + range, y + (height / 2), x + width, y + (height / 2));

        g.drawLine(x + width / 2, y + height / 2 - range, x + width / 2 - range, y + height / 2);
        g.drawLine(x + width / 2, y + height / 2 - range, x + width / 2 + range, y + height / 2);
        g.drawLine(x + width / 2 - range, y + height / 2, x + width / 2, y + height / 2 + range);
        g.drawLine(x + width / 2 + range, y + height / 2, x + width / 2, y + height / 2 + range);
    }

    @Override
    public TGComponent isOnMe(int _x, int _y) {
        // vertical line
        if ((int) (Line2D.ptSegDistSq(x + width / 2, y, x + width / 2, y + height, _x, _y)) < distanceSelected) {
            return this;
        }
        // horizontal line
        if ((int) (Line2D.ptSegDistSq(x, y + (height / 2), x + width, y + (height / 2), _x, _y)) < distanceSelected) {
            return this;
        }
        return null;
    }

    @Override
    public int getType() {
        return TGComponentManager.AAD_JUNCTION;
    }

//    public int getDefaultConnector() {
//        return TGComponentManager.AAD_ASSOCIATION_CONNECTOR;
//    }
}
