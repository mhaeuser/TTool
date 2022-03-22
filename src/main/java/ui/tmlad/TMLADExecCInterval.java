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

import java.awt.Graphics;

import ui.CDElement;
import ui.TDiagramPanel;
import ui.TGCTimeInterval;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.ad.TADExec;
import ui.ad.TGConnectingPointAD;

/**
 * Class TMLADExecCInterval
 * Non deterministic duration and custom operator. To be used in TML activity diagrams
 * Creation: 21/05/2008
 * @version 1.0 21/05/2008
 * @author Ludovic APVRILLE
 */
public class TMLADExecCInterval extends TADExec {

    
    public TMLADExecCInterval(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp, "", "value of the time interval" );

        name = "execIInterval";

    }

    @Override
    protected TGCTimeInterval createInternalComponent() {
    	return new TGCTimeInterval(x+textX, y+textY, -75, 30, textY - 10, textY + 10, true, this, tdp);
    }
    
    protected TGCTimeInterval getTimeInterval() {
    	return (TGCTimeInterval) tgcomponent[0];
    }
    

    
    public String getMinDelayValue() {
        return getTimeInterval().getMinDelay();
    }
    
    public String getMaxDelayValue() {
        return getTimeInterval().getMaxDelay();
    }
    
    public void setMinValue(String val) {
    	getTimeInterval().setMinDelay(val);
    }
    
    public void setMaxValue(String val) {
    	getTimeInterval().setMaxDelay(val);
    }
    
    @Override
    public int getType() {
        return TGComponentManager.TMLAD_EXECC_INTERVAL;
    }
    
    @Override
    public int getDefaultConnector() {
    	return TGComponentManager.CONNECTOR_TMLAD;
    }


	@Override
	protected TGConnectingPointAD createConnectingPoint(CDElement _container, int _x, int _y, boolean _in, boolean _out,
			double _w, double _h) {
		return new TGConnectingPointTMLAD( _container, _x, _y, _in, _out, _w, _h );
	}

	@Override
	protected void drawInternalSymbol(	Graphics g,
										int symbolWidth,
										int symbolHeight ) {
        // -
        g.drawLine(x + (width/2) - symbolWidth, y+(height-symbolHeight)/2,  x + (width/2) + symbolWidth, y+(height-symbolHeight)/2);
        
        // |
        g.drawLine(x + (width/2) - symbolWidth, y+(height-symbolHeight)/2, x + (width/2)- symbolWidth, y+(height+symbolHeight)/2);
        
        // -
        g.drawLine(x + (width/2) - symbolWidth, y+(height-symbolHeight)/2 + symbolHeight,  x + (width/2) + symbolWidth, y+(height-symbolHeight)/2 + symbolHeight);
	}
}
