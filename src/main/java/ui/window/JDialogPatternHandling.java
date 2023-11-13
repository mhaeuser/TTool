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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
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
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
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

import launcher.LauncherException;
import launcher.RshClient;
import myutil.GraphicLib;
import myutil.MasterProcessInterface;
import myutil.TraceManager;
import tmltranslator.HwNode;
import tmltranslator.TMLArchitecture;
import tmltranslator.TMLChannel;
import tmltranslator.TMLEvent;
import tmltranslator.TMLModeling;
import tmltranslator.TMLRequest;
import tmltranslator.TMLTask;
import tmltranslator.patternhandling.AttributeTaskJsonFile;
import tmltranslator.patternhandling.MappingPatternChannel;
import tmltranslator.patternhandling.MappingPatternTask;
import tmltranslator.patternhandling.PatternChannelWithSecurity;
import tmltranslator.patternhandling.PatternCloneTask;
import tmltranslator.patternhandling.PatternConfiguration;
import tmltranslator.patternhandling.PatternConnection;
import tmltranslator.patternhandling.PatternCreation;
import tmltranslator.patternhandling.PatternPortsConfig;
import tmltranslator.patternhandling.PortTaskJsonFile;
import tmltranslator.patternhandling.TaskPattern;
import tmltranslator.patternhandling.TaskPorts;
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

