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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import myutil.TraceManager;
import ui.AvatarSignal;
import ui.avatarbd.AvatarBDBlock;
import ui.avatarbd.AvatarBDPortConnector;

/**
   * Class JDialogSignalAssociation
   * Dialog for managing associations between signals (AVATAR profile)
   * Creation: 09/04/2010
   * @version 1.0 09/04/2010
   * @author Ludovic APVRILLE
 */
public class JDialogSignalAssociation extends JDialogBase implements ActionListener, ListSelectionListener  {
    private Vector<String> signalAssociation, localSignalAssociations;
    private AvatarBDBlock block1, block2;
    private List<AvatarSignal> available1, available2;
    private AvatarBDPortConnector connector;

    private JRadioButton synchronous, asynchronous, AMS;
    
    private JLabel labelFIFO;
    private JTextField sizeOfFIFO;
    private JCheckBox blocking, isPrivate, isBroadcast, isLossy, isAMS;
    private JPanel panel1, panel2, panel3, panel4;

    private boolean cancelled = true;

    // Panel1
    private JComboBox<String> signalsBlock1, signalsBlock2;
    private JButton addButton;

    //Panel2
    private JList<String> listSignals;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;

    /* Creates new form  */
    public JDialogSignalAssociation(Frame _f, AvatarBDBlock _block1, AvatarBDBlock _block2, Vector<String> _signalAssociation,
                                    AvatarBDPortConnector _connector, String _title) {
        super(_f, _title, true);
        block1 = _block1;
        block2 = _block2;
        connector = _connector;
        signalAssociation = _signalAssociation;
        localSignalAssociations = new Vector<>();
        localSignalAssociations.addAll(signalAssociation);

        // Available signals
		if (block1 != block2) {
		    available1 = block1.getListOfAvailableSignals();
		    available2 = block2.getListOfAvailableSignals();
		} else {
		    available1 = block1.getListOfAvailableOutSignals();
		    available2 = block2.getListOfAvailableInSignals();
		}

        initComponents();
        myInitComponents();
        pack();
    }

    private void myInitComponents() {
        removeButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
        makeComboBoxes();
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagLayout gridbag3 = new GridBagLayout();
        GridBagLayout gridbag4 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        GridBagConstraints c3 = new GridBagConstraints();
        GridBagConstraints c4 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Connecting signals"));
        panel1.setMinimumSize(new Dimension(325, 250));

        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Connected signals"));
        panel2.setMinimumSize(new Dimension(325, 250));

        panel3 = new JPanel();
        panel3.setLayout(gridbag3);
        panel3.setBorder(new javax.swing.border.TitledBorder("Connector type"));
        panel3.setMinimumSize(new Dimension(700, 150));

        panel4 = new JPanel();
        panel4.setLayout(gridbag4);
        panel4.setBorder(new javax.swing.border.TitledBorder("Security issues"));
        panel4.setMinimumSize(new Dimension(700, 50));

        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);

        // second line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;

        signalsBlock1 = new JComboBox<>();
        signalsBlock1.addActionListener(this);
        //signalsBlock1.setMinimumSize(new Dimension(150, 50));
        panel1.add(signalsBlock1, c1);
        c1.gridwidth = 1;
        panel1.add(new JLabel(" = "), c1);

        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        signalsBlock2 = new JComboBox<>();
        //signalsBlock2.setMinimumSize(new Dimension(150, 50));
        signalsBlock2.addActionListener(this);
        panel1.add(signalsBlock2, c1);

