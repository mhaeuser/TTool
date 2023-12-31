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
import ui.*;
import ui.util.IconManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class TADChoice
 * Choice with guards. to be used in activity diagrams
 * Creation: 21/12/2003
 * @version 1.0 21/12/2003
 * @author Ludovic APVRILLE
 */
public class TADChoice extends TADComponentWithSubcomponents implements FatherDependencyDrawingInterface {

	public static final String EMPTY_GUARD_TEXT = "[ ]";
	private static final String TRUE_GUARD_TEXT = "[ true ]";

	//protected int lineLength = 10;
    
	protected static final int OUT_LINE_LENGTH = 25;
	protected static final int MARGIN = 5;
    
//	Issue # 31 private int textX1, textY1, textX2, textY2, textX3, textY3;
	//private double dtextX1, dtextY1, dtextX2, dtextY2, dtextX3, dtextY3;
    
    protected int stateOfError = 0;
    
    public TADChoice(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        createConnectingPoints();

        initScaling( 20, 20 );
        
        nbInternalTGComponent = 3;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        createGuards();
        
        moveable = true;
        editable = false;
        removable = true;
        
        name = "choice";
        
        myImageIcon = IconManager.imgic208;
    }

    protected void createConnectingPoints() {
        nbConnectingPoint = 4;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new TGConnectingPointAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointAD(this, -OUT_LINE_LENGTH, 0, false, true, 0.0, 0.5);
        connectingPoint[2] = new TGConnectingPointAD(this, OUT_LINE_LENGTH, 0, false, true, 1.0, 0.5);
        connectingPoint[3] = new TGConnectingPointAD(this, 0, OUT_LINE_LENGTH,  false, true, 0.5, 1.0);
        addTGConnectingPointsComment();
    }
    
    protected void createGuards() {
    	
    	// Issue #31
    	final int textX1 = - scale( OUT_LINE_LENGTH );
    	final int scaledMargin = scale( MARGIN ); 
    	final int textY1 = height / 2 - scaledMargin;
        TGCOneLineText tgc = new TGCOneLineText( x + textX1, y + textY1, textX1-100, textX1 + 15 , textY1, textY1 + 25, true, this, tdp );
        tgc.setValue( EMPTY_GUARD_TEXT );
        tgc.setName("guard 1");
        tgcomponent[ 0 ] = tgc;
        
    	// Issue #31
        final int textX2 = width + scaledMargin;
        final int textY2 = height / 2 - scaledMargin;
        tgc = new TGCOneLineText( x + textX2, y + textY2, textX2, textX2 + 80, textY2, textY2 + 25, true, this, tdp);
        tgc.setValue( EMPTY_GUARD_TEXT );
        tgc.setName("guard 2");
        tgcomponent[ 1 ] = tgc;
        
    	// Issue #31
        final int textX3 = width / 2 + scaledMargin;
        final int textY3 = height + scale( 15 );
        tgc = new TGCOneLineText( x + textX3, y + textY3, textX3 - 20, textX3 + 80, textY3, textY3 + 25, true, this, tdp );
        tgc.setValue( EMPTY_GUARD_TEXT );
        tgc.setName("guard 3");
        tgcomponent[ 2 ] = tgc;
    }

    public void setGuard(int index, String g) {
        if ((index < 0) || (index > 2)) {
            return;
        }
        tgcomponent[index].setValue(g);

    }
    
