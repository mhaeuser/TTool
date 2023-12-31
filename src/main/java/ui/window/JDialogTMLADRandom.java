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

import avatartranslator.AvatarRandom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import javax.swing.event.*;
//import java.util.*;


/**
 * Class JDialogTMLADRandom
 * Dialog for managing attributes of cpu nodes
 * Creation: 10/06/2008
 * @version 1.0 10/06/2008
 * @author Ludovic APVRILLE
 */
public class JDialogTMLADRandom extends JDialogBase implements ActionListener  {
    
    private boolean regularClose;
    
    private JPanel panel2;
    private Frame frame;
	private String variable, minValue, maxValue, extraAttribute1, extraAttribute2;

	private int functionId;
    
	
	// Panel2
    private JTextField jvariable, jminValue, jmaxValue, jextraAttribute1, jextraAttribute2;
    private JLabel jextraAttribute1L, jextraAttribute2L;
	private JComboBox<String> randomFunction;
    
    /* Creates new form  */
    public JDialogTMLADRandom(Frame _frame, String _title, String _variable,
                              String _minValue, String _maxValue, int _functionId,
                              String _extraAttribute1, String _extraAttribute2) {
        super(_frame, _title, true);
        frame = _frame;
        variable = _variable;
		minValue = _minValue;
		maxValue = _maxValue;
		functionId = _functionId;
		extraAttribute1 = _extraAttribute1;
        extraAttribute2 = _extraAttribute2;
        
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
        
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("RANDOM Attributes"));
        panel2.setPreferredSize(new Dimension(250, 200));
        
		c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Variable name:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        jvariable = new JTextField(variable, 30);
        jvariable.setEditable(true);
        jvariable.setFont(new Font("times", Font.PLAIN, 12));
		panel2.add(jvariable, c2);
 
        c2.gridwidth = 1;
        panel2.add(new JLabel("Minimum value:"), c2);
		c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        jminValue = new JTextField(minValue, 30);
        jminValue.setEditable(true);
        jminValue.setFont(new Font("times", Font.PLAIN, 12));
		panel2.add(jminValue, c2);
		
		c2.gridwidth = 1;
        panel2.add(new JLabel("Maximum value:"), c2);
		c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        jmaxValue = new JTextField(maxValue, 30);
        jmaxValue.setEditable(true);
        jmaxValue.setFont(new Font("times", Font.PLAIN, 12));
		panel2.add(jmaxValue, c2);
        
		c2.gridwidth = 1;
        panel2.add(new JLabel("Distribution law:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (extraAttribute1 != null) {
            randomFunction = new JComboBox<>(AvatarRandom.DISTRIBUTION_LAWS);
        } else {
            randomFunction = new JComboBox<String>();
            randomFunction.addItem("Uniform");
        }

		randomFunction.setSelectedIndex(functionId);
        randomFunction.addActionListener(this);
        panel2.add(randomFunction, c2);

        if (extraAttribute1 != null) {
            c2.gridwidth = 1;
            jextraAttribute1L = new JLabel("");
            panel2.add(jextraAttribute1L, c2);
            c2.gridwidth = GridBagConstraints.REMAINDER; //end row
            jextraAttribute1 = new JTextField(extraAttribute1, 30);
            jextraAttribute1.setEditable(true);
            jextraAttribute1.setFont(new Font("times", Font.PLAIN, 12));
            panel2.add(jextraAttribute1, c2);
        }

        if (extraAttribute2 != null) {
            c2.gridwidth = 1;
            jextraAttribute2L = new JLabel("");
            panel2.add(jextraAttribute2L, c2);
            c2.gridwidth = GridBagConstraints.REMAINDER; //end row
            jextraAttribute2 = new JTextField(extraAttribute2, 30);
            jextraAttribute2.setEditable(true);
            jextraAttribute2.setFont(new Font("times", Font.PLAIN, 12));
            panel2.add(jextraAttribute2, c2);
        }

        checkAttributesDistributionLawB();
        
        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.BOTH;
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
        } else if (evt.getSource() == randomFunction) {
            checkAttributesDistributionLawB();
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
	
	public String getVariable() {
        return jvariable.getText();
    }
    
    public String getMinValue() {
        return jminValue.getText();
    }
	
	public String getMaxValue() {
        return jmaxValue.getText();
    }
    
    public int getFunctionId() {
        return randomFunction.getSelectedIndex();
    }

    public String getExtraAttribute1() {return jextraAttribute1.getText();}

    public String getExtraAttribute2() {return jextraAttribute2.getText();}

    private void checkAttributesDistributionLawB() {
        if (extraAttribute1 != null) {
            functionId = randomFunction.getSelectedIndex();
            int nbOfExtras = AvatarRandom.NB_OF_EXTRA_ATTRIBUTES[functionId];
            jextraAttribute1.setEnabled(nbOfExtras > 0);
            if (AvatarRandom.LABELS_OF_EXTRA_ATTRIBUTES_1[functionId].length() > 0)
                jextraAttribute1L.setText(AvatarRandom.LABELS_OF_EXTRA_ATTRIBUTES_1[functionId] + ":");
            else {
                jextraAttribute1L.setText("");
            }
        }

        if (extraAttribute2 != null) {
            functionId = randomFunction.getSelectedIndex();
            int nbOfExtras = AvatarRandom.NB_OF_EXTRA_ATTRIBUTES[functionId];
            jextraAttribute2.setEnabled(nbOfExtras > 1);
            if (AvatarRandom.LABELS_OF_EXTRA_ATTRIBUTES_2[functionId].length() > 0)
                jextraAttribute2L.setText(AvatarRandom.LABELS_OF_EXTRA_ATTRIBUTES_2[functionId] + ":");
            else {
                jextraAttribute2L.setText("");
            }
        }
    }
    
    
}
