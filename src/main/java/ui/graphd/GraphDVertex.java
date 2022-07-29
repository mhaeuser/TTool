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


package ui.graphd;

import avatartranslator.ElementWithNew;
import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;

/**
 * Class GraphDVertex
 * Vertex in a GraphD
 * Creation: 24/06/2022
 *
 * @author Ludovic APVRILLE
 * @version 1.0 25/06/2022
 */
public class GraphDVertex extends TGCScalableOneLineText implements ColorCustomizable, ElementWithNew {
    /*protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;*/
    protected int w, h; //w1;

    public GraphDVertex(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 35;
        height = 35;
        initScaling(width, height);


        nbConnectingPoint = 24;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        int i;
        for (int j = 0; j < 24; j++) {
            connectingPoint[j] = new GraphDConnectingPoint(this, 0, 0, true, true, 0.5, 0.5);
        }
        addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;

        value = "0";
        name = "vertex";

        myImageIcon = IconManager.imgic600;
    }

    @Override
    public void drawNewSymbol(Graphics g) {
    }

    public void internalDrawing(Graphics g) {
        //g.drawRoundRect(x - width/2, y, width, height, arc, arc);
        Color c = g.getColor();
        g.setColor(getCurrentColor());
        g.fillOval(x, y, width, height);
        if (isNew()) {
            g.setColor(ColorManager.NEW);
        } else {
            g.setColor(c);
        }
        g.drawOval(x, y, width, height);
        g.setColor(c);

        // Vertex name
        FontMetrics fm = g.getFontMetrics();
        w = fm.stringWidth(value);
        h = fm.getAscent() - fm.getDescent() - fm.getLeading();
        drawSingleString(g, value, getCenter(g, value), this.y + this.height / 2 + h / 2);

    }

    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        if (GraphicLib.isInRectangle(_x, _y, x + width / 2 - w / 2, y + height / 2 - h / 2, w, h)) {
            return this;
        }
        return null;
    }
    
    /*public int getMyCurrentMinX() {
        return Math.min(x + width / 2 - w / 2, x);

    }
    
    public int getMyCurrentMaxX() {
        return Math.max(x + width / 2 + w / 2, x + width);
    }
  
    public String getActorName() {
        return value;
    }*/


    public int getType() {
        return TGComponentManager.GRAPHD_VERTEX;
    }

    // Color management
    public Color getMainColor() {
        return Color.LIGHT_GRAY;
    }

    public ImageIcon getImageIcon() {
        return IconManager.imgic452;
    }
}
