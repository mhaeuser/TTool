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

import ui.*;
import ui.util.IconManager;

import java.awt.*;
import java.util.Vector;

/**
* Class AvatarCDAggragationConnector
* Connector used in AVATAR Context Diagrams
* Creation: 05/01/2021
* @version 1.0 05/01/2021
* @author Ludovic APVRILLE
 */
public  class AvatarCDAggregationConnector extends TGConnectorWithMultiplicity implements ColorCustomizable {
    protected int d = 20;
	protected int D = 26;
    //protected int widthValue, heightValue, maxWidthValue, h;
	protected Polygon p;
	protected int xp1, xp2, yp1, yp2;
//	protected double oldScaleFactor;
//	protected boolean rescaled;
	
    
    public AvatarCDAggregationConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector<Point> _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
       
        myImageIcon = IconManager.imgic202;

        value = "{info}";
        editable = true;
//		oldScaleFactor = tdp.getZoom();
//		rescaled = true;
    }
    
    @Override
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2) {
		if ((p == null) || (rescaled) || (xp1 != x1) || (xp2 != x2) || (yp1 != y1) || (yp2 != y2)){
			p = new Polygon();
			xp1 = x1;
			xp2 = x2;
			yp1 = y1;
			yp2 = y2;
			//Double alpha;
			
			int dd = (int)(d*tdp.getZoom());
			int DD = (int)(D*tdp.getZoom());
			
			if (x1 == x2) {
				if (y1 > y2) {
					p.addPoint(x2, y2+DD);
					p.addPoint(x2+(dd/2), y2+(DD/2));
					p.addPoint(x2, y2);
					p.addPoint(x2-(dd/2), y2+(DD/2));
				} else {
					p.addPoint(x2, y2-DD);
					p.addPoint(x2+(dd/2), y2-(DD/2));
					p.addPoint(x2, y2);
					p.addPoint(x2-(dd/2), y2-(DD/2));
				}
			} else {
				double xd[] = new double[4];
				double yd[] = new double[4];
				
				
				//P
				xd[0] = x2;
				yd[0] = y2;
				
				int x0 = x1 - x2;
				int y0 = y1 - y2;
				double k = 1/(Math.sqrt((x0*x0)+(y0*y0)));
				double u = x0*k;
				double v = y0*k;
				
				double Ex = DD*u;
				double Ey = DD*v;
				double Fx = dd*v;
				double Fy = -dd*u;
				
				//Q
				xd[1] = x2+((Ex+Fx)/2);
				yd[1] = y2+((Ey+Fy)/2);
				
				//R
				xd[2] = x2+Ex;
				yd[2] = y2+Ey;
				
				//S
				xd[3] = xd[1] - Fx;
				yd[3] = yd[1] - Fy;
				
				for(int i=0; i<4; i++) {
					p.addPoint((int)xd[i], (int)yd[i]);
				}
			}
			
		}
		g.drawLine(x1, y1, x2, y2);
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		g.fillPolygon(p);
		g.setColor(c);
		g.drawPolygon(p);

		rescaled = false;
    }
	
    @Override
	public TGComponent extraIsOnOnlyMe(int x1, int y1) {
		if (p != null) {
			if (p.contains(x1, y1)) {
				return this;
			}
		}
		
        return null;
    }



    
	@Override
    public int getType() {
        return TGComponentManager.ACD_AGGREGATION_CONNECTOR;
    }

	// Color management
	public Color getMainColor() {
		return Color.BLACK;
	}
}