public class JDialogPatternHandling extends JDialog implements ActionListener, ListSelectionListener, MouseListener, Runnable,
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

    // Pattern Creation
    String newPatternName;
    protected JTextField jFieldNewPatternName;
    protected JButton buttonAddAllTasksAsPattern, buttonAddSelectedTasksAsPattern, buttonRemoveAllTasksAsPattern, buttonRemoveSelectedTasksAsPattern;
    JList<String> jListNoSelectedTasksAsPattern, jListSelectedTasksAsPattern;
    Vector<String> selectedTasksAsPattern = new Vector<String>();
    Vector<String> noSelectedTasksAsPattern = new Vector<String>();
    Map<String, String> tasksFullName = new LinkedHashMap<String, String>();
    JPanel jPanelPatternSelection;

    // Pattern Configuration
    Vector<String> listPatterns = new Vector<String>();
    Vector<String> tasksOfPatternWithExternalPort = new Vector<String>();
    Vector<String> externalPortsOfTaskInPattern = new Vector<String>();
    Vector<String> tasksOfModel = new Vector<String>();
    Vector<String> portsOfTaskInModel = new Vector<String>();
    JComboBox<String> jComboBoxPatterns;
    JComboBox<String> jComboBoxPatternsTaskWithExternalPort;
    DefaultComboBoxModel<String> modelPatternsTaskWithExternalPort = new DefaultComboBoxModel<>(tasksOfPatternWithExternalPort);
    JComboBox<String> jComboBoxPatternExternalPortOfATask;
    DefaultComboBoxModel<String> modelPatternExternalPortOfATask = new DefaultComboBoxModel<>(externalPortsOfTaskInPattern);
    JComboBox<String> jComboBoxModelsTask;
    DefaultComboBoxModel<String> modelTask = new DefaultComboBoxModel<>(tasksOfModel);
    JComboBox<String> jComboBoxModelsPortOfTask;
    DefaultComboBoxModel<String> modelPortOfATask = new DefaultComboBoxModel<>(portsOfTaskInModel);
    JCheckBox jCheckBoxConnectToNewPort;
    JCheckBox jCheckBoxAddConfidentiality;
    JLabel jLabelAddAuthenticity;
    Vector<String> authenticityModes = new Vector<String>(Arrays.asList(PatternCreation.WITHOUT_AUTHENTICITY, PatternCreation.WEAK_AUTHENTICITY, PatternCreation.STRONG_AUTHENTICITY));
    JComboBox<String> jComboBoxAddAuthenticity;
    JList<String> jListConnectedPorts;
    Vector<String> connectedPorts = new Vector<String>();
    List<PatternConnection> patternConnectionList = new ArrayList<PatternConnection>();
    JButton addConnectionBetweenSelectedPorts, removeConnectionBetweenPorts;
    //JPanel jPanelPatternIntegration;
    JButton buttonCloneTask, buttonAddPortInTask;
    Vector<String> portsConfig = new Vector<String>();
    JComboBox<String> jComboBoxPortsConfig;
    DefaultComboBoxModel<String> modelPortsConfig = new DefaultComboBoxModel<>(portsConfig);
    ButtonGroup portsConfigGroup;
    JRadioButton jRadioPortConfigRemove, jRadioPortConfigMerge;
    Vector<String> portsConfigMerge = new Vector<String>();
    DefaultComboBoxModel<String> modelPortsConfigMerge = new DefaultComboBoxModel<>(portsConfigMerge);
    JComboBox<String> jComboBoxPortsConfigMerge;
    JList<String> jListConfigPorts;
    Vector<String> configuredPorts = new Vector<String>();
    List<PatternPortsConfig> configuredPortsList = new ArrayList<PatternPortsConfig>();
    JButton addConfigPorts, removeConfigPorts;
    JButton buttonTasksMapInArch;
    JButton buttonChannelsMapInArch;
    JButton buttonUpdatePatternsAttributes;

    Vector<String> tasksCanBeCloned = new Vector<String>();
    DefaultComboBoxModel<String> modelTaskToClone = new DefaultComboBoxModel<>(tasksCanBeCloned);
    JComboBox<String> jComboBoxTaskToClone;
    String newClonedTaskName;
    protected JTextField jFieldNewClonedTaskName;
    JButton addClonedTask, removeClonedTask;
    JList<String> jListClonedTasks;
    Vector<String> clonedTasks = new Vector<String>();
    List<PatternCloneTask> clonedTasksList = new ArrayList<PatternCloneTask>();

    ButtonGroup mapTaskGroup;
    JRadioButton jRadioMapTaskInExistingHw, jRadioMapTaskInNewHw;
    Vector<String> tasksToMap = new Vector<String>();
    List<MappingPatternTask> tasksToMapList = new ArrayList<MappingPatternTask>();
    Vector<String> tasksToMapInSameHw = new Vector<String>();
    List<MappingPatternTask> tasksToMapInSameHwList = new ArrayList<MappingPatternTask>();
    Vector<String> busToLinkNewHw = new Vector<String>();
    DefaultComboBoxModel<String> modelTaskToMap = new DefaultComboBoxModel<>(tasksToMap);
    DefaultComboBoxModel<String> modelMapTaskInSameHwAs = new DefaultComboBoxModel<>(tasksToMapInSameHw);
    JComboBox<String> jComboBoxTaskToMap, jComboBoxMapTaskInSameHwAs, jComboBoxMapTaskInNewHw;
    JButton addMappedTask, removeMappedTask;
    JList<String> jListMappedTasks;
    Vector<String> mappedTasks = new Vector<String>();
    List<MappingPatternTask> mappedTasksList = new ArrayList<MappingPatternTask>();

    ButtonGroup mapChannelGroup;
    JRadioButton jRadioMapChannelInExistingMem, jRadioMapChannelInNewMem;
    Vector<String> channelsToMap = new Vector<String>();
    List<MappingPatternChannel> channelsToMapList = new ArrayList<MappingPatternChannel>();
    Vector<String> channelsToMapInSameMem = new Vector<String>();
    List<MappingPatternChannel> channelsToMapInSameMemList = new ArrayList<MappingPatternChannel>();
    Vector<String> busToLinkNewMem = new Vector<String>();
    DefaultComboBoxModel<String> modelChannelToMap = new DefaultComboBoxModel<>(channelsToMap);
    DefaultComboBoxModel<String> modelMapChannelInSameMemAs = new DefaultComboBoxModel<>(channelsToMapInSameMem);
    JComboBox<String> jComboBoxChannelToMap, jComboBoxMapChannelInSameMemAs, jComboBoxMapChannelInNewMem;
    JButton addMappedChannel, removeMappedChannel;
    JList<String> jListMappedChannels;
    Vector<String> mappedChannels = new Vector<String>();
    List<MappingPatternChannel> mappedChannelsList = new ArrayList<MappingPatternChannel>();

    Vector<String> tasksToUpdateAttributes = new Vector<String>();
    DefaultComboBoxModel<String> modelTasksToUpdateAttributes = new DefaultComboBoxModel<>(tasksToUpdateAttributes);
    JComboBox<String> jComboBoxTasksToUpdateAttributes;
    Vector<String> attributesOfTaskToUpdate = new Vector<String>();
    DefaultComboBoxModel<String> modelAttributesOfTaskToUpdate = new DefaultComboBoxModel<>(attributesOfTaskToUpdate);
    JComboBox<String> jComboBoxAttributesOfTaskToUpdate;
    String newTaskAttibuteValue;
    protected JTextField jFieldNewTaskAttibuteValue;
    JButton buttonUpdateTaskAttributeValue;
    

    //LinkedHashMap<String, List<AttributeTaskJsonFile>> patternTasksAttributes = new LinkedHashMap<String, List<AttributeTaskJsonFile>>();
    //LinkedHashMap<String, List<PortTaskJsonFile>> patternTasksExternalPorts = new LinkedHashMap<String, List<PortTaskJsonFile>>();
    //LinkedHashMap<String, List<PortTaskJsonFile>> patternTasksInternalPorts = new LinkedHashMap<String, List<PortTaskJsonFile>>();
    LinkedHashMap<String, TaskPattern> patternTasksAll = new LinkedHashMap<String, TaskPattern>();
    LinkedHashMap<String, TaskPattern> patternTasksNotConnected = new LinkedHashMap<String, TaskPattern>();
    LinkedHashMap<String, TaskPorts> portsTaskOfModelAll = new LinkedHashMap<String, TaskPorts>();
    LinkedHashMap<String, TaskPorts> portsTaskOfModelLeft = new LinkedHashMap<String, TaskPorts>();
    List<PatternPortsConfig> portsTaskConfig = new ArrayList<PatternPortsConfig>();
    LinkedHashMap<String, List<AttributeTaskJsonFile>> updatedPatternAttributes = new LinkedHashMap<String, List<AttributeTaskJsonFile>>();
    List<PatternChannelWithSecurity> channelsWithSecurity = new ArrayList<PatternChannelWithSecurity>();
    
    List<String> busesOfModel = new ArrayList<String>();

    // Pattern Integration
    Vector<String> listPatternsJson = new Vector<String>();
    JComboBox<String> jComboBoxPatternsJson;
    String patternJsonPathValue;
    protected JTextField jFieldPatternJsonPath;

    //components
    protected JScrollPane jsp;
    protected JPanel jta;
    private JButton startButton;
    protected JButton stop;
    protected JButton close;

    public TGHelpButton myButton;
    public static String helpString = "securityverification.html";

    private boolean go = false;

    protected RshClient rshc;

    protected JTabbedPane jp1;

    /*
     * Creates new form
     */
    public JDialogPatternHandling(Frame f, MainGUI _mgui, String title, String _pathPatterns) {
        super(f, title, Dialog.ModalityType.DOCUMENT_MODAL);

        mgui = _mgui;
        this.pathPatterns = _pathPatterns;

        TMLModeling<?> tmlmNew = mgui.gtm.getTMLMapping().getTMLModeling();
        for (TMLTask task : tmlmNew.getTasks()) {
            String[] taskNameSplit = task.getName().split("__");
            task.setName(taskNameSplit[taskNameSplit.length-1]);
        }
        for (TMLChannel ch : tmlmNew.getChannels()) {
            String[] channelNameSplit = ch.getName().split("__");
            ch.setName(channelNameSplit[channelNameSplit.length-1]);
        }
        for (TMLEvent evt : tmlmNew.getEvents()) {
            String[] eventNameSplit = evt.getName().split("__");
            evt.setName(eventNameSplit[eventNameSplit.length-1]);
        }
        for (TMLRequest req : tmlmNew.getRequests()) {
            String[] requestNameSplit = req.getName().split("__");
            req.setName(requestNameSplit[requestNameSplit.length-1]);
        }

        for (int i=0; i < mgui.gtm.getTMLMapping().getMappedTasks().size(); i++) {
            String taskFullName = mgui.gtm.getTMLMapping().getMappedTasks().get(i).getName();
            String taskShortName = taskFullName.split("__")[taskFullName.split("__").length - 1];
            tasksFullName.put(taskShortName, taskFullName);
            noSelectedTasksAsPattern.add(taskShortName);
        } 
        currPanel = mgui.getCurrentTURTLEPanel();
        listPatterns = getFoldersName(_pathPatterns);
        listPatternsJson = getFoldersName(_pathPatterns);
        portsTaskOfModelAll = TaskPorts.getListPortsTask(mgui.gtm.getTMLMapping().getTMLModeling());
        portsTaskOfModelLeft = TaskPorts.getListPortsTask(mgui.gtm.getTMLMapping().getTMLModeling());
        busesOfModel = getListBus(mgui.gtm.getTMLMapping().getArch());
        initComponents();
        myInitComponents();
        pack();
    }

    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
    }

    List<String> getListBus(TMLArchitecture tmlarch) {
        List<String> listBusName = new ArrayList<String>();
        for (HwNode bus : tmlarch.getBUSs()) {
            listBusName.add(bus.getName());
        }
        return listBusName;
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

    protected void panelPatternCreation() {
        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Pattern Creation"));

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

        jp1.add("Pattern Creation", jp01);
    }

    protected void panelPatternConfiguration() {
        JPanel jp02 = new JPanel();
        GridBagLayout gridbag02 = new GridBagLayout();
        GridBagConstraints c02 = new GridBagConstraints();
        jp02.setLayout(gridbag02);
        jp02.setBorder(new javax.swing.border.TitledBorder("Pattern Selection and Configuration"));

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
        cPatternConnetion.fill = GridBagConstraints.HORIZONTAL;
        cPatternConnetion.anchor = GridBagConstraints.LINE_START;
        jpPatternConnetion.add(new JLabel("Select an external port of the pattern:"), cPatternConnetion);
        
        
        jComboBoxPatternsTaskWithExternalPort = new JComboBox<String>(modelPatternsTaskWithExternalPort);
        jComboBoxPatternsTaskWithExternalPort.setSelectedIndex(-1);
        jComboBoxPatternsTaskWithExternalPort.setEnabled(false);
        jComboBoxPatternsTaskWithExternalPort.addActionListener(this);
        cPatternConnetion.weightx = 0.4;
        cPatternConnetion.gridy = 1;
        jpPatternConnetion.add(jComboBoxPatternsTaskWithExternalPort, cPatternConnetion);
        jComboBoxPatternExternalPortOfATask = new JComboBox<String>(modelPatternExternalPortOfATask);
        jComboBoxPatternExternalPortOfATask.setSelectedIndex(-1);
        jComboBoxPatternExternalPortOfATask.setEnabled(false);
        jComboBoxPatternExternalPortOfATask.addActionListener(this);
        cPatternConnetion.gridx = 1;
        cPatternConnetion.weightx = 0.8;
        jpPatternConnetion.add(jComboBoxPatternExternalPortOfATask, cPatternConnetion);
        buttonCloneTask = new JButton("Clone Task");
        //buttonCloneTask.setPreferredSize(new Dimension(50, 25));
        buttonCloneTask.setEnabled(false);
        buttonCloneTask.addActionListener(this);
        buttonCloneTask.setActionCommand("cloneTask");
        cPatternConnetion.gridx = 2;
        cPatternConnetion.weightx = 0;
        jpPatternConnetion.add(buttonCloneTask, cPatternConnetion);

        jComboBoxModelsTask = new JComboBox<String>(modelTask);
        jComboBoxModelsTask.setSelectedIndex(-1);
        jComboBoxModelsTask.setEnabled(false);
        jComboBoxModelsTask.addActionListener(this);
        cPatternConnetion.gridx = 0;
        cPatternConnetion.gridy = 2;
        
        jpPatternConnetion.add(new JLabel("Select a model port to connect with:"), cPatternConnetion);
        cPatternConnetion.gridy = 3;
        cPatternConnetion.weightx = 0.8;
        jpPatternConnetion.add(jComboBoxModelsTask, cPatternConnetion);

        jComboBoxModelsPortOfTask = new JComboBox<String>(modelPortOfATask);
        jComboBoxModelsPortOfTask.setSelectedIndex(-1);
        jComboBoxModelsPortOfTask.setEnabled(false);
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

        cPatternConnetion.gridy = 4;
        cPatternConnetion.gridx = 0;
        jCheckBoxAddConfidentiality = new JCheckBox("Add Confidentiality");
        jCheckBoxAddConfidentiality.setEnabled(false);
        jCheckBoxAddConfidentiality.setVisible(false);
        jCheckBoxAddConfidentiality.addActionListener(this);
        jpPatternConnetion.add(jCheckBoxAddConfidentiality, cPatternConnetion);
        cPatternConnetion.gridx = 1;
        jLabelAddAuthenticity = new JLabel("Add Authenticity:");
        jLabelAddAuthenticity.setVisible(false);
        jpPatternConnetion.add(jLabelAddAuthenticity, cPatternConnetion);
        cPatternConnetion.gridx = 2;
        jComboBoxAddAuthenticity = new JComboBox<String>(authenticityModes);
        jComboBoxAddAuthenticity.setSelectedIndex(authenticityModes.indexOf(PatternCreation.WITHOUT_AUTHENTICITY));
        jComboBoxAddAuthenticity.setEnabled(false);
        jComboBoxAddAuthenticity.setVisible(false);
        jComboBoxAddAuthenticity.addActionListener(this);
        jpPatternConnetion.add(jComboBoxAddAuthenticity, cPatternConnetion);

        cPatternConnetionMain.gridy = 1;
        jpPatternConnetionMain.add(jpPatternConnetion, cPatternConnetionMain);


        jListConnectedPorts = new JList<String>(connectedPorts);
		JPanel jPanelPatternIntegration = new JPanel();
        jPanelPatternIntegration.setLayout(new GridBagLayout());
        GridBagConstraints cPatternIntegration = new GridBagConstraints();
        cPatternIntegration.gridx = 0;
        cPatternIntegration.gridy = 0;
        cPatternIntegration.weightx = 0.9;
        cPatternIntegration.fill = GridBagConstraints.HORIZONTAL;
        cPatternIntegration.anchor = GridBagConstraints.LINE_START;
        
		//jPanelPatternIntegration.setPreferredSize(new Dimension(600, 130));
        jListConnectedPorts.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jListConnectedPorts.addListSelectionListener(this);
        JScrollPane scrollPaneConnectedPorts = new JScrollPane(jListConnectedPorts);
        scrollPaneConnectedPorts.setPreferredSize(new Dimension(450, 125));
        jPanelPatternIntegration.add(scrollPaneConnectedPorts, cPatternIntegration);
        
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
        addConnectionBetweenSelectedPorts.setActionCommand("addConnectionBetweenSelectedPorts");
        pannelButtonConnectPorts.add(addConnectionBetweenSelectedPorts, cConnectedPorts);

        removeConnectionBetweenPorts = new JButton("-");
        removeConnectionBetweenPorts.setEnabled(false);
        removeConnectionBetweenPorts.setPreferredSize(new Dimension(50, 25));
        removeConnectionBetweenPorts.addActionListener(this);
        removeConnectionBetweenPorts.setActionCommand("removeConnectionBetweenPorts");
        cConnectedPorts.gridy = 1;
        pannelButtonConnectPorts.add(removeConnectionBetweenPorts, cConnectedPorts);
        //pannelButtonConnectPorts.setPreferredSize(new Dimension(50, 125));
        //jPanelPatternIntegration.setMinimumSize(new Dimension(600, 130));
        cPatternIntegration.gridx = 1;
        cPatternIntegration.weightx = 0.1;
        jPanelPatternIntegration.add(pannelButtonConnectPorts, cPatternIntegration);
        cPatternConnetionMain.gridy = 2;
        jpPatternConnetionMain.add(jPanelPatternIntegration, cPatternConnetionMain);
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

        jComboBoxPortsConfig = new JComboBox<String>(modelPortsConfig);
        jComboBoxPortsConfig.setSelectedIndex(-1);
        jComboBoxPortsConfig.setEnabled(false);
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
        jComboBoxPortsConfigMerge = new JComboBox<String>(modelPortsConfigMerge);
        jComboBoxPortsConfigMerge.setSelectedIndex(-1);
        jComboBoxPortsConfigMerge.setEnabled(false);
        jComboBoxPortsConfigMerge.addActionListener(this);
        jComboBoxPortsConfigMerge.setPreferredSize(new Dimension(200, 25));
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
        addConfigPorts.setActionCommand("addConfigPorts");
        pannelButtonConfigPorts.add(addConfigPorts, cButtonConfigPorts);

        removeConfigPorts = new JButton("-");
        removeConfigPorts.setEnabled(false);
        removeConfigPorts.setPreferredSize(new Dimension(50, 25));
        removeConfigPorts.addActionListener(this);
        removeConfigPorts.setActionCommand("removeConfigPorts");
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
        cOptions.gridwidth = 3;
        jpOptions.add(new JLabel("Options:"), cOptions);
        
        
        buttonTasksMapInArch = new JButton("Map pattern tasks");
        buttonTasksMapInArch.setEnabled(false);
        buttonTasksMapInArch.addActionListener(this);
        buttonTasksMapInArch.setActionCommand("mapTasksManuallyInArchitecture");
        cOptions.gridy = 1;
        cOptions.gridwidth = 1;
        //cOptions.weightx = 0.3;
        cOptions.fill = GridBagConstraints.NONE;
        jpOptions.add(buttonTasksMapInArch, cOptions);

        buttonChannelsMapInArch = new JButton("Map pattern channels");
        buttonChannelsMapInArch.setEnabled(false);
        buttonChannelsMapInArch.addActionListener(this);
        buttonChannelsMapInArch.setActionCommand("mapChannelsManuallyInArchitecture");
        cOptions.gridx = 1;
        jpOptions.add(buttonChannelsMapInArch, cOptions);

        buttonUpdatePatternsAttributes = new JButton("Update Pattern's Attributes values");
        buttonUpdatePatternsAttributes.setEnabled(false);
        buttonUpdatePatternsAttributes.addActionListener(this);
        buttonUpdatePatternsAttributes.setActionCommand("updatePatternsAttributes");
        cOptions.gridx = 2;
        jpOptions.add(buttonUpdatePatternsAttributes, cOptions);

        c02.gridy = 3;
        jp02.add(jpOptions, c02);

        jp1.add("Pattern Selection and Configuration", jp02);
    }

    protected void panelPatternIntegration() {
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

        jComboBoxPatternsJson = new JComboBox<String>(listPatternsJson);
        jComboBoxPatternsJson.setSelectedIndex(-1);
        jComboBoxPatternsJson.addActionListener(this);
        cPatternSelection.gridx = 1;
        jpPatternSelection.add(jComboBoxPatternsJson, cPatternSelection);

        cPatternSelection.gridy = 1;
        cPatternSelection.gridx = 0;
        jpPatternSelection.add(new JLabel("Path To Json File :"), cPatternSelection);


        jFieldPatternJsonPath = new JTextField(patternJsonPathValue, 10);
        jFieldPatternJsonPath.setEnabled(false);
        jFieldPatternJsonPath.setPreferredSize(new Dimension(10, 25));
        jFieldPatternJsonPath.addActionListener(this);
        cPatternSelection.gridx = 1;
        jpPatternSelection.add(jFieldPatternJsonPath, cPatternSelection);
        jp02.add(jpPatternSelection, c02);

        jp1.add("Pattern Integration", jp02);
    }

    protected void initComponents() {

        jp1 = GraphicLib.createTabbedPane();
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

        panelPatternCreation();
        panelPatternConfiguration();
        panelPatternIntegration();

        c.add(jp1, BorderLayout.NORTH);
        c.add(jp2, BorderLayout.SOUTH);
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

    private void mapTasksManuallyInArchitecture() {
        JDialog mapTasksInArchDialog = new JDialog(this.getOwner(), "Map manually tasks of the pattern in the architecture", Dialog.ModalityType.DOCUMENT_MODAL);
        Container contMapTasksInArch = mapTasksInArchDialog.getContentPane();
        contMapTasksInArch.setLayout(new BorderLayout());
        JPanel jpMapTasksInArch = new JPanel();
        jpMapTasksInArch.setLayout(new GridBagLayout());
        GridBagConstraints cMapTasksInArch = new GridBagConstraints();
        cMapTasksInArch.gridx = 0;
        cMapTasksInArch.gridy = 0;
        cMapTasksInArch.fill = GridBagConstraints.HORIZONTAL;
        cMapTasksInArch.anchor = GridBagConstraints.FIRST_LINE_START;
        cMapTasksInArch.weightx = 0.0;
        cMapTasksInArch.insets = new Insets(10,0,0,0);  //top padding
        jpMapTasksInArch.add(new JLabel("Select a Task to map:"), cMapTasksInArch);

        jComboBoxTaskToMap = new JComboBox<String>(modelTaskToMap);
        jComboBoxTaskToMap.setSelectedIndex(-1);
        //jComboBoxTaskToMap.setEnabled(false);
        jComboBoxTaskToMap.addActionListener(this);
        //jComboBoxPortsConfigMerge.setPreferredSize(new Dimension(150, 25));
        cMapTasksInArch.gridx = 1;
        cMapTasksInArch.weightx = 1.0;
        jpMapTasksInArch.add(jComboBoxTaskToMap, cMapTasksInArch);
        cMapTasksInArch.gridx = 0;
        cMapTasksInArch.gridy = 1;
        cMapTasksInArch.weightx = 0.0;
        
        mapTaskGroup =new ButtonGroup();
        jRadioMapTaskInExistingHw = new JRadioButton("Map in the same HW as : ");
        jRadioMapTaskInExistingHw.setEnabled(false);
        jRadioMapTaskInExistingHw.addActionListener(this);
        jpMapTasksInArch.add(jRadioMapTaskInExistingHw, cMapTasksInArch);
        cMapTasksInArch.gridx = 1;
        cMapTasksInArch.weightx = 1.0;
        jComboBoxMapTaskInSameHwAs = new JComboBox<String>(modelMapTaskInSameHwAs);
        jComboBoxMapTaskInSameHwAs.setSelectedIndex(-1);
        jComboBoxMapTaskInSameHwAs.setEnabled(false);
        jComboBoxMapTaskInSameHwAs.addActionListener(this);
        jpMapTasksInArch.add(jComboBoxMapTaskInSameHwAs, cMapTasksInArch);

        cMapTasksInArch.gridy = 2;
        cMapTasksInArch.gridx = 0;
        cMapTasksInArch.weightx = 0.0;
        jRadioMapTaskInNewHw = new JRadioButton("Map in a new HW that will be linked to bus :");
        jRadioMapTaskInNewHw.setEnabled(false);
        jRadioMapTaskInNewHw.addActionListener(this);
        jpMapTasksInArch.add(jRadioMapTaskInNewHw, cMapTasksInArch);
        cMapTasksInArch.gridx = 1;
        cMapTasksInArch.weightx = 1.0;
        busToLinkNewHw = new Vector<String>(busesOfModel) ;
        jComboBoxMapTaskInNewHw = new JComboBox<String>(busToLinkNewHw);
        jComboBoxMapTaskInNewHw.setSelectedIndex(-1);
        jComboBoxMapTaskInNewHw.setEnabled(false);
        jComboBoxMapTaskInNewHw.addActionListener(this);
        jpMapTasksInArch.add(jComboBoxMapTaskInNewHw, cMapTasksInArch);
        mapTaskGroup.add(jRadioMapTaskInExistingHw);
        mapTaskGroup.add(jRadioMapTaskInNewHw);


        jListMappedTasks = new JList<String>(mappedTasks);
		JPanel jPanelMappedTasks = new JPanel();
        jPanelMappedTasks.setLayout(new GridBagLayout());
        GridBagConstraints cMappedTasks = new GridBagConstraints();
        cMappedTasks.gridx = 0;
        cMappedTasks.gridy = 0;
        cMappedTasks.weightx = 0.95;
        cMappedTasks.fill = GridBagConstraints.HORIZONTAL;
        cMappedTasks.anchor = GridBagConstraints.LINE_START;
        
        jListMappedTasks.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jListMappedTasks.addListSelectionListener(this);
        JScrollPane scrollPaneMappedTasks = new JScrollPane(jListMappedTasks);
        scrollPaneMappedTasks.setPreferredSize(new Dimension(250, 175));
        jPanelMappedTasks.add(scrollPaneMappedTasks, cMappedTasks);
        
        JPanel pannelButtonMappedTasks = new JPanel();
        pannelButtonMappedTasks.setLayout(new GridBagLayout());
        GridBagConstraints cButtonMappedTasks = new GridBagConstraints();
        cButtonMappedTasks.gridx = 0;
        cButtonMappedTasks.weightx = 1.0;
        cButtonMappedTasks.gridy = 0;
        cButtonMappedTasks.fill = GridBagConstraints.HORIZONTAL;
        cButtonMappedTasks.anchor = GridBagConstraints.LINE_START;
        addMappedTask = new JButton("+");
        addMappedTask.setEnabled(false);
        addMappedTask.setPreferredSize(new Dimension(40, 25));
        addMappedTask.addActionListener(this);
        addMappedTask.setActionCommand("addMappedTask");
        pannelButtonMappedTasks.add(addMappedTask, cButtonMappedTasks);

        removeMappedTask = new JButton("-");
        //removeMappedTask.setEnabled(false);
        removeMappedTask.setPreferredSize(new Dimension(40, 25));
        removeMappedTask.addActionListener(this);
        removeMappedTask.setActionCommand("removeMappedTask");
        cButtonMappedTasks.gridy = 1;
        pannelButtonMappedTasks.add(removeMappedTask, cButtonMappedTasks);

        cMappedTasks.gridx = 1;
        cMappedTasks.weightx = 0.05;
        jPanelMappedTasks.add(pannelButtonMappedTasks, cMappedTasks);
        cMapTasksInArch.gridy = 3;
        cMapTasksInArch.gridx = 0;
        cMapTasksInArch.gridwidth = 2;
        jpMapTasksInArch.add(jPanelMappedTasks, cMapTasksInArch);
        contMapTasksInArch.add(jpMapTasksInArch, BorderLayout.NORTH);
        GraphicLib.centerOnParent(mapTasksInArchDialog, 500, 400);
        mapTasksInArchDialog.setVisible(true);
    }

    private void mapChannelsManuallyInArchitecture() {
        JDialog mapChannelsInArchDialog = new JDialog(this.getOwner(), "Map manually channels of the pattern in the architecture", Dialog.ModalityType.DOCUMENT_MODAL);
        Container contMapChannelsInArch = mapChannelsInArchDialog.getContentPane();
        contMapChannelsInArch.setLayout(new BorderLayout());
        JPanel jpMapChannelsInArch = new JPanel();
        jpMapChannelsInArch.setLayout(new GridBagLayout());
        GridBagConstraints cMapChannelsInArch = new GridBagConstraints();
        cMapChannelsInArch.gridx = 0;
        cMapChannelsInArch.gridy = 0;
        cMapChannelsInArch.fill = GridBagConstraints.HORIZONTAL;
        cMapChannelsInArch.anchor = GridBagConstraints.FIRST_LINE_START;
        cMapChannelsInArch.weightx = 0.0;
        cMapChannelsInArch.insets = new Insets(10,0,0,0);  //top padding
        jpMapChannelsInArch.add(new JLabel("Select a Channel to map:"), cMapChannelsInArch);

        jComboBoxChannelToMap = new JComboBox<String>(modelChannelToMap);
        jComboBoxChannelToMap.setSelectedIndex(-1);
        //jComboBoxChannelToMap.setEnabled(false);
        jComboBoxChannelToMap.addActionListener(this);
        //jComboBoxPortsConfigMerge.setPreferredSize(new Dimension(150, 25));
        cMapChannelsInArch.gridx = 1;
        cMapChannelsInArch.weightx = 1.0;
        jpMapChannelsInArch.add(jComboBoxChannelToMap, cMapChannelsInArch);
        cMapChannelsInArch.gridx = 0;
        cMapChannelsInArch.gridy = 1;
        cMapChannelsInArch.weightx = 0.0;
        
        mapChannelGroup =new ButtonGroup();
        jRadioMapChannelInExistingMem = new JRadioButton("Map in the same Memory as : ");
        jRadioMapChannelInExistingMem.setEnabled(false);
        jRadioMapChannelInExistingMem.addActionListener(this);
        jpMapChannelsInArch.add(jRadioMapChannelInExistingMem, cMapChannelsInArch);
        cMapChannelsInArch.gridx = 1;
        cMapChannelsInArch.weightx = 1.0;
        jComboBoxMapChannelInSameMemAs = new JComboBox<String>(modelMapChannelInSameMemAs);
        jComboBoxMapChannelInSameMemAs.setSelectedIndex(-1);
        jComboBoxMapChannelInSameMemAs.setEnabled(false);
        jComboBoxMapChannelInSameMemAs.addActionListener(this);
        jpMapChannelsInArch.add(jComboBoxMapChannelInSameMemAs, cMapChannelsInArch);

        cMapChannelsInArch.gridy = 2;
        cMapChannelsInArch.gridx = 0;
        cMapChannelsInArch.weightx = 0.0;
        jRadioMapChannelInNewMem = new JRadioButton("Map in a new Memory that will be linked to bus :");
        jRadioMapChannelInNewMem.setEnabled(false);
        jRadioMapChannelInNewMem.addActionListener(this);
        jpMapChannelsInArch.add(jRadioMapChannelInNewMem, cMapChannelsInArch);
        cMapChannelsInArch.gridx = 1;
        cMapChannelsInArch.weightx = 1.0;
        busToLinkNewMem = new Vector<String>(busesOfModel) ;
        jComboBoxMapChannelInNewMem = new JComboBox<String>(busToLinkNewMem);
        jComboBoxMapChannelInNewMem.setSelectedIndex(-1);
        jComboBoxMapChannelInNewMem.setEnabled(false);
        jComboBoxMapChannelInNewMem.addActionListener(this);
        jpMapChannelsInArch.add(jComboBoxMapChannelInNewMem, cMapChannelsInArch);
        mapChannelGroup.add(jRadioMapChannelInExistingMem);
        mapChannelGroup.add(jRadioMapChannelInNewMem);


        jListMappedChannels = new JList<String>(mappedChannels);
		JPanel jPanelMappedChannels = new JPanel();
        jPanelMappedChannels.setLayout(new GridBagLayout());
        GridBagConstraints cMappedChannels = new GridBagConstraints();
        cMappedChannels.gridx = 0;
        cMappedChannels.gridy = 0;
        cMappedChannels.weightx = 0.95;
        cMappedChannels.fill = GridBagConstraints.HORIZONTAL;
        cMappedChannels.anchor = GridBagConstraints.LINE_START;
        
        jListMappedChannels.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jListMappedChannels.addListSelectionListener(this);
        JScrollPane scrollPaneMappedChannels = new JScrollPane(jListMappedChannels);
        scrollPaneMappedChannels.setPreferredSize(new Dimension(250, 175));
        jPanelMappedChannels.add(scrollPaneMappedChannels, cMappedChannels);
        
        JPanel pannelButtonMappedChannels = new JPanel();
        pannelButtonMappedChannels.setLayout(new GridBagLayout());
        GridBagConstraints cButtonMappedChannels = new GridBagConstraints();
        cButtonMappedChannels.gridx = 0;
        cButtonMappedChannels.weightx = 1.0;
        cButtonMappedChannels.gridy = 0;
        cButtonMappedChannels.fill = GridBagConstraints.HORIZONTAL;
        cButtonMappedChannels.anchor = GridBagConstraints.LINE_START;
        addMappedChannel = new JButton("+");
        addMappedChannel.setEnabled(false);
        addMappedChannel.setPreferredSize(new Dimension(40, 25));
        addMappedChannel.addActionListener(this);
        addMappedChannel.setActionCommand("addMappedChannel");
        pannelButtonMappedChannels.add(addMappedChannel, cButtonMappedChannels);

        removeMappedChannel = new JButton("-");
        //removeMappedChannel.setEnabled(false);
        removeMappedChannel.setPreferredSize(new Dimension(40, 25));
        removeMappedChannel.addActionListener(this);
        removeMappedChannel.setActionCommand("removeMappedChannel");
        cButtonMappedChannels.gridy = 1;
        pannelButtonMappedChannels.add(removeMappedChannel, cButtonMappedChannels);

        cMappedChannels.gridx = 1;
        cMappedChannels.weightx = 0.05;
        jPanelMappedChannels.add(pannelButtonMappedChannels, cMappedChannels);
        cMapChannelsInArch.gridy = 3;
        cMapChannelsInArch.gridx = 0;
        cMapChannelsInArch.gridwidth = 2;
        jpMapChannelsInArch.add(jPanelMappedChannels, cMapChannelsInArch);
        contMapChannelsInArch.add(jpMapChannelsInArch, BorderLayout.NORTH);
        GraphicLib.centerOnParent(mapChannelsInArchDialog, 500, 400);
        mapChannelsInArchDialog.setVisible(true);
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

        tasksCanBeCloned.removeAllElements();
        for (String s : portsTaskOfModelAll.keySet()) {
            tasksCanBeCloned.add(s);
        }
        jComboBoxTaskToClone = new JComboBox<String>(modelTaskToClone);
        jComboBoxTaskToClone.setSelectedIndex(-1);
        //jComboBoxTaskToClone.setEnabled(false);
        jComboBoxTaskToClone.addActionListener(this);
        //jComboBoxPortsConfigMerge.setPreferredSize(new Dimension(150, 25));
        cCloneTask.gridx = 1;
        jpCloneTask.add(jComboBoxTaskToClone, cCloneTask);
        cCloneTask.gridx = 0;
        cCloneTask.gridy = 1;
        jpCloneTask.add(new JLabel("Cloned task name:"), cCloneTask);
        jFieldNewClonedTaskName = new JTextField(newClonedTaskName, 10);
        jFieldNewClonedTaskName.setEnabled(false);
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
        scrollPaneClonedTasks.setPreferredSize(new Dimension(250, 150));
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
        addClonedTask.setActionCommand("addClonedTask");
        pannelButtonClonedTasks.add(addClonedTask, cButtonClonedTasks);

        removeClonedTask = new JButton("-");
        //removeClonedTask.setEnabled(false);
        removeClonedTask.setPreferredSize(new Dimension(40, 25));
        removeClonedTask.addActionListener(this);
        removeClonedTask.setActionCommand("removeClonedTask");
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
        GraphicLib.centerOnParent(cloneTaskDialog, 470, 350);
        cloneTaskDialog.setVisible(true);
    }

    private void updatePatternsAttributes() {
        JDialog updatePatternsAttributesDialog = new JDialog(this.getOwner(), "Update Pattern's Attributes Values", Dialog.ModalityType.DOCUMENT_MODAL);
        Container contUpdatePatternsAttributes = updatePatternsAttributesDialog.getContentPane();
        contUpdatePatternsAttributes.setLayout(new BorderLayout());
        JPanel jpUpdatePatternsAttributes = new JPanel();
        jpUpdatePatternsAttributes.setLayout(new GridBagLayout());
        GridBagConstraints cUpdatePatternsAttributes = new GridBagConstraints();
        cUpdatePatternsAttributes.gridx = 0;
        cUpdatePatternsAttributes.gridy = 0;
        cUpdatePatternsAttributes.fill = GridBagConstraints.HORIZONTAL;
        cUpdatePatternsAttributes.anchor = GridBagConstraints.FIRST_LINE_START;
        cUpdatePatternsAttributes.weightx = 1.0;
        cUpdatePatternsAttributes.insets = new Insets(10,0,0,0);  //top padding
        jpUpdatePatternsAttributes.add(new JLabel("Select a Task to update its attributes:"), cUpdatePatternsAttributes);

        tasksToUpdateAttributes.removeAllElements();
        for (String s : patternTasksAll.keySet()) {
            tasksToUpdateAttributes.add(s);
        }
        jComboBoxTasksToUpdateAttributes = new JComboBox<String>(modelTasksToUpdateAttributes);
        jComboBoxTasksToUpdateAttributes.setSelectedIndex(-1);
        //jComboBoxTasksToUpdateAttributes.setEnabled(false);
        jComboBoxTasksToUpdateAttributes.addActionListener(this);
        cUpdatePatternsAttributes.gridx = 1;
        jpUpdatePatternsAttributes.add(jComboBoxTasksToUpdateAttributes, cUpdatePatternsAttributes);

        cUpdatePatternsAttributes.gridx = 0;
        cUpdatePatternsAttributes.gridy = 1;
        jpUpdatePatternsAttributes.add(new JLabel("Select an attribute to update:"), cUpdatePatternsAttributes);
        attributesOfTaskToUpdate.removeAllElements();
        jComboBoxAttributesOfTaskToUpdate = new JComboBox<String>(modelAttributesOfTaskToUpdate);
        jComboBoxAttributesOfTaskToUpdate.setSelectedIndex(-1);
        jComboBoxAttributesOfTaskToUpdate.setEnabled(false);
        jComboBoxAttributesOfTaskToUpdate.addActionListener(this);
        cUpdatePatternsAttributes.gridx = 1;
        jpUpdatePatternsAttributes.add(jComboBoxAttributesOfTaskToUpdate, cUpdatePatternsAttributes);

        cUpdatePatternsAttributes.gridx = 0;
        cUpdatePatternsAttributes.gridy = 2;
        jpUpdatePatternsAttributes.add(new JLabel("New Value:"), cUpdatePatternsAttributes);
        jFieldNewTaskAttibuteValue = new JTextField(newTaskAttibuteValue, 10);
        jFieldNewTaskAttibuteValue.setEnabled(false);
        jFieldNewTaskAttibuteValue.setPreferredSize(new Dimension(10, 25));
        jFieldNewTaskAttibuteValue.addActionListener(this);
        cUpdatePatternsAttributes.gridx = 1;
        jpUpdatePatternsAttributes.add(jFieldNewTaskAttibuteValue, cUpdatePatternsAttributes);
        
        JPanel pannelButtonUpdatePatternsAttributesValues = new JPanel();
        pannelButtonUpdatePatternsAttributesValues.setLayout(new GridBagLayout());
        GridBagConstraints cButtonUpdatePatternsAttributesValues = new GridBagConstraints();
        cButtonUpdatePatternsAttributesValues.gridx = 0;
        cButtonUpdatePatternsAttributesValues.weightx = 1.0;
        cButtonUpdatePatternsAttributesValues.gridy = 0;
        cButtonUpdatePatternsAttributesValues.fill = GridBagConstraints.HORIZONTAL;
        cButtonUpdatePatternsAttributesValues.anchor = GridBagConstraints.LINE_START;
        buttonUpdateTaskAttributeValue = new JButton("Update");
        buttonUpdateTaskAttributeValue.setEnabled(false);
        buttonUpdateTaskAttributeValue.setPreferredSize(new Dimension(100, 50));
        buttonUpdateTaskAttributeValue.addActionListener(this);
        buttonUpdateTaskAttributeValue.setActionCommand("updateTaskAttributeValue");
        pannelButtonUpdatePatternsAttributesValues.add(buttonUpdateTaskAttributeValue, cButtonUpdatePatternsAttributesValues);

        cUpdatePatternsAttributes.gridy = 3;
        cUpdatePatternsAttributes.gridx = 0;
        cUpdatePatternsAttributes.gridwidth = 2;
        cUpdatePatternsAttributes.fill = GridBagConstraints.NONE;
        cUpdatePatternsAttributes.anchor = GridBagConstraints.CENTER;
        jpUpdatePatternsAttributes.add(pannelButtonUpdatePatternsAttributesValues, cUpdatePatternsAttributes);
        contUpdatePatternsAttributes.add(jpUpdatePatternsAttributes, BorderLayout.NORTH);
        GraphicLib.centerOnParent(updatePatternsAttributesDialog, 470, 350);
        updatePatternsAttributesDialog.setVisible(true);
    }

    private void updateTaskAttributeValue() {
        String selectedTaskToUpdateAttributes = jComboBoxTasksToUpdateAttributes.getSelectedItem().toString();
        String selectedAttributeToUpdate = jComboBoxAttributesOfTaskToUpdate.getSelectedItem().toString();
        String newValueAttribute = jFieldNewTaskAttibuteValue.getText();
        if (newValueAttribute.matches("-?\\d+") || newValueAttribute.matches("(?i)^(true|false)")) {
            for (AttributeTaskJsonFile attributeTaskJsonFile : patternTasksAll.get(selectedTaskToUpdateAttributes).getAttributes()) {
                if (attributeTaskJsonFile.getName().equals(selectedAttributeToUpdate)) {
                    attributeTaskJsonFile.setValue(newValueAttribute);
                    jFieldNewTaskAttibuteValue.setText(attributeTaskJsonFile.getValue());
                    if (updatedPatternAttributes.containsKey(selectedTaskToUpdateAttributes)) {
                        boolean attributeExist = false;
                        for (AttributeTaskJsonFile attribUpdate : updatedPatternAttributes.get(selectedTaskToUpdateAttributes)) {
                            if (attribUpdate.getName().equals(selectedAttributeToUpdate)) {
                                attribUpdate.setValue(newValueAttribute);
                                attributeExist = true;
                            }
                        }
                        if (!attributeExist) {
                            AttributeTaskJsonFile attrib = new AttributeTaskJsonFile(selectedAttributeToUpdate, attributeTaskJsonFile.getType(), newValueAttribute);
                            updatedPatternAttributes.get(selectedTaskToUpdateAttributes).add(attrib);
                        }
                    } else {
                        List<AttributeTaskJsonFile> listAttrib = new ArrayList<AttributeTaskJsonFile>();
                        AttributeTaskJsonFile attrib = new AttributeTaskJsonFile(selectedAttributeToUpdate, attributeTaskJsonFile.getType(), newValueAttribute);
                        listAttrib.add(attrib);
                        updatedPatternAttributes.put(selectedTaskToUpdateAttributes, listAttrib);
                    }
                    
                }
            }
        }
    }

    private void addConnectionBetweenSelectedPorts() {
        String patternTaskName = jComboBoxPatternsTaskWithExternalPort.getSelectedItem().toString();
        String patternTaskPortName = jComboBoxPatternExternalPortOfATask.getSelectedItem().toString();
        String modelTaskName = jComboBoxModelsTask.getSelectedItem().toString();
        String modelTaskPortName = jComboBoxModelsPortOfTask.getSelectedItem().toString();
        Boolean isNewPort = jCheckBoxConnectToNewPort.isSelected();
        PatternConnection patternConnection = new PatternConnection(patternTaskName, patternTaskPortName, modelTaskName, modelTaskPortName, isNewPort);
        
        connectedPorts.add(patternConnection.getStringDisplay());
        if (!isNewPort) {
            TaskPorts pt = portsTaskOfModelLeft.get(modelTaskName);
            if (pt.getWriteChannels().contains(modelTaskPortName)) {
                pt.getWriteChannels().remove(modelTaskPortName);
            } else if (pt.getReadChannels().contains(modelTaskPortName)) {
                pt.getReadChannels().remove(modelTaskPortName);
            } else if (pt.getSendEvents().contains(modelTaskPortName)) {
                pt.getSendEvents().remove(modelTaskPortName);
            } else if (pt.getWaitEvents().contains(modelTaskPortName)) {
                pt.getWaitEvents().remove(modelTaskPortName);
            } 
        }
        TaskPattern tp = patternTasksNotConnected.get(patternTaskName);
        int indexElemToRemove = -1;
        for (int i = 0; i < tp.getExternalPorts().size(); i++){
            if (tp.getExternalPorts().get(i).getName().equals(patternTaskPortName)) {
                indexElemToRemove = i;
                break;
            }
        }
        if (indexElemToRemove >= 0) {
            tp.getExternalPorts().remove(indexElemToRemove);
        }
        if (tp.getExternalPorts().size() == 0) {
            patternTasksNotConnected.remove(patternTaskName);
            tasksOfPatternWithExternalPort.removeAllElements();
            for (String pTaskName : patternTasksNotConnected.keySet()) {
                if (patternTasksNotConnected.get(pTaskName).getExternalPorts().size() > 0) {
                    tasksOfPatternWithExternalPort.add(pTaskName);
                }
            }
        }

        for (PortTaskJsonFile  portTaskJsonFile : patternTasksAll.get(patternTaskName).getExternalPorts()) {
            if (portTaskJsonFile.getName().equals(patternTaskPortName)) {
                if (portTaskJsonFile.getType().equals(PatternCreation.CHANNEL)) {
                    if (jCheckBoxAddConfidentiality.isSelected()) {
                        portTaskJsonFile.setConfidentiality(PatternCreation.WITH_CONFIDENTIALITY);
                    } else {
                        portTaskJsonFile.setConfidentiality(PatternCreation.WITHOUT_CONFIDENTIALITY);
                    }
                    portTaskJsonFile.setAuthenticity(jComboBoxAddAuthenticity.getSelectedItem().toString());
                    break;
                }
            }
        }

        patternConnectionList.add(patternConnection);
        
        /*if (jCheckBoxAddConfidentiality.isSelected() || (jComboBoxAddAuthenticity.getSelectedIndex() >= 0 && !jComboBoxAddAuthenticity.getSelectedItem().toString().toUpperCase().equals(PatternCreation.WITHOUT_AUTHENTICITY.toUpperCase()))) {
            if (channelsWithSecurity.containsKey(patternTaskName)) {
                boolean channelWithSecToUpdate = false;
                for (PortTaskJsonFile pt : channelsWithSecurity.get(patternTaskName)) {
                    if (pt.getName().equals(patternTaskPortName)) {
                        if (jCheckBoxAddConfidentiality.isSelected()) {
                            pt.setConfidentiality(PatternCreation.WITH_CONFIDENTIALITY);
                        }
                        if (jComboBoxAddAuthenticity.getSelectedIndex() >= 0 && !jComboBoxAddAuthenticity.getSelectedItem().toString().toUpperCase().equals(PatternCreation.WITHOUT_AUTHENTICITY.toUpperCase())) {
                            pt.setAuthenticity(jComboBoxAddAuthenticity.getSelectedItem().toString());
                        }
                        channelWithSecToUpdate = true;
                        break;
                    }
                }
                if (!channelWithSecToUpdate) {
                    for (PortTaskJsonFile  portTaskJsonFile : patternTasksAll.get(patternTaskName).getExternalPorts()) {
                        if (portTaskJsonFile.getName().equals(patternTaskPortName)) {
                            if (jCheckBoxAddConfidentiality.isSelected()) {
                                portTaskJsonFile.setConfidentiality(PatternCreation.WITH_CONFIDENTIALITY);
                            }
                            if (jComboBoxAddAuthenticity.getSelectedIndex() >= 0 && !jComboBoxAddAuthenticity.getSelectedItem().toString().toUpperCase().equals(PatternCreation.WITHOUT_AUTHENTICITY.toUpperCase())) {
                                portTaskJsonFile.setAuthenticity(jComboBoxAddAuthenticity.getSelectedItem().toString());
                            }
                            channelsWithSecurity.get(patternTaskName).add(portTaskJsonFile);
                            break;
                        }
                    }
                }
            } else {
                List<PortTaskJsonFile> channelWithSecInfo = new ArrayList<PortTaskJsonFile>();
                for (PortTaskJsonFile  portTaskJsonFile : patternTasksAll.get(patternTaskName).getExternalPorts()) {
                    if (portTaskJsonFile.getName().equals(patternTaskPortName)) {
                        if (jCheckBoxAddConfidentiality.isSelected()) {
                            portTaskJsonFile.setConfidentiality(PatternCreation.WITH_CONFIDENTIALITY);
                        }
                        if (jComboBoxAddAuthenticity.getSelectedIndex() >= 0 && !jComboBoxAddAuthenticity.getSelectedItem().toString().toUpperCase().equals(PatternCreation.WITHOUT_AUTHENTICITY.toUpperCase())) {
                            portTaskJsonFile.setAuthenticity(jComboBoxAddAuthenticity.getSelectedItem().toString());
                        }
                        channelWithSecInfo.add(portTaskJsonFile);
                        channelsWithSecurity.put(patternTaskName, channelWithSecInfo);
                        break;
                    }
                }
            } 
        }*/

        jComboBoxPatternsTaskWithExternalPort.setSelectedIndex(-1);
        jCheckBoxConnectToNewPort.setSelected(false);
        jListConnectedPorts.setListData(connectedPorts);
        setButtons();
    }

    private void removeConnectionBetweenPorts() {
        int[] list = jListConnectedPorts.getSelectedIndices();
        List<PatternConnection> patternConnectionsToRemove = new ArrayList<PatternConnection>();
        for (int i = 0; i < list.length; i++) {
            PatternConnection patternConnection = patternConnectionList.get(list[i]);
            String patternTaskName = patternConnection.getPatternTaskName();
            String patternTaskPortName = patternConnection.getPatternChannel();
            String modelTaskName = patternConnection.getModelTaskName();
            String modelTaskPortName = patternConnection.getModelChannelName();
            for (String patternTask : patternTasksAll.keySet()) {
                for (PortTaskJsonFile  portTaskJsonFile : patternTasksAll.get(patternTask).getExternalPorts()) {
                    if (portTaskJsonFile.getName().equals(patternTaskPortName)) {
                        if (patternTasksNotConnected.containsKey(patternTaskName)) {
                            TaskPattern tp  = patternTasksNotConnected.get(patternTaskName);
                            tp.getExternalPorts().add(portTaskJsonFile);
                        } else {
                            List<PortTaskJsonFile> portTaskJsonFilesList = new ArrayList<PortTaskJsonFile>(Arrays.asList(portTaskJsonFile));
                            TaskPattern tp = new TaskPattern(patternTasksAll.get(patternTask).getAttributes(), patternTasksAll.get(patternTask).getInternalPorts(), portTaskJsonFilesList);
                            patternTasksNotConnected.put(patternTaskName, tp);
                            tasksOfPatternWithExternalPort.removeAllElements();
                            for (String pTaskName : patternTasksNotConnected.keySet()) {
                                if (patternTasksNotConnected.get(pTaskName).getExternalPorts().size() > 0) {
                                    tasksOfPatternWithExternalPort.add(pTaskName);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            if (!patternConnection.isNewPort()) {
                TaskPorts pt = portsTaskOfModelLeft.get(modelTaskName);
                TaskPorts ptAll = portsTaskOfModelAll.get(modelTaskName);
                if (ptAll.getWriteChannels().contains(modelTaskPortName)) {
                    pt.getWriteChannels().add(modelTaskPortName);
                } else if (ptAll.getReadChannels().contains(modelTaskPortName)) {
                    pt.getReadChannels().add(modelTaskPortName);
                } else if (ptAll.getSendEvents().contains(modelTaskPortName)) {
                    pt.getSendEvents().add(modelTaskPortName);
                } else if (ptAll.getWaitEvents().contains(modelTaskPortName)) {
                    pt.getWaitEvents().add(modelTaskPortName);
                } 
            }
            connectedPorts.remove(list[i]);
            patternConnectionsToRemove.add(patternConnection);
            jComboBoxPatternsTaskWithExternalPort.setSelectedIndex(-1);
            jCheckBoxConnectToNewPort.setSelected(false);
        }
        patternConnectionList.removeAll(patternConnectionsToRemove);
        jListConnectedPorts.setListData(connectedPorts);
        setButtons();
    }

    private void addClonedTask() {
        String selectedTaskToClone = jComboBoxTaskToClone.getSelectedItem().toString();
        String nameNewTaskToClone = jFieldNewClonedTaskName.getText();
        
        if (!tasksCanBeCloned.contains(nameNewTaskToClone) && !tasksOfModel.contains(nameNewTaskToClone) && !nameNewTaskToClone.contains(" ")) {
            PatternCloneTask patternCloneTask = new PatternCloneTask(nameNewTaskToClone, selectedTaskToClone);
            clonedTasks.add(patternCloneTask.getStringDisplay());
            clonedTasksList.add(patternCloneTask);
            List<String> wcs = new ArrayList<String>(portsTaskOfModelAll.get(selectedTaskToClone).getWriteChannels());
            List<String> rcs = new ArrayList<String>(portsTaskOfModelAll.get(selectedTaskToClone).getReadChannels());
            List<String> ses = new ArrayList<String>(portsTaskOfModelAll.get(selectedTaskToClone).getSendEvents());
            List<String> wes = new ArrayList<String>(portsTaskOfModelAll.get(selectedTaskToClone).getWaitEvents());

            List<String> wcs1 = new ArrayList<String>(portsTaskOfModelAll.get(selectedTaskToClone).getWriteChannels());
            List<String> rcs1 = new ArrayList<String>(portsTaskOfModelAll.get(selectedTaskToClone).getReadChannels());
            List<String> ses1 = new ArrayList<String>(portsTaskOfModelAll.get(selectedTaskToClone).getSendEvents());
            List<String> wes1 = new ArrayList<String>(portsTaskOfModelAll.get(selectedTaskToClone).getWaitEvents());

            TaskPorts portTasksClone = new TaskPorts(wcs, rcs, ses, wes);
            TaskPorts portTasksClone1 = new TaskPorts(wcs1, rcs1, ses1, wes1);
            portsTaskOfModelLeft.put(nameNewTaskToClone, portTasksClone);
            portsTaskOfModelAll.put(nameNewTaskToClone, portTasksClone1);
        }
        tasksOfModel.removeAllElements();
        for (String pTaskName : portsTaskOfModelLeft.keySet()) {
            tasksOfModel.add(pTaskName);
        }
        jListClonedTasks.setListData(clonedTasks);
        setButtons();
    }

    private void removeClonedTask() {
        int[] list = jListClonedTasks.getSelectedIndices();
        List<PatternCloneTask> patternCloneTasksToRemove = new ArrayList<PatternCloneTask>();
        for (int i = 0; i < list.length; i++) {
            PatternCloneTask patternCloneTask = clonedTasksList.get(list[i]);
            patternCloneTasksToRemove.add(patternCloneTask);
            portsTaskOfModelLeft.remove(patternCloneTask.getClonedTask());
            portsTaskOfModelAll.remove(patternCloneTask.getClonedTask());
            List<PatternConnection> patternConnectionsToRemove = new ArrayList<PatternConnection>();
            for (PatternConnection pConnection : patternConnectionList) {
                String patternTaskName = pConnection.getPatternTaskName();
                String patternTaskPortName = pConnection.getPatternChannel();
                String modelTaskName = pConnection.getModelTaskName();

                if (modelTaskName.equals(patternCloneTask.getClonedTask())) {
                    for (String patternTask : patternTasksAll.keySet()) {
                        for (PortTaskJsonFile  portTaskJsonFile : patternTasksAll.get(patternTask).getExternalPorts()) {
                            if (portTaskJsonFile.getName().equals(patternTaskPortName)) {
                                if (patternTasksNotConnected.containsKey(patternTaskName)) {
                                    TaskPattern tp  = patternTasksNotConnected.get(patternTaskName);
                                    tp.getExternalPorts().add(portTaskJsonFile);
                                } else {
                                    List<PortTaskJsonFile> portTaskJsonFilesList = new ArrayList<PortTaskJsonFile>(Arrays.asList(portTaskJsonFile));
                                    TaskPattern tp = new TaskPattern(patternTasksAll.get(patternTask).getAttributes(), patternTasksAll.get(patternTask).getInternalPorts(), portTaskJsonFilesList);
                                    patternTasksNotConnected.put(patternTaskName, tp);
                                    tasksOfPatternWithExternalPort.removeAllElements();
                                    for (String pTaskName : patternTasksNotConnected.keySet()) {
                                        if (patternTasksNotConnected.get(pTaskName).getExternalPorts().size() > 0) {
                                            tasksOfPatternWithExternalPort.add(pTaskName);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    connectedPorts.remove(pConnection.getStringDisplay());
                    patternConnectionsToRemove.add(pConnection);
                }
            }
            clonedTasks.remove(list[i]);
            patternConnectionList.removeAll(patternConnectionsToRemove);
            jListConnectedPorts.setListData(connectedPorts);
        }
        clonedTasksList.removeAll(patternCloneTasksToRemove);

        tasksOfModel.removeAllElements();
        for (String pTaskName : portsTaskOfModelLeft.keySet()) {
            tasksOfModel.add(pTaskName);
        }
        jListClonedTasks.setListData(clonedTasks);
        setButtons();
    }

    private void addConfigPorts() {
        String selectedTaskToConfig = jComboBoxPortsConfig.getSelectedItem().toString();
        String[] taskAndChannelSelected = PatternPortsConfig.seperateTaskAndChannel(selectedTaskToConfig);
        PatternPortsConfig patternPortConfig = new PatternPortsConfig(taskAndChannelSelected[0], taskAndChannelSelected[1]);
        if (jRadioPortConfigRemove.isSelected()) {
            patternPortConfig.setIsChannelToRemove(true);
        } else if (jRadioPortConfigMerge.isSelected()) {
            patternPortConfig.setMergeWith(jComboBoxPortsConfigMerge.getSelectedItem().toString());
        }
        configuredPorts.add(patternPortConfig.getStringDisplay());
        configuredPortsList.add(patternPortConfig);
        portsConfig.remove(selectedTaskToConfig);
        jComboBoxPortsConfig.setSelectedIndex(-1);
        jListConfigPorts.setListData(configuredPorts);
        setButtons();
    }

    private void removeConfigPorts() {
        int[] list = jListConfigPorts.getSelectedIndices();
        List<PatternPortsConfig> patternPortsConfigToRemove = new ArrayList<PatternPortsConfig>();
        for (int i = 0; i < list.length; i++) {
            PatternPortsConfig patternPortConfigToRemove = configuredPortsList.get(list[i]);
            portsConfig.add(patternPortConfigToRemove.getTaskChannelToConfig());
            configuredPorts.remove(list[i]);
            patternPortsConfigToRemove.add(patternPortConfigToRemove);
        }
        configuredPortsList.removeAll(patternPortsConfigToRemove);
        jListConfigPorts.setListData(configuredPorts);
        jComboBoxPortsConfig.setSelectedIndex(-1);
        setButtons();
    }

    private void addMappedTask() {
        //String selectedTaskToMap = jComboBoxTaskToMap.getSelectedItem().toString();
        int selectedTaskToMapIndice = jComboBoxTaskToMap.getSelectedIndex();
        MappingPatternTask mappingPatternTask = tasksToMapList.get(selectedTaskToMapIndice);
        if (jRadioMapTaskInExistingHw.isSelected()) {
            int selectedTaskInSameHwIndice = jComboBoxMapTaskInSameHwAs.getSelectedIndex();
            MappingPatternTask sameHwTask = tasksToMapInSameHwList.get(selectedTaskInSameHwIndice);
            mappingPatternTask.setSameHwAs(sameHwTask.getTaskToMapName(), sameHwTask.getOrigin());
        } else if (jRadioMapTaskInNewHw.isSelected()) {
            mappingPatternTask.setBusNameForNewHw(jComboBoxMapTaskInNewHw.getSelectedItem().toString());
        }
        mappedTasks.add(mappingPatternTask.getStringDisplay());
        mappedTasksList.add(mappingPatternTask);
        tasksToMapInSameHw.add(tasksToMap.remove(selectedTaskToMapIndice));
        tasksToMapInSameHwList.add(tasksToMapList.remove(selectedTaskToMapIndice));
        jComboBoxTaskToMap.setSelectedIndex(-1);
        jListMappedTasks.setListData(mappedTasks);
        setButtons();
    }

    private void removeMappedTask() {
        int[] list = jListMappedTasks.getSelectedIndices();
        List<MappingPatternTask> mappingPatternTasksToRemove = new ArrayList<MappingPatternTask>();
        for (int i = 0; i < list.length; i++) {
            MappingPatternTask mappingPatternTaskSelected = mappedTasksList.get(list[i]);
            tasksToMap.add(mappingPatternTaskSelected.getTaskToMapName());
            tasksToMapList.add(mappingPatternTaskSelected);
            tasksToMapInSameHw.remove(mappingPatternTaskSelected.getSameHwAs());
            tasksToMapInSameHwList.remove(mappingPatternTaskSelected);
            mappingPatternTasksToRemove.add(mappingPatternTaskSelected);
            mappedTasks.remove(list[i]);
        }
        mappedTasksList.removeAll(mappingPatternTasksToRemove);
        jListMappedTasks.setListData(mappedTasks);
        jComboBoxTaskToMap.setSelectedIndex(-1);
        setButtons();
    }

    private void addMappedChannel() {
        //String selectedChannelToMap = jComboBoxChannelToMap.getSelectedItem().toString();
        int selectedChannelToMapIndex = jComboBoxChannelToMap.getSelectedIndex();
        //String[] taskAndChannelSelected = MappingPatternChannel.seperateTaskAndChannel(selectedChannelToMap);
        if (selectedChannelToMapIndex >= 0) {
            MappingPatternChannel mappingPatternChannel = channelsToMapList.get(selectedChannelToMapIndex);
            if (jRadioMapChannelInExistingMem.isSelected()) {
                int selectedChannelSameMemIndex = jComboBoxMapChannelInSameMemAs.getSelectedIndex();
                if (selectedChannelSameMemIndex >= 0) {
                    MappingPatternChannel mappingPatternChannelSameMem = channelsToMapInSameMemList.get(selectedChannelSameMemIndex);
                    mappingPatternChannel.setTaskAndChannelInSameMem(mappingPatternChannelSameMem.getTaskOfChannelToMap(), mappingPatternChannelSameMem.getChannelToMapName(), mappingPatternChannelSameMem.getOrigin());
                }
            } else if (jRadioMapChannelInNewMem.isSelected()) {
                mappingPatternChannel.setBusNameForNewMem(jComboBoxMapChannelInNewMem.getSelectedItem().toString());
            }
            mappedChannels.add(mappingPatternChannel.getStringDisplay());
            channelsToMap.remove(mappingPatternChannel.getTaskChannelToMap());
            channelsToMapList.remove(mappingPatternChannel);
            mappedChannelsList.add(mappingPatternChannel);
            channelsToMapInSameMem.add(mappingPatternChannel.getTaskChannelToMap());
            channelsToMapInSameMemList.add(mappingPatternChannel);
            jComboBoxChannelToMap.setSelectedIndex(-1);
            jListMappedChannels.setListData(mappedChannels);
            setButtons();
        }   
    }

    private void removeMappedChannel() {
        int[] list = jListMappedChannels.getSelectedIndices();
        List<MappingPatternChannel> mappingPatternChannelsToRemove = new ArrayList<MappingPatternChannel>();
        for (int i = 0; i < list.length; i++) {
            MappingPatternChannel mappingPatternChannelSelected = mappedChannelsList.get(list[i]);
            channelsToMap.add(mappingPatternChannelSelected.getTaskChannelToMap());
            channelsToMapList.add(mappingPatternChannelSelected);
            channelsToMapInSameMem.remove(mappingPatternChannelSelected.getTaskChannelToMap());
            channelsToMapInSameMemList.remove(mappingPatternChannelSelected);
            mappingPatternChannelsToRemove.add(mappingPatternChannelSelected);
            mappedChannels.remove(list[i]);
        }
        mappedChannelsList.removeAll(mappingPatternChannelsToRemove);
        jListMappedChannels.setListData(mappedChannels);
        jComboBoxChannelToMap.setSelectedIndex(-1);
        setButtons();
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
                } else if (command.equals("mapTasksManuallyInArchitecture")) {
                    mapTasksManuallyInArchitecture();
                } else if (command.equals("mapChannelsManuallyInArchitecture")) {
                    mapChannelsManuallyInArchitecture();
                } else if (command.equals("updatePatternsAttributes")) {
                    updatePatternsAttributes();
                } else if (command.equals("addClonedTask")) {
                    addClonedTask();
                } else if (command.equals("removeClonedTask")) {
                    removeClonedTask();
                } else if (command.equals("addConnectionBetweenSelectedPorts")) {
                    addConnectionBetweenSelectedPorts();
                } else if (command.equals("removeConnectionBetweenPorts")) {
                    removeConnectionBetweenPorts();
                } else if (command.equals("addConfigPorts")) {
                    addConfigPorts();
                } else if (command.equals("removeConfigPorts")) {
                    removeConfigPorts();
                } else if (command.equals("addMappedTask")) {
                    addMappedTask();
                } else if (command.equals("removeMappedTask")) {
                    removeMappedTask();
                } else if (command.equals("addMappedChannel")) {
                    addMappedChannel();
                } else if (command.equals("removeMappedChannel")) {
                    removeMappedChannel();
                } else if (command.equals("updateTaskAttributeValue")) {
                    updateTaskAttributeValue();
                }
                if (evt.getSource() == jp1) {
                    listPatterns = getFoldersName(pathPatterns);
                    listPatternsJson = getFoldersName(pathPatterns);
                }
                if (evt.getSource() == jComboBoxPatterns) {
                    int selectedPatternIndex = jComboBoxPatterns.getSelectedIndex();
                    patternTasksAll = TaskPattern.parsePatternJsonFile(pathPatterns + listPatterns.get(selectedPatternIndex), listPatterns.get(selectedPatternIndex)+".json");
                    patternTasksNotConnected = TaskPattern.parsePatternJsonFile(pathPatterns + listPatterns.get(selectedPatternIndex), listPatterns.get(selectedPatternIndex)+".json");
                    portsTaskOfModelLeft = TaskPorts.getListPortsTask(mgui.gtm.getTMLMapping().getTMLModeling());
                    //Vector<String> patternTasksVector = new Vector<String>();
                    tasksOfPatternWithExternalPort.removeAllElements();
                    clonedTasks.removeAllElements();
                    clonedTasksList.clear();
                    for (String pTaskName : patternTasksNotConnected.keySet()) {
                        if (patternTasksNotConnected.get(pTaskName).getExternalPorts().size() > 0) {
                            tasksOfPatternWithExternalPort.add(pTaskName);
                        }
                    }
                    
                    //tasksOfPatternWithExternalPort = patternTasksVector;
                    //modelPatternsTaskWithExternalPort.removeAllElements();
                    
                    //modelPatternsTaskWithExternalPort.addAll(tasksOfPatternWithExternalPort);
                    jComboBoxPatternsTaskWithExternalPort.setEnabled(true);
                    jComboBoxPatternsTaskWithExternalPort.setSelectedIndex(-1);
                    buttonCloneTask.setEnabled(true);
                    removeConnectionBetweenPorts.setEnabled(true);
                    connectedPorts.removeAllElements();
                    patternConnectionList.clear();
                    jListConnectedPorts.setListData(connectedPorts);
                    portsConfig.removeAllElements();
                    configuredPorts.removeAllElements();
                    mappedTasks.removeAllElements();
                    mappedTasksList.clear();
                    tasksToMap.removeAllElements();
                    tasksToMapList.clear();
                    channelsToMap.removeAllElements();
                    channelsToMapList.clear();
                    mappedChannels.removeAllElements();
                    mappedChannelsList.clear();
                }
                if (evt.getSource() == jComboBoxPatternsTaskWithExternalPort) {
                    externalPortsOfTaskInPattern.removeAllElements();
                    if (jComboBoxPatternsTaskWithExternalPort.getSelectedIndex() >= 0) {
                        for (PortTaskJsonFile portTask : patternTasksNotConnected.get(jComboBoxPatternsTaskWithExternalPort.getSelectedItem().toString()).getExternalPorts()) {
                            externalPortsOfTaskInPattern.add(portTask.getName());
                        }
                        jComboBoxPatternExternalPortOfATask.setEnabled(true);
                    } else {
                        jComboBoxPatternExternalPortOfATask.setEnabled(false);
                    }
                    //modelPatternExternalPortOfATask.removeAllElements();
                    //modelPatternExternalPortOfATask.addAll(externalPortsOfTaskInPattern);
                    jComboBoxPatternExternalPortOfATask.setSelectedIndex(-1);
                    if (tasksOfPatternWithExternalPort.size() == 0) {
                        removeConfigPorts.setEnabled(true);
                        configuredPorts.removeAllElements();
                        portsConfig.removeAllElements();
                        List<String> clonedTasksAll = new ArrayList<String>();
                        for (PatternCloneTask cloneT : clonedTasksList) {
                            clonedTasksAll.add(cloneT.getClonedTask());
                        }
                        portsTaskConfig = PatternPortsConfig.getPortsToConfig(portsTaskOfModelAll, portsTaskOfModelLeft, clonedTasksAll, mgui.gtm.getTMLMapping().getTMLModeling());
                        for (PatternPortsConfig portTaskConfig : portsTaskConfig) {
                            portTaskConfig.setIsChannelToRemove(true);
                            configuredPorts.add(portTaskConfig.getStringDisplay());
                            configuredPortsList.add(portTaskConfig);
                        }
                        jListConfigPorts.setListData(configuredPorts);
                        jComboBoxPortsConfig.setSelectedIndex(-1);
                        jComboBoxPortsConfig.setEnabled(true);
                    } else {
                        configuredPorts.removeAllElements();
                        jListConfigPorts.setListData(configuredPorts);
                        portsConfig.removeAllElements();
                        jComboBoxPortsConfig.setEnabled(false);
                        removeConfigPorts.setEnabled(false);
                        mappedTasks.removeAllElements();
                        mappedTasksList.clear();
                        tasksToMap.removeAllElements();
                        tasksToMapList.clear();
                        channelsToMap.removeAllElements();
                        channelsToMapList.clear();
                        mappedChannels.removeAllElements();
                        mappedChannelsList.clear();
                        buttonTasksMapInArch.setEnabled(false);
                        buttonChannelsMapInArch.setEnabled(false);
                        buttonUpdatePatternsAttributes.setEnabled(false);
                    }
                }
                if (evt.getSource() == jComboBoxPatternExternalPortOfATask) {
                    if (jComboBoxPatternExternalPortOfATask.getSelectedIndex() >= 0) {
                        jComboBoxModelsTask.setEnabled(true);
                        tasksOfModel.removeAllElements();
                        for (String pTaskName : portsTaskOfModelLeft.keySet()) {
                            tasksOfModel.add(pTaskName);
                        }
                    } else {
                        jComboBoxModelsTask.setEnabled(false);
                    }
                    jComboBoxModelsTask.setSelectedIndex(-1);
                }
                if (evt.getSource() == jComboBoxModelsTask || evt.getSource() == jCheckBoxConnectToNewPort) {
                    if (jComboBoxModelsTask.getSelectedIndex() >= 0) {
                        jComboBoxModelsPortOfTask.setEnabled(true);
                        jCheckBoxConnectToNewPort.setEnabled(true);
                        String selectedModelTask = jComboBoxModelsTask.getSelectedItem().toString();
                        TaskPorts pT = portsTaskOfModelLeft.get(selectedModelTask);
                        String selectedPatternTask = jComboBoxPatternsTaskWithExternalPort.getSelectedItem().toString();
                        int selectedIndexPatternPort = jComboBoxPatternExternalPortOfATask.getSelectedIndex();
                        
                        portsOfTaskInModel.removeAllElements();
                        if (!jCheckBoxConnectToNewPort.isSelected()) { 
                            String modeSelectedPatternPort = patternTasksNotConnected.get(selectedPatternTask).getExternalPorts().get(selectedIndexPatternPort).getMode();
                            String typeSelectedPatternPort = patternTasksNotConnected.get(selectedPatternTask).getExternalPorts().get(selectedIndexPatternPort).getType();
                            if (typeSelectedPatternPort.equals(PatternCreation.CHANNEL)) {
                                if (modeSelectedPatternPort.equals(PatternCreation.MODE_INPUT)) {
                                    for (String st : portsTaskOfModelLeft.get(selectedModelTask).getWriteChannels()) {
                                        portsOfTaskInModel.add(st);
                                    }
                                } else if (modeSelectedPatternPort.equals(PatternCreation.MODE_OUTPUT)) {
                                    for (String st : portsTaskOfModelLeft.get(selectedModelTask).getReadChannels()) {
                                        portsOfTaskInModel.add(st);
                                    }
                                }
                            } else if (typeSelectedPatternPort.equals(PatternCreation.EVENT)) {
                                if (modeSelectedPatternPort.equals(PatternCreation.MODE_INPUT)) {
                                    for (String st : portsTaskOfModelLeft.get(selectedModelTask).getSendEvents()) {
                                        portsOfTaskInModel.add(st);
                                    }
                                } else if (modeSelectedPatternPort.equals(PatternCreation.MODE_OUTPUT)) {
                                    for (String st : portsTaskOfModelLeft.get(selectedModelTask).getWaitEvents()) {
                                        portsOfTaskInModel.add(st);
                                    }
                                }
                            }
                        } else {
                            for (String st : portsTaskOfModelAll.get(selectedModelTask).getWriteChannels()) {
                                portsOfTaskInModel.add(st);
                            }
                            for (String st : portsTaskOfModelAll.get(selectedModelTask).getReadChannels()) {
                                portsOfTaskInModel.add(st);
                            }
                            for (String st : portsTaskOfModelAll.get(selectedModelTask).getSendEvents()) {
                                portsOfTaskInModel.add(st);
                            }
                            for (String st : portsTaskOfModelAll.get(selectedModelTask).getWaitEvents()) {
                                portsOfTaskInModel.add(st);
                            }
                        }
                        for (String st : portsOfTaskInModel) {
                        }
                        
                    } else {
                        jComboBoxModelsPortOfTask.setEnabled(false);
                        jCheckBoxConnectToNewPort.setEnabled(false);
                    }
                    jComboBoxModelsPortOfTask.setSelectedIndex(-1);
                }
                if (evt.getSource() == jComboBoxModelsPortOfTask) {
                    if (jComboBoxModelsPortOfTask.getSelectedIndex() >= 0) {
                        String selectedPatternTask = jComboBoxPatternsTaskWithExternalPort.getSelectedItem().toString();
                        int selectedIndexPatternPort = jComboBoxPatternExternalPortOfATask.getSelectedIndex();
                        String typeSelectedPatternPort = patternTasksNotConnected.get(selectedPatternTask).getExternalPorts().get(selectedIndexPatternPort).getType();
                        if (typeSelectedPatternPort.equals(PatternCreation.CHANNEL)){
                            String confSelectedPatternPort = patternTasksNotConnected.get(selectedPatternTask).getExternalPorts().get(selectedIndexPatternPort).getConfidentiality();
                            String authSelectedPatternPort = patternTasksNotConnected.get(selectedPatternTask).getExternalPorts().get(selectedIndexPatternPort).getAuthenticity();
                            jCheckBoxAddConfidentiality.setVisible(true);
                            jCheckBoxAddConfidentiality.setEnabled(true);
                            if (confSelectedPatternPort.toUpperCase().equals(PatternCreation.WITH_CONFIDENTIALITY.toUpperCase())) {
                                jCheckBoxAddConfidentiality.setSelected(true);
                            }
                            for (int indexModeAuth = 0; indexModeAuth < authenticityModes.size(); indexModeAuth++) {
                                if (authSelectedPatternPort.toUpperCase().equals(authenticityModes.get(indexModeAuth).toUpperCase())) {
                                    jComboBoxAddAuthenticity.setSelectedIndex(indexModeAuth);
                                }
                            }
                            jComboBoxAddAuthenticity.setEnabled(true);
                            jComboBoxAddAuthenticity.setVisible(true);
                            jLabelAddAuthenticity.setVisible(true);
                        }
                        addConnectionBetweenSelectedPorts.setEnabled(true);
                    } else {
                        addConnectionBetweenSelectedPorts.setEnabled(false);
                        jCheckBoxAddConfidentiality.setVisible(false);
                        jCheckBoxAddConfidentiality.setEnabled(false);
                        jCheckBoxAddConfidentiality.setSelected(false);
                        jComboBoxAddAuthenticity.setEnabled(false);
                        jComboBoxAddAuthenticity.setVisible(false);
                        jComboBoxAddAuthenticity.setSelectedIndex(authenticityModes.indexOf(PatternCreation.WITHOUT_AUTHENTICITY));
                        jLabelAddAuthenticity.setVisible(false);
                    }
                }
                if (evt.getSource() == jComboBoxTaskToClone) {
                    if (jComboBoxTaskToClone.getSelectedIndex() >= 0) {
                        jFieldNewClonedTaskName.setEnabled(true);
                        addClonedTask.setEnabled(true);
                    } else {
                        jFieldNewClonedTaskName.setEnabled(false);
                        addClonedTask.setEnabled(false);
                    }
                }

                if (evt.getSource() == jComboBoxPortsConfig) {
                    if (jComboBoxPortsConfig.getSelectedIndex() >= 0) {
                        jRadioPortConfigRemove.setEnabled(true);
                        jRadioPortConfigMerge.setEnabled(true);
                        
                    } else {
                        jRadioPortConfigRemove.setEnabled(false);
                        jRadioPortConfigMerge.setEnabled(false);
                    }
                    if (portsConfig.size() == 0 && tasksOfPatternWithExternalPort.size() == 0) {
                        tasksToMap.removeAllElements();
                        tasksToMapList.clear();
                        tasksToMapInSameHw.removeAllElements();
                        tasksToMapInSameHwList.clear();
                        mappedTasks.removeAllElements();
                        mappedTasksList.clear();
                        channelsToMap.removeAllElements();
                        channelsToMapList.clear();
                        channelsToMapInSameMem.removeAllElements();
                        channelsToMapInSameMemList.clear();
                        mappedChannels.removeAllElements();
                        mappedChannelsList.clear();
                        List<String> clonedTasksName = new ArrayList<String>();
                        for(PatternCloneTask cloneT: clonedTasksList) {
                            tasksToMap.add(cloneT.getClonedTask());
                            MappingPatternTask mapTask = new MappingPatternTask(cloneT.getClonedTask(), MappingPatternTask.ORIGIN_CLONE);
                            tasksToMapList.add(mapTask);
                            clonedTasksName.add(cloneT.getClonedTask());
                        }
                        for(String st : patternTasksAll.keySet()) {
                            tasksToMap.add(st);
                            MappingPatternTask mapTask = new MappingPatternTask(st, MappingPatternTask.ORIGIN_PATTERN);
                            tasksToMapList.add(mapTask);
                        }
                        for(String st : portsTaskOfModelAll.keySet()) {
                            if (!clonedTasksName.contains(st)) {
                                tasksToMapInSameHw.add(st);
                                MappingPatternTask mapTask = new MappingPatternTask(st, MappingPatternTask.ORIGIN_MODEL);
                                tasksToMapInSameHwList.add(mapTask);
                            }
                        }
                        List<MappingPatternChannel> channelsToMapListA = MappingPatternChannel.getChannelsToMap(patternConnectionList, patternTasksAll, clonedTasksList);
                        for(MappingPatternChannel channelToMap : channelsToMapListA) {
                            channelsToMap.add(channelToMap.getTaskChannelToMap());
                            channelsToMapList.add(channelToMap);
                        }
                        
                        for(String st : portsTaskOfModelAll.keySet()) {
                            if (!clonedTasksName.contains(st)) {
                                for (String wc : portsTaskOfModelAll.get(st).getWriteChannels()) {
                                    if (!channelsToMapInSameMem.contains(MappingPatternChannel.getTaskChannel(st, wc))) {
                                        MappingPatternChannel mappingPatternChannel = new MappingPatternChannel(st, wc, MappingPatternChannel.ORIGIN_MODEL);
                                        channelsToMapInSameMem.add(mappingPatternChannel.getTaskChannelToMap());
                                        channelsToMapInSameMemList.add(mappingPatternChannel);
                                    } 
                                }
                            }
                        }
                        
                        buttonTasksMapInArch.setEnabled(true);
                        buttonChannelsMapInArch.setEnabled(true); 
                        buttonUpdatePatternsAttributes.setEnabled(true); 
                    } else {
                        buttonTasksMapInArch.setEnabled(false);
                        buttonChannelsMapInArch.setEnabled(false);
                        buttonUpdatePatternsAttributes.setEnabled(false);
                    }
                    
                    jRadioPortConfigRemove.setSelected(false);
                    jRadioPortConfigMerge.setSelected(false);
                    portsConfigGroup.clearSelection();
                    portsConfigMerge.removeAllElements();
                    jComboBoxPortsConfigMerge.setSelectedIndex(-1);
                }
                if (evt.getSource() == jRadioPortConfigRemove) {
                    jComboBoxPortsConfigMerge.setSelectedIndex(-1);
                    jComboBoxPortsConfigMerge.setEnabled(false);
                }

                if (evt.getSource() == jRadioPortConfigMerge) {
                    if (jRadioPortConfigMerge.isSelected()) {
                        jComboBoxPortsConfigMerge.setEnabled(true);
                        String jCo = jComboBoxPortsConfig.getSelectedItem().toString();
                        String[] taskPortSelected = PatternPortsConfig.seperateTaskAndChannel(jCo);
                        String taskSelected = taskPortSelected[0];
                        List<String> portsToConfOfTaskAll = new ArrayList<String>();
                        for (PatternPortsConfig portTaskConfig : portsTaskConfig) {
                            if (taskSelected.equals(portTaskConfig.getTaskOfChannelToConfig())) {
                                portsToConfOfTaskAll.add(portTaskConfig.getChannelToConfig());
                            }
                        }
                        for (String wc : portsTaskOfModelAll.get(taskSelected).getWriteChannels()) {
                            if (!portsToConfOfTaskAll.contains(wc)) {
                                portsConfigMerge.add(wc);
                            }
                        }
                        for (String rc : portsTaskOfModelAll.get(taskSelected).getReadChannels()) {
                            if (!portsToConfOfTaskAll.contains(rc)) {
                                portsConfigMerge.add(rc);
                            }
                        }
                        for (String se : portsTaskOfModelAll.get(taskSelected).getSendEvents()) {
                            if (!portsToConfOfTaskAll.contains(se)) {
                                portsConfigMerge.add(se);
                            }
                        }
                        for (String we : portsTaskOfModelAll.get(taskSelected).getWaitEvents()) {
                            if (!portsToConfOfTaskAll.contains(we)) {
                                portsConfigMerge.add(we);
                            }
                        } 
                        
                    } else {
                        jComboBoxPortsConfigMerge.setEnabled(false);
                    }
                    jComboBoxPortsConfigMerge.setSelectedIndex(-1);
                }

                if (evt.getSource() == jRadioPortConfigMerge || evt.getSource() == jRadioPortConfigRemove || evt.getSource() == jComboBoxPortsConfigMerge) {
                    if (portsConfig.size()>0 && (jRadioPortConfigRemove.isSelected() || (jRadioPortConfigMerge.isSelected() && jComboBoxPortsConfigMerge.getSelectedIndex() >= 0))) {
                        addConfigPorts.setEnabled(true);
                    } else {
                        addConfigPorts.setEnabled(false);
                    }
                }
                if (evt.getSource() == jComboBoxTaskToMap) {
                    if (jComboBoxTaskToMap.getSelectedIndex() >= 0) {
                        jRadioMapTaskInNewHw.setEnabled(true);
                        jRadioMapTaskInExistingHw.setEnabled(true);
                    } else {
                        jRadioMapTaskInNewHw.setEnabled(false);
                        jRadioMapTaskInExistingHw.setEnabled(false);
                    }
                    mapTaskGroup.clearSelection();
                    jComboBoxMapTaskInSameHwAs.setEnabled(false);
                    jComboBoxMapTaskInNewHw.setEnabled(false);
                    jComboBoxMapTaskInSameHwAs.setSelectedIndex(-1);
                    jComboBoxMapTaskInNewHw.setSelectedIndex(-1);
                }
                if (evt.getSource() == jRadioMapTaskInNewHw) {
                    if (jRadioMapTaskInNewHw.isSelected()) {
                        jComboBoxMapTaskInNewHw.setEnabled(true);
                        jComboBoxMapTaskInSameHwAs.setEnabled(false);
                        jComboBoxMapTaskInSameHwAs.setSelectedIndex(-1);
                    } else {
                        jComboBoxMapTaskInNewHw.setEnabled(false);
                    }
                }
                if (evt.getSource() == jRadioMapTaskInExistingHw) {
                    if (jRadioMapTaskInExistingHw.isSelected()) {
                        jComboBoxMapTaskInSameHwAs.setEnabled(true);
                        jComboBoxMapTaskInNewHw.setEnabled(false);
                        jComboBoxMapTaskInNewHw.setSelectedIndex(-1);
                    } else {
                        jComboBoxMapTaskInSameHwAs.setEnabled(false);
                    }
                }
                if (evt.getSource() == jComboBoxMapTaskInSameHwAs || evt.getSource() == jComboBoxMapTaskInNewHw) {
                    if (tasksToMap.size() > 0 && ((jRadioMapTaskInExistingHw.isSelected() &&  jComboBoxMapTaskInSameHwAs.getSelectedIndex() >= 0) || (jRadioMapTaskInNewHw.isSelected() && jComboBoxMapTaskInNewHw.getSelectedIndex() >= 0))) {
                        addMappedTask.setEnabled(true);
                    } else {
                        addMappedTask.setEnabled(false);
                    }
                }
                if (evt.getSource() == jComboBoxChannelToMap) {
                    if (jComboBoxChannelToMap.getSelectedIndex() >= 0) {
                        jRadioMapChannelInNewMem.setEnabled(true);
                        jRadioMapChannelInExistingMem.setEnabled(true);
                    } else {
                        jRadioMapChannelInNewMem.setEnabled(false);
                        jRadioMapChannelInExistingMem.setEnabled(false);
                    }
                    mapChannelGroup.clearSelection();
                    jComboBoxMapChannelInSameMemAs.setEnabled(false);
                    jComboBoxMapChannelInNewMem.setEnabled(false);
                    jComboBoxMapChannelInSameMemAs.setSelectedIndex(-1);
                    jComboBoxMapChannelInNewMem.setSelectedIndex(-1);
                }
                if (evt.getSource() == jRadioMapChannelInNewMem) {
                    if (jRadioMapChannelInNewMem.isSelected()) {
                        jComboBoxMapChannelInNewMem.setEnabled(true);
                        jComboBoxMapChannelInSameMemAs.setEnabled(false);
                        jComboBoxMapChannelInSameMemAs.setSelectedIndex(-1);
                    } else {
                        jComboBoxMapChannelInNewMem.setEnabled(false);
                    }
                }
                if (evt.getSource() == jRadioMapChannelInExistingMem) {
                    if (jRadioMapChannelInExistingMem.isSelected()) {
                        jComboBoxMapChannelInSameMemAs.setEnabled(true);
                        jComboBoxMapChannelInNewMem.setEnabled(false);
                        jComboBoxMapChannelInNewMem.setSelectedIndex(-1);
                    } else {
                        jComboBoxMapChannelInSameMemAs.setEnabled(false);
                    }
                }
                if (evt.getSource() == jComboBoxMapChannelInSameMemAs || evt.getSource() == jComboBoxMapChannelInNewMem) {
                    if (channelsToMap.size() > 0 && ((jRadioMapChannelInExistingMem.isSelected() &&  jComboBoxMapChannelInSameMemAs.getSelectedIndex() >= 0) || (jRadioMapChannelInNewMem.isSelected() && jComboBoxMapChannelInNewMem.getSelectedIndex() >= 0))) {
                        addMappedChannel.setEnabled(true);
                    } else {
                        addMappedChannel.setEnabled(false);
                    }
                }

                if (evt.getSource() == jComboBoxTasksToUpdateAttributes) {
                    attributesOfTaskToUpdate.removeAllElements();
                    if (jComboBoxTasksToUpdateAttributes.getSelectedIndex() >= 0) {
                        jComboBoxAttributesOfTaskToUpdate.setEnabled(true);
                        String selectedTaskToUpdateAttributes = jComboBoxTasksToUpdateAttributes.getSelectedItem().toString();
                        for (AttributeTaskJsonFile attributeTaskJsonFile : patternTasksAll.get(selectedTaskToUpdateAttributes).getAttributes()) {
                            attributesOfTaskToUpdate.add(attributeTaskJsonFile.getName());
                        }
                    } else {
                        jComboBoxAttributesOfTaskToUpdate.setEnabled(false);
                    }
                    jComboBoxAttributesOfTaskToUpdate.setSelectedIndex(-1);
                    jFieldNewTaskAttibuteValue.setText("");
                }

                if (evt.getSource() == jComboBoxAttributesOfTaskToUpdate) {
                    if (jComboBoxAttributesOfTaskToUpdate.getSelectedIndex() >= 0) {
                        jFieldNewTaskAttibuteValue.setEnabled(true);
                        buttonUpdateTaskAttributeValue.setEnabled(true);
                        String selectedTaskToUpdateAttributes = jComboBoxTasksToUpdateAttributes.getSelectedItem().toString();
                        String selectedAttributeToUpdate = jComboBoxAttributesOfTaskToUpdate.getSelectedItem().toString();
                        for (AttributeTaskJsonFile attributeTaskJsonFile : patternTasksAll.get(selectedTaskToUpdateAttributes).getAttributes()) {
                            if (attributeTaskJsonFile.getName().equals(selectedAttributeToUpdate)) {
                                jFieldNewTaskAttibuteValue.setText(attributeTaskJsonFile.getValue());
                            }
                        }
                    } else {
                        jFieldNewTaskAttibuteValue.setEnabled(false);
                        jFieldNewTaskAttibuteValue.setText("");
                        buttonUpdateTaskAttributeValue.setEnabled(false);
                    }
                }

                if (evt.getSource() == jComboBoxPatternsJson) {
                    if (jComboBoxPatternsJson.getSelectedIndex() >= 0) {
                        jFieldPatternJsonPath.setEnabled(true);
                        jFieldPatternJsonPath.setText(pathPatterns + listPatternsJson.get(jComboBoxPatternsJson.getSelectedIndex()) + "/"+listPatternsJson.get(jComboBoxPatternsJson.getSelectedIndex())+"-config.json");
                    } else {
                        jFieldPatternJsonPath.setEnabled(false);
                        jFieldPatternJsonPath.setText("");
                    }
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
                mgui.gtm.createPattern(selectedTasksAsPattern, newPatternName, pathPatterns);
                JLabel label = new JLabel("Pattern Creation Completed");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
            }
            mode = NOT_STARTED;
        } else  if (jp1.getSelectedIndex() == 1) {
            newPatternName = jFieldNewPatternName.getText();
            jta.removeAll();
            boolean findErr = false;
            if (tasksOfPatternWithExternalPort.size() > 0) {
                JLabel label = new JLabel("ERROR: Some connections between the pattern and the model are missing");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (portsConfig.size() > 0) {
                JLabel label = new JLabel("ERROR: There are some port's configurations left to do");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (!findErr) {
                int selectedPatternIndex = jComboBoxPatterns.getSelectedIndex();
                String selectedPatternPath = pathPatterns+listPatterns.get(selectedPatternIndex)+"/";
                String selectedPatternName = listPatterns.get(selectedPatternIndex);
                PatternConfiguration patternConfiguration = new PatternConfiguration(patternConnectionList, clonedTasksList, configuredPortsList, mappedTasksList, mappedChannelsList, updatedPatternAttributes, channelsWithSecurity);
                patternConfiguration.loadChannelsWithSecurity(patternTasksAll);
                GTURTLEModeling.createJsonPatternConfigFile(selectedPatternPath, selectedPatternName, patternConfiguration);
                mgui.gtm.integratePattern(mgui, selectedPatternPath, selectedPatternName);
                JLabel label = new JLabel("Pattern Configuration Completed");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
            }
            mode = NOT_STARTED;
        } else  if (jp1.getSelectedIndex() == 2) {
            jta.removeAll();
            boolean findErr = false;
            String textJFieldPathJson = jFieldPatternJsonPath.getText();
            File f = new File(textJFieldPathJson);
            
            if (jComboBoxPatternsJson.getSelectedIndex() == -1) {
                JLabel label = new JLabel("ERROR: Select a pattern");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (!f.isFile() || !f.exists()) { 
                JLabel label = new JLabel("ERROR: Json file doesn't exist");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }

            if (!findErr) {
                int selectedPatternIndex = jComboBoxPatternsJson.getSelectedIndex();
                String selectedPatternPath = pathPatterns+listPatternsJson.get(selectedPatternIndex) + "/";
                String selectedPatternName = listPatternsJson.get(selectedPatternIndex);
                mgui.gtm.integratePattern(mgui, selectedPatternPath, selectedPatternName, textJFieldPathJson);
                JLabel label = new JLabel("Pattern Integration Completed");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
            }
            mode = NOT_STARTED;
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

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

}
