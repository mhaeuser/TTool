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
import org.apache.batik.anim.timing.Trace;
import ui.TGComponent;
import ui.tmldd.TMLArchiCommunicationArtifact;
import ui.tmldd.TMLArchiCommunicationNode;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmldd.TMLArchiMemoryNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

//import javax.swing.event.*;


/**
 * Class JDialogTMLTaskArtifact
 * Dialog for managing artifacts on hw nodes
 * Creation: 19/09/2007
 *
 * @author Ludovic APVRILLE
 * @version 1.0 19/09/2007
 */
public class JDialogCommunicationArtifact extends JDialogBase implements ActionListener {

    private boolean regularClose;
    private boolean emptyList = false;

    private JPanel panel2, panel3, panel4, panel5;
    private JList<String> mappableElements, mappedElements;
    private DefaultListModel<String> mappableElementsModel, mappedElementsModel;
    private JButton mapButton;

    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;

    private final Frame frame;
    private final TMLArchiCommunicationArtifact artifact;


    protected JComboBox<String> referenceCommunicationName, priority;

    private ArrayList<String> mappedUnits = new ArrayList<>();

    /* Creates new form  */
    public JDialogCommunicationArtifact(Frame _frame, String _title, TMLArchiCommunicationArtifact _artifact, ArrayList<String> _mappedUnits) {
        super(_frame, _title, true);
        frame = _frame;
        artifact = _artifact;
        if (_mappedUnits != null) {
            mappedUnits.addAll(_mappedUnits);
        }


        //TraceManager.addDev("init components");

        initComponents();

        //TraceManager.addDev("my init components");

        myInitComponents();

        //TraceManager.addDev("pack");
        pack();
    }

