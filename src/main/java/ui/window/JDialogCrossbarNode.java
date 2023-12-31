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
import ui.tmldd.TMLArchiCrossbarNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import javax.swing.event.*;
//import java.util.*;


/**
 * Class JDialogCrossbarNode
 * Dialog for managing attributes of crossbar nodes
 * Creation: 31/10/2007
 * @version 1.0 31/10/2007
 * @author Ludovic APVRILLE adapted by Daniela Genius 10.08.2016
 */
public class JDialogCrossbarNode extends JDialogBase implements ActionListener  {

    private boolean regularClose;

    private JPanel panel2;
    private Frame frame;
    private TMLArchiCrossbarNode node;


    // Panel1
    protected JTextField nodeName;

    // Panel2
    protected JTextField byteDataSize, pipelineSize, clockRatio;
    protected JComboBox arbitrationPolicy, privacy;
    protected JTextField sliceTime;

    /* Creates new form  */
    public JDialogCrossbarNode(Frame _frame, String _title, TMLArchiCrossbarNode _node) {
        super(_frame, _title, true);
        frame = _frame;
        node = _node;

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
        panel2.setBorder(new javax.swing.border.TitledBorder("Crossbar attributes"));
        panel2.setPreferredSize(new Dimension(400, 200));

        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Crossbar name:"), c2);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        nodeName = new JTextField(node.getNodeName(), 30);
        nodeName.setEditable(true);
        nodeName.setFont(new Font("times", Font.PLAIN, 12));
        panel2.add(nodeName, c1);

	/* c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Arbitration policy:"), c2);*/

	/*   c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        arbitrationPolicy = new JComboBox();
        arbitrationPolicy.addItem("Round Robin");
        arbitrationPolicy.addItem("Priority Based");
        arbitrationPolicy.addItem("CAN");
        arbitrationPolicy.addItem("Crossbar");
        arbitrationPolicy.setSelectedIndex(node.getArbitrationPolicy());
        panel2.add(arbitrationPolicy, c2);*/

        c2.gridwidth = 1;
        panel2.add(new JLabel("Data size (in byte):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        byteDataSize = new JTextField(""+node.getByteDataSize(), 15);
        panel2.add(byteDataSize, c2);

	/*     c2.gridwidth = 1;
        panel2.add(new JLabel("Pipeline size (num. stages):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        pipelineSize = new JTextField(""+node.getPipelineSize(), 15);
        panel2.add(pipelineSize, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Slice time (in microseconds):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        sliceTime = new JTextField(""+node.getSliceTime(), 15);
        panel2.add(sliceTime, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Clock diviser:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        clockRatio = new JTextField(""+node.getClockRatio(), 15);
        panel2.add(clockRatio, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Bus Privacy:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        privacy = new JComboBox();
        privacy.addItem("Public");
        privacy.addItem("Private");
        privacy.setSelectedIndex(node.getPrivacy());
        panel2.add(privacy, c2);*/

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

    public void actionPerformed(ActionEvent evt)  {
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

    public String getNodeName() {
        return nodeName.getText();
    }

    public String getByteDataSize() {
        return byteDataSize.getText();
    }

    /*   public String getPipelineSize(){
        return pipelineSize.getText();
    }

    public String getClockRatio(){
        return clockRatio.getText();
    }

    public int getArbitrationPolicy() {
        return arbitrationPolicy.getSelectedIndex();
    }


    public int getPrivacy(){
        return privacy.getSelectedIndex();
    }
    public String getSliceTime() {
    return sliceTime.getText();
    }*/


}
