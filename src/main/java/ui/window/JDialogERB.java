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




package ui.window;

import ui.util.IconManager;
import ui.ebrdd.EBRDDERB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import javax.swing.event.*;
//import java.util.*;


/**
 * Class JDialogERB
 * Dialog for managing attributes of Event Reaction Blocks (ERC)
 * Creation: 10/09/2009
 * @version 1.0 10/09/2009
 * @author Ludovic APVRILLE
 */
public class JDialogERB extends JDialogBase implements ActionListener  {
    
    private boolean regularClose;
    
    private JPanel panel2;
    private Frame frame;
	private EBRDDERB erb;
 
	// Panel
    protected JTextField evt, condition, action;
    
    /* Creates new form  */
    public JDialogERB(Frame _frame, EBRDDERB _erb) {
        super(_frame, "Event Reaction Block", true);
        frame = _frame;
        erb = _erb;
        
        initComponents();
        myInitComponents();
        pack();
    }
    
    private void myInitComponents() {
    }
    
    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        //GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("ERB attributes"));
        panel2.setPreferredSize(new Dimension(400, 300));
        
		c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
       
        c2.gridwidth = 1;
        panel2.add(new JLabel("Event:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        evt = new JTextField(""+erb.getEvent(), 30);
        panel2.add(evt, c2);
		
		c2.gridwidth = 1;
        panel2.add(new JLabel("Condititon:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        condition = new JTextField(""+erb.getCondition(), 30);
        panel2.add(condition, c2);
		
		c2.gridwidth = 1;
        panel2.add(new JLabel("Action:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        action = new JTextField(""+erb.getAction(), 30);
        panel2.add(action, c2);
        
        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c0);
        
        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        
        initButtons(c0, c, this);
    }
	
    public void	actionPerformed(ActionEvent evt)  {
       /* if (evt.getSource() == typeBox) {
            boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
            initialValue.setEnabled(b);
            return;
        }*/

        
        String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
    }
    
    public void closeDialog() {
        regularClose = true;
        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
	
	public boolean isRegularClose() {
        return regularClose;
    }
    
	public String getEvent() {
		return evt.getText();
	}
	
	public String getCondition() {
		return condition.getText();
	}
	
	public String getAction() {
		return action.getText();
	}
	
	
}
