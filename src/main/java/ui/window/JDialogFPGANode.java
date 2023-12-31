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

import myutil.*;
import tmltranslator.TMLMapping;
import tmltranslator.modelcompiler.ArchUnitMEC;
import tmltranslator.simulation.SimulationTransaction;
import ui.ColorManager;
import ui.MainGUI;
import ui.TGTextFieldWithHelp;
import ui.tmldd.TMLArchiFPGANode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import org.json.JSONObject;
import org.json.JSONArray;



/**
 * Class JDialogFPGA
 * Dialog for managing attributes of cpu nodes
 * Creation: 07/02/2018
 *
 * @author Ludovic APVRILLE
 * @version 2.0 05/03/2019
 */
public class JDialogFPGANode extends JDialogBase implements ActionListener, Runnable {

    public static final String[] helpStrings = {"fpga.html"};

    protected MainGUI mgui;

    private boolean regularClose;

    private JPanel panel2, panel4, panel5;
    //   private Frame frame;
    private TMLArchiFPGANode node;

    private String[] labels;
    private JTextField[] extraVals;

    protected TGTextFieldWithHelp nodeName;

    protected LinkedList<JButton> schedulingButtons;
    protected LinkedList<JLabel> schedulingLabels;
    protected LinkedList<Plugin> schedulingPlugins;

    protected Plugin selectedPlugin; // Plugin being called
    protected JLabel selectedLabel;


    // Panel2
    protected TGTextFieldWithHelp byteDataSize, goIdleTime, maxConsecutiveIdleCycles, clockRatio, execiTime, execcTime,
            capacity, mappingPenalty, reconfigurationTime, operation, scheduling;

    // Tabbed pane for panel1 and panel2
    //private JTabbedPane tabbedPane;


    /*
     * Creates new form
     */
    public JDialogFPGANode(MainGUI _mgui, Frame _frame, String _title, TMLArchiFPGANode _node) {
        super(_frame, _title, true);
        //  frame = _frame;
        mgui = _mgui;
        node = _node;
        initComponents();
        //     myInitComponents();
        pack();
    }
//
//    private void myInitComponents() {
//    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagLayout gridbag4 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        //GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        GridBagConstraints c4 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Attributes"));
        panel2.setPreferredSize(new Dimension(400, 300));

