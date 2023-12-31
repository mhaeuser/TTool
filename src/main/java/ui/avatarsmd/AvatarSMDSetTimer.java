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

package ui.avatarsmd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import myutil.GraphicLib;
import myutil.TraceManager;
import ui.AvatarSignal;
import ui.BasicErrorHighlight;
import ui.ColorManager;
import ui.ErrorHighlight;
import ui.PartOfInvariant;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.TGConnectingPoint;
import ui.util.IconManager;
import ui.window.JDialogAvatarTimer;

/**
 * Class AvatarSMDSetTimer
 * Action of setting a timer
 * Creation: 15/07/2010
 * @version 1.0 15/07/2010
 * @author Ludovic APVRILLE
 */
public class AvatarSMDSetTimer extends AvatarSMDBasicCanBeDisabledComponent /* Issue #69 AvatarSMDBasicComponent*/ implements BasicErrorHighlight, PartOfInvariant {
    protected int lineLength = 5;
//    protected int textX =  5;
//    protected int textY =  15;
    protected int arc = 5;
    protected int linebreak = 10;
	
	protected int hourglassWidth = 10;
	protected int hourglassSpace = 2;
    
	
	protected int stateOfError = 0; // Not yet checked
    
    public AvatarSMDSetTimer(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        textX = 5;
        textY = 15;
        initScaling(30,20);
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new AvatarSMDConnectingPoint(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new AvatarSMDConnectingPoint(this, 0, lineLength, false, true, 0.5, 1.0);
        
		addTGConnectingPointsComment();
		
        moveable = true;
        editable = true;
        removable = true;
        
        name = "Set timer";
		value = "set(timer1, 10)";
        //makeValue();
        
        myImageIcon = IconManager.imgic904;
    }
    
    @Override
    public void internalDrawing(Graphics g) {
		int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;            //updateConnectingPoints();
        }
		
		
		if (stateOfError > 0)  {
			Color c = g.getColor();
			switch(stateOfError) {
			case ErrorHighlight.OK:
				g.setColor(ColorManager.AVATAR_SET_TIMER);
				break;
			default:
				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}
			// Making the polygon
			int [] px1 = {x, x+width-linebreak, x+width, x+width-linebreak, x};
			int [] py1 = {y, y, y+(height/2), y+height, y+height};
			g.fillPolygon(px1, py1, 5);
			g.setColor(c);
		}
		
        //g.drawRoundRect(x, y, width, height, arc, arc);
		Color c = g.getColor();
		//
		
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
		
		int x1 = x + 1;
		int y1 = y + 1;
		int height1 = height;
		int width1 = width;
		g.setColor(ColorManager.AVATAR_SET_TIMER);
		g.drawLine(x1, y1, x1+width1-linebreak, y1);
        g.drawLine(x1, y1+height1, x1+width1-linebreak, y1+height1);
        g.drawLine(x1, y1, x1, y1+height1);
        g.drawLine(x1+width1-linebreak, y1, x1+width1, y1+height1/2);
        g.drawLine(x1+width1-linebreak, y1+height1, x1+width1, y1+height1/2);
		g.setColor(c);

		final Polygon shape = new Polygon();
		shape.addPoint( x, y );
		shape.addPoint( x + width - linebreak, y );
		shape.addPoint( x + width, y + height / 2 );
		shape.addPoint( x + width - linebreak, y + height );
		shape.addPoint(x, y + height );
		
		g.drawPolygon( shape );
//		
//		g.drawLine(x, y, x+width-linebreak, y);
//        g.drawLine(x, y+height, x+width-linebreak, y+height);
//        g.drawLine(x, y, x, y+height);
//        g.drawLine(x+width-linebreak, y, x+width, y+height/2);
//        g.drawLine(x+width-linebreak, y+height, x+width, y+height/2);
		
        // Issue #69
    	if ( !isEnabled() && isContainedInEnabledState() ) {
	    	g.setColor( ColorManager.DISABLED_FILLING );
	    	g.fillPolygon( shape );
	    	g.setColor( c );
    	}

    	// hourglass
		g.setColor(ColorManager.AVATAR_SET_TIMER);
		g.drawLine(x+width+hourglassSpace+1, y+1, x+width+hourglassSpace + hourglassWidth+1, y+1);
		g.drawLine(x+width+hourglassSpace+1, y+height+1, x+width+hourglassSpace + hourglassWidth+1, y+height+1);
		g.drawLine(x+width+hourglassSpace+1, y+1, x+width+hourglassSpace + hourglassWidth+1, y+height+1);
		g.drawLine(x+width+hourglassSpace+1, y+height+1, x+width+hourglassSpace + hourglassWidth+1, y+1);
		g.setColor(c);
		g.drawLine(x+width+hourglassSpace, y, x+width+hourglassSpace + hourglassWidth, y);
		g.drawLine(x+width+hourglassSpace, y+height, x+width+hourglassSpace + hourglassWidth, y+height);
		g.drawLine(x+width+hourglassSpace, y, x+width+hourglassSpace + hourglassWidth, y+height);
		g.drawLine(x+width+hourglassSpace, y+height, x+width+hourglassSpace + hourglassWidth, y);
		
        //g.drawString("sig()", x+(width-w) / 2, y);
//        g.drawString(value, x + (width - w) / 2 , y + textY);
		drawSingleString(g, value, x + (width - w) / 2 , y + textY);
    }
    
    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width + hourglassSpace + hourglassWidth, height)) {
            return this;
        }
        
        if ((int)(Line2D.ptSegDistSq(x+(width/2), y-lineLength, x+(width/2), y + lineLength + height, _x, _y)) < distanceSelected) {
			return this;	
		}
        
        return null;
    }
    
//    public void makeValue() {
//    }
    
    public String getTimerName() {
        return AvatarSignal.getValue(value, 0);
    }
	
	public String getTimerValue() {
		return AvatarSignal.getValue(value, 1);
	}
    
	@Override
    public boolean editOnDoubleClick(JFrame frame) {
		List<String> timers = tdp.getMGUI().getAllTimers();
		TraceManager.addDev("Nb of timers:" + timers.size());
		
		JDialogAvatarTimer jdat = new JDialogAvatarTimer(frame, "Setting timer set",  getTimerName(), getTimerValue(), timers, true);
	//	jdat.setSize(350, 300);
        GraphicLib.centerOnParent(jdat, 350, 300);
        jdat.setVisible( true ); // blocked until dialog has been closed
		
		if (jdat.hasBeenCancelled()) {
			return false;
		}
		
		String val0 = jdat.getTimer();
		String val1 = jdat.getValue();
		
		// valid signal?
		if (!AvatarSignal.isAValidUseSignal(val0)) {
			JOptionPane.showMessageDialog(frame,
			"Could not change the setting of the timer: invalid name for the timer",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		if (val1.length() == 0) {
			JOptionPane.showMessageDialog(frame,
			"Could not change the setting of the timer: invalid value for the timer",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		setComplexValue(val0, val1);
		
		return true;
         
    }

	public void setComplexValue(String timerName, String timerValue) {
		value = "setTimer(" + timerName + "," + timerValue + ")";
	}

	@Override
    public int getType() {
        return TGComponentManager.AVATARSMD_SET_TIMER;
    }
    
	@Override
    public int getDefaultConnector() {
		return TGComponentManager.AVATARSMD_CONNECTOR;
    }
	
	@Override
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
}
