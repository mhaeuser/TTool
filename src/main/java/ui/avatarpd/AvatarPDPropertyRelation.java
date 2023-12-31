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




package ui.avatarpd;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogConstraint;

import javax.swing.*;
import java.awt.*;

/**
 * Class AvatarPDPropertyRelation
 * Constraint of SysML Parametric diagrams, adapted to properties
 * Creation: 23/04/2010
 * @version 1.0 23/04/2010
 * @author Ludovic APVRILLE
 */
public class AvatarPDPropertyRelation extends TGCScalableWithInternalComponent implements ConstraintListInterface {
    //private int textY1 = 5;
    //private int textY2 = 30;
	
	public static final String[] STEREOTYPES = {"OR", "AND", "=>", "<=>"}; 
	
    protected String oldValue = "";
	
//	private int maxFontSize = 12;
//	private int minFontSize = 4;
	private int currentFontSize = -1;
//	private boolean displayText = true;
//	private int textX = 1;
    
    public AvatarPDPropertyRelation(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 80;
        height = 40;
        minWidth = 75;
        textY = 5;
        textX = 1;
        //Issue #31
        initScaling(80,40);
        
        nbConnectingPoint = 12;
        connectingPoint = new TGConnectingPoint[12];
        
		connectingPoint[0] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[1] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, false, 0.0, 0.25);
        connectingPoint[2] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, false, 0.25, 0.0);
        connectingPoint[3] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[4] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, false, 0.75, 0.0);
        connectingPoint[5] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, false, 1.0, 0.25);
        connectingPoint[6] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, false, 1.0, 0.50);
		
        connectingPoint[7] = new AvatarPDPropertyConnectingPoint(this, 0, 0, false, true, 0.0, 0.75);
        connectingPoint[8] = new AvatarPDPropertyConnectingPoint(this, 0, 0, false, true, 0.25, 1.0);
        connectingPoint[9] = new AvatarPDPropertyConnectingPoint(this, 0, 0, false, true, 0.5, 1.0);
        connectingPoint[10] = new AvatarPDPropertyConnectingPoint(this, 0, 0, false, true, 0.75, 1.0);
        connectingPoint[11] = new AvatarPDPropertyConnectingPoint(this, 0, 0, false, true, 1.0, 0.75);
		
        /*connectingPoint[0] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[1] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[2] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[3] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[4] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[5] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[6] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[7] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[8] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[9] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[10] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[11] = new AvatarPDPropertyConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);*/
        //addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = "OR";
		
//		currentFontSize = maxFontSize;
//		oldScaleFactor = tdp.getZoom();
        
        myImageIcon = IconManager.imgic1078;
    }
    @Override
    public void internalDrawing(Graphics g)
    {
    	//Rectangle
    	GraphicLib.draw3DRoundRectangle(g, x, y, width, height, AvatarPDPanel.ARC, ColorManager.AVATARPD_PROPERTY_RELATION, g.getColor());
    	
    	//String
    	if (!isTextReadable(g))
    		return;
    	currentFontSize = g.getFont().getSize();
    	g.drawString(value, getCenter(g, value), y + currentFontSize);
    }
//    public void internalDrawing(Graphics g) {
//        
//		Font f = g.getFont();
//	//	Font fold = f;
//		
//		if ((rescaled) && (!tdp.isScaled())) {
//			
//			if (currentFontSize == -1) {
//				currentFontSize = f.getSize();
//			}
//			rescaled = false;
//			// Must set the font size ..
//			// Find the biggest font not greater than max_font size
//			// By Increment of 1
//			// Or decrement of 1
//			// If font is less than 4, no text is displayed
//			
//			int maxCurrentFontSize = Math.max(0, Math.min(height, maxFontSize));
//			int w0;
//			f = f.deriveFont((float)maxCurrentFontSize);
//			g.setFont(f);
//			//
//			while(maxCurrentFontSize > (minFontSize-1)) {
//				w0 = g.getFontMetrics().stringWidth(value);
//				if (w0 < (width - (2*textX))) {
//					break;
//				}
//				maxCurrentFontSize --;
//				f = f.deriveFont((float)maxCurrentFontSize);
//				g.setFont(f);
//			}
//			currentFontSize = maxCurrentFontSize;
//			
//			if(currentFontSize <minFontSize) {
//				displayText = false;
//			} else {
//				displayText = true;
//				f = f.deriveFont((float)currentFontSize);
//				g.setFont(f);
//			}
//			
//		}
//		
//        /*Color c = g.getColor();
//		g.draw3DRect(x, y, width, height, true);
//		
//		g.setColor(ColorManager.AVATARPD_PROPERTY_RELATION);
//		g.fill3DRect(x+1, y+1, width-1, height-1, true);
//		g.setColor(c);*/
//		
//		GraphicLib.draw3DRoundRectangle(g, x, y, width, height, AvatarPDPanel.ARC, ColorManager.AVATARPD_PROPERTY_RELATION, g.getColor());
//		
//        
//		Font f0 = g.getFont();
//		if (displayText) {
//			f = f.deriveFont((float)currentFontSize);
//			g.setFont(f.deriveFont(Font.BOLD));
//			int w  = g.getFontMetrics().stringWidth(value);
//			g.drawString(value, x + (width - w)/2, y + currentFontSize + (int)(textY*tdp.getZoom()));
//			g.setFont(f0);
//		}
//        
//    }
    
   /* public void setValue(String val, Graphics g) {
        oldValue = value;
        int w  = g.getFontMetrics().stringWidth(value);
		int w1 = Math.max(minWidth, w + 2 * textX + fileX + space);
		
        //
        if (w1 != width) { 
            width = w1;
            resizeWithFather();
        }
        //
    }*/
    

    
    
     public boolean editOnDoubleClick(JFrame frame) {
//		String tmp;
//		boolean error = false;
		
		JDialogConstraint dialog = new JDialogConstraint(frame, "Setting constraint attributes", this);
		//dialog.setSize(450, 350);
        GraphicLib.centerOnParent(dialog, 450, 350);
        dialog.setVisible( true ); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		if (dialog.getStereotype() == null) {
			return false;
		}
		
		if (dialog.getStereotype().length() > 0) {
			value = dialog.getStereotype();
		}
			
		rescaled = true;
		
		return true;
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
        return TGComponentManager.APD_PROPERTY_RELATION;
    }
	
	public String[] getConstraintList() {
		return STEREOTYPES;
	}
	
	public String getCurrentConstraint() {
		return value;
	}
	
	public boolean isOr() {
		return (value.compareTo(STEREOTYPES[0]) == 0);
	}
	
	public boolean isAnd() {
		return (value.compareTo(STEREOTYPES[1]) == 0);
	}
	
	public boolean isImply() {
		return (value.compareTo(STEREOTYPES[2]) == 0);
	}
	
	public boolean isEquivalent() {
		return (value.compareTo(STEREOTYPES[3]) == 0);
	}
  
    
}
