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

//import java.awt.*;
import tmltranslator.*;
import ui.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

//import org.w3c.dom.*;
//import org.xml.sax.*;
//import javax.xml.parsers.*;

/**
 * Class TMLActivityDiagramPanel
 * Panel used for drawing activity diagrams of TML Tasks
 * Creation: 28/10/2005
 * @version 1.0 28/10/2005
 * @author Ludovic APVRILLE
 */
public class TMLActivityDiagramPanel extends TDiagramPanel {
    
    public  TMLActivityDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        addComponent(400, 50, TGComponentManager.TMLAD_START_STATE, false);
        /*TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);*/
    }
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
        return false;
    }
    
    public boolean actionOnAdd(TGComponent tgc) {
        return false;
    }
    public boolean actionOnValueChanged(TGComponent tgc) {
        return false;
    }
    
    public  boolean actionOnRemove(TGComponent tgc) {
        return false;
    }

    public String getXMLHead() {
        return "<TMLActivityDiagramPanel name=\"" + name + "\"" + sizeParam() + zoomParam() + " >";
    }
    
    public String getXMLTail() {
        return "</TMLActivityDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<TMLActivityDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel +
                "\"" + zoomParam()  + ">";
    }
    
    public String getXMLSelectedTail() {
        return "</TMLActivityDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<TMLActivityDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 +
                "\"" + zoomParam()  + ">";
    }
    
    public String getXMLCloneTail() {
        return "</TMLActivityDiagramPanelCopy>";
    }
    
    public void makeGraphicalOptimizations() {
        // Segments of connector that mask components
        
        // Components over others
        
        // Position correctly guards of choice
    }
    
    public void enhance() {
        //
        Vector<TGComponent> v = new Vector<TGComponent>();
        TGComponent o;
        Iterator<TGComponent> iterator = componentList.iterator();
        
        while(iterator.hasNext()) {
            o = iterator.next();
            if (o instanceof TMLADStartState) {
                enhance(v, o);
            }
        }
        
        mgui.changeMade(this, MOVE_CONNECTOR);
        repaint();
    }
    
    public void enhance(Vector<TGComponent> v, TGComponent tgc) {
        TGComponent tgc1;
        TGConnector tgcon;
        int i;
        
        //
        
        if (tgc == null) {
            return;
        }
        
        if (v.contains(tgc)) {
            return;
        }
        
        v.add(tgc);
        
        //
        if (!(tgc instanceof TMLADStartState)) {
            for(i=0; i<tgc.getNbNext(); i++) {
                tgc1 = getNextTGComponent(tgc, i);
                tgcon = getNextTGConnector(tgc, i);
                if (tgcon.getAutomaticDrawing()) {
                    if ((tgc1 != null) && (tgcon != null)) {
                        tgcon.alignOrMakeSquareTGComponents();
                    }
                }
            }
        }
        
        // Explore next elements
        for(i=0; i<tgc.getNbNext(); i++) {
            tgc1 = getNextTGComponent(tgc, i);
            enhance(v, tgc1);
        }
    }
	
	public boolean hasAutoConnect() {
		return true;
	}
	
    public List<String> getAllCryptoConfig() {
		List<String> cc = new ArrayList<String>();
		List<TGComponent> comps = getAllComponentList();
		
		for (TGComponent c: comps) {
		    if (c instanceof TMLADEncrypt) {
		    	TMLADEncrypt en= (TMLADEncrypt) c;
		    	
		    	if (!en.securityContext.isEmpty()) {
		    		cc.add(en.securityContext);
		    	}
		    }
		}
		
		return cc;
    }

    public List<String> getAllNonce() {
    	List<String> ns=new ArrayList<String>();
    	List<TGComponent> comps= getAllComponentList();
    	
    	for (TGComponent c: comps) {
    		if (c instanceof TMLADEncrypt) {
    			TMLADEncrypt en= (TMLADEncrypt) c;
    			if (!en.securityContext.isEmpty() && en.type.equals(SecurityPattern.NONCE_PATTERN)) {
    				ns.add(en.securityContext);
    			}
    		}
    	}
    	
    	return ns;
    }

    public List<String> getAllKeys() {
    	List<String> ns=new ArrayList<String>();
    	List<TGComponent> comps= getAllComponentList();
    	
    	for (TGComponent c: comps) {
    		if (c instanceof TMLADEncrypt) {
    			TMLADEncrypt en= (TMLADEncrypt) c;
    			if (!en.securityContext.isEmpty()) {
    				if ((en.type.equals(SecurityPattern.SYMMETRIC_ENC_PATTERN) || en.type.equals(SecurityPattern.MAC_PATTERN))) {
	    				ns.add(en.securityContext);
	    			}
	    			else if (en.type.equals(SecurityPattern.ASYMMETRIC_ENC_PATTERN)) {
	    				ns.add(en.securityContext);
	    				//ns.add("pubKey" + en.securityContext);
	    			}
    			}
    		}
    	}
    	
    	return ns;
    }

}