        // third line panel1
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);

        // fourth line panel1
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        addButton = new JButton("Connect Signals");
        addButton.addActionListener(this);
        panel1.add(addButton, c1);

        // 1st line panel2
        listSignals = new JList<>(localSignalAssociations);
        listSignals.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSignals.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(listSignals);
        //scrollPane.setSize(300, 250);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 5;
        c2.weighty = 10.0;
        c2.weightx = 10.0;
        panel2.add(scrollPane, c2);

        // 2nd line panel2
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        panel2.add(new JLabel(""), c2);

        // third line panel2
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
        upButton = new JButton("Up");
        upButton.addActionListener(this);
        panel2.add(upButton, c2);

        downButton = new JButton("Down");
        downButton.addActionListener(this);
        panel2.add(downButton, c2);

        removeButton = new JButton("Remove connected signals");
        removeButton.addActionListener(this);
        panel2.add(removeButton, c2);

        // panel3
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 1;
        c3.weighty = 1;
        c3.weightx = 10.0;
        synchronous = new JRadioButton("synchronous");
        synchronous.setToolTipText("The sender and receiver must synchronize to exchange a message");
        synchronous.addActionListener(this);

        panel3.add(synchronous, c3);
        isBroadcast = new JCheckBox("Broadcast channel");
        isBroadcast.setSelected(connector.isBroadcast());
        panel3.add(isBroadcast, c3);

        asynchronous = new JRadioButton("asynchronous");
        asynchronous.setToolTipText("FIFO-based communication");
        asynchronous.addActionListener(this);
        panel3.add(asynchronous, c3);

	    AMS = new JRadioButton("AMS");
        AMS.setToolTipText("Communication with analog component");
        AMS.addActionListener(this);
        panel3.add(AMS, c3);
	
        ButtonGroup bt = new ButtonGroup();
	
        bt.add(synchronous);
        bt.add(asynchronous);
	    bt.add(AMS);
	
        asynchronous.setSelected(connector.isAsynchronous());
        synchronous.setSelected(connector.isSynchronous());
		AMS.setSelected(connector.isAMS());
	
        isLossy = new JCheckBox("Lossy channel");
        isLossy.setToolTipText("A lossy channel randomly losses messages");
        isLossy.setSelected(connector.isLossy());
        panel3.add(isLossy, c3);


        c3.gridwidth = 3;
        labelFIFO = new JLabel("Size of FIFO:");
        panel3.add(labelFIFO, c3);
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        sizeOfFIFO = new JTextField(""+connector.getSizeOfFIFO());
        panel3.add(sizeOfFIFO, c3);

        blocking = new JCheckBox("Blocking on write when FIFO is full");
        blocking.setToolTipText("With a non blocking channel, write(m) in a full FIFO losses m");
        blocking.setSelected(connector.isBlocking());
        panel3.add(blocking, c3);


        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.BOTH;
        c4.gridheight = 1;
        c4.weighty = 1;
        c4.weightx = 10.0;
        //panel3.add(new JLabel(" "), c3);

        isPrivate = new JCheckBox("Private channel");
        isPrivate.setToolTipText("An attacker cannot spy messages on private channels");
        isPrivate.setSelected(connector.isPrivate());
        panel4.add(isPrivate, c4);

        updateSynchronousElements();


        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.fill = GridBagConstraints.BOTH;

        c.add(panel1, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c0);
        c.add(panel3, c0);
        c.add(panel4, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.VERTICAL;
        
        initButtons(c0, c, this);

        /*JPanel panelButton = new JPanel();
          closeButton = new JButton("Save and Close", IconManager.imgic25);
          //closeButton.setPreferredSize(new Dimension(600, 50));
          closeButton.addActionListener(this);
          panelButton.add(closeButton);
          cancelButton = new JButton("Cancel", IconManager.imgic27);
          cancelButton.addActionListener(this);
          panelButton.add(cancelButton);

          JPanel middlePanel = new JPanel(new BorderLayout());
          middlePanel.add(panel3, BorderLayout.NORTH);
          middlePanel.add(panel4, BorderLayout.CENTER);
          middlePanel.add(panelButton, BorderLayout.SOUTH);

          JPanel topPanel = new JPanel();
          topPanel.add(panel1);
          topPanel.add(panel2);
          c.setLayout(new BorderLayout());
          c.add(topPanel, BorderLayout.CENTER);
          c.add(middlePanel, BorderLayout.SOUTH);*/

    }

    public void actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getActionCommand().equals("Save and Close"))  {
            closeDialog();
        } else if (evt.getSource() == addButton) {
            addSignals();
        } else if (evt.getActionCommand().equals("Cancel")) {
            cancelDialog();
        } else if (evt.getSource() == removeButton) {
            removeSignals();
        } else if (evt.getSource() == downButton) {
            downSignals();
        } else if (evt.getSource() == upButton) {
            upSignals();
        } else if (evt.getSource() == signalsBlock1) {
            updateAddButton();
        } else if (evt.getSource() == signalsBlock2) {
            updateAddButton();
        } else if (evt.getSource() == synchronous) {
            updateSynchronousElements();
        } else if (evt.getSource() == asynchronous) {
            updateSynchronousElements();
        }
	else if (evt.getSource() == AMS) {
            updateSynchronousElements();
        }
    }

    private void updateSynchronousElements() {
        boolean b = asynchronous.isSelected();
        labelFIFO.setEnabled(b);
        sizeOfFIFO.setEnabled(b);
        blocking.setEnabled(b);
        isBroadcast.setEnabled(!b);
        isLossy.setEnabled(b);
    }

    private void updateAddButton() {
        int i1 = signalsBlock1.getSelectedIndex();
        int i2 = signalsBlock2.getSelectedIndex();


        if ((i1 > -1) && (i2 > -1)) {
            AvatarSignal as1 = available1.get (i1);
            AvatarSignal as2 = available2.get (i2);

            addButton.setEnabled(as1.isCompatibleWith(as2));
        }
    }

    private void makeComboBoxes() {
        signalsBlock1.removeAllItems ();
        signalsBlock2.removeAllItems ();

        for (AvatarSignal as: available1)
            signalsBlock1.addItem(as.toString());

        for (AvatarSignal as: available2)
            signalsBlock2.addItem(as.toString());
    }

    public void addSignals() {
        int i1 = signalsBlock1.getSelectedIndex();
        int i2 = signalsBlock2.getSelectedIndex();


        if ((i1 > -1) && (i2 > -1)) {
            AvatarSignal as1 = available1.get (i1);
            AvatarSignal as2 = available2.get (i2);

            String s = AvatarBDPortConnector.makeSignalAssociation(block1, as1, block2, as2);
            localSignalAssociations.add(s);
            available1.remove (i1);
            available2.remove (i2);
            makeComboBoxes();
            listSignals.setListData(localSignalAssociations);
        }
    }

    public void removeSignals() {
        int i = listSignals.getSelectedIndex() ;
        if (i!= -1) {
            String s = localSignalAssociations.get(i);
            localSignalAssociations.removeElementAt(i);
            listSignals.setListData(localSignalAssociations);
            String sig1 = connector.getFirstSignalOfSignalAssociation(s);
            String sig2 = connector.getSecondSignalOfSignalAssociation(s);
            TraceManager.addDev("sig1"+ sig1);
            TraceManager.addDev("sig2"+ sig2);
            AvatarSignal as1 = block1.getSignalNameBySignalDef(sig1);
            AvatarSignal as2 = block2.getSignalNameBySignalDef(sig2);

            if ((as1 != null) && (as2 != null)) {
                available1.add(as1);
                available2.add(as2);
                makeComboBoxes();
            }
        }
    }

    public void downSignals() {
        int i = listSignals.getSelectedIndex();
        if ((i!= -1) && (i != localSignalAssociations.size() - 1)) {
            String o = localSignalAssociations.elementAt(i);
            localSignalAssociations.removeElementAt(i);
            localSignalAssociations.insertElementAt(o, i+1);
            listSignals.setListData(localSignalAssociations);
            listSignals.setSelectedIndex(i+1);
        }
    }

    public void upSignals() {
        int i = listSignals.getSelectedIndex();
        if (i > 0) {
            String o = localSignalAssociations.elementAt(i);
            localSignalAssociations.removeElementAt(i);
            localSignalAssociations.insertElementAt(o, i-1);
            listSignals.setListData(localSignalAssociations);
            listSignals.setSelectedIndex(i-1);
        }
    }


    public void closeDialog() {
        signalAssociation.removeAllElements();
        signalAssociation.addAll(localSignalAssociations);

        cancelled = false;
        dispose();
    }

    public void cancelDialog() {
        dispose();
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public void valueChanged(ListSelectionEvent e) {
        int i = listSignals.getSelectedIndex() ;
        if (i == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        } else {
            removeButton.setEnabled(true);
            if (i > 0) {
                upButton.setEnabled(true);
            } else {
                upButton.setEnabled(false);
            }
            if (i != localSignalAssociations.size() - 1) {
                downButton.setEnabled(true);
            } else {
                downButton.setEnabled(false);
            }
        }
    }

    public boolean isAsynchronous() {
        return asynchronous.isSelected();
    }

    public boolean isAMS() {
        return AMS.isSelected();
    }

    public boolean isSynchronous() {return synchronous.isSelected();}
    
    public String getSizeOfFIFO() {
        return sizeOfFIFO.getText();
    }

    public boolean isBlocking() {
        return blocking.isSelected();
    }

    public boolean isPrivate() {
        return isPrivate.isSelected();
    }

    public boolean isBroadcast() {
        return isBroadcast.isSelected();
    }

    public boolean isLossy() {
        return isLossy.isSelected();
    }

}
