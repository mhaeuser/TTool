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

package ui.sysmlv2;


import javax.swing.*;


/**
 * Class TextBar
 * Sysmlv2 edition
 * Creation: 15/07/2021
 * @version 1.0 15/07/2021
 * @author Ludovic APVRILLE
 */
public class TextBar extends JToolBar {
   
    protected JFrameSysMLV2Text frame;
    
    public TextBar(JFrameSysMLV2Text _frame) {
        super();
        frame = _frame;
        setOrientation(SwingConstants.HORIZONTAL);
        setFloatable(true) ;
		setEnableActions(false);
        setButtons();
    }
	
	public void setEnableActions(boolean b) {
		/*mgui.actions[LEGUIAction.TEXT_BOLD].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_ITALICS].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_UNDERLINE].setEnabled(b);
		
		mgui.actions[LEGUIAction.ALIGN_LEFT].setEnabled(b);
		mgui.actions[LEGUIAction.ALIGN_RIGHT].setEnabled(b);   
		mgui.actions[LEGUIAction.ALIGN_CENTER].setEnabled(b);
		mgui.actions[LEGUIAction.ALIGN_JUSTIFY].setEnabled(b);
		
		mgui.actions[LEGUIAction.TEXT_SIZE_0].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_SIZE_1].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_SIZE_2].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_SIZE_3].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_SIZE_4].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_SIZE_5].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_SIZE_6].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_SIZE_7].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_SIZE_8].setEnabled(b);
		mgui.actions[LEGUIAction.TEXT_SIZE_9].setEnabled(b);*/
		
		
	}
    
     protected void setButtons() {
        JButton button;
        
		/*button = this.add(mgui.actions[LEGUIAction.TEXT_BOLD]);
        button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[LEGUIAction.TEXT_ITALICS]);
        button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[LEGUIAction.TEXT_UNDERLINE]);
        button.addMouseListener(mgui.mouseHandler);
		
		
		this.addSeparator();
		
		button = this.add(mgui.actions[LEGUIAction.ALIGN_LEFT]);
        button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[LEGUIAction.ALIGN_CENTER]);
        button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[LEGUIAction.ALIGN_JUSTIFY]);
        button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[LEGUIAction.ALIGN_RIGHT]);
        button.addMouseListener(mgui.mouseHandler);*/
		
		
		this.addSeparator();
		
		/*button = this.add(mgui.actions[LEGUIAction.TEXT_SIZE_0]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[LEGUIAction.TEXT_SIZE_1]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[LEGUIAction.TEXT_SIZE_2]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[LEGUIAction.TEXT_SIZE_3]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[LEGUIAction.TEXT_SIZE_4]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[LEGUIAction.TEXT_SIZE_5]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[LEGUIAction.TEXT_SIZE_6]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[LEGUIAction.TEXT_SIZE_7]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[LEGUIAction.TEXT_SIZE_8]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[LEGUIAction.TEXT_SIZE_9]);
        button.addMouseListener(mgui.mouseHandler);*/
		
        
	 }
    
} // Class





