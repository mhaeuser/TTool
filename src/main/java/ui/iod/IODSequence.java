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




package ui.iod;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

import java.awt.*;

/**
 * Class IODSequence
 * Preemption operator. Scenarios on the left can be interrupted by scenarios on the right
 * To be used in interaction overview diagrams
 * Creation: 02/05/2006
 * @version 1.0 02/05/2006
 * @author Ludovic APVRILLE
 */
public class IODSequence extends TGCWithoutInternalComponent{
    private int lineLength = 0;
    //private int textX, textY;
    
    public IODSequence(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 150;
        height = 5;
        
        nbConnectingPoint = 6;
        connectingPoint = new TGConnectingPoint[6];
        connectingPoint[0] = new TGConnectingPointIOD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointIOD(this, 0, lineLength, false, true, 0.167, 1.0);
        connectingPoint[2] = new TGConnectingPointIOD(this, 0, lineLength, false, true, 0.333, 1.0);
        connectingPoint[3] = new TGConnectingPointIOD(this, 0, lineLength, false, true, 0.5, 1.0);
        connectingPoint[4] = new TGConnectingPointIOD(this, 0, lineLength, false, true, 0.667, 1.0);
        connectingPoint[5] = new TGConnectingPointIOD(this, 0, lineLength, false, true, 0.833, 1.0);
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = false;
        removable = true;
        
        name = "sequence";
        
        myImageIcon = IconManager.imgic226;
    }
    
    public void internalDrawing(Graphics g) {
        g.drawRect(x, y, width, height);
        g.fillRect(x, y, width, height);
        g.drawString(">>", x+width+2, y+height+1);
    }
    
    public TGComponent isOnMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width + 10, height)) {
            return this;
        }
        return null;
    }
    
    public String getValueGate() {
        return tgcomponent[0].getValue();
    }
    
    public int getType() {
        return TGComponentManager.IOD_SEQUENCE;
    }
	
	public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_INTERACTION;
    }
}
