/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class JDialogRequirement
 * Dialog for managing attributes of requirements
 * Creation: 31/05/2006
 * @version 1.0 31/05/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.event.*;
//import java.util.*;

import ui.*;


public class JDialogRequirement extends javax.swing.JDialog implements ActionListener  {
    
    private boolean regularClose;
    
    private JPanel panel1, panel2;
    private Frame frame;
    private String kind, criticality, violatedAction, attackTreeNode;
    //private String actionbegin1, actionend1, actionbegin2, actionend2;
    //private String time1, time2;
    private String text;
    private int type;
    
    // Panel1
    protected JTextArea jta;
    protected JTextField actionbegin1Text, actionend1Text, actionbegin2Text, actionend2Text, time1Text, time2Text;
    
    
    //Panel2
    private JComboBox kindBox, criticalityBox;
    private JTextField violatedActionBox, attackTreeNodeBox;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    /** Creates new form  */
    public JDialogRequirement(Frame _frame, String _title, String _text, String _kind, String _criticality, String _violatedAction, int _type, String _attackTreeNode) {
        super(_frame, _title, true);
        frame = _frame;
        text = _text;
        kind = _kind;
        criticality = _criticality;
        violatedAction = _violatedAction;
        type = _type;
		attackTreeNode = _attackTreeNode;
        
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
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        if (type == 1) {
            panel1.setBorder(new javax.swing.border.TitledBorder("Setting formal information on requirement "));
        } else if (type == 0) {
            panel1.setBorder(new javax.swing.border.TitledBorder("Setting unformal text information on requirement "));
        } else {
			panel1.setBorder(new javax.swing.border.TitledBorder("Setting text information on security requirement "));
		}
        panel1.setPreferredSize(new Dimension(350, 250));
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Other attributes:"));
        panel2.setPreferredSize(new Dimension(350, 250));
        
        // Panel 1
        /*if (formal) {
            c1.gridwidth = 1;
            c1.gridheight = 1;
            c1.weighty = 1.0;
            c1.weightx = 1.0;
            c1.fill = GridBagConstraints.BOTH;
            panel1.add(new JLabel("Begin action:", 15), c1);
            actionbegin1Text = new JTextField(actionbegin1);
            panel1.add(actionbegin1Text);
            panel1.add(new JLabel("End Action:", 15), c1);
            c1.gridwidth = GridBagConstraints.REMAINDER; //end row
            actionend1Text = new JTextField(actionend1);
            panel1.add(actionend1Text);
         
        } else {*/
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        jta = new JTextArea();
        jta.setEditable(true);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append(text);
        jta.setFont(new Font("times", Font.PLAIN, 12));
        jta.setPreferredSize(new Dimension(300, 200));
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel1.add(jsp);
        //}
        
        // Panel2
        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Type:"), c2);
        
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        kindBox = new JComboBox();
        kindBox.addItem("Functional");
        kindBox.addItem("Non functional");
        kindBox.addItem("Performance");
        kindBox.addItem("Other");
        if (kind.compareTo("Functional") == 0) {
            kindBox.setSelectedIndex(0);
        }
        if (kind.compareTo("Non functional") == 0) {
            kindBox.setSelectedIndex(1);
        }
        if (kind.compareTo("Performance") == 0) {
            kindBox.setSelectedIndex(2);
        }
        if (kind.compareTo("Other") == 0) {
            kindBox.setSelectedIndex(3);
        }
        panel2.add(kindBox, c2);
        
        c2.gridwidth = 1;
        panel2.add(new JLabel("Risk:"), c2);
        
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        criticalityBox = new JComboBox();
        criticalityBox.addItem("Low");
        criticalityBox.addItem("Medium");
        criticalityBox.addItem("High");
        if (criticality.compareTo("Low") == 0) {
            criticalityBox.setSelectedIndex(0);
        }
        if (criticality.compareTo("Medium") == 0) {
            criticalityBox.setSelectedIndex(1);
        }
        if (criticality.compareTo("High") == 0) {
            criticalityBox.setSelectedIndex(2);
        }
        panel2.add(criticalityBox, c2);
        
        c2.gridwidth = 1;
        panel2.add(new JLabel("Violated action:"), c2);
        
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        violatedActionBox = new JTextField(violatedAction, 15);
        if (type != 1) {
            violatedActionBox.setEnabled(false);
        } else {
			violatedActionBox.setEnabled(true);
		}
        panel2.add(violatedActionBox, c2);
		
		c2.gridwidth = 1;
        panel2.add(new JLabel("AttackTreeNode:"), c2);
		
		c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        attackTreeNodeBox = new JTextField(attackTreeNode, 50);
        if (type != 2) {
            attackTreeNodeBox.setEnabled(false);
        } else {
			attackTreeNodeBox.setEnabled(true);
		}
        panel2.add(attackTreeNodeBox, c2);
        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        
        c.add(panel1, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c0);
        
        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        closeButton = new JButton("Save and Close", IconManager.imgic25);
        //closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        c.add(closeButton, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        c.add(cancelButton, c0);
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
    
    public String getText() {
        return jta.getText();
    }
    
    public String getKind() {
        return (String)(kindBox.getSelectedItem());
    }
    
    public String getCriticality() {
        return (String)(criticalityBox.getSelectedItem());
    }
    
    public String getViolatedAction() {
        return violatedActionBox.getText();
    }
	
	public String getAttackTreeNode() {
        return attackTreeNodeBox.getText();
    }
    
}
