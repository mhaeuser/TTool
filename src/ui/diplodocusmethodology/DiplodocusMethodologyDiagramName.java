/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class DiplodocusMethodologyDiagramName
 * Internal component that shows the diagram name and validation/simu
 * references
 * Creation: 31/03/2014
 * @version 1.0 31/03/2014
 * @author Ludovic APVRILLE
 * @see
 */

package ui.diplodocusmethodology;

import java.awt.*;
//import java.awt.geom.*;
import javax.swing.*;

import ui.*;
import myutil.*;

public class DiplodocusMethodologyDiagramName extends TGCWithoutInternalComponent implements SwallowedTGComponent { 
    //protected boolean emptyText;
    
    protected int minSim = -1;
    protected int maxSim = -1;
    protected final static String Sim = "sim";
    
    protected int minUpp = -1;
    protected int maxUpp = -1;
    protected final static String Upp = "ipp";
    
    protected int minLot = -1;
    protected int maxLot = -1;
    protected final static String Lot = "lot";
    
    protected int minTml = -1;
    protected int maxTml = -1;
	protected final static String Tml = "tml";
	
    public DiplodocusMethodologyDiagramName(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        nbConnectingPoint = 0;
        minWidth = 10;
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = false;
        
        name = "value ";
        
        myImageIcon = IconManager.imgic302;
    }
    
    public void internalDrawing(Graphics g) {
        if (!tdp.isScaled()) {
            width = g.getFontMetrics().stringWidth(value);
            height = g.getFontMetrics().getHeight();
        }
        
        
        
        g.drawString(value, x, y);
        if (value.equals("")) {
            g.drawString("value?", x, y);
        }
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y - height, Math.max(width, minWidth), height)) {
            return this;
        }
        return null;
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        
         
        return false;
    }
    
    
    public  int getType() {
        return TGComponentManager.DIPLODODUSMETHODOLOGY_DIAGRAM_NAME;
    }
    
   	public int getDefaultConnector() {
      return TGComponentManager.DIPLODOCUSMETHODOLOGY_CONNECTOR;
    }
}
