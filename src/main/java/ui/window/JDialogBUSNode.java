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
import ui.MainGUI;
import ui.TGComboBoxWithHelp;
import ui.TGTextFieldWithHelp;
import ui.util.IconManager;
import ui.tmldd.TMLArchiBUSNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Class JDialogBUSNode
 * Dialog for managing attributes of bus nodes
 * Creation: 31/10/2007
 * @version 1.0 31/10/2007
 * @author Ludovic APVRILLE
 */
public class JDialogBUSNode extends JDialogBase implements ActionListener  {

    public static final String[] helpStrings = {"bus.html"};
    protected MainGUI mgui;


    private boolean regularClose;

    private TMLArchiBUSNode node;


    // Panel1
    protected TGTextFieldWithHelp nodeName;

    // Panel2
    protected TGTextFieldWithHelp byteDataSize, pipelineSize, burstSize, clockRatio;
    private TGComboBoxWithHelp<String> arbitrationPolicy, privacy, refAttacks;
    protected TGTextFieldWithHelp sliceTime;
    private Vector<String> refs;

    /* Creates new form  */
    public JDialogBUSNode(MainGUI _mgui, Frame _frame, String _title, TMLArchiBUSNode _node, Vector<String> _refs) {
        super(_frame, _title, true);
        mgui = _mgui;
        node = _node;
		refs= _refs;
        initComponents();
        myInitComponents();
        pack();
    }

    private void myInitComponents() {
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();

        final int defaultMargin = 3;
        final Insets lblInsets = new Insets(defaultMargin, defaultMargin, 0, defaultMargin);
        final Insets tfdInsets = new Insets(0, defaultMargin, defaultMargin, defaultMargin);

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        //c.setLayout(gridbag0);
        c.setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        JPanel panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Bus attributes"));
        panel2.setPreferredSize(new Dimension(400, 200));


        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = lblInsets;
        //c1.anchor = GridBagConstraints.EAST;
        panel2.add(new JLabel("Bus name:", SwingConstants.RIGHT), c2);
        nodeName = new TGTextFieldWithHelp(node.getNodeName(), 30);
        nodeName.setEditable(true);
        nodeName.setFont(new Font("times", Font.PLAIN, 12));
        panel2.add(nodeName, c2);
        nodeName.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Arbitration policy:", SwingConstants.RIGHT), c2);
        arbitrationPolicy = new TGComboBoxWithHelp<>();
        arbitrationPolicy.addItem("Round Robin");
        arbitrationPolicy.addItem("Priority Based");
        arbitrationPolicy.addItem("CAN");
        arbitrationPolicy.addItem("Crossbar");
        arbitrationPolicy.setSelectedIndex(node.getArbitrationPolicy());
        panel2.add(arbitrationPolicy, c2);
        arbitrationPolicy.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Data size (in byte):", SwingConstants.RIGHT), c2);
        byteDataSize = new TGTextFieldWithHelp(""+node.getByteDataSize(), 15);
        panel2.add(byteDataSize, c2);
        byteDataSize.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Pipeline size (num. stages):", SwingConstants.RIGHT), c2);
        pipelineSize = new TGTextFieldWithHelp(""+node.getPipelineSize(), 15);
        panel2.add(pipelineSize, c2);
        pipelineSize.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Slice time (in microseconds):", SwingConstants.RIGHT), c2);
        sliceTime = new TGTextFieldWithHelp(""+node.getSliceTime(), 15);
        panel2.add(sliceTime, c2);
        sliceTime.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Burst size :", SwingConstants.RIGHT), c2);
        burstSize = new TGTextFieldWithHelp(""+node.getBurstSize(), 15);
        panel2.add(burstSize, c2);
        burstSize.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Clock divider:", SwingConstants.RIGHT), c2);
        clockRatio = new TGTextFieldWithHelp(""+node.getClockRatio(), 15);
        panel2.add(clockRatio, c2);
        clockRatio.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Bus Privacy:", SwingConstants.RIGHT), c2);
        privacy = new TGComboBoxWithHelp<>();
        privacy.addItem("Public");
        privacy.addItem("Private");
        privacy.setSelectedIndex(node.getPrivacy());
        panel2.add(privacy, c2);
        privacy.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Reference Attack:", SwingConstants.RIGHT), c2);
        refAttacks = new TGComboBoxWithHelp<>(refs);
        refAttacks.setSelectedItem(node.getRefAttack());
        panel2.add(refAttacks, c2);
        refAttacks.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        // main panel;
        /*c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.fill = GridBagConstraints.BOTH;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        
        initButtons(c0, c, this);



        // main panel;
		/*c0.gridwidth = 1;
		c0.gridheight = 1;
		c0.fill = GridBagConstraints.HORIZONTAL;
		initButtons(c0, c, this);*/
        c.add(panel2, BorderLayout.CENTER);
        initButtons(c, this);
        JPanel panelButton = initBasicButtons(this);
        c.add(panelButton, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent evt)  {
        /* if (evt.getSource() == typeBox) {
           boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
           initialValue.setEnabled(b);
           return;
           }*/


        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            TraceManager.addDev("Closing button");
            closeDialog();
        } else if (evt.getSource() == cancelButton) {
            TraceManager.addDev("Cancel button");
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

    public String getPipelineSize(){
        return pipelineSize.getText();
    }

    public String getBurstSize(){
        return burstSize.getText();
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
	
	public String getReferenceAttack(){
		return (String) refAttacks.getSelectedItem();
	}
    public String getSliceTime() {
        return sliceTime.getText();
    }


}
