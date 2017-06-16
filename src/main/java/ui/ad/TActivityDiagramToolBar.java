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

import ui.MainGUI;
import ui.TGUIAction;
import ui.TToolBar;

import javax.swing.*;

//import java.awt.*;
//import java.awt.event.*;

/**
 * Class TActivityDiagramToolBar
 * Toolbar associated with activity diagrams
 * Creation: 12/12/2003
 * @version 1.0 12/12/2003
 * @author Ludovic APVRILLE
 */
public class TActivityDiagramToolBar extends TToolBar {
    
    
    public TActivityDiagramToolBar(MainGUI _mgui) {
        super(_mgui);
    }
    
    protected void setActive(boolean b) {
        mgui.actions[TGUIAction.AD_EDIT].setEnabled(b);
        mgui.actions[TGUIAction.UML_NOTE].setEnabled(b);
        mgui.actions[TGUIAction.CONNECTOR_COMMENT].setEnabled(b);
        mgui.actions[TGUIAction.AD_CONNECTOR].setEnabled(b);
        mgui.actions[TGUIAction.AD_ACTION_STATE].setEnabled(b);
        mgui.actions[TGUIAction.AD_PARALLEL].setEnabled(b);
        mgui.actions[TGUIAction.AD_SEQUENCE].setEnabled(b);
        mgui.actions[TGUIAction.AD_PREEMPTION].setEnabled(b);
        mgui.actions[TGUIAction.AD_CHOICE].setEnabled(b);
        mgui.actions[TGUIAction.AD_START].setEnabled(b);
        mgui.actions[TGUIAction.AD_STOP].setEnabled(b);
        mgui.actions[TGUIAction.AD_JUNCTION].setEnabled(b);
        mgui.actions[TGUIAction.AD_DETERMINISTIC_DELAY].setEnabled(b);
        mgui.actions[TGUIAction.AD_NON_DETERMINISTIC_DELAY].setEnabled(b);
        mgui.actions[TGUIAction.AD_DELAY_NON_DETERMINISTIC_DELAY].setEnabled(b);
        mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER].setEnabled(b);
        mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER_WITH_LATENCY].setEnabled(b);
		mgui.actions[TGUIAction.AD_TIME_CAPTURE].setEnabled(b);
		mgui.actions[TGUIAction.AD_ARRAY_GET].setEnabled(b);
		mgui.actions[TGUIAction.AD_ARRAY_SET].setEnabled(b);
        mgui.actions[TGUIAction.ACT_TOGGLE_JAVA].setEnabled(b);
        mgui.actions[TGUIAction.ACT_ENHANCE].setEnabled(b);
		
		mgui.actions[TGUIAction.ACT_ZOOM_MORE].setEnabled(false);
		mgui.actions[TGUIAction.ACT_ZOOM_LESS].setEnabled(false);
		mgui.actions[TGUIAction.ACT_SHOW_ZOOM].setEnabled(false);
		
		mgui.actions[TGUIAction.ACT_MODEL_CHECKING].setEnabled(b);
		mgui.actions[TGUIAction.ACT_ONECLICK_LOTOS_RG].setEnabled(b);
		mgui.actions[TGUIAction.ACT_ONECLICK_RTLOTOS_RG].setEnabled(b);
		
		mgui.updateZoomInfo();
    }
    
    protected void setButtons() {
        JButton button;
        
        button = this.add(mgui.actions[TGUIAction.AD_EDIT]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.UML_NOTE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.CONNECTOR_COMMENT]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.AD_CONNECTOR]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.AD_START]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_STOP]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.AD_ACTION_STATE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_PARALLEL]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_SEQUENCE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_PREEMPTION]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_CHOICE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_JUNCTION]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.AD_DETERMINISTIC_DELAY]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_NON_DETERMINISTIC_DELAY]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_DELAY_NON_DETERMINISTIC_DELAY]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER_WITH_LATENCY]);
        button.addMouseListener(mgui.mouseHandler);
		
		/*button = this.add(mgui.actions[TGUIAction.AD_TIME_CAPTURE]);
        button.addMouseListener(mgui.mouseHandler);*/
		
        this.addSeparator();
         
        button = this.add(mgui.actions[TGUIAction.AD_ARRAY_GET]);
        button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.AD_ARRAY_SET]);
        button.addMouseListener(mgui.mouseHandler);
		
        this.addSeparator();
         
        button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_JAVA]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
         
        button = this.add(mgui.actions[TGUIAction.ACT_ENHANCE]);
        button.addMouseListener(mgui.mouseHandler);
    }
} // Class





