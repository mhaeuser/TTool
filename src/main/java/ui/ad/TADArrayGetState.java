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

package ui.ad;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogArrayGet;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Class TADArrayGetState
 * Getting the element of an array, in an activity diagram
 * Creation: 20/03/2009
 * @version 1.0 20/03/2009
 * @author Ludovic APVRILLE
 */
public class TADArrayGetState extends TADComponentWithoutSubcomponents/* Issue #69 TGCWithoutInternalComponent*/ implements BasicErrorHighlight {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
	
	// variableName = arrayName[index]
	protected String variable = "x";
	protected String array = "tab";
	protected String index = "i";
	
	protected int stateAction = 0; // 0: unchecked 1: ok; 2:unknown
	public static final int OK = 1;
	public static final int UNKNOWN = 2;
    
    public TADArrayGetState(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointAD(this, 0, lineLength, false, true, 0.5, 1.0);
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = "x = tab[i]";
        name = "array get";
        
        myImageIcon = IconManager.imgic230;
    }
	
    @Override
    public void internalDrawing(Graphics g) {
        int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;
            //updateConnectingPoints();
        }
		
		if (stateAction > 0)  {
			Color c = g.getColor();
			switch(stateAction) {
			case 1:
				g.setColor(ColorManager.ARRAY_BOX_ACTION);
				break;
			default:
				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}
			g.fillRoundRect(x, y, width, height, arc, arc);
			g.setColor(c);
		}
		
        g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        
        g.drawString(value, x + (width - w) / 2 , y + textY);
		
		/*if (accessibility) {
			//g.drawString("acc", x + width - 10, y+height-10);
			g.drawLine(x+width-2, y+2, x+width-6, y+6);
			g.drawLine(x+width-6, y+2, x+width-2, y+6);
			//
		}*/
    }
	
    @Override
	public boolean editOnDoubleClick(JFrame frame) {
        String oldValue = value;
        
        JDialogArrayGet jdag = new JDialogArrayGet(frame, variable, array, index, "Getting value from an array");
       // jdag.setSize(350, 300);
        GraphicLib.centerOnParent(jdag, 350, 300);
        jdag.setVisible( true ); // blocked until dialog has been closed
        
        if (jdag.hasNewData() && jdag.hasValidData()) {
            variable = jdag.getVariableName();
            array = jdag.getArrayName();
			index = jdag.getIndexName();
        }
        
        makeValue();

        return !oldValue.equals(value);

    }
	
	public void makeValue() {
		value = variable + " = " + array + "[" + index + "]"; 
	}
    
    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Prop variable=\"");
        sb.append(getVariable());
        sb.append("\" array=\"");
        sb.append(getArray());
        sb.append("\" index=\"");
        sb.append(getIndex());
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            
            //
            //
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Prop")) {
                                variable = elt.getAttribute("variable");
                                array = elt.getAttribute("array");
								index = elt.getAttribute("index");
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
          //
            throw new MalformedModelingException();
        }
        makeValue();
    }
    
    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        
        if ((int)(Line2D.ptSegDistSq(x +width/2, y- lineLength,  x+width/2, y + lineLength + height, _x, _y)) < distanceSelected) {
			return this;	
		}
        

        return null;
    }
    
    public String getAction() {
        return value;
    }
	
	public String getVariable() {
		return variable;
	}
	
	public String getArray() {
		return array;
	}
	
	public String getIndex() {
		return index;
	}
    
    @Override
    public int getType() {
        return TGComponentManager.TAD_ARRAY_GET;
    }
    
    @Override
   	public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_AD_DIAGRAM;
    }
	
    @Override
	public void setStateAction(int _stateAction) {
		stateAction = _stateAction;
	}
}