    @Override
    protected void internalDrawing(Graphics g) {
		if (stateOfError > 0)  {
			Color c = g.getColor();
			switch(stateOfError) {
			case ErrorHighlight.OK:
				g.setColor(ColorManager.CHOICE);
				break;
			default:
				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}
			// Making the polygon
			int [] px1 = {x+(width/2), x+width+2, x + (width/2), x};
			int [] py1 = {y, y + height/2, y+height+2, y+height/2};
			g.fillPolygon(px1, py1, 4);
			g.setColor(c);
		}

		g.drawLine(x+(width/2), y, x+width, y + height/2);
        g.drawLine(x, y + height / 2, x+width/2, y + height);
        g.drawLine(x + width/2, y, x, y + height/2);
        g.drawLine(x + width, y + height/2, x + width/2, y + height);
        
        // Issue #31
        final int lineOutLength = scale( OUT_LINE_LENGTH );
        
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x, y + height/2, x-lineOutLength, y + height/2);
        g.drawLine(x + width, y + height/2, x+ width + lineOutLength, y + height/2);
        g.drawLine(x+(width/2), y + height, x+(width/2), y + height + lineOutLength);
    }
    
    @Override
    public TGComponent isOnOnlyMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }

        // Issue #31
        final int lineOutLength = scale( OUT_LINE_LENGTH );

        // horizontal line
		if ((int)(Line2D.ptSegDistSq(x+(width/2), y + height, x+(width/2), y + height + lineOutLength, _x, _y)) < distanceSelected) {
			return this;	
		}
		
		if ((int)(Line2D.ptSegDistSq(x + width, y + height/2, x+ width + lineOutLength, y + height/2, _x, _y)) < distanceSelected) {
			return this;
		}
		
		if ((int)(Line2D.ptSegDistSq(x, y + height/2, x-lineOutLength, y + height/2, _x, _y)) < distanceSelected) {
			return this;
		}
		
		if ((int)(Line2D.ptSegDistSq(x+(width/2), y, x+(width/2), y - lineLength, _x, _y)) < distanceSelected) {
			return this;
		}

        return null;
    }
    
    public String getGuard(int i) {
        if ((i>=0) && (i<nbInternalTGComponent)) {
            return tgcomponent[i].getValue();
        }
        return "";
    }
    
    public void setGuard(String guard, int i) {
         if ((i>=0) && (i<nbInternalTGComponent)) {
            tgcomponent[i].setValue(guard);
         }
    }

	public List<String> getGuards()        {
		List<String> guards = new ArrayList<String>();
		for( int i = 0; i < nbInternalTGComponent; i++ ) {
			guards.add( getGuard(i) );
		}
		return guards;
	}

    /**
     * Issue #69
     * @param point :   Connecting point
     * @return      :   Guard of connecting point
     */
    public TGCOneLineText getGuardForConnectingPoint( final TGConnectingPoint point ) {
		final int index = Arrays.asList( connectingPoint ).indexOf( point );

		if ( index < 0 ) {
			throw new IllegalArgumentException( "No guard found for connecting point " + point );
		}
		
		return (TGCOneLineText) tgcomponent[ index - 1 ];
    }
   
    @Override
    public int getType() {
        return TGComponentManager.TAD_CHOICE;
    }
    
    @Override
   	public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_AD_DIAGRAM;
    }
	
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
    
    /* Issue #69
     * (non-Javadoc)
     * @see ui.AbstractCDElement#canBeDisabled()
     */
    @Override
    public boolean canBeDisabled() {
    	return false;
    }
    
    /**
     * Issue #69
     * @param label :   TGOneLineText
     * @return      :   true if label is neither null nor empty nor []
     */
    @Override
    public boolean canLabelBeDisabled( final TGCOneLineText label ) {
    	return label.getValue() != null && !label.getValue().isEmpty() && !EMPTY_GUARD_TEXT.equals( label.getValue() );
    }
    
    /**
     * Issue #69
     * @param guard :    Guard value
     * @return      :    Effective condition
     */
    public String getEffectiveCondition( final TGCOneLineText guard ) {
    	if ( guard.isEnabled() ) {
   	 	   return guard.getValue();
 	   	}

		return TRUE_GUARD_TEXT;
    }

    public boolean shallBeDrawn(TGComponent _tgc) {
        // Must find which subcomponent ist concerns.
        int index = -1;
        for(int i=0; i<tgcomponent.length; i++) {
            if (tgcomponent[i] == _tgc) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return true;
        }

        return !tgconnectingPointAtIndex(index+1).isFree();

      
    }
}
