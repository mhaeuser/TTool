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

package ui.tmlad;

import ui.AllowedBreakpoint;
import ui.BasicErrorHighlight;
import ui.EmbeddedComment;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.TGConnectingPoint;
import ui.ad.TADChoice;

/**
 * Class TMLADChoice
 * Choice with guards. to be used in activity diagrams
 * Creation: 21/11/2005
 * @version 1.0 21/11/2005
 * @author Ludovic APVRILLE
 */
public class TMLADChoice extends TADChoice /* Issue #69 TGCWithInternalComponent*/ implements EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {

	// Issue #31
//    private int lineLength = 10;
//    private int lineOutLength = 25;
//    private int textX1, textY1, textX2, textY2, textX3, textY3;
//    
//    protected int stateOfError = 0; // Not yet checked
//    public final static int NOT_VERIFIED = 0;
    public final static int REACHABLE = 1;
    public final static int NOT_REACHABLE = 2;

    public int securityInformation;

    public TMLADChoice(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
//        width = 30;
//        height = 30;
//        textX1 = -lineOutLength;
//        textY1 = height/2 - 5;
//        textX2 = width + 5;
//        textY2 = height/2 - 5;
//        textX3 = width /2 + 5;
//        textY3 = height + 15;
//        
//        nbConnectingPoint = 4;
//        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
//        connectingPoint[0] = new TGConnectingPointTMLAD(this, 0, -lineLength, true, false, 0.5, 0.0);
//        connectingPoint[1] = new TGConnectingPointTMLAD(this, -lineOutLength, 0, false, true, 0.0, 0.5);
//        connectingPoint[2] = new TGConnectingPointTMLAD(this, lineOutLength, 0, false, true, 1.0, 0.5);
//        connectingPoint[3] = new TGConnectingPointTMLAD(this, 0, lineOutLength,  false, true, 0.5, 1.0);
//        
//        nbInternalTGComponent = 3;
//        tgcomponent = new TGComponent[nbInternalTGComponent];
//        
//        TGCOneLineText tgc = new TGCOneLineText(x+textX1, y+textY1, textX1-50, textX1+5, textY1, textY1 + 25, true, this, _tdp);
//        tgc.setValue("[ ]");
//        tgc.setName("guard 1");
//        tgcomponent[0] = tgc;
//        
//        tgc = new TGCOneLineText(x+textX2, y+textY2, textX2, textX2+20, textY2, textY2+25, true, this, _tdp);
//        tgc.setValue("[ ]");
//        tgc.setName("guard 2");
//        tgcomponent[1] = tgc;
//        
//        tgc = new TGCOneLineText(x+textX3, y+textY3, textX3, textX3+20, textY3, textY3+25, true, this, _tdp);
//        tgc.setValue("[ ]");
//        tgc.setName("guard 3");
//        tgcomponent[2] = tgc;
//        
//        moveable = true;
//        editable = false;
//        removable = true;
//        
//        name = "choice";
//        
//        myImageIcon = IconManager.imgic208;
    }

    @Override
    protected void createConnectingPoints() {
    	nbConnectingPoint = 4;
    	connectingPoint = new TGConnectingPoint[nbConnectingPoint];
    	connectingPoint[ 0 ] = new TGConnectingPointTMLAD(this, 0, -lineLength, true, false, 0.5, 0.0);
    	connectingPoint[ 1 ] = new TGConnectingPointTMLAD(this, -OUT_LINE_LENGTH, 0, false, true, 0.0, 0.5);
    	connectingPoint[ 2 ] = new TGConnectingPointTMLAD(this, OUT_LINE_LENGTH, 0, false, true, 1.0, 0.5);
    	connectingPoint[ 3 ] = new TGConnectingPointTMLAD(this, 0, OUT_LINE_LENGTH,  false, true, 0.5, 1.0);
    }
    
//    public void internalDrawing(Graphics g) {
//		if (stateOfError > 0)  {
//			Color c = g.getColor();
//			switch(stateOfError) {
//			case ErrorHighlight.OK:
//				g.setColor(ColorManager.CHOICE);
//				break;
//			default:
//				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
//			}
//			// Making the polygon
//			int [] px1 = {x+(width/2), x+width+2, x + (width/2), x};
//			int [] py1 = {y, y + height/2, y+height+2, y+height/2};
//			g.fillPolygon(px1, py1, 4);
//			g.setColor(c);
//		}
//		
//        g.drawLine(x+(width/2), y, x+width, y + height/2);
//        g.drawLine(x, y + height / 2, x+width/2, y + height);
//        g.drawLine(x + width/2, y, x, y + height/2);
//        g.drawLine(x + width, y + height/2, x + width/2, y + height);
//        
//        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
//        g.drawLine(x, y + height/2, x-lineOutLength, y + height/2);
//        g.drawLine(x + width, y + height/2, x+ width + lineOutLength, y + height/2);
//        g.drawLine(x+(width/2), y + height, x+(width/2), y + height + lineOutLength);
//    }
//    
//    public TGComponent isOnOnlyMe(int _x, int _y) {
//        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
//            return this;
//        }
//        
//        if ((int)(Line2D.ptSegDistSq(x+(width/2), y + height, x+(width/2), y + height + lineOutLength, _x, _y)) < distanceSelected) {
//			return this;	
//		}
//		
//		if ((int)(Line2D.ptSegDistSq(x + width, y + height/2, x+ width + lineOutLength, y + height/2, _x, _y)) < distanceSelected) {
//			return this;
//		}
//		
//		if ((int)(Line2D.ptSegDistSq(x, y + height/2, x-lineOutLength, y + height/2, _x, _y)) < distanceSelected) {
//			return this;
//		}
//		
//		if ((int)(Line2D.ptSegDistSq(x+(width/2), y, x+(width/2), y - lineLength, _x, _y)) < distanceSelected) {
//			return this;
//		}
//        
//        return null;
//    }
    
//    public String getGuard(int i) {
//        if ((i>=0) && (i<nbInternalTGComponent)) {
//            return tgcomponent[i].getValue();
//        }
//        return "";
//    }
//    public void drawSecurityInformation(Graphics g) {
//        if (securityInformation > 0) {
//
//            Color c = g.getColor();
//            Color c1;
//            switch(securityInformation) {
//            case REACHABLE:
//                c1 = Color.green;
//                break;
//            case NOT_REACHABLE:
//                c1 = Color.red;
//                break;
//            default:
//                return;
//            }
//
//            GraphicLib.arrowWithLine(g, 1, 0, 10, x-30, y+4, x-15, y+4, true);
//            g.drawOval(x-11, y-3, 7, 9);
//            g.setColor(c1);
//            g.fillRect(x-12, y, 9, 7);
//            g.setColor(c);
//            g.drawRect(x-12, y, 9, 7);
//
//        }
//
//    }
//    
//    public void setGuard(String guard, int i) {
//         if ((i>=0) && (i<nbInternalTGComponent)) {
//            tgcomponent[i].setValue(guard);
//         }
//    }
    
    @Override
    public int getType() {
        return TGComponentManager.TMLAD_CHOICE;
    }
    
    @Override
    public int getDefaultConnector() {
    	return TGComponentManager.CONNECTOR_TMLAD;
    }
//	
//	public void setStateAction(int _stateAction) {
//		stateOfError = _stateAction;
//	}
}
