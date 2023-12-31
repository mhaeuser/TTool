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

import myutil.TraceManager;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

//import javax.swing.event.*;
//import java.util.*;


/**
 * Class JDialogRequirement
 * Dialog for managing attributes of requirements
 * Creation: 31/05/2006
 * @version 1.0 31/05/2006
 * @author Ludovic APVRILLE
 */
public class JDialogRequirement extends JDialogBase implements ActionListener  {
    
    public static String[] kinds = {"Safety", "Functional", "Non-functional", "Performance", "Privacy", "Confidentiality", "Non-repudiation",
            "Controlled access (authorization)", "Availability", "Immunity", "Integrity", "Data origin authenticity", "Freshness", "Business",
            "Stakeholder need", "Certification", "Other"};
    
	
    private boolean regularClose;
    
    private JPanel panel1, panel2;
    private Frame frame;
    private String kind, criticality, violatedAction, attackTreeNode, referenceElements;
    //private String actionbegin1, actionend1, actionbegin2, actionend2;
    //private String time1, time2;
    private String text;
	private String id;
    private int type;

    protected ArrayList<String> extraParamIDs, extraParamValues;
    protected JTextArea jtaAttributes;
    //protected ArrayList<JTextField> extraParamTextFieldIDs, extraParamTextFieldValues;
    
    // Panel1
    protected JTextArea jta;
    protected JTextField actionbegin1Text, actionend1Text, actionbegin2Text, actionend2Text, time1Text, time2Text;
    
    
    //Panel2
    private JComboBox<String> kindBox, criticalityBox;
    private JTextField idBox, violatedActionBox, attackTreeNodeBox, referenceElementsBox;
    
    /* Creates new form  */
    public JDialogRequirement(Frame _frame, String _title, String _id, String _text, String _kind, String _criticality, String _violatedAction, int
            _type, String _attackTreeNode, String _referenceElements, ArrayList<String> _extraParamIDs, ArrayList<String> _extraParamValues) {
        super(_frame, _title, true);
        frame = _frame;
		id = _id;
        text = _text;
        kind = _kind;
        criticality = _criticality;
        violatedAction = _violatedAction;
        type = _type;
		attackTreeNode = _attackTreeNode;
		referenceElements = _referenceElements;

		extraParamIDs = _extraParamIDs;
		extraParamValues = _extraParamValues;
        //extraParamTextFieldIDs = new ArrayList<JTextField>();
        //extraParamTextFieldValues = new ArrayList<JTextField>();
        
        initComponents();
        myInitComponents();
        pack();
    }
    
    private void myInitComponents() {
    }
    
    private void initComponents() {
		int i;
		
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
        c1.fill = GridBagConstraints.BOTH;
        jta = new JTextArea();
        jta.setEditable(true);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append(text);
        jta.setFont(new Font("times", Font.PLAIN, 12));


        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //jsp.setPreferredSize(new Dimension(300, 200));
        panel1.add(jsp, c1);
        //}

        panel1.add(new JLabel("Extra attributes. ID:Value (in one line)"), c1);
        jtaAttributes = new JTextArea();
        jtaAttributes.setEditable(true);
        jtaAttributes.setMargin(new Insets(10, 10, 10, 10));
        jtaAttributes.setTabSize(3);
        for(int k=0; k<extraParamIDs.size(); k++) {
            jtaAttributes.append(extraParamIDs.get(k)+ ": " + extraParamValues.get(k));
        }
        jtaAttributes.setFont(new Font("times", Font.PLAIN, 12));

        jsp = new JScrollPane(jtaAttributes, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //jsp.setPreferredSize(new Dimension(300, 200));
        panel1.add(jsp, c1);
        
        // Panel2
        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
		
		
        panel2.add(new JLabel("ID: "), c2);
		
		c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        idBox = new JTextField(id, 50);
        panel2.add(idBox, c2);
		
		c2.gridwidth = 1;
        panel2.add(new JLabel("Type:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        kindBox = new JComboBox<>();
		
		for(i=0; i<kinds.length; i++) {
			kindBox.addItem(kinds[i]);
		}
        /*kindBox.addItem("Functional");
        kindBox.addItem("Non functional");
        kindBox.addItem("Performance");
		kindBox.addItem("Privacy");
		kindBox.addItem("Privacy");
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
        }*/
		
		for(i=0; i<kinds.length; i++) {
			if (kind.compareTo(kinds[i]) == 0) {
				kindBox.setSelectedIndex(i);
			}
		}
		
        panel2.add(kindBox, c2);
        
        c2.gridwidth = 1;
        panel2.add(new JLabel("Risk:"), c2);
        
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        criticalityBox = new JComboBox<>();
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
        
        if (referenceElements != null) {
			c2.gridwidth = 1;
			panel2.add(new JLabel("References:"), c2);
			
			c2.gridwidth = GridBagConstraints.REMAINDER; //end row
			referenceElementsBox = new JTextField(referenceElements, 50);
			referenceElementsBox.setEnabled(true);
			panel2.add(referenceElementsBox, c2);
		}
        
		if (violatedAction != null) {
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
		}
		
		if (attackTreeNode != null) {
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
		}
		
		
        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.fill = GridBagConstraints.BOTH;
        
        c.add(panel1, c0);
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
	
	public String getId() {
        return idBox.getText();
    }
    
    public String getText() {
        return jta.getText();
    }
    
    public String getKind() {
        return kindBox.getModel().getElementAt(kindBox.getSelectedIndex());
    }
    
    public String getCriticality() {
        return criticalityBox.getModel().getElementAt(criticalityBox.getSelectedIndex());
    }
    
    public String getViolatedAction() {
        return violatedActionBox.getText();
    }
	
	public String getAttackTreeNode() {
        return attackTreeNodeBox.getText();
    }
    
    public String getReferenceElements() {
        return referenceElementsBox.getText();
    }

    public String getExtraAttributes() {
        return jtaAttributes.getText();
    }

    public static String getKindFromString(String _kind) {
        _kind = _kind.toLowerCase();
        for (int i=0; i<kinds.length; i++) {
            //TraceManager.addDev("Comparing >" + kinds[i] + "< with >" + _kind + "<");
            if (kinds[i].toLowerCase().compareTo(_kind) ==0 ) {
                TraceManager.addDev("ok");
                return kinds[i];
            }
        }

        return null;
    }
    
}
