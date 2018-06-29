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

package ui.eln;

import ui.*;
import ui.util.IconManager;

import java.awt.*;
import java.util.Vector;

/**
 * Class ELNConnector
 * Connector used in ELN diagrams
 * Creation: 11/06/2018
 * @version 1.0 11/06/2018
 * @author Irina Kit Yan LEE
 */

public  class ELNConnector extends TGConnector implements ScalableTGComponent {
   
	protected double oldScaleFactor;
    protected int c = 10; //square length

    public ELNConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector<Point> _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "Connector between ports";
        editable = false;
        oldScaleFactor = tdp.getZoom();
        p1 = _p1;	
        p2 = _p2;
    }

    public TGConnectingPoint get_p1(){
    	return p1;
	}

    public TGConnectingPoint get_p2(){
    	return p2;
	}

    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2) {
        int cz = (int) (tdp.getZoom() * c);
        if (p1.getName().equals("")) {
        	g.fillOval(x2 - (cz / 2), y2 - (cz / 2), cz, cz);
 	        g.drawLine(x1, y1, x2, y2);
        } else if (p2.getName().equals("")) {
 	        g.fillOval(x1 - (cz / 2), y1 - (cz / 2), cz, cz);
 	        g.drawLine(x1, y1, x2, y2);
        } else {
	        g.fillOval(x2 - (cz / 2), y2 - (cz / 2), cz, cz);
	        g.fillOval(x1 - (cz / 2), y1 - (cz / 2), cz, cz);
	        g.drawLine(x1, y1, x2, y2);
        }
    }

    public void rescale(double scaleFactor){
        int xx, yy;

        for(int i=0; i<nbInternalTGComponent; i++) {
            xx = tgcomponent[i].getX();
            yy = tgcomponent[i].getY();
            tgcomponent[i].dx = (tgcomponent[i].dx + xx) / oldScaleFactor * scaleFactor;
            tgcomponent[i].dy = (tgcomponent[i].dy + yy) / oldScaleFactor * scaleFactor;
            xx = (int)(tgcomponent[i].dx);
            tgcomponent[i].dx = tgcomponent[i].dx - xx;
            yy = (int)(tgcomponent[i].dy);
            tgcomponent[i].dy = tgcomponent[i].dy - yy;
            tgcomponent[i].setCd(xx, yy);
        }
        oldScaleFactor = scaleFactor;
    }

    public int getType() {
        return TGComponentManager.ELN_CONNECTOR;
    }
}