        // Issue #41 Ordering of tabbed panes 
        //tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();

        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("FPGA name:"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        nodeName = new TGTextFieldWithHelp(node.getNodeName(), 50);
        nodeName.setEditable(true);
        nodeName.setFont(new Font("times", Font.PLAIN, 12));
        panel2.add(nodeName, c2);
        nodeName.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Data size (in byte):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        byteDataSize = new TGTextFieldWithHelp("" + node.getByteDataSize(), 50);
        panel2.add(byteDataSize, c2);
        byteDataSize.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Overall mapping capacity:"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        capacity = new TGTextFieldWithHelp("" + node.getCapacity(), 50);
        panel2.add(capacity, c2);
        capacity.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Mapping penalty:"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        mappingPenalty = new TGTextFieldWithHelp("" + node.getMappingPenalty(), 50);
        panel2.add(mappingPenalty, c2);
        mappingPenalty.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Reconfiguration time:"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        reconfigurationTime = new TGTextFieldWithHelp("" + node.getReconfigurationTime(), 50);
        panel2.add(reconfigurationTime, c2);
        reconfigurationTime.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        panel2.add(new JLabel("Go idle time (in cycle):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        goIdleTime = new TGTextFieldWithHelp("" + node.getGoIdleTime(), 50);
        panel2.add(goIdleTime, c2);
        goIdleTime.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        panel2.add(new JLabel("Max consecutive cycles before idle:"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        maxConsecutiveIdleCycles = new TGTextFieldWithHelp("" + node.getMaxConsecutiveIdleCycles(), 50);
        panel2.add(maxConsecutiveIdleCycles, c2);
        maxConsecutiveIdleCycles.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        panel2.add(new JLabel("EXECI execution time (in cycle):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        execiTime = new TGTextFieldWithHelp("" + node.getExeciTime(), 50);
        panel2.add(execiTime, c2);
        execiTime.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        panel2.add(new JLabel("EXECC execution time (in cycle):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        execcTime = new TGTextFieldWithHelp("" + node.getExeccTime(), 50);
        panel2.add(execcTime, c2);
        execcTime.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        panel2.add(new JLabel("Operation:"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        operation = new TGTextFieldWithHelp(""+node.getOperation(), 50);
        panel2.add(operation, c2);
        operation.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        panel2.add(new JLabel("Scheduling:"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        scheduling = new TGTextFieldWithHelp(""+node.getScheduling(), 100);
        scheduling.setToolTipText(node.getScheduling());
        panel2.add(scheduling, c2);
        scheduling.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);

        schedulingButtons = new LinkedList<>();
        schedulingLabels = new LinkedList<>();
        schedulingPlugins = new LinkedList<>();

        LinkedList<Plugin> list = PluginManager.pluginManager.getPluginFPGAScheduling();
        for (Plugin p : list) {
            TraceManager.addDev("Found plugin=" + p.getName());
            String desc = p.getFPGASchedulingIdentifier();
            if (desc != null) {
                TraceManager.addDev("Found plugin=" + p.getName() + " desc=" + desc);
                JButton buttonPlugin = new JButton(desc);
                buttonPlugin.addActionListener(this);
                schedulingButtons.add(buttonPlugin);
                schedulingPlugins.add(p);
                JLabel labelPlugin = new JLabel("Not yet computed");
                schedulingLabels.add(labelPlugin);

                c2.gridwidth = 1;
                panel2.add(new JLabel("Compute schedule with plugin:"), c2);
                c2.gridwidth = GridBagConstraints.REMAINDER;
                panel2.add(buttonPlugin, c2);
                c2.gridwidth = 1;
                panel2.add(new JLabel("Result of " + desc), c2);
                c2.gridwidth = GridBagConstraints.REMAINDER;
                panel2.add(labelPlugin, c2);
            }
        }


        c2.gridwidth = 1;
        panel2.add(new JLabel("Clock divider:"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.gridwidth = 3;
        clockRatio = new TGTextFieldWithHelp("" + node.getClockRatio(), 50);
        panel2.add(clockRatio, c2);
        clockRatio.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);


        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.BOTH;
        c.add(panel2, c0);

        String ret = "";
        list = PluginManager.pluginManager.getPluginFPGAScheduling();
        for (Plugin p : list) {
            TraceManager.addDev("Found plugin=" + p.getName());
            String desc = p.getFPGASchedulingIdentifier();
            if (desc != null) {
                String tmp = p.executeStaticRetStringOneStringMethod(p.getClassFPGAScheduling(),
                        "hasCustomData", "TMLArchiFPGANode");
                if (tmp != null) {
                    ret += tmp;
                    if ((ret.length() > 0) && (!(ret.endsWith("|")))) {
                        ret += "|";
                    }
                }
            }
        }


        if ((ret != null) && (ret.length() > 0)) {

            GridBagLayout gridbag3 = new GridBagLayout();
            GridBagConstraints c3 = new GridBagConstraints();
            JPanel panel3 = new JPanel();
            panel3.setLayout(gridbag3);
            panel3.setBorder(new javax.swing.border.TitledBorder("Extra attributes"));
            //panel2.setPreferredSize(new Dimension(400, 300));

            c3.gridwidth = 1;
            c3.gridheight = 1;
            c3.weighty = 1.0;
            c3.weightx = 1.0;
            c3.fill = GridBagConstraints.HORIZONTAL;


            String custom = node.getCustomData();
            if (custom == null) {
                custom = "";
            }
            String[] customs = custom.split("\\|");
            String[] rets= ret.split("\\|");
            extraVals = new JTextField[rets.length];
            labels = new String[rets.length];

            for(int i=0; i<rets.length; i++) {
                c3.gridwidth = 1;
                JLabel lab = new JLabel(rets[i]);
                panel3.add(new JLabel(rets[i] + ":"), c3);
                labels[i] = rets[i];
                c3.gridwidth = GridBagConstraints.REMAINDER; //end row
                extraVals[i] = new JTextField();
                for (int j=0; j<customs.length-1; j++) {
                    if (customs[j].compareTo(rets[i]) == 0) {
                        extraVals[i].setText(customs[j+1]);
                        break;
                    }
                }
                panel3.add(extraVals[i], c3);
            }
            c.add(panel3, c0);

        }


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

//        if (evt.getSource() == tracemode) {
//            selectedTracemode = tracemode.getSelectedIndex();
//        }

        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Save and Close")) {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }

        // Checking plugin buttons
        int cpt = 0;
        for(JButton but: schedulingButtons) {
            if (but.getActionCommand().compareTo(command) == 0) {
                selectedPlugin = schedulingPlugins.get(cpt);
                selectedLabel = schedulingLabels.get(cpt);
                Thread t = new Thread(this);
                t.start();
            }
            cpt++;
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

    public String getCapacity() {
        return capacity.getText();
    }

    public String getByteDataSize() {
        return byteDataSize.getText();
    }

    public String getReconfigurationTime() {
        return reconfigurationTime.getText();
    }

    public String getMappingPenalty() {
        return mappingPenalty.getText();
    }

    public String getGoIdleTime() {
        return goIdleTime.getText();
    }

    public String getMaxConsecutiveIdleCycles() {
        return maxConsecutiveIdleCycles.getText();
    }

    public String getExeciTime() {
        return execiTime.getText();
    }

    public String getExeccTime() {
        return execcTime.getText();
    }

    public String getOperation() {
        return operation.getText();
    }

    public String getScheduling() {
        return scheduling.getText();
    }


    public String getClockRatio() {
        return clockRatio.getText();
    }

    public String getCustomData() {
        String ret = "";

        if ((labels == null) || (extraVals == null)) {
            return ret;
        }

        for(int i=0; i<labels.length; i++) {
            ret += labels[i] + "|" + extraVals[i].getText() + "|";
        }

        return ret;
    }

    // Make the right call in the Selected Plugin
    public void run() {
        TraceManager.addDev("Calling the selected plugin: " + selectedPlugin.getFPGASchedulingIdentifier());

         // Getting the XML of the system
        if (!mgui.checkModelingSyntax(true)) {
            TraceManager.addDev("Syntax is incorrect. ");
        }

        //TraceManager.addDev("Syntax ok");

        TMLMapping<?> tmap = mgui.gtm.getTMLMapping();

        if (tmap == null) {
            TraceManager.addDev("Invalid mapping");
            return;
        }

        String XML = tmap.toXML();

        //TraceManager.addDev("Got an XML");

        selectedPlugin.executeStaticRetVoidOneStringMethod(selectedPlugin.getClassFPGAScheduling(),
                "setFPGAName", nodeName.getText());

        //TraceManager.addDev("FPGA name set");

        selectedPlugin.executeStaticRetVoidOneStringMethod(selectedPlugin.getClassFPGAScheduling(),
                "setCustomData", node.getCustomData());

        // precompute
        TraceManager.addDev("Precompute");
        String ret = selectedPlugin.executeStaticRetStringOneStringMethod(selectedPlugin.getClassFPGAScheduling(),
                "precomputeData", XML);
        //node.setCustomData(ret);


        ret = selectedPlugin.executeRetStringMethod(selectedPlugin.getClassFPGAScheduling(), "computeScheduling");

        if (ret.startsWith("error")) {
            scheduling.setText(ret);
            return;
        }

        // ret contains a json in the form of e.g.
        // {makespan:1821,slots:[[Application__A0, Application__A1],[Application__A4, Application__A2, Application__A3],[Application__A5,
        // Application__A6, Application__A7],[Application__A10, Application__A8, Application__A9]]}

        TraceManager.addDev("Received JSON String: " + ret);

        // For instance: {"scheduling": [[Application__A0, Application__A1, Application__A4], [Application__A5, Application__A2, Application__A3,
        // Application__A6], [Application__A8, Application__A9, Application__A7], [Application__A10]], "makespan": 0.001537]}
        try {
            int indexBegTab = ret.indexOf("[[");
            int indexEndTab = ret.indexOf("]]");

            if ((indexBegTab > 0) && (indexEndTab > 0)) {
                String partial = ret.substring(indexBegTab + 2, indexEndTab);
                partial = Conversion.replaceAllString(partial, "], [", " ; ");
                partial = Conversion.replaceAllString(partial, ",", " ");
                scheduling.setText(partial);
                scheduling.setToolTipText(partial);
            }

            int indexBegMakespan = ret.indexOf("makespan");
            if (indexBegMakespan > 0) {
                String time = ret.substring(indexBegMakespan + 10);
                int indexEndMakespan = time.indexOf("]");
                if (indexEndMakespan == -1) {
                    indexEndMakespan = time.indexOf("}");
                }
                time = time.substring(0, indexEndMakespan);
                selectedLabel.setText("makespan: " + time + " ms");
            }



        // Old JSON solution
        /*JSONObject jo = new JSONObject(ret);
        TraceManager.addDev("Solution: " + jo.toString());

        try {
            int makespan = jo.getInt("makespan");
            if ((selectedLabel != null) ) {
                selectedLabel.setText("makespan: " + makespan + " ms");
            }
        } catch (Exception e) {
            selectedLabel.setText("No makespan computed");
        }

        String solution = "";
        try {
            JSONArray solutions = jo.getJSONArray("slots");
            for (int i=0; i <solutions.length(); i++) {
                JSONArray intSol = (JSONArray)(solutions.get(i));
                //TraceManager.addDev("intSol:" + intSol.toString());
                String partial = intSol.toString();
                partial = Conversion.replaceAllString(partial, "[", "");
                partial = Conversion.replaceAllString(partial, "]", "");
                partial = Conversion.replaceAllString(partial, "\"", "");
                partial = Conversion.replaceAllString(partial, ",", " ");
                if (i != 0 ) {
                    solution += " ; ";
                }
                solution += partial.trim();
            }
            scheduling.setText(solution);*/


        } catch (Exception e) {
                TraceManager.addDev("Exception when parsing solution:" + e.getMessage());
        }



    }


}
