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




package ui.oscd;

import myutil.Conversion;
import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;

//import java.awt.geom.*;

/**
 * Class TOSEvtInfo
 * Management of information coming along with Evt composition operator in TURTLE-OS
 * Creation: 09/01/2007
 * @version 1.0 09/01/2007
 * @author Ludovic APVRILLE
 */
public class TOSEvtInfo extends TGCWithoutInternalComponent{
    
    public TOSEvtInfo(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        nbConnectingPoint = 0;
        minWidth = 10;
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = false;
        
        name = "action";
        value = "action";
        
        myImageIcon = IconManager.imgic302;
    }
    
    public void internalDrawing(Graphics g) {
        if (!tdp.isScaled()) {
            width = g.getFontMetrics().stringWidth(value);
            height = g.getFontMetrics().getHeight();
        }
        g.drawString("{" + value + "}", x, y);
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
    
    public boolean editOnDoubleClick(JFrame frame) {
        String oldValue = value;
        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String)JOptionPane.showInputDialog(frame, text,
        "Setting evt name", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
        null,
        getValue());
        
        if (s != null) {
            s = Conversion.removeFirstSpaces(s);
        }
        
        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
          if (!TAttribute.isAValidId(s, false, false, false)) {
                  JOptionPane.showMessageDialog(frame,
                  "Could not set the name: the new name is not a valid name",
                  "Error",
                  JOptionPane.INFORMATION_MESSAGE);
                  return false;
            }
            setValue(s);
            return true;
        }
         
         
        return false;
    }
}
