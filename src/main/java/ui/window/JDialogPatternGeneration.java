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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import avatartranslator.AvatarPragma;
import common.SpecConfigTTool;
import launcher.LauncherException;
import launcher.RshClient;
import launcher.RshClientReader;
import myutil.FileException;
import myutil.GraphicLib;
import myutil.MasterProcessInterface;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import ui.*;
import ui.interactivesimulation.JFrameSimulationSDPanel;
import ui.util.IconManager;

/**
 * Class JDialogProverifVerification
 * Dialog for managing the generation of Patterns
 *
 * Creation: 16/08/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 16/08/2023
 */

public class JDialogPatternGeneration extends JDialog implements ActionListener, ListSelectionListener, MouseListener, Runnable,
        MasterProcessInterface {

    private static final Insets insets = new Insets(0, 0, 0, 0);
    private static final Insets WEST_INSETS = new Insets(0, 0, 0, 0);

    protected MainGUI mgui;

    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    String pathPatterns;
    TURTLEPanel currPanel;

    int mode;

    // Pattern Generation
    String newPatternName;
    protected JTextField jFieldNewPatternName;
    protected JButton buttonAddAllTasksAsPattern, buttonAddSelectedTasksAsPattern, buttonRemoveAllTasksAsPattern, buttonRemoveSelectedTasksAsPattern;
    JList<String> jListNoSelectedTasksAsPattern, jListSelectedTasksAsPattern;
    Vector<String> selectedTasksAsPattern = new Vector<String>();
    Vector<String> noSelectedTasksAsPattern = new Vector<String>();
    Map<String, String> tasksFullName = new LinkedHashMap<String, String>();
    JPanel jPanelPatternSelection;

    // Pattern Intergration
    Vector<String> listPatterns = new Vector<String>();
    Vector<String> tasksOfPatternWithExternalPort = new Vector<String>();
    Vector<String> externalPortsOfTaskInPattern = new Vector<String>();
    Vector<String> tasksOfModel = new Vector<String>();
    Vector<String> portsOfTaskInModel = new Vector<String>();
    JComboBox<String> jComboBoxPatterns;
    JComboBox<String> jComboBoxPatternsTaskWithExternalPort;
    JComboBox<String> jComboBoxPatternExternalPortOfATask;
    JComboBox<String> jComboBoxModelsTask;
    JComboBox<String> jComboBoxModelsPortOfTask;
    JCheckBox jCheckBoxConnectToNewPort;
    JList<String> jListConnectedPorts;
    Vector<String> connectedPorts = new Vector<String>();
    JButton addConnectionBetweenSelectedPorts, removeConnectionBetweenPorts;
    //JPanel jPanelPatternIntergration;
    JButton buttonCloneTask, buttonAddPortInTask;
    Vector<String> portsConfig = new Vector<String>();
    JComboBox<String> jComboBoxPortsConfig;
    ButtonGroup portsConfigGroup;
    JRadioButton jRadioPortConfigRemove, jRadioPortConfigMerge;
    Vector<String> portsConfigMerge = new Vector<String>();
    JComboBox<String> jComboBoxPortsConfigMerge;
    JList<String> jListConfigPorts;
    Vector<String> configuredPorts = new Vector<String>();
    JButton addConfigPorts, removeConfigPorts;
    JButton buttonMapInArch;

    JComboBox<String> jComboBoxTaskToClone;
    String newClonedTaskName;
    protected JTextField jFieldNewClonedTaskName;
    JButton addClonedTask, removeClonedTask;
    JList<String> jListClonedTasks;
    Vector<String> clonedTasks = new Vector<String>();
    

    

    //components
    protected JScrollPane jsp;
    protected JPanel jta;
    private JButton startButton;
    protected JButton stop;
    protected JButton close;
    private JPopupMenu popup;

    public TGHelpButton myButton;
    public static String helpString = "securityverification.html";

    private Map<JCheckBox, List<JCheckBox>> cpuTaskObjs = new HashMap<JCheckBox, List<JCheckBox>>();

    private class MyMenuItem extends JMenuItem {
        /**
		 * 
		 */
		private static final long serialVersionUID = -344414299222823444L;
		

        MyMenuItem(String text) {
            super(text);
        }
    }

    private MyMenuItem menuItem;

    private boolean go = false;

    protected RshClient rshc;

    protected JTabbedPane jp1;

    private class ProVerifVerificationException extends Exception {
        /**
		 * 
		 */
		private static final long serialVersionUID = -2359743729229833671L;
	

        public ProVerifVerificationException(String message) {
            super( message );
        }
    }

    /*
     * Creates new form
     */
    public JDialogPatternGeneration(Frame f, MainGUI _mgui, String title, String _pathPatterns) {
        super(f, title, Dialog.ModalityType.DOCUMENT_MODAL);

        mgui = _mgui;
        this.pathPatterns = _pathPatterns;


        for (int i=0; i<mgui.gtm.getTMLMapping().getMappedTasks().size(); i++) {
            String taskFullName = mgui.gtm.getTMLMapping().getMappedTasks().get(i).getName();
            String taskShortName = taskFullName.split("__")[taskFullName.split("__").length - 1];
            tasksFullName.put(taskShortName, taskFullName);
            noSelectedTasksAsPattern.add(taskShortName);
        } 
        currPanel = mgui.getCurrentTURTLEPanel();
        listPatterns = getFoldersName(_pathPatterns);
        initComponents();
        myInitComponents();
        pack();
    }

    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
    }

    private void addComponent(Container container, Component component, int gridx, int gridy,
                              int gridwidth, int anchor, int fill) {
        GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, 1, 0, 0,
                anchor, fill, insets, 0, 0);
        container.add(component, gbc);
    }

    Vector<String> getListChannelsBetweenTwoTasks(String originTaskName, String destinationTaskName) {
        Vector<String> channels = new Vector<String>();
        if (mgui.gtm.getTMLMapping().getTaskByName(originTaskName) != null && mgui.gtm.getTMLMapping().getTaskByName(destinationTaskName) != null) {
            if (mgui.gtm.getTMLMapping().getTaskByName(originTaskName).getWriteChannels().size() > 0) {
                for (int i=0; i < mgui.gtm.getTMLMapping().getTaskByName(destinationTaskName).getReadChannels().size(); i++) {
                    for (int j=0 ; j < mgui.gtm.getTMLMapping().getTaskByName(destinationTaskName).getReadChannels().get(i).getNbOfChannels(); j++) {
                        for (int k=0; k < mgui.gtm.getTMLMapping().getTaskByName(originTaskName).getWriteChannels().size(); k++) {
                            for (int l=0; l < mgui.gtm.getTMLMapping().getTaskByName(originTaskName).getWriteChannels().get(k).getNbOfChannels(); l++) {
                                if (mgui.gtm.getTMLMapping().getTaskByName(originTaskName).getWriteChannels().get(k).getChannel(l) == mgui.gtm.getTMLMapping().getTaskByName(destinationTaskName).getReadChannels().get(i).getChannel(j)) {
                                    String channelLongName = mgui.gtm.getTMLMapping().getTaskByName(destinationTaskName).getReadChannels().get(i).getChannel(j).getName();
                                    String channelShortName = channelLongName.split("__")[channelLongName.split("__").length - 1];
                                    if (!channels.contains(channelShortName)) {
                                        channels.add(channelShortName);
                                    }
                                }
                            }
                        }
                    } 
                }
            }
        }
        return channels;
    }

    Vector<String> getFoldersName(String path) {
        Vector<String> folders = new Vector<String>();
        File directoryPath = new File(path);
        String[] directories = directoryPath.list((dir, name) -> new File(dir, name).isDirectory());
        if (directories != null) {
            folders = new Vector<String>(Arrays.asList(directories));
        }
        return folders;
    }

    private GridBagConstraints createGbc(int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.insets = WEST_INSETS;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        return gbc;
    }

    protected void panelPatternGeneration() {
        int curY = 0;
        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Pattern Generation"));

        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.fill = GridBagConstraints.HORIZONTAL;
        c01.gridheight = 1;
        c01.gridwidth = 2;
        
        JLabel labelPatternName = new JLabel("Name of the new pattern: ");
        jp01.add(labelPatternName, c01);
        //addComponent(jp01, labelPatternName, 0, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        c01.gridwidth = GridBagConstraints.REMAINDER;
        jFieldNewPatternName = new JTextField(newPatternName, 10);
        jFieldNewPatternName.setPreferredSize(new Dimension(10, 25));
        jp01.add(jFieldNewPatternName, c01);
        //addComponent(jp01, jFieldNewPatternName, 1, curY, 3, GridBagConstraints.EAST, GridBagConstraints.BOTH);
        curY++;
        c01.gridwidth = GridBagConstraints.REMAINDER;
        jp01.add(new JLabel(" "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER;
        JLabel labelSelectPattern = new JLabel("Select tasks of the new Pattern: ");
        jp01.add(labelSelectPattern, c01);
        //addComponent(jp01, labelSelectPattern, 0, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        jListNoSelectedTasksAsPattern = new JList<String>(noSelectedTasksAsPattern);
		jPanelPatternSelection = new JPanel();
		jPanelPatternSelection.setPreferredSize(new Dimension(250, 200));
		GridBagConstraints c02 = new GridBagConstraints();
		c02.gridwidth = 1;
		c02.gridheight = 1;
		c02.fill= GridBagConstraints.BOTH;
     	jListNoSelectedTasksAsPattern.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jListNoSelectedTasksAsPattern.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(jListNoSelectedTasksAsPattern);
        scrollPane1.setPreferredSize(new Dimension(250, 200));
        jPanelPatternSelection.add(scrollPane1, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel();
        GridBagConstraints c13 = new GridBagConstraints();
        c13.gridwidth = GridBagConstraints.REMAINDER;
        c13.gridheight = 1;
        
        buttonAddAllTasksAsPattern = new JButton(IconManager.imgic50);
        buttonAddAllTasksAsPattern.setPreferredSize(new Dimension(50, 25));
        buttonAddAllTasksAsPattern.addActionListener(this);
        buttonAddAllTasksAsPattern.setActionCommand("allTasksSelectedAsPattern");
        buttonPanel.add(buttonAddAllTasksAsPattern, c13);
		//buttonAddAllTasksAsPattern.setEnabled(false);

        buttonAddSelectedTasksAsPattern = new JButton(IconManager.imgic48);
        buttonAddSelectedTasksAsPattern.setPreferredSize(new Dimension(50, 25));
        buttonAddSelectedTasksAsPattern.addActionListener(this);
        buttonAddSelectedTasksAsPattern.setActionCommand("addTaskAsPattern");
        buttonPanel.add(buttonAddSelectedTasksAsPattern, c13);
		//buttonAddSelectedTasksAsPattern.setEnabled(false);

        buttonPanel.add(new JLabel(" "), c13);

        buttonRemoveSelectedTasksAsPattern = new JButton(IconManager.imgic46);
        buttonRemoveSelectedTasksAsPattern.addActionListener(this);
        buttonRemoveSelectedTasksAsPattern.setPreferredSize(new Dimension(50, 25));
        buttonRemoveSelectedTasksAsPattern.setActionCommand("removeTaskAsPattern");
        buttonPanel.add(buttonRemoveSelectedTasksAsPattern, c13);
		//buttonRemoveSelectedTasksAsPattern.setEnabled(false);

        buttonRemoveAllTasksAsPattern = new JButton(IconManager.imgic44);
        buttonRemoveAllTasksAsPattern.addActionListener(this);
        buttonRemoveAllTasksAsPattern.setPreferredSize(new Dimension(50, 25));
        buttonRemoveAllTasksAsPattern.setActionCommand("allTasksNoSelectedAsPattern");
        buttonPanel.add(buttonRemoveAllTasksAsPattern, c13);
        jPanelPatternSelection.add(buttonPanel, c02);
        buttonPanel.setPreferredSize(new Dimension(50, 200));
		//buttonRemoveAllTasksAsPattern.setEnabled(false);
		
        jListSelectedTasksAsPattern = new JList<String>(selectedTasksAsPattern);

        jListSelectedTasksAsPattern.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jListSelectedTasksAsPattern.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(jListSelectedTasksAsPattern);
        scrollPane2.setPreferredSize(new Dimension(250, 200));
        jPanelPatternSelection.add(scrollPane2, BorderLayout.CENTER);
        jPanelPatternSelection.setPreferredSize(new Dimension(600, 250));
        jPanelPatternSelection.setMinimumSize(new Dimension(600, 250));
        //c01.gridheight = 10;
        jp01.add(jPanelPatternSelection, c01);
        //c02.gridheight = 1;

        jp1.add("Pattern Generation", jp01);
    }

    protected void panelPatternIntergration() {
        JPanel jp02 = new JPanel();
        GridBagLayout gridbag02 = new GridBagLayout();
        GridBagConstraints c02 = new GridBagConstraints();
        jp02.setLayout(gridbag02);
        jp02.setBorder(new javax.swing.border.TitledBorder("Pattern Integration"));

        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.fill = GridBagConstraints.HORIZONTAL;
        c02.anchor = GridBagConstraints.LINE_START;
        c02.gridheight = 1;
        c02.gridwidth = 1;
        
        JPanel jpPatternSelection = new JPanel();
        jpPatternSelection.setLayout(new GridBagLayout());
        GridBagConstraints cPatternSelection = new GridBagConstraints();
        cPatternSelection.gridx = 0;
        cPatternSelection.gridy = 0;
        cPatternSelection.fill = GridBagConstraints.HORIZONTAL;
        cPatternSelection.anchor = GridBagConstraints.LINE_START;
        cPatternSelection.weightx = 0.5;

        JLabel labelPatternName = new JLabel("Select a pattern: ");
        jpPatternSelection.add(labelPatternName, cPatternSelection);
        jComboBoxPatterns = new JComboBox<String>(listPatterns);
        jComboBoxPatterns.setSelectedIndex(-1);
        jComboBoxPatterns.addActionListener(this);
        cPatternSelection.gridx = 1;
        jpPatternSelection.add(jComboBoxPatterns, cPatternSelection);
        jp02.add(jpPatternSelection, c02);

        JPanel jpPatternConnetionMain = new JPanel();
        jpPatternConnetionMain.setLayout(new GridBagLayout());
        GridBagConstraints cPatternConnetionMain = new GridBagConstraints();
        cPatternConnetionMain.gridx = 0;
        cPatternConnetionMain.gridy = 0;
        cPatternConnetionMain.fill = GridBagConstraints.HORIZONTAL;
        cPatternConnetionMain.anchor = GridBagConstraints.LINE_START;
        cPatternConnetionMain.weightx = 1.0;
        jpPatternConnetionMain.add(new JLabel("Connect pattern's external ports:"), cPatternConnetionMain);

        
        JPanel jpPatternConnetion = new JPanel();
        jpPatternConnetion.setLayout(new GridBagLayout());
        GridBagConstraints cPatternConnetion = new GridBagConstraints();
        cPatternConnetion.gridx = 0;
        cPatternConnetion.gridy = 0;
        cPatternConnetion.weightx = 0.4;
        cPatternConnetion.fill = GridBagConstraints.HORIZONTAL;
        cPatternConnetion.anchor = GridBagConstraints.LINE_START;

        jComboBoxPatternsTaskWithExternalPort = new JComboBox<String>(tasksOfPatternWithExternalPort);
        jComboBoxPatternsTaskWithExternalPort.setSelectedIndex(-1);
        jComboBoxPatternsTaskWithExternalPort.addActionListener(this);
        jpPatternConnetion.add(jComboBoxPatternsTaskWithExternalPort, cPatternConnetion);
        jComboBoxPatternExternalPortOfATask = new JComboBox<String>(externalPortsOfTaskInPattern);
        jComboBoxPatternExternalPortOfATask.setSelectedIndex(-1);
        jComboBoxPatternExternalPortOfATask.addActionListener(this);
        cPatternConnetion.gridx = 1;
        cPatternConnetion.weightx = 0.8;
        jpPatternConnetion.add(jComboBoxPatternExternalPortOfATask, cPatternConnetion);
        buttonCloneTask = new JButton("Clone Task");
        //buttonCloneTask.setPreferredSize(new Dimension(50, 25));
        //buttonCloneTask.setEnabled(false);
        buttonCloneTask.addActionListener(this);
        buttonCloneTask.setActionCommand("cloneTask");
        cPatternConnetion.gridx = 2;
        cPatternConnetion.weightx = 0;
        jpPatternConnetion.add(buttonCloneTask, cPatternConnetion);

        jComboBoxModelsTask = new JComboBox<String>(tasksOfModel);
        jComboBoxModelsTask.setSelectedIndex(-1);
        jComboBoxModelsTask.addActionListener(this);
        cPatternConnetion.gridx = 0;
        cPatternConnetion.gridy = 1;
        cPatternConnetion.weightx = 0.8;
        jpPatternConnetion.add(jComboBoxModelsTask, cPatternConnetion);

        jComboBoxModelsPortOfTask = new JComboBox<String>(portsOfTaskInModel);
        jComboBoxModelsPortOfTask.setSelectedIndex(-1);
        jComboBoxModelsPortOfTask.addActionListener(this);
        cPatternConnetion.gridx = 1;
        cPatternConnetion.weightx = 0.8;
        jpPatternConnetion.add(jComboBoxModelsPortOfTask, cPatternConnetion);
        jCheckBoxConnectToNewPort = new JCheckBox("Connect to new Port");
        jCheckBoxConnectToNewPort.setEnabled(false);
        jCheckBoxConnectToNewPort.addActionListener(this);
        cPatternConnetion.gridx = 2;
        cPatternConnetion.weightx = 0;
        jpPatternConnetion.add(jCheckBoxConnectToNewPort, cPatternConnetion);
        cPatternConnetionMain.gridy = 1;
        jpPatternConnetionMain.add(jpPatternConnetion, cPatternConnetionMain);


        jListConnectedPorts = new JList<String>(connectedPorts);
		JPanel jPanelPatternIntergration = new JPanel();
        jPanelPatternIntergration.setLayout(new GridBagLayout());
        GridBagConstraints cPatternIntergration = new GridBagConstraints();
        cPatternIntergration.gridx = 0;
        cPatternIntergration.gridy = 0;
        cPatternIntergration.weightx = 0.9;
        cPatternIntergration.fill = GridBagConstraints.HORIZONTAL;
        cPatternIntergration.anchor = GridBagConstraints.LINE_START;
        
		//jPanelPatternIntergration.setPreferredSize(new Dimension(600, 130));
        jListConnectedPorts.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jListConnectedPorts.addListSelectionListener(this);
        JScrollPane scrollPaneConnectedPorts = new JScrollPane(jListConnectedPorts);
        scrollPaneConnectedPorts.setPreferredSize(new Dimension(450, 125));
        jPanelPatternIntergration.add(scrollPaneConnectedPorts, cPatternIntergration);
        
        JPanel pannelButtonConnectPorts = new JPanel();
        pannelButtonConnectPorts.setLayout(new GridBagLayout());
        GridBagConstraints cConnectedPorts = new GridBagConstraints();
        cConnectedPorts.gridx = 0;
        cConnectedPorts.weightx = 1.0;
        cConnectedPorts.gridy = 0;
        cConnectedPorts.fill = GridBagConstraints.HORIZONTAL;
        cConnectedPorts.anchor = GridBagConstraints.LINE_START;
        addConnectionBetweenSelectedPorts = new JButton("+");
        addConnectionBetweenSelectedPorts.setEnabled(false);
        addConnectionBetweenSelectedPorts.setPreferredSize(new Dimension(50, 25));
        addConnectionBetweenSelectedPorts.addActionListener(this);
        addConnectionBetweenSelectedPorts.setActionCommand("addChannelSecondSensor");
        pannelButtonConnectPorts.add(addConnectionBetweenSelectedPorts, cConnectedPorts);

        removeConnectionBetweenPorts = new JButton("-");
        removeConnectionBetweenPorts.setEnabled(false);
        removeConnectionBetweenPorts.setPreferredSize(new Dimension(50, 25));
        removeConnectionBetweenPorts.addActionListener(this);
        removeConnectionBetweenPorts.setActionCommand("removeChannelSecondSensor");
        cConnectedPorts.gridy = 1;
        pannelButtonConnectPorts.add(removeConnectionBetweenPorts, cConnectedPorts);
        //pannelButtonConnectPorts.setPreferredSize(new Dimension(50, 125));
        //jPanelPatternIntergration.setMinimumSize(new Dimension(600, 130));
        cPatternIntergration.gridx = 1;
        cPatternIntergration.weightx = 0.1;
        jPanelPatternIntergration.add(pannelButtonConnectPorts, cPatternIntergration);
        cPatternConnetionMain.gridy = 2;
        jpPatternConnetionMain.add(jPanelPatternIntergration, cPatternConnetionMain);
        //c02.gridwidth = GridBagConstraints.REMAINDER;
        c02.gridy = 1;
        c02.insets = new Insets(10,0,0,0); 
        jp02.add(jpPatternConnetionMain, c02);
        
        JPanel jpPortConfigurationMain = new JPanel();
        jpPortConfigurationMain.setLayout(new GridBagLayout());
        GridBagConstraints cPortConfigurationMain = new GridBagConstraints();
        cPortConfigurationMain.gridx = 0;
        cPortConfigurationMain.gridy = 0;
        cPortConfigurationMain.gridwidth = 2;
        cPortConfigurationMain.anchor = GridBagConstraints.LINE_START;
        //cPortConfigurationMain.fill= GridBagConstraints.BOTH;

        jpPortConfigurationMain.add(new JLabel("Ports to configure:"), cPortConfigurationMain);
        //cPortConfigurationMain.gridwidth = GridBagConstraints.REMAINDER;
        JPanel jpPortConfiguration = new JPanel();
        jpPortConfiguration.setLayout(new GridBagLayout());
        GridBagConstraints cPortConfiguration = new GridBagConstraints();
        cPortConfiguration.gridx = 0;
        cPortConfiguration.gridy = 0;
        cPortConfiguration.gridwidth = 2;
        cPortConfiguration.fill = GridBagConstraints.HORIZONTAL;
        cPortConfiguration.anchor = GridBagConstraints.LINE_START;

        jComboBoxPortsConfig = new JComboBox<String>(portsConfig);
        jComboBoxPortsConfig.setSelectedIndex(-1);
        jComboBoxPortsConfig.addActionListener(this);
        jpPortConfiguration.add(jComboBoxPortsConfig, cPortConfiguration);
        //cPortConfiguration.gridwidth = GridBagConstraints.REMAINDER;
        portsConfigGroup =new ButtonGroup();
        jRadioPortConfigRemove = new JRadioButton("Remove port");
        jRadioPortConfigRemove.setEnabled(false);
        jRadioPortConfigRemove.addActionListener(this);
        cPortConfiguration.gridy = 1;
        jpPortConfiguration.add(jRadioPortConfigRemove, cPortConfiguration);
        
        jRadioPortConfigMerge = new JRadioButton("Merge with:");
        jRadioPortConfigMerge.setEnabled(false);
        jRadioPortConfigMerge.addActionListener(this);
        cPortConfiguration.gridy = 2;
        cPortConfiguration.gridwidth = 1;
        jpPortConfiguration.add(jRadioPortConfigMerge, cPortConfiguration);
        //cPortConfiguration.gridwidth = GridBagConstraints.REMAINDER;
        portsConfigGroup.add(jRadioPortConfigRemove);
        portsConfigGroup.add(jRadioPortConfigMerge);
        jComboBoxPortsConfigMerge = new JComboBox<String>(portsConfigMerge);
        jComboBoxPortsConfigMerge.setSelectedIndex(-1);
        jComboBoxPortsConfigMerge.setEnabled(false);
        jComboBoxPortsConfigMerge.addActionListener(this);
        jComboBoxPortsConfigMerge.setPreferredSize(new Dimension(150, 25));
        cPortConfiguration.gridx = 1;
        cPortConfiguration.weightx = 1.0;
        jpPortConfiguration.add(jComboBoxPortsConfigMerge, cPortConfiguration);
        
        cPortConfigurationMain.gridx = 0;
        cPortConfigurationMain.gridy = 1;
        cPortConfigurationMain.weightx = 0.6;
        cPortConfigurationMain.gridwidth = 1;
        //cPortConfigurationMain.ipady = 100;
        //cPortConfigurationMain.fill = GridBagConstraints.BOTH;
        cPortConfigurationMain.anchor = GridBagConstraints.FIRST_LINE_START;
        //jpPortConfiguration.setBackground(Color.gray);
        cPortConfigurationMain.insets = new Insets(5,0,0,0);  //top padding
        jpPortConfigurationMain.add(jpPortConfiguration, cPortConfigurationMain);
        //cPortConfigurationMain.gridwidth = GridBagConstraints.REMAINDER;

        jListConfigPorts = new JList<String>(configuredPorts);
		JPanel jPanelConfigPorts = new JPanel(new GridBagLayout());
        jPanelConfigPorts.setLayout(new GridBagLayout());
        GridBagConstraints cConfigPorts = new GridBagConstraints();
		//jPanelConfigPorts.setPreferredSize(new Dimension(200, 100));
        jListConfigPorts.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jListConfigPorts.addListSelectionListener(this);
        JScrollPane scrollPaneConfigPorts = new JScrollPane(jListConfigPorts);
        //scrollPaneConfigPorts.setPreferredSize(new Dimension(200, 100));
        cConfigPorts.gridx = 0;
        cConfigPorts.gridy = 0;
        cConfigPorts.fill = GridBagConstraints.HORIZONTAL;
        jPanelConfigPorts.add(scrollPaneConfigPorts, cConfigPorts);
        
        JPanel pannelButtonConfigPorts = new JPanel();
        pannelButtonConfigPorts.setLayout(new GridBagLayout());
        GridBagConstraints cButtonConfigPorts = new GridBagConstraints();
        cButtonConfigPorts.gridx = 0;
        cButtonConfigPorts.gridy = 0;
        addConfigPorts = new JButton("+");
        addConfigPorts.setEnabled(false);
        addConfigPorts.setPreferredSize(new Dimension(50, 25));
        addConfigPorts.addActionListener(this);
        addConfigPorts.setActionCommand("addChannelSecondSensor");
        pannelButtonConfigPorts.add(addConfigPorts, cButtonConfigPorts);

        removeConfigPorts = new JButton("-");
        removeConfigPorts.setEnabled(false);
        removeConfigPorts.setPreferredSize(new Dimension(50, 25));
        removeConfigPorts.addActionListener(this);
        removeConfigPorts.setActionCommand("removeChannelSecondSensor");
        cButtonConfigPorts.gridx = 1;
        pannelButtonConfigPorts.add(removeConfigPorts, cButtonConfigPorts);
        //pannelButtonConfigPorts.setPreferredSize(new Dimension(200, 30));
        //jPanelConfigPorts.setMinimumSize(new Dimension(220, 110));
        cConfigPorts.gridy = 1;
        jPanelConfigPorts.add(pannelButtonConfigPorts, cConfigPorts);

        cPortConfigurationMain.gridx = 1;
        cPortConfigurationMain.gridy = 1;
        cPortConfigurationMain.weightx = 0.4;
        cPortConfigurationMain.fill = GridBagConstraints.HORIZONTAL;
        cPortConfigurationMain.anchor = GridBagConstraints.FIRST_LINE_START;
        //jPanelConfigPorts.setBackground(Color.red);  
        jpPortConfigurationMain.add(jPanelConfigPorts, cPortConfigurationMain);
        c02.gridy = 2;
        jp02.add(jpPortConfigurationMain, c02);
        //c02.gridwidth = GridBagConstraints.REMAINDER;

        JPanel jpOptions = new JPanel();
        jpOptions.setLayout(new GridBagLayout());
        GridBagConstraints cOptions = new GridBagConstraints();
        cOptions.gridx = 0;
        cOptions.gridy = 0;
        cOptions.fill = GridBagConstraints.HORIZONTAL;
        cOptions.anchor = GridBagConstraints.FIRST_LINE_START;
        cOptions.weightx = 1.0;
        jpOptions.add(new JLabel("Options:"), cOptions);
        
        
        buttonMapInArch = new JButton("Manually Map in Arch");
        //buttonMapInArch.setPreferredSize(new Dimension(50, 25));
        //buttonMapInArch.setEnabled(false);
        buttonMapInArch.addActionListener(this);
        buttonMapInArch.setActionCommand("addChannelSecondSensor");
        cOptions.gridy = 1;
        cOptions.weightx = 0.3;
        cOptions.fill = GridBagConstraints.NONE;
        jpOptions.add(buttonMapInArch, cOptions);
        c02.gridy = 3;
        jp02.add(jpOptions, c02);

        jp1.add("Pattern Integration", jp02);
    }

    protected void initComponents() {

        jp1 = GraphicLib.createTabbedPane();
        int curY = 0;
        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());

        jta = new JPanel();
        jta.setLayout(new GridBagLayout());
        jta.setBorder(new javax.swing.border.TitledBorder("Results"));
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);

        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setPreferredSize(new Dimension(300, 100));
        c.add(jsp, BorderLayout.CENTER);

        startButton = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);

        startButton.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(120, 30));

        startButton.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);

        JPanel jp2 = new JPanel();
        jp2.add(startButton);
        jp2.add(stop);
        jp2.add(close);

        panelPatternGeneration();
        panelPatternIntergration();
        c.add(jp1, BorderLayout.NORTH);
        c.add(jp2, BorderLayout.SOUTH);

        this.popup = new JPopupMenu();
        this.menuItem = new MyMenuItem("Show trace");
        this.menuItem.addActionListener(this);
        popup.add(this.menuItem);
    }

    private void removeTaskAsPattern() {
        int[] list = jListSelectedTasksAsPattern.getSelectedIndices();
        Vector<String> v = new Vector<String>();
        String o;
        for (int i = 0; i < list.length; i++) {
            o = selectedTasksAsPattern.elementAt(list[i]);
            noSelectedTasksAsPattern.addElement(o);
            v.addElement(o);
        }

        selectedTasksAsPattern.removeAll(v);
        jListNoSelectedTasksAsPattern.setListData(noSelectedTasksAsPattern);
        jListSelectedTasksAsPattern.setListData(selectedTasksAsPattern);
        setButtons();
    }

    private void addTaskAsPattern() {
        int[] list = jListNoSelectedTasksAsPattern.getSelectedIndices();
        Vector<String> v = new Vector<String>();
        String o;

        for (int i = 0; i < list.length; i++) {
            o = noSelectedTasksAsPattern.elementAt(list[i]);
            selectedTasksAsPattern.addElement(o);
            v.addElement(o);
        }
        noSelectedTasksAsPattern.removeAll(v);
        jListNoSelectedTasksAsPattern.setListData(noSelectedTasksAsPattern);
        jListSelectedTasksAsPattern.setListData(selectedTasksAsPattern);
        setButtons();
    }

    private void allTasksSelectedAsPattern() {
        selectedTasksAsPattern.addAll(noSelectedTasksAsPattern);
        noSelectedTasksAsPattern.removeAllElements();
        jListNoSelectedTasksAsPattern.setListData(noSelectedTasksAsPattern);
        jListSelectedTasksAsPattern.setListData(selectedTasksAsPattern);
        setButtons();
    }

    private void allTasksNoSelectedAsPattern() {
        noSelectedTasksAsPattern.addAll(selectedTasksAsPattern);
        selectedTasksAsPattern.removeAllElements();
        jListNoSelectedTasksAsPattern.setListData(noSelectedTasksAsPattern);
        jListSelectedTasksAsPattern.setListData(selectedTasksAsPattern);
        setButtons();
    }

    private void cloneTask() {
        JDialog cloneTaskDialog = new JDialog(this.getOwner(), "Clone a Task", Dialog.ModalityType.DOCUMENT_MODAL);
        Container contCloneTask = cloneTaskDialog.getContentPane();
        contCloneTask.setLayout(new BorderLayout());
        JPanel jpCloneTask = new JPanel();
        jpCloneTask.setLayout(new GridBagLayout());
        GridBagConstraints cCloneTask = new GridBagConstraints();
        cCloneTask.gridx = 0;
        cCloneTask.gridy = 0;
        cCloneTask.fill = GridBagConstraints.HORIZONTAL;
        cCloneTask.anchor = GridBagConstraints.FIRST_LINE_START;
        cCloneTask.weightx = 1.0;
        cCloneTask.insets = new Insets(10,0,0,0);  //top padding
        jpCloneTask.add(new JLabel("Select a Task to clone:"), cCloneTask);

        jComboBoxTaskToClone = new JComboBox<String>(tasksOfModel);
        jComboBoxTaskToClone.setSelectedIndex(-1);
        jComboBoxTaskToClone.setEnabled(false);
        jComboBoxTaskToClone.addActionListener(this);
        //jComboBoxPortsConfigMerge.setPreferredSize(new Dimension(150, 25));
        cCloneTask.gridx = 1;
        jpCloneTask.add(jComboBoxPortsConfigMerge, cCloneTask);
        cCloneTask.gridx = 0;
        cCloneTask.gridy = 1;
        jpCloneTask.add(new JLabel("Cloned task name:"), cCloneTask);
        jFieldNewClonedTaskName = new JTextField(newClonedTaskName, 10);
        jFieldNewClonedTaskName.setPreferredSize(new Dimension(10, 25));
        cCloneTask.gridx = 1;
        jpCloneTask.add(jFieldNewClonedTaskName, cCloneTask);

        jListClonedTasks = new JList<String>(clonedTasks);
		JPanel jPanelClonedTasks = new JPanel();
        jPanelClonedTasks.setLayout(new GridBagLayout());
        GridBagConstraints cClonedTasks = new GridBagConstraints();
        cClonedTasks.gridx = 0;
        cClonedTasks.gridy = 0;
        cClonedTasks.weightx = 0.95;
        cClonedTasks.fill = GridBagConstraints.HORIZONTAL;
        cClonedTasks.anchor = GridBagConstraints.LINE_START;
        
        jListClonedTasks.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jListClonedTasks.addListSelectionListener(this);
        JScrollPane scrollPaneClonedTasks = new JScrollPane(jListClonedTasks);
        scrollPaneClonedTasks.setPreferredSize(new Dimension(250, 175));
        jPanelClonedTasks.add(scrollPaneClonedTasks, cClonedTasks);
        
        JPanel pannelButtonClonedTasks = new JPanel();
        pannelButtonClonedTasks.setLayout(new GridBagLayout());
        GridBagConstraints cButtonClonedTasks = new GridBagConstraints();
        cButtonClonedTasks.gridx = 0;
        cButtonClonedTasks.weightx = 1.0;
        cButtonClonedTasks.gridy = 0;
        cButtonClonedTasks.fill = GridBagConstraints.HORIZONTAL;
        cButtonClonedTasks.anchor = GridBagConstraints.LINE_START;
        addClonedTask = new JButton("+");
        addClonedTask.setEnabled(false);
        addClonedTask.setPreferredSize(new Dimension(40, 25));
        addClonedTask.addActionListener(this);
        addClonedTask.setActionCommand("addChannelSecondSensor");
        pannelButtonClonedTasks.add(addClonedTask, cButtonClonedTasks);

        removeClonedTask = new JButton("-");
        removeClonedTask.setEnabled(false);
        removeClonedTask.setPreferredSize(new Dimension(40, 25));
        removeClonedTask.addActionListener(this);
        removeClonedTask.setActionCommand("removeChannelSecondSensor");
        cButtonClonedTasks.gridy = 1;
        pannelButtonClonedTasks.add(removeClonedTask, cButtonClonedTasks);

        cClonedTasks.gridx = 1;
        cClonedTasks.weightx = 0.05;
        jPanelClonedTasks.add(pannelButtonClonedTasks, cClonedTasks);
        cCloneTask.gridy = 2;
        cCloneTask.gridx = 0;
        cCloneTask.gridwidth = 2;
        jpCloneTask.add(jPanelClonedTasks, cCloneTask);
        contCloneTask.add(jpCloneTask, BorderLayout.NORTH);
        GraphicLib.centerOnParent(cloneTaskDialog, 400, 300);
        cloneTaskDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        switch (command) {
            case "Start":
                startProcess();
                break;
            case "Stop":
                stopProcess();
                break;
            case "Close":
                closeDialog();
                break;
            default:
                if (command.equals("addTaskAsPattern")) {
                    addTaskAsPattern();
                } else if (command.equals("removeTaskAsPattern")) {
                    removeTaskAsPattern();
                } else if (command.equals("allTasksSelectedAsPattern")) {
                    allTasksSelectedAsPattern();
                } else if (command.equals("allTasksNoSelectedAsPattern")) {
                    allTasksNoSelectedAsPattern();
                } else if (command.equals("cloneTask")) {
                    cloneTask();
                } else if (evt.getSource() == jp1) {
                    listPatterns = getFoldersName(pathPatterns);
                }
        }
    }

    private void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
    }

    private void stopProcess() {
        if (rshc != null) {
            try {
                rshc.stopCommand();
            } catch (LauncherException ignored) {
            }
        }
        rshc = null;
        mode = STOPPED;
        setButtons();
        go = false;
    }

    private void startProcess() {
        Thread t = new Thread(this);
        mode = STARTED;
        setButtons();
        go = true;
        t.start();
    }

    private void testGo() throws InterruptedException {
        if (!go) {
            throw new InterruptedException("Stopped by user");
        }
    }

    @Override
    public void run() {
        TraceManager.addDev("Thread started");
        int currentPosY = 0;
        int stepY = 10; 
        if (jp1.getSelectedIndex() == 0) {
            newPatternName = jFieldNewPatternName.getText();
            jta.removeAll();
            boolean findErr = false;
            if (selectedTasksAsPattern.size() == 0) {
                JLabel label = new JLabel("ERROR: No Task Selected As Pattern");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (newPatternName.length() <= 0) {
                JLabel label = new JLabel("ERROR: Pattern name is missing");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            /*if (selectedTasksAsPattern.size() > 1) {
                for (int i = 0; i < selectedTasksAsPattern.size(); i++) {
                    boolean isLinkedToPattern = false;
                    for (int j = 0; j < selectedTasksAsPattern.size(); j++) {
                        if (i != j) {
                            int nbChWr = getListChannelsBetweenTwoTasks(tasksFullName.get(selectedTasksAsPattern.get(i)), tasksFullName.get(selectedTasksAsPattern.get(j))).size();
                            int nbChRd = getListChannelsBetweenTwoTasks(tasksFullName.get(selectedTasksAsPattern.get(j)), tasksFullName.get(selectedTasksAsPattern.get(i))).size();
                            if (nbChWr != 0 || nbChRd != 0) {
                                isLinkedToPattern = true;
                                break;
                            }   
                        }
                    }
                    if (!isLinkedToPattern) {
                        JLabel label = new JLabel("ERROR: The task " + selectedTasksAsPattern.get(i) +" is not linked to the others selected tasks");
                        label.setAlignmentX(Component.LEFT_ALIGNMENT);
                        this.jta.add(label, this.createGbc(currentPosY));
                        currentPosY += stepY;
                        findErr = true;
                    }
                }
            }*/
    
            if (!findErr) {
                mgui.gtm.createPattern(mgui, selectedTasksAsPattern, newPatternName, pathPatterns);
                JLabel label = new JLabel("Pattern Generation Complete");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
            }
            mode = NOT_STARTED;
        } else  if (jp1.getSelectedIndex() == 1) {
            try {
                testGo();
                mode = NOT_STARTED;
            /* } catch (LauncherException le) {
                JLabel label = new JLabel("Error: " + le.getMessage());
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(0));
                mode = STOPPED;*/
            } catch (InterruptedException ie) {
                mode = NOT_STARTED;
            /*} catch (FileException e) {
                System.err.println(e.getMessage() + " : Can't generate TML file."); */
            } catch (Exception e) {
                mode = STOPPED;
                TraceManager.addDev("General exception in pattern generation: " + e.getMessage());
                //throw e;
            }
        }
        setButtons();
    }

    protected void setButtons() {
        switch (mode) {
            case NOT_STARTED:
                startButton.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                startButton.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                break;
            case STOPPED:
            default:
                startButton.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
        }
    }

    @Override
    public void setError() {
    }

    @Override
    public void appendOut(String s) {
    }

    @Override
    public boolean hasToContinue() {
        return this.go;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.maybeShowPopup(e);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger() && e.getComponent() instanceof JList) {
            JList<?> curList = (JList<?>) e.getComponent();
            int row = curList.locationToIndex(e.getPoint());
            curList.clearSelection();
            curList.setSelectedIndex(row);
            Object o = curList.getModel().getElementAt(row);
            if (o instanceof AvatarPragma) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