    private void myInitComponents() {
        //selectPriority();
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag3 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagLayout gridbag4 = new GridBagLayout();
        GridBagLayout gridbag5 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c3 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        GridBagConstraints c4 = new GridBagConstraints();
        GridBagConstraints c5 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Artifact attributes"));
        panel2.setPreferredSize(new Dimension(650, 350));

        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Channel:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        TraceManager.addDev("Getting communications");
        Vector<String> list = artifact.getTDiagramPanel().getMGUI().getAllTMLCommunicationNames();
        int index = 0;
        if (list.size() == 0) {
            list.add("No communication to map");
            emptyList = true;
        } else {
            index = indexOf(list, artifact.getValue());
        }

        TraceManager.addDev("Got communications. Index=" + index);

        referenceCommunicationName = new JComboBox<>(list);
        referenceCommunicationName.setSelectedIndex(index);
        referenceCommunicationName.addActionListener(this);
        //referenceTaskName.setEditable(true);
        //referenceTaskName.setFont(new Font("times", Font.PLAIN, 12));
        panel2.add(referenceCommunicationName, c2);

        list = new Vector<String>();
        for (int i = 0; i < 11; i++) {
            list.add("" + i);
        }
        panel2.add(new JLabel("Priority:"), c2);
        priority = new JComboBox<>(list);
        //TraceManager.addDev("Priority: " + artifact.getPriority());
        panel2.add(priority, c2);
        priority.setSelectedIndex(artifact.getPriority());


        // Panel for multiple mappings
        panel3 = new JPanel();
        panel3.setLayout(gridbag3);
        panel3.setBorder(new javax.swing.border.TitledBorder("To be selected"));
        panel3.setPreferredSize(new Dimension(300, 350));
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.gridheight = 1;
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(new JLabel("Memories and buses:"), c3);
        Vector<String> memAndBuses = makeListOfMappableArchUnits(mappedUnits);
        //TraceManager.addDev("Size of jlist:" + memAndBuses.size());
        mappableElementsModel = new DefaultListModel<>();
        for (String s: memAndBuses) {
            mappableElementsModel.addElement(s);
        }
        mappableElements = new JList<>(mappableElementsModel);
        mappableElements.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mappableElements.setVisibleRowCount(-1);
        mappableElements.addListSelectionListener(e -> enableMapButton());
        JScrollPane scrollPane = new JScrollPane(mappableElements);
        scrollPane.setPreferredSize(new Dimension(300, 250));
        c3.gridheight = 20;
        c3.fill = GridBagConstraints.BOTH;
        panel3.add(scrollPane, c3);

        c3.gridheight = 1;
        c3.fill = GridBagConstraints.HORIZONTAL;
        mapButton = new JButton("Map");
        enableMapButton();
        mapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapSelectedElements();
                enableMapButton();
                enableRemoveUpDownButton();
            }
        });
        panel3.add(mapButton, c3);


        panel4 = new JPanel();
        panel4.setLayout(gridbag4);
        panel4.setBorder(new javax.swing.border.TitledBorder("Already selected"));
        panel4.setPreferredSize(new Dimension(300, 350));
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.gridheight = 1;
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(new JLabel("Selected memories and buses:"), c4);
        mappedElementsModel = new DefaultListModel<>();
        for (String s: mappedUnits) {
            mappedElementsModel.addElement(s);
        }
        mappedElements = new JList<>(mappedElementsModel);
        mappedElements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mappedElements.setVisibleRowCount(-1);
        mappedElements.addListSelectionListener(e -> enableRemoveUpDownButton());
        scrollPane = new JScrollPane(mappedElements);
        scrollPane.setPreferredSize(new Dimension(300, 250));
        c4.gridheight = 20;
        c4.fill = GridBagConstraints.BOTH;
        panel4.add(scrollPane, c4);

        c4.gridheight = 1;
        c4.fill = GridBagConstraints.HORIZONTAL;
        upButton = new JButton("Up");
        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				up();
            }
        });
        panel4.add(upButton, c4);
        downButton = new JButton("Down");
        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				down();
            }
        });
        panel4.add(downButton, c4);
        removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				remove();
            }
        });
        panel4.add(removeButton, c4);

        enableRemoveUpDownButton();


        panel5 = new JPanel();
        panel5.setLayout(gridbag5);
        c5.gridwidth = 1;
        c5.gridheight = 1;
        c5.weighty = 1.0;
        c5.weightx = 1.0;
        c5.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(panel3, c5);
        c5.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel5.add(panel4, c5);


        // main panel;
        JTabbedPane pane = new JTabbedPane();
        pane.addTab("Basic options", panel2);
        pane.addTab("Multi mapping", panel5);


        c0.gridheight = 30;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        //c0.gridwidth = 1; //end row
        c0.fill = GridBagConstraints.BOTH;
        //c.add(panel2, c0);

        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(pane, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;

        initButtons(c0, c, this);
    }

    public void actionPerformed(ActionEvent evt) {
       /* if (evt.getSource() == typeBox) {
            boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
            initialValue.setEnabled(b);
            return;
        }*/

        if (evt.getSource() == referenceCommunicationName) {
            //selectPriority();
        }


        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Save and Close")) {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
    }

    public void enableMapButton() {
        if (mapButton == null) {
            return;
        }
        if (mappableElements == null) {
            mapButton.setEnabled(false);
            return;
        }
        mapButton.setEnabled(mappableElements.getSelectedIndex() != -1);

    }

    public void enableRemoveUpDownButton() {
        if ((upButton == null) || (downButton == null) || (removeButton == null)) {
            return;
        }
        if (mappedElements == null) {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            removeButton.setEnabled(false);
            return;
        }
        int index = mappedElements.getSelectedIndex();
        upButton.setEnabled(index > 0);
        downButton.setEnabled((index > -1) && (index <mappedElementsModel.size()-1));
        removeButton.setEnabled(index > -1);
    }


    public void selectPriority() {
        //
        TraceManager.addDev("Select priority");
        int index = ((TMLArchiDiagramPanel) artifact.getTDiagramPanel()).getMaxPriority((String) (referenceCommunicationName.getSelectedItem()));
        priority.setSelectedIndex(index);
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

    public String getReferenceCommunicationName() {
        if (emptyList) {
            return null;
        }
        String tmp = (String) (referenceCommunicationName.getSelectedItem());
        int index = tmp.indexOf("::");
        if (index == -1) {
            return tmp;
        }
        return tmp.substring(0, index);
    }

    public String getCommunicationName() {
        String tmp = (String) (referenceCommunicationName.getSelectedItem());
        int index = tmp.indexOf("::");
        if (index == -1) {
            return tmp;
        }
        tmp = tmp.substring(index + 2, tmp.length());

        index = tmp.indexOf("(");
        if (index > -1) {
            tmp = tmp.substring(0, index).trim();
        }
        //
        return tmp;
    }

    public String getTypeName() {
        String tmp = (String) (referenceCommunicationName.getSelectedItem());
        int index1 = tmp.indexOf("(");
        int index2 = tmp.indexOf(")");
        if ((index1 > -1) && (index2 > index1)) {
            return tmp.substring(index1 + 1, index2);
        }
        return "";
    }


    public int indexOf(Vector<String> _list, String name) {
        //TraceManager.addDev("Computing index with name=" + name + "\n");
        int i = 0;
        for (String s : _list) {
            if (s.equals(name)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public int getPriority() {
        return priority.getSelectedIndex();
    }

    private Vector<String> makeListOfMappableArchUnits(ArrayList<String> alreadyMapped) {

        java.util.List<TGComponent> componentList = artifact.getTDiagramPanel().getComponentList();
        Vector<String> list = new Vector<String>();

        for (TGComponent tgc : componentList) {
            if (tgc instanceof TMLArchiCommunicationNode) {
                if (!(alreadyMapped.contains(tgc.getName()))) {
                    //TraceManager.addDev("Adding component: " + tgc.getName());
                    list.add(tgc.getName());
                }
            }
        }

        return list;
    }

    private void mapSelectedElements() {
        // Each selected value is added to the list of mapped elements
        for(String s: mappableElements.getSelectedValuesList()) {
            mappedElementsModel.addElement(s);
            mappableElementsModel.removeElement(s);
        }

    }

    private void up() {
        int index = mappedElements.getSelectedIndex();
        if (index > 0) {
            String s = mappedElementsModel.getElementAt(index);
            mappedElementsModel.removeElementAt(index);
            mappedElementsModel.insertElementAt(s,index-1);
        }
    }

    private void down() {
        int index = mappedElements.getSelectedIndex();
        if ((index > -1) && (index <mappedElementsModel.size()-1)) {
            String s = mappedElementsModel.getElementAt(index);
            mappedElementsModel.removeElementAt(index);
            mappedElementsModel.insertElementAt(s,index+1);
        }
    }

    private void remove() {
        int index = mappedElements.getSelectedIndex();
        if (index > -1) {
            String s = mappedElementsModel.getElementAt(index);
            mappedElementsModel.removeElementAt(index);
            mappableElementsModel.addElement(s);
        }
    }

    public ArrayList<String> getMappedElements() {
        return Collections.list(mappedElementsModel.elements());
    }

}
