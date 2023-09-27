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
import avatartranslator.AvatarPragmaAuthenticity;
import avatartranslator.AvatarPragmaReachability;
import avatartranslator.AvatarPragmaSecret;
import common.SpecConfigTTool;
import launcher.LauncherException;
import launcher.RshClient;
import launcher.RshClientReader;
import myutil.FileException;
import myutil.GraphicLib;
import myutil.MasterProcessInterface;
import myutil.TraceManager;
import proverifspec.ProVerifOutputAnalyzer;
import proverifspec.ProVerifOutputListener;
import proverifspec.ProVerifQueryAuthResult;
import proverifspec.ProVerifQueryResult;
import proverifspec.ProVerifResultTraceStep;
import tmltranslator.TMLMapping;
import ui.*;
import ui.interactivesimulation.JFrameSimulationSDPanel;
import ui.util.IconManager;

/**
 * Class JDialogProverifVerification
 * Dialog for managing the generation of ProVerif code and execution of
 * ProVerif
 * Creation: 19/02/2017
 *
 * @author Ludovic APVRILLE
 * @version 1.0 19/02/2017
 */

public class JDialogProverifVerification extends JDialog implements ActionListener, ListSelectionListener, MouseListener, Runnable,
        MasterProcessInterface, ProVerifOutputListener {

    private static final Insets insets = new Insets(0, 0, 0, 0);
    private static final Insets WEST_INSETS = new Insets(0, 0, 0, 0);

    public final static int REACHABILITY_ALL = 1;
    public final static int REACHABILITY_SELECTED = 2;
    public final static int REACHABILITY_NONE = 3;

    private static String CODE_PATH = null;
    private static String EXECUTE_PATH = null;
    private static int REACHABILITY_OPTION = REACHABILITY_ALL;
    private static boolean MSG_DUPLICATION = true;
    private static boolean PI_CALCULUS = true;
    public static int LOOP_ITERATION = 1;

    public static String ADD_CONFIDENTIALITY = "Conf";
    public static String ADD_WEAK_AUTHENTICITY = "Integrity";
    public static String ADD_STRONG_AUTHENTICITY = "Strong Authenticity";

    private static boolean DRAW_AVATAR = false;

    protected MainGUI mgui;
    private AvatarDesignPanel adp;

    private static String pathCode;
    private static String pathExecute;


    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;



    TURTLEPanel currPanel;

    int mode;

    //Security
    Map<String, HashSet<String>> cpuTaskMap = new HashMap<String, HashSet<String>>();
    Map<String, String> taskCpuMap = new HashMap<String, String>();
    Vector<String> selectedTasks = new Vector<String>();
    Vector<String> ignoredTasks = new Vector<String>();
    JList<String> listSelected;
    JList<String> listIgnored;

    Vector<String> selectedChannelsToAddSec = new Vector<String>();
    Vector<String> ignoredChannelsToAddSec = new Vector<String>();
    JList<String> listSelectedChannelsToAddSec;
    JList<String> listIgnoredChannelsToAddSec;

    //Patterns
    Vector<String> listPatterns = new Vector<String>(Arrays.asList("TMR"));
    
    JComboBox<String> jComboBoxPatterns;
    JComboBox<String> listCompSelectReceiver;
    JComboBox<String> listCompSelectMainSensor, listChannelsMainSensor;
    JComboBox<String> listCompSelectSecondSensor, listChannelsSecondSensor;
    JComboBox<String> listCompSelectThirdSensor, listChannelsThirdSensor;
    Vector<String> allTasksFullName = new Vector<String>();
    Vector<String> allTasksAsReceiver = new Vector<String>();
    Vector<String> allTasksAsMainSensor = new Vector<String>();
    Vector<String> allTasksAsSecondSensor = new Vector<String>();
    Vector<String> allTasksAsThirdSensor = new Vector<String>();
    Vector<String> allChannelsMainSensor = new Vector<String>();
    Vector<String> allChannelsSecondSensor = new Vector<String>();
    Vector<String> allChannelsThirdSensor = new Vector<String>();
    JButton addChannelFromMainSensor, removeChannelFromMainSensor, moveUpChannelFromMainSensor, moveDownChannelFromMainSensor;
    JButton addChannelFromSecondSensor, removeChannelFromSecondSensor, moveUpChannelFromSecondSensor, moveDownChannelFromSecondSensor;
    JButton addChannelFromThirdSensor, removeChannelFromThirdSensor, moveUpChannelFromThirdSensor, moveDownChannelFromThirdSensor;
    JList<String> listSelectedChannelsMainSensor;
    JList<String> listSelectedChannelsSecondSensor;
    JList<String> listSelectedChannelsThirdSensor;
    Vector<String> selectedChannelsMainSensor = new Vector<String>();
    Vector<String> selectedChannelsSecondSensor = new Vector<String>();
    Vector<String> selectedChannelsThirdSensor = new Vector<String>();
    Map<String, List<String>> selectedSensors = new LinkedHashMap<String, List<String>>();

    protected JCheckBox selectTwoSensors;
    protected JTextField interpretersCompTime, voterCompTime, voterTimeOut;
    protected static String interpretersCompTimeDefault = "10";
    protected static String voterCompTimeDefault = "10";
    protected static String voterTimeOutDefault = "1000";

    protected static String encCC = "100";
    protected static String decCC = "100";
    protected static String secOv = "100";

    private ProVerifOutputAnalyzer pvoa;

    //components
    protected JPanel jta;
    private JButton startButton;
    protected JButton stop;
    protected JButton close;
    private JPopupMenu popup;
    protected JPanel listPanel;


    // Security generation buttons
    ButtonGroup secGroup;

    protected JCheckBox drawAvatarDesign, autoConf, autoWeakAuth, autoStrongAuth, custom, addHSM;
	protected JRadioButton autoSec, autoMapKeys;
    protected JTextField encTime, decTime, secOverhead;
    protected JComboBox<String> addtoCPU;
	protected JButton allValidated, addOneValidated, allIgnored, addOneIgnored;
	protected JButton addAllChannelsToAddSec, addOneChannelToAddSec, removeAllChannelsToAddSec, removeOneChannelToAddSec;

    protected JCheckBox removeForkAndJoin;

    public TGHelpButton myButton;
    public static String helpString = "securityverification.html";

    private Map<JCheckBox, List<JCheckBox>> cpuTaskObjs = new HashMap<JCheckBox, List<JCheckBox>>();

    private class MyMenuItem extends JMenuItem {
        /**
		 * 
		 */
		private static final long serialVersionUID = -344414299222823444L;
		
		AvatarPragma pragma;
        ProVerifQueryResult result;

        MyMenuItem(String text) {
            super(text);
        }
    }

    private MyMenuItem menuItem;

    private JTextField code1, exe2, loopLimit;
    protected JScrollPane jsp;
    private JCheckBox typedLanguage;
    private JRadioButton stateReachabilityAll;
    private JRadioButton stateReachabilitySelected;
    private JRadioButton privateChannelDup;


    private Map<AvatarPragma, ProVerifQueryResult> results;
    private boolean limit;
    private boolean go = false;

    private String hostProVerif;

    protected RshClient rshc;

    protected JTabbedPane jp1;

    private class ProVerifVerificationException extends Exception {
        /**
		 * 
		 */
		private static final long serialVersionUID = -2359743729229833671L;
		
		// Issue #131 Already defined in super class
		//private String message;

        public ProVerifVerificationException(String message) {
            super( message );
        }
//
//        public String getMessage() {
//            return this.message;
//        }
    }

    /*
     * Creates new form
     */
    public JDialogProverifVerification(Frame f, MainGUI _mgui, String title, String _hostProVerif, String _pathCode, String _pathExecute,
                                       AvatarDesignPanel adp, boolean lim, HashMap<String, HashSet<String>> cpuTasks) {
        super(f, title, Dialog.ModalityType.DOCUMENT_MODAL);

        mgui = _mgui;
        this.adp = adp;
        this.pvoa = null;
        this.limit = lim;

        pathCode = ((CODE_PATH == null) ? _pathCode : CODE_PATH);
        pathExecute = ((EXECUTE_PATH == null) ? _pathExecute : EXECUTE_PATH);


        hostProVerif = _hostProVerif;
        this.cpuTaskMap = cpuTasks;
        for (String cpu : cpuTasks.keySet()) {
            for (String task : cpuTasks.get(cpu)) {
                ignoredTasks.add(task);
                taskCpuMap.put(task, cpu);
            }
        }

        currPanel = mgui.getCurrentTURTLEPanel();
        
        if (currPanel instanceof TMLArchiPanel) {
            for (int i=0; i < mgui.gtm.getTMLMapping().getTMLModeling().getChannels().size(); i++) {
                String channelFullName = mgui.gtm.getTMLMapping().getTMLModeling().getChannels().get(i).getName();
                String channelShortName = channelFullName.split("__")[channelFullName.split("__").length - 1];
                ignoredChannelsToAddSec.add(channelShortName);
            }

            for (int i=0; i<mgui.gtm.getTMLMapping().getMappedTasks().size(); i++) {
                String taskFullName = mgui.gtm.getTMLMapping().getMappedTasks().get(i).getName();
                String taskShortName = taskFullName.split("__")[taskFullName.split("__").length - 1];
                allTasksFullName.add(taskFullName);
                allTasksAsReceiver.add(taskShortName);
                allTasksAsMainSensor.add(taskShortName);
                allTasksAsSecondSensor.add(taskShortName);
                allTasksAsThirdSensor.add(taskShortName);
            }
        }

        initComponents();
        myInitComponents();
        pack();

        // getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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

    protected void panelAutoPattern() {
        JPanel jp03 = new JPanel();
        GridBagLayout gridbag03 = new GridBagLayout();
        GridBagConstraints c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        jp03.setBorder(new javax.swing.border.TitledBorder("Auto add Pattern"));

        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.fill = GridBagConstraints.HORIZONTAL;
        c03.gridheight = 1;
        c03.gridwidth = 2;
        jp03.add(new JLabel("Select Pattern:"), c03);
        c03.gridwidth = GridBagConstraints.REMAINDER;
        jComboBoxPatterns = new JComboBox<String>(listPatterns);
        jp03.add(jComboBoxPatterns, c03);

        jp03.add(new JLabel("Select receiver Component:"), c03);
        c03.gridwidth = GridBagConstraints.REMAINDER;
        listCompSelectReceiver = new JComboBox<String>(allTasksAsReceiver);
        listCompSelectReceiver.setSelectedIndex(-1);
        listCompSelectReceiver.addActionListener(this);
        
        jp03.add(listCompSelectReceiver, c03);
        c03.gridwidth = GridBagConstraints.REMAINDER;

        jp03.add(new JLabel("Select a sensor and its ports for which a TMR will be integrated:"), c03);
        c03.gridwidth = 2;
        listCompSelectMainSensor = new JComboBox<String>(allTasksAsMainSensor);
        listCompSelectMainSensor.setSelectedIndex(-1);
        listCompSelectMainSensor.addActionListener(this);
        jp03.add(listCompSelectMainSensor, c03);
        c03.gridwidth = GridBagConstraints.REMAINDER;

        listChannelsMainSensor = new JComboBox<String>(allChannelsMainSensor);
        listChannelsMainSensor.setSelectedIndex(-1);
        listChannelsMainSensor.addActionListener(this);
        jp03.add(listChannelsMainSensor, c03);
        c03.gridwidth = GridBagConstraints.REMAINDER;

        listSelectedChannelsMainSensor = new JList<String>(selectedChannelsMainSensor);
		JPanel panelSelectChannelsMainSensor = new JPanel();
		panelSelectChannelsMainSensor.setPreferredSize(new Dimension(200, 100));
        listSelectedChannelsMainSensor.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        listSelectedChannelsMainSensor.addListSelectionListener(this);
        JScrollPane scrollPaneSelectedChannelsMainSensor = new JScrollPane(listSelectedChannelsMainSensor);
        scrollPaneSelectedChannelsMainSensor.setPreferredSize(new Dimension(200, 100));
        panelSelectChannelsMainSensor.add(scrollPaneSelectedChannelsMainSensor, BorderLayout.WEST);
        
        JPanel selectChannelsMainSensorPanel = new JPanel();
        GridBagConstraints c13MainSensor = new GridBagConstraints();
        c13MainSensor.gridwidth = GridBagConstraints.REMAINDER;
        c13MainSensor.gridheight = 1;
        addChannelFromMainSensor = new JButton("+");
        addChannelFromMainSensor.setPreferredSize(new Dimension(50, 25));
        addChannelFromMainSensor.addActionListener(this);
        addChannelFromMainSensor.setActionCommand("addChannelMainSensor");
        selectChannelsMainSensorPanel.add(addChannelFromMainSensor, c13MainSensor);

        removeChannelFromMainSensor = new JButton("-");
        removeChannelFromMainSensor.setPreferredSize(new Dimension(50, 25));
        removeChannelFromMainSensor.addActionListener(this);
        removeChannelFromMainSensor.setActionCommand("removeChannelMainSensor");
        selectChannelsMainSensorPanel.add(removeChannelFromMainSensor, c13MainSensor);

        moveUpChannelFromMainSensor = new JButton(IconManager.imgic78);
        moveUpChannelFromMainSensor.setPreferredSize(new Dimension(50, 25));
        moveUpChannelFromMainSensor.addActionListener(this);
        moveUpChannelFromMainSensor.setActionCommand("moveUpChannelMainSensor");
        selectChannelsMainSensorPanel.add(moveUpChannelFromMainSensor, c13MainSensor);

        moveDownChannelFromMainSensor = new JButton(IconManager.imgic79);
        moveDownChannelFromMainSensor.setPreferredSize(new Dimension(50, 25));
        moveDownChannelFromMainSensor.addActionListener(this);
        moveDownChannelFromMainSensor.setActionCommand("moveDownChannelMainSensor");
        selectChannelsMainSensorPanel.add(moveDownChannelFromMainSensor, c13MainSensor);

        panelSelectChannelsMainSensor.add(selectChannelsMainSensorPanel, c03);
        jp03.add(panelSelectChannelsMainSensor, c03);


		GridBagConstraints c04 = new GridBagConstraints();
        c04.weighty = 1.0;
        c04.weightx = 1.0;
		c04.gridwidth = GridBagConstraints.REMAINDER;
        c04.gridheight = 1;
		c04.fill= GridBagConstraints.HORIZONTAL;
        jp03.add(new JLabel(""), c04);

        selectTwoSensors = new JCheckBox("Select two other sensors");
        jp03.add(selectTwoSensors, c03);
        selectTwoSensors.addActionListener(this);

        c03.gridwidth = 2;
        listCompSelectSecondSensor = new JComboBox<String>(allTasksAsSecondSensor);
        listCompSelectSecondSensor.setSelectedIndex(-1);
        listCompSelectSecondSensor.setEnabled(false);
        listCompSelectSecondSensor.addActionListener(this);
        jp03.add(listCompSelectSecondSensor, c03);
        c03.gridwidth = GridBagConstraints.REMAINDER;

        listChannelsSecondSensor = new JComboBox<String>(allChannelsSecondSensor);
        listChannelsSecondSensor.setSelectedIndex(-1);
        listChannelsSecondSensor.setEnabled(false);
        listChannelsSecondSensor.addActionListener(this);
        jp03.add(listChannelsSecondSensor, c03);
        c03.gridwidth = GridBagConstraints.REMAINDER;

        listSelectedChannelsSecondSensor = new JList<String>(selectedChannelsSecondSensor);
		JPanel panelSelectChannelsSecondSensor = new JPanel();
		panelSelectChannelsSecondSensor.setPreferredSize(new Dimension(200, 100));
        listSelectedChannelsSecondSensor.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        listSelectedChannelsSecondSensor.addListSelectionListener(this);
        JScrollPane scrollPaneSelectedChannelsSecondSensor = new JScrollPane(listSelectedChannelsSecondSensor);
        scrollPaneSelectedChannelsSecondSensor.setPreferredSize(new Dimension(200, 100));
        panelSelectChannelsSecondSensor.add(scrollPaneSelectedChannelsSecondSensor, BorderLayout.WEST);
        
        JPanel selectChannelsSecondSensorPanel = new JPanel();
        GridBagConstraints c13SecondSensor = new GridBagConstraints();
        c13SecondSensor.gridwidth = GridBagConstraints.REMAINDER;
        c13SecondSensor.gridheight = 1;
        addChannelFromSecondSensor = new JButton("+");
        addChannelFromSecondSensor.setEnabled(false);
        addChannelFromSecondSensor.setPreferredSize(new Dimension(50, 25));
        addChannelFromSecondSensor.addActionListener(this);
        addChannelFromSecondSensor.setActionCommand("addChannelSecondSensor");
        selectChannelsSecondSensorPanel.add(addChannelFromSecondSensor, c13SecondSensor);

        removeChannelFromSecondSensor = new JButton("-");
        removeChannelFromSecondSensor.setEnabled(false);
        removeChannelFromSecondSensor.setPreferredSize(new Dimension(50, 25));
        removeChannelFromSecondSensor.addActionListener(this);
        removeChannelFromSecondSensor.setActionCommand("removeChannelSecondSensor");
        selectChannelsSecondSensorPanel.add(removeChannelFromSecondSensor, c13SecondSensor);

        moveUpChannelFromSecondSensor = new JButton(IconManager.imgic78);
        moveUpChannelFromSecondSensor.setEnabled(false);
        moveUpChannelFromSecondSensor.setPreferredSize(new Dimension(50, 25));
        moveUpChannelFromSecondSensor.addActionListener(this);
        moveUpChannelFromSecondSensor.setActionCommand("moveUpChannelSecondSensor");
        selectChannelsSecondSensorPanel.add(moveUpChannelFromSecondSensor, c13SecondSensor);

        moveDownChannelFromSecondSensor = new JButton(IconManager.imgic79);
        moveDownChannelFromSecondSensor.setEnabled(false);
        moveDownChannelFromSecondSensor.setPreferredSize(new Dimension(50, 25));
        moveDownChannelFromSecondSensor.addActionListener(this);
        moveDownChannelFromSecondSensor.setActionCommand("moveDownChannelSecondSensor");
        selectChannelsSecondSensorPanel.add(moveDownChannelFromSecondSensor, c13SecondSensor);

        panelSelectChannelsSecondSensor.add(selectChannelsSecondSensorPanel, c04);
        jp03.add(panelSelectChannelsSecondSensor, c04);

        c03.gridwidth = 2;
        listCompSelectThirdSensor = new JComboBox<String>(allTasksAsThirdSensor);
        listCompSelectThirdSensor.setSelectedIndex(-1);
        listCompSelectThirdSensor.setEnabled(false);
        listCompSelectThirdSensor.addActionListener(this);
        jp03.add(listCompSelectThirdSensor, c03);
        c03.gridwidth = GridBagConstraints.REMAINDER;

        listChannelsThirdSensor = new JComboBox<String>(allChannelsThirdSensor);
        listChannelsThirdSensor.setSelectedIndex(-1);
        listChannelsThirdSensor.setEnabled(false);
        listChannelsThirdSensor.addActionListener(this);
        jp03.add(listChannelsThirdSensor, c03);
        c03.gridwidth = GridBagConstraints.REMAINDER;

        listSelectedChannelsThirdSensor = new JList<String>(selectedChannelsThirdSensor);
		JPanel panelSelectChannelsThirdSensor = new JPanel();
		panelSelectChannelsThirdSensor.setPreferredSize(new Dimension(200, 100));
        listSelectedChannelsThirdSensor.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        listSelectedChannelsThirdSensor.addListSelectionListener(this);
        JScrollPane scrollPaneSelectedChannelsThirdSensor = new JScrollPane(listSelectedChannelsThirdSensor);
        scrollPaneSelectedChannelsThirdSensor.setPreferredSize(new Dimension(200, 100));
        panelSelectChannelsThirdSensor.add(scrollPaneSelectedChannelsThirdSensor, BorderLayout.WEST);
        
        JPanel selectChannelsThirdSensorPanel = new JPanel();
        GridBagConstraints c13ThirdSensor = new GridBagConstraints();
        c13ThirdSensor.gridwidth = GridBagConstraints.REMAINDER;
        c13ThirdSensor.gridheight = 1;
        addChannelFromThirdSensor = new JButton("+");
        addChannelFromThirdSensor.setEnabled(false);
        addChannelFromThirdSensor.setPreferredSize(new Dimension(50, 25));
        addChannelFromThirdSensor.addActionListener(this);
        addChannelFromThirdSensor.setActionCommand("addChannelThirdSensor");
        selectChannelsThirdSensorPanel.add(addChannelFromThirdSensor, c13ThirdSensor);

        removeChannelFromThirdSensor = new JButton("-");
        removeChannelFromThirdSensor.setEnabled(false);
        removeChannelFromThirdSensor.setPreferredSize(new Dimension(50, 25));
        removeChannelFromThirdSensor.addActionListener(this);
        removeChannelFromThirdSensor.setActionCommand("removeChannelThirdSensor");
        selectChannelsThirdSensorPanel.add(removeChannelFromThirdSensor, c13ThirdSensor);

        moveUpChannelFromThirdSensor = new JButton(IconManager.imgic78);
        moveUpChannelFromThirdSensor.setEnabled(false);
        moveUpChannelFromThirdSensor.setPreferredSize(new Dimension(50, 25));
        moveUpChannelFromThirdSensor.addActionListener(this);
        moveUpChannelFromThirdSensor.setActionCommand("moveUpChannelThirdSensor");
        selectChannelsThirdSensorPanel.add(moveUpChannelFromThirdSensor, c13ThirdSensor);

        moveDownChannelFromThirdSensor = new JButton(IconManager.imgic79);
        moveDownChannelFromThirdSensor.setEnabled(false);
        moveDownChannelFromThirdSensor.setPreferredSize(new Dimension(50, 25));
        moveDownChannelFromThirdSensor.addActionListener(this);
        moveDownChannelFromThirdSensor.setActionCommand("moveDownChannelThirdSensor");
        selectChannelsThirdSensorPanel.add(moveDownChannelFromThirdSensor, c13ThirdSensor);

        panelSelectChannelsThirdSensor.add(selectChannelsThirdSensorPanel, c04);
        jp03.add(panelSelectChannelsThirdSensor, c04);

        jp03.add(new JLabel("Interpreters computation time:"), c04);
        c04.gridwidth = GridBagConstraints.REMAINDER;
        interpretersCompTime = new JTextField(interpretersCompTimeDefault);
        jp03.add(interpretersCompTime, c04);
        jp03.add(new JLabel("Voter computation time:"), c04);
        c04.gridwidth = GridBagConstraints.REMAINDER;
        voterCompTime = new JTextField(voterCompTimeDefault);
        jp03.add(voterCompTime, c04);
        jp03.add(new JLabel("Timeout voter:"), c04);
        c04.gridwidth = GridBagConstraints.REMAINDER;
        voterTimeOut = new JTextField(voterTimeOutDefault);
        jp03.add(voterTimeOut, c04);
        if (currPanel instanceof TMLArchiPanel) {
            //Can only secure a mapping
            jp1.add("Auto add Pattern", jp03);
        }
    }

    private void addChannelMainSensor() {
        if (listChannelsMainSensor.getSelectedIndex() >= 0) {
            String selectedChannel = listChannelsMainSensor.getSelectedItem().toString();
            if (selectedChannel != "" && !selectedChannelsMainSensor.contains(selectedChannel)) {
                selectedChannelsMainSensor.addElement(selectedChannel);
                listSelectedChannelsMainSensor.setListData(selectedChannelsMainSensor);
            }
        }
    }

    private void removeChannelMainSensor() {
        int selectedChannelIndex = listSelectedChannelsMainSensor.getSelectedIndex();
        if (selectedChannelIndex >= 0) {
            selectedChannelsMainSensor.remove(selectedChannelIndex);
            listSelectedChannelsMainSensor.setListData(selectedChannelsMainSensor);
        }
    }

    private void moveUpChannelMainSensor() {
        int selectedChannelIndex = listSelectedChannelsMainSensor.getSelectedIndex();
        String selectedChannel = listSelectedChannelsMainSensor.getSelectedValue();
        if (selectedChannelIndex > 0) {
            selectedChannelsMainSensor.remove(selectedChannelIndex);
            selectedChannelsMainSensor.add(selectedChannelIndex-1, selectedChannel);
            listSelectedChannelsMainSensor.setListData(selectedChannelsMainSensor);
        }
    }

    private void moveDownChannelMainSensor() {
        int selectedChannelIndex = listSelectedChannelsMainSensor.getSelectedIndex();
        String selectedChannel = listSelectedChannelsMainSensor.getSelectedValue();
        if (selectedChannelIndex < selectedChannelsMainSensor.size()-1 && selectedChannelIndex >= 0) {
            selectedChannelsMainSensor.remove(selectedChannelIndex);
            selectedChannelsMainSensor.add(selectedChannelIndex+1, selectedChannel);  
            listSelectedChannelsMainSensor.setListData(selectedChannelsMainSensor);
        }
    }

    private void addChannelSecondSensor() {
        if (listChannelsSecondSensor.getSelectedIndex() >= 0) {
            String selectedChannel = listChannelsSecondSensor.getSelectedItem().toString();
            if (selectedChannel != "" && !selectedChannelsSecondSensor.contains(selectedChannel)) {
                selectedChannelsSecondSensor.addElement(selectedChannel);
                listSelectedChannelsSecondSensor.setListData(selectedChannelsSecondSensor);
            }
        }
    }

    private void removeChannelSecondSensor() {
        int selectedChannelIndex = listSelectedChannelsSecondSensor.getSelectedIndex();
        if (selectedChannelIndex >= 0) {
            selectedChannelsSecondSensor.remove(selectedChannelIndex);
            listSelectedChannelsSecondSensor.setListData(selectedChannelsSecondSensor);
        }
    }

    private void moveUpChannelSecondSensor() {
        int selectedChannelIndex = listSelectedChannelsSecondSensor.getSelectedIndex();
        String selectedChannel = listSelectedChannelsSecondSensor.getSelectedValue();
        if (selectedChannelIndex > 0) {
            selectedChannelsSecondSensor.remove(selectedChannelIndex);
            selectedChannelsSecondSensor.add(selectedChannelIndex-1, selectedChannel);
            listSelectedChannelsSecondSensor.setListData(selectedChannelsSecondSensor);
        } 
    }

    private void moveDownChannelSecondSensor() {
        int selectedChannelIndex = listSelectedChannelsSecondSensor.getSelectedIndex();
        String selectedChannel = listSelectedChannelsSecondSensor.getSelectedValue();
        if (selectedChannelIndex < selectedChannelsSecondSensor.size()-1 && selectedChannelIndex >= 0) {
            selectedChannelsSecondSensor.remove(selectedChannelIndex);
            selectedChannelsSecondSensor.add(selectedChannelIndex+1, selectedChannel);
            listSelectedChannelsSecondSensor.setListData(selectedChannelsSecondSensor);
        }
    }

    private void addChannelThirdSensor() {
        if (listChannelsThirdSensor.getSelectedIndex() >= 0) {
            String selectedChannel = listChannelsThirdSensor.getSelectedItem().toString();
            if (selectedChannel != "" && !selectedChannelsThirdSensor.contains(selectedChannel)) {
                selectedChannelsThirdSensor.addElement(selectedChannel);
                listSelectedChannelsThirdSensor.setListData(selectedChannelsThirdSensor);
            }
        }
    }

    private void removeChannelThirdSensor() {
        int selectedChannelIndex = listSelectedChannelsThirdSensor.getSelectedIndex();
        if (selectedChannelIndex >= 0) {
            selectedChannelsThirdSensor.remove(selectedChannelIndex);
            listSelectedChannelsThirdSensor.setListData(selectedChannelsThirdSensor);
        }
    }

    private void moveUpChannelThirdSensor() {
        int selectedChannelIndex = listSelectedChannelsThirdSensor.getSelectedIndex();
        String selectedChannel = listSelectedChannelsThirdSensor.getSelectedValue();
        if (selectedChannelIndex > 0) {
            selectedChannelsThirdSensor.remove(selectedChannelIndex);
            selectedChannelsThirdSensor.add(selectedChannelIndex-1, selectedChannel);
            listSelectedChannelsThirdSensor.setListData(selectedChannelsThirdSensor); 
        }
    }

    private void moveDownChannelThirdSensor() {
        int selectedChannelIndex = listSelectedChannelsThirdSensor.getSelectedIndex();
        String selectedChannel = listSelectedChannelsThirdSensor.getSelectedValue();
        if (selectedChannelIndex < selectedChannelsThirdSensor.size()-1 && selectedChannelIndex >= 0) {
            selectedChannelsThirdSensor.remove(selectedChannelIndex);
            selectedChannelsThirdSensor.add(selectedChannelIndex+1, selectedChannel);  
            listSelectedChannelsThirdSensor.setListData(selectedChannelsThirdSensor);
        }
    }

    protected void initComponents() {

        jp1 = GraphicLib.createTabbedPane();//new JTabbedPane();
        int curY = 0;
        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());


        JPanel jp02 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp02.setLayout(gridbag01);
        jp02.setBorder(new javax.swing.border.TitledBorder("Automated Security"));


        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        //genJava.addActionListener(this);

		secGroup =new ButtonGroup();
        autoSec = new JRadioButton("Add security");
		jp02.add(autoSec, c01);
		autoSec.addActionListener(this);
		secGroup.add(autoSec);
        autoConf = new JCheckBox("Add security (Confidentiality)");
        jp02.add(autoConf, c01);
        autoConf.setEnabled(false);
        autoConf.addActionListener(this);
        autoWeakAuth = new JCheckBox("Add security (Integrity)");
        autoWeakAuth.setEnabled(false);
        jp02.add(autoWeakAuth, c01);
        autoWeakAuth.addActionListener(this);

        autoStrongAuth = new JCheckBox("Add security (Strong Authenticity)");
        autoStrongAuth.setEnabled(false);
        jp02.add(autoStrongAuth, c01);
		autoStrongAuth.addActionListener(this);
		
        listIgnoredChannelsToAddSec = new JList<String>(ignoredChannelsToAddSec);
        JPanel panelListChannelsAddSec = new JPanel();
        panelListChannelsAddSec.setPreferredSize(new Dimension(250, 150));
        GridBagConstraints cListChannelsAddSec = new GridBagConstraints();
		cListChannelsAddSec.gridwidth = 1;
		cListChannelsAddSec.gridheight = 1;
		cListChannelsAddSec.fill= GridBagConstraints.BOTH;
     	listIgnoredChannelsToAddSec.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
     	listIgnoredChannelsToAddSec.addListSelectionListener(this);

        JScrollPane scrollPane1ChannelsToAddSec = new JScrollPane(listIgnoredChannelsToAddSec);
        scrollPane1ChannelsToAddSec.setPreferredSize(new Dimension(250, 150));
        panelListChannelsAddSec.add(scrollPane1ChannelsToAddSec, BorderLayout.WEST);

        JPanel buttonPanelChannelsToAddSec = new JPanel();
        GridBagConstraints cButtonChannelsToAddSec = new GridBagConstraints();
        cButtonChannelsToAddSec.gridwidth = GridBagConstraints.REMAINDER;
        cButtonChannelsToAddSec.gridheight = 1;

        addAllChannelsToAddSec = new JButton(IconManager.imgic50);
        addAllChannelsToAddSec.setPreferredSize(new Dimension(50, 25));
        addAllChannelsToAddSec.setEnabled(false);
        addAllChannelsToAddSec.addActionListener(this);
        addAllChannelsToAddSec.setActionCommand("addAllChannelsToAddSec");
        buttonPanelChannelsToAddSec.add(addAllChannelsToAddSec, cButtonChannelsToAddSec);

        addOneChannelToAddSec = new JButton(IconManager.imgic48);
        addOneChannelToAddSec.setPreferredSize(new Dimension(50, 25));
        addOneChannelToAddSec.setEnabled(false);
        addOneChannelToAddSec.addActionListener(this);
        addOneChannelToAddSec.setActionCommand("addOneChannelToAddSec");
        buttonPanelChannelsToAddSec.add(addOneChannelToAddSec, cButtonChannelsToAddSec);

        buttonPanelChannelsToAddSec.add(new JLabel(" "), cButtonChannelsToAddSec);

        removeOneChannelToAddSec = new JButton(IconManager.imgic46);
        removeOneChannelToAddSec.setEnabled(false);
        removeOneChannelToAddSec.addActionListener(this);
        removeOneChannelToAddSec.setPreferredSize(new Dimension(50, 25));
        removeOneChannelToAddSec.setActionCommand("removeOneChannelToAddSec");
        buttonPanelChannelsToAddSec.add(removeOneChannelToAddSec, cButtonChannelsToAddSec);

        removeAllChannelsToAddSec = new JButton(IconManager.imgic44);
        removeAllChannelsToAddSec.setEnabled(false);
        removeAllChannelsToAddSec.addActionListener(this);
        removeAllChannelsToAddSec.setPreferredSize(new Dimension(50, 25));
        removeAllChannelsToAddSec.setActionCommand("removeAllChannelsToAddSec");
        buttonPanelChannelsToAddSec.add(removeAllChannelsToAddSec, cButtonChannelsToAddSec);
        buttonPanelChannelsToAddSec.setPreferredSize(new Dimension(50, 150));
        panelListChannelsAddSec.add(buttonPanelChannelsToAddSec, cListChannelsAddSec);

        listSelectedChannelsToAddSec = new JList<String>(selectedChannelsToAddSec);
        listSelectedChannelsToAddSec.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listSelectedChannelsToAddSec.addListSelectionListener(this);
        JScrollPane scrollPane2ChannelsToAddSec = new JScrollPane(listSelectedChannelsToAddSec);
        scrollPane2ChannelsToAddSec.setPreferredSize(new Dimension(250, 150));
        panelListChannelsAddSec.add(scrollPane2ChannelsToAddSec, BorderLayout.CENTER);
        panelListChannelsAddSec.setPreferredSize(new Dimension(600, 175));
        panelListChannelsAddSec.setMinimumSize(new Dimension(600, 175));
        c01.gridheight = 10;
        jp02.add(panelListChannelsAddSec, c01);

		addHSM = new JCheckBox("Add HSM to component:");
        addHSM.addActionListener(this);
		addHSM.setEnabled(false);
		jp02.add(addHSM, c01);
		
		listIgnored = new JList<String>(ignoredTasks);


		listPanel = new JPanel();
		listPanel.setPreferredSize(new Dimension(250, 150));
		GridBagConstraints c02 = new GridBagConstraints();
		c02.gridwidth=1;
		c02.gridheight=1;
		c02.fill= GridBagConstraints.BOTH;
     	listIgnored.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listIgnored.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listIgnored);
        scrollPane1.setPreferredSize(new Dimension(250, 150));
        listPanel.add(scrollPane1, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel();
        GridBagConstraints c13 = new GridBagConstraints();
        c13.gridwidth = GridBagConstraints.REMAINDER;
        c13.gridheight = 1;
        
        allValidated = new JButton(IconManager.imgic50);
        allValidated.setPreferredSize(new Dimension(50, 25));
        allValidated.addActionListener(this);
        allValidated.setActionCommand("allValidated");
        buttonPanel.add(allValidated, c13);

		allValidated.setEnabled(false);
		

        addOneValidated = new JButton(IconManager.imgic48);
        addOneValidated.setPreferredSize(new Dimension(50, 25));
        addOneValidated.addActionListener(this);
        addOneValidated.setActionCommand("addOneValidated");
        buttonPanel.add(addOneValidated, c13);


		addOneValidated.setEnabled(false);

        buttonPanel.add(new JLabel(" "), c13);

        addOneIgnored = new JButton(IconManager.imgic46);
        addOneIgnored.addActionListener(this);
        addOneIgnored.setPreferredSize(new Dimension(50, 25));
        addOneIgnored.setActionCommand("addOneIgnored");
        buttonPanel.add(addOneIgnored, c13);

		addOneIgnored.setEnabled(false);

        allIgnored = new JButton(IconManager.imgic44);
        allIgnored.addActionListener(this);
        allIgnored.setPreferredSize(new Dimension(50, 25));
        allIgnored.setActionCommand("allIgnored");
        buttonPanel.add(allIgnored, c13);
        listPanel.add(buttonPanel, c02);
        buttonPanel.setPreferredSize(new Dimension(50, 150));

		allIgnored.setEnabled(false);
		
        listSelected = new JList<String>(selectedTasks);

        //listValidated.setPreferredSize(new Dimension(200, 250));
        listSelected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listSelected.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(listSelected);
        scrollPane2.setPreferredSize(new Dimension(250, 150));
        listPanel.add(scrollPane2, BorderLayout.CENTER);
        listPanel.setPreferredSize(new Dimension(600, 175));
        listPanel.setMinimumSize(new Dimension(600, 175));
        c01.gridheight = 10;
        jp02.add(listPanel, c01);
        c02.gridheight = 1;
		
		custom = new JCheckBox("Custom performance attributes");
        jp02.add(custom, c01);
        custom.addActionListener(this);

        jp02.add(new JLabel("Encryption Computational Complexity"), c01);
        encTime = new JTextField(encCC);
        encTime.setEnabled(false);
        jp02.add(encTime, c01);

        jp02.add(new JLabel("Decryption Computational Complexity"), c01);
        decTime = new JTextField(decCC);
        decTime.setEnabled(false);
        jp02.add(decTime, c01);

        jp02.add(new JLabel("Data Overhead (bits)"), c01);
        secOverhead = new JTextField(secOv);
        secOverhead.setEnabled(false);
        jp02.add(secOverhead, c01);
		
		
        autoMapKeys= new JRadioButton("Add Keys Only");
		autoMapKeys.addActionListener(this);
        jp02.add(autoMapKeys, c01);
		secGroup.add(autoMapKeys);

		
/*
		for (String cpuName: cpuTaskMap.keySet()){
			JCheckBox cpu = new JCheckBox(cpuName);
			jp02.add(cpu,c01);		
			cpu.setEnabled(false);
			cpu.addActionListener(this);
			ArrayList<JCheckBox> tasks = new ArrayList<JCheckBox>();
			for (String s: cpuTaskMap.get(cpuName)){

				JCheckBox task = new JCheckBox(s);
				jp02.add(task,c01);
				task.setEnabled(false);
				tasks.add(task);
			}
			cpuTaskObjs.put(cpu, tasks);

		}
		if (cpuTaskMap.keySet().size()==0){
			addHSM.setEnabled(false);
		}
*/
        //     addToComp = new JTextField(compName);
        //jp01.add(addToComp,c01);

   /*     removeForkAndJoin = new JCheckBox("Remove fork and join operators");
        if (mgui.isExperimentalOn()) {
            //jp02.add(removeForkAndJoin, c01);
            //removeForkAndJoin.addActionListener(this);
        }*/

        JPanel jp01 = new JPanel();
        gridbag01 = new GridBagLayout();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Verification options"));

        myButton = new TGHelpButton("Help me!", IconManager.imgic32, helpString, mgui, mgui.getHelpManager());
        jp01.add(myButton, new GridBagConstraints(3, curY, 1, 1, 1.0, 1.0, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, insets, 0, 0));
        curY++;



        JLabel gen = new JLabel("Generate ProVerif code in: ");

        addComponent(jp01, gen, 0, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        code1 = new JTextField(pathCode, 100);
        code1.setPreferredSize(new Dimension(100, 10));
        addComponent(jp01, code1, 1, curY, 3, GridBagConstraints.EAST, GridBagConstraints.BOTH);
        curY++;

        JLabel exe = new JLabel("Execute ProVerif as: ");
        addComponent(jp01, exe, 0, curY, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH);

        exe2 = new JTextField(pathExecute, 100);
        addComponent(jp01, exe2, 1, curY, 3, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        curY++;

        addComponent(jp01, new JLabel("Compute state reachability: "), 0, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        ButtonGroup stateReachabilityGroup = new ButtonGroup();
        stateReachabilityAll = new JRadioButton("all");
        stateReachabilitySelected = new JRadioButton("selected");
        JRadioButton stateReachabilityNone = new JRadioButton("none");
        addComponent(jp01, stateReachabilityAll, 1, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        addComponent(jp01, stateReachabilitySelected, 2, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        addComponent(jp01, stateReachabilityNone, 3, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        stateReachabilityGroup.add(stateReachabilityAll);
        stateReachabilityGroup.add(stateReachabilitySelected);
        stateReachabilityGroup.add(stateReachabilityNone);
        stateReachabilityAll.setSelected(REACHABILITY_OPTION == REACHABILITY_ALL);
        stateReachabilitySelected.setSelected(REACHABILITY_OPTION == REACHABILITY_SELECTED);
        stateReachabilityNone.setSelected(REACHABILITY_OPTION == REACHABILITY_NONE);
        curY++;

        addComponent(jp01, new JLabel("Allow message duplication in private channels: "), 0, curY, 2,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        ButtonGroup privateChannelGroup = new ButtonGroup();
        privateChannelDup = new JRadioButton("Yes");
        JRadioButton privateChannelNoDup = new JRadioButton("No");
        addComponent(jp01, privateChannelDup, 2, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        addComponent(jp01, privateChannelNoDup, 3, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        privateChannelGroup.add(privateChannelDup);
        privateChannelGroup.add(privateChannelNoDup);
        // TODO: change that
        // privateChannelNoDup.setSelected(true);
        privateChannelDup.setSelected(MSG_DUPLICATION);
        privateChannelNoDup.setSelected(!MSG_DUPLICATION);
        curY++;

        typedLanguage = new JCheckBox("Generate typed Pi calculus");
        typedLanguage.setSelected(PI_CALCULUS);
        addComponent(jp01, typedLanguage, 0, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        curY++;
        loopLimit = new JTextField(""+LOOP_ITERATION, 3);
        if (limit) {
            addComponent(jp01, new JLabel("Limit on loop iterations:"), 0, curY, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
            addComponent(jp01, loopLimit, 1, curY, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
            curY++;
        }

        if (mgui.isExperimentalOn()) {
            drawAvatarDesign = new JCheckBox("Draw Avatar model");
            drawAvatarDesign.setSelected(DRAW_AVATAR);
            drawAvatarDesign.addActionListener(this);
            addComponent(jp01, drawAvatarDesign, 2, curY, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        }

        JLabel empty = new JLabel("");
        jp01.add(empty, new GridBagConstraints(0, curY, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, insets, 0, 0));




        jta = new JPanel();
        jta.setLayout(new GridBagLayout());
        jta.setBorder(new javax.swing.border.TitledBorder("Results"));
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);

        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setPreferredSize(new Dimension(300, 300));
        c.add(jsp, BorderLayout.CENTER);

        //	addComponent(jp01, jsp, 1, curY, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
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


        jp1.add("Security Verification", jp01);

        if (currPanel instanceof TMLArchiPanel) {
            //Can only secure a mapping
            jp1.add("Automated Security", jp02);
        }
        panelAutoPattern();
        c.add(jp1, BorderLayout.NORTH);
        c.add(jp2, BorderLayout.SOUTH);

        this.popup = new JPopupMenu();
        this.menuItem = new MyMenuItem("Show trace");
        this.menuItem.addActionListener(this);
        popup.add(this.menuItem);
    }

    private void handleStartButton() {
        //

    }

    private void addOneIgnored() {
        int[] list = listSelected.getSelectedIndices();
        Vector<String> v = new Vector<String>();
        String o;
        for (int i = 0; i < list.length; i++) {
            o = selectedTasks.elementAt(list[i]);
            ignoredTasks.addElement(o);
            v.addElement(o);
        }

        selectedTasks.removeAll(v);
        listIgnored.setListData(ignoredTasks);
        listSelected.setListData(selectedTasks);
        setButtons();
    }

    private void addOneValidated() {
        int[] list = listIgnored.getSelectedIndices();
        Vector<String> v = new Vector<String>();
        String o;

        for (int i = 0; i < list.length; i++) {
            o = ignoredTasks.elementAt(list[i]);
            selectedTasks.addElement(o);
            v.addElement(o);
        }

        ignoredTasks.removeAll(v);
        listIgnored.setListData(ignoredTasks);
        listSelected.setListData(selectedTasks);
        setButtons();
    }

    private void allValidated() {
        selectedTasks.addAll(ignoredTasks);
        ignoredTasks.removeAllElements();
        listIgnored.setListData(ignoredTasks);
        listSelected.setListData(selectedTasks);
        setButtons();
    }

    private void allIgnored() {
        ignoredTasks.addAll(selectedTasks);
        selectedTasks.removeAllElements();
        listIgnored.setListData(ignoredTasks);
        listSelected.setListData(selectedTasks);
        setButtons();
    }

    private void addAllChannelsToAddSec() {
        for(String ch : ignoredChannelsToAddSec) {
            String toAdd = ch;
            if (autoConf.isSelected() || autoStrongAuth.isSelected() || autoWeakAuth.isSelected()) {
                toAdd += ":";
                if (autoConf.isSelected()) {
                    toAdd += " " + ADD_CONFIDENTIALITY + " +"; 
                }
                if (autoStrongAuth.isSelected()) {
                    toAdd += " " + ADD_STRONG_AUTHENTICITY + " +"; 
                } else if (autoWeakAuth.isSelected()) {
                    toAdd += " " + ADD_WEAK_AUTHENTICITY + " +"; 
                }
                toAdd = toAdd.substring(0, toAdd.length() - 2);
            }
            selectedChannelsToAddSec.add(toAdd);
        }
        ignoredChannelsToAddSec.removeAllElements();
        listIgnoredChannelsToAddSec.setListData(ignoredChannelsToAddSec);
        listSelectedChannelsToAddSec.setListData(selectedChannelsToAddSec);
        setButtons();
    }

    private void removeAllChannelsToAddSec() {
        for(String ch : selectedChannelsToAddSec) {
            ignoredChannelsToAddSec.add(ch.split(": ")[0]);
        }
        selectedChannelsToAddSec.removeAllElements();
        listIgnoredChannelsToAddSec.setListData(ignoredChannelsToAddSec);
        listSelectedChannelsToAddSec.setListData(selectedChannelsToAddSec);
        setButtons();
    }

    private void addOneChannelToAddSec() {
        int[] list = listIgnoredChannelsToAddSec.getSelectedIndices();
        Vector<String> v = new Vector<String>();
        String o;

        for (int i = 0; i < list.length; i++) {
            o = ignoredChannelsToAddSec.elementAt(list[i]);
            String toAdd = o;
            if (autoConf.isSelected() || autoStrongAuth.isSelected() || autoWeakAuth.isSelected()) {
                toAdd += ":";
                if (autoConf.isSelected()) {
                    toAdd += " " + ADD_CONFIDENTIALITY + " +"; 
                }
                if (autoStrongAuth.isSelected()) {
                    toAdd += " " + ADD_STRONG_AUTHENTICITY + " +"; 
                } else if (autoWeakAuth.isSelected()) {
                    toAdd += " " + ADD_WEAK_AUTHENTICITY + " +"; 
                }
                toAdd = toAdd.substring(0, toAdd.length() - 2);
            }
            selectedChannelsToAddSec.addElement(toAdd);
            v.addElement(o);
        }
        ignoredChannelsToAddSec.removeAll(v);
        listIgnoredChannelsToAddSec.setListData(ignoredChannelsToAddSec);
        listSelectedChannelsToAddSec.setListData(selectedChannelsToAddSec);
        setButtons();
    }

    private void removeOneChannelToAddSec() {
        int[] list = listSelectedChannelsToAddSec.getSelectedIndices();
        Vector<String> v = new Vector<String>();
        String o;

        for (int i = 0; i < list.length; i++) {
            o = selectedChannelsToAddSec.elementAt(list[i]);
            ignoredChannelsToAddSec.addElement(o.split(": ")[0]);
            v.addElement(o);
        }
        selectedChannelsToAddSec.removeAll(v);
        listIgnoredChannelsToAddSec.setListData(ignoredChannelsToAddSec);
        listSelectedChannelsToAddSec.setListData(selectedChannelsToAddSec);
        setButtons();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        if (evt.getSource() == drawAvatarDesign) {
            DRAW_AVATAR = drawAvatarDesign.isSelected();
            return;
        }

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
            case "Show trace":
                if (evt.getSource() == this.menuItem) {
                    PipedOutputStream pos = new PipedOutputStream();
                    try {
                        PipedInputStream pis = new PipedInputStream(pos, 4096);
                        BufferedWriter bw = new BufferedWriter(new
                                OutputStreamWriter(pos));

                        JFrameSimulationSDPanel jfssdp = new
                                JFrameSimulationSDPanel(null, this.mgui, this
                                .menuItem.pragma.toString());
                        jfssdp.setIconImage(IconManager.img8);
                        GraphicLib.centerOnParent(jfssdp, 600, 600);
                        jfssdp.setFileReference(new BufferedReader(new
                                InputStreamReader(pis)));
                        jfssdp.setVisible(true);
                        jfssdp.setModalExclusionType(ModalExclusionType
                                .APPLICATION_EXCLUDE);
						jfssdp.setLimitEntity(false);
                        jfssdp.toFront();

                        // TraceManager.addDev("\n--- Trace ---");
                        int i = 0;
                        if (adp != null) {
                            for (ProVerifResultTraceStep step : this.menuItem
                                    .result.getTrace().getTrace()) {
                                step.describeAsSDTransaction(this.adp, bw, i);
                                i++;
                                // TraceManager.addDev(step.describeAsString
                                // (this.adp));
                            }
                        } else {
                            for (ProVerifResultTraceStep step : this.menuItem
                                    .result.getTrace().getTrace()) {
                                step.describeAsTMLSDTransaction(bw, i);
                                i++;
                                // TraceManager.addDev(step.describeAsString
                                // (this.adp));
                            }
                        }
                        bw.close();
                    } catch (IOException e) {
                        TraceManager.addDev("Error when writing trace step SD transaction");
                    } finally {
                        try {
                            pos.close();
                        } catch (IOException ignored) {
                        }
                    }
                    // TraceManager.addDev("");
                }
                break;
            default:
                if ((evt.getSource() == autoWeakAuth) || (evt.getSource() == autoStrongAuth) || (evt.getSource() == autoConf) || (evt.getSource() == autoMapKeys)) {
                    handleStartButton();
                } else if (evt.getSource() instanceof JCheckBox) {
                    //Disable and enable tasks
                    JCheckBox src = (JCheckBox) evt.getSource();
                    if (cpuTaskObjs.containsKey(src)) {
                        for (JCheckBox taskBox : cpuTaskObjs.get(src)) {
                            taskBox.setEnabled(src.isSelected());
                        }
                    }
                } else if (command.equals("addOneIgnored")) {
                    addOneIgnored();
                } else if (command.equals("addOneValidated")) {
                    addOneValidated();
                } else if (command.equals("allValidated")) {
                    allValidated();
                } else if (command.equals("allIgnored")) {
                    allIgnored();
                }  else if (command.equals("addAllChannelsToAddSec")) {
                    addAllChannelsToAddSec();
                } else if (command.equals("removeAllChannelsToAddSec")) {
                    removeAllChannelsToAddSec();
                } else if (command.equals("addOneChannelToAddSec")) {
                    addOneChannelToAddSec();
                } else if (command.equals("removeOneChannelToAddSec")) {
                    removeOneChannelToAddSec();
                } else if (command.equals("addChannelMainSensor")) {
                    addChannelMainSensor();
                } else if (command.equals("removeChannelMainSensor")) {
                    removeChannelMainSensor();
                } else if (command.equals("moveUpChannelMainSensor")) {
                    moveUpChannelMainSensor();
                } else if (command.equals("moveDownChannelMainSensor")) {
                    moveDownChannelMainSensor();
                } else if (command.equals("addChannelSecondSensor")) {
                    addChannelSecondSensor();
                } else if (command.equals("removeChannelSecondSensor")) {
                    removeChannelSecondSensor();
                } else if (command.equals("moveUpChannelSecondSensor")) {
                    moveUpChannelSecondSensor();
                } else if (command.equals("moveDownChannelSecondSensor")) {
                    moveDownChannelSecondSensor();
                } else if (command.equals("addChannelThirdSensor")) {
                    addChannelThirdSensor();
                } else if (command.equals("removeChannelThirdSensor")) {
                    removeChannelThirdSensor();
                } else if (command.equals("moveUpChannelThirdSensor")) {
                    moveUpChannelThirdSensor();
                } else if (command.equals("moveDownChannelThirdSensor")) {
                    moveDownChannelThirdSensor();
                }
                if (evt.getSource() == autoConf || evt.getSource() == autoSec || evt.getSource() == autoMapKeys || evt.getSource() == autoWeakAuth || evt.getSource()==addHSM) {
                    //autoWeakAuth.setEnabled(autoConf.isSelected());
                    autoConf.setEnabled(autoSec.isSelected());
                    addHSM.setEnabled(autoSec.isSelected());
                    addOneValidated.setEnabled(addHSM.isSelected());
                    allValidated.setEnabled(addHSM.isSelected());
                    addOneIgnored.setEnabled(addHSM.isSelected());
                    allIgnored.setEnabled(addHSM.isSelected());
                    autoWeakAuth.setEnabled(autoSec.isSelected());
                    autoStrongAuth.setEnabled(autoWeakAuth.isSelected());
                    
                    if (!autoSec.isSelected()) {
                        autoConf.setSelected(false);
                        autoWeakAuth.setSelected(false);
                        autoStrongAuth.setSelected(false);
                        addHSM.setSelected(false);

                        autoConf.setEnabled(false);
                        autoWeakAuth.setEnabled(false);
                        autoStrongAuth.setEnabled(false);
                        allIgnored();
                        removeAllChannelsToAddSec();
                    }

                    if (!autoWeakAuth.isSelected()) {
                        autoStrongAuth.setSelected(false);
                    }
                }
                if (evt.getSource() == autoSec || evt.getSource() == autoConf || evt.getSource() == autoWeakAuth) {
                    if (autoSec.isSelected() && (autoWeakAuth.isSelected() || autoConf.isSelected())) {
                        addAllChannelsToAddSec.setEnabled(true);
                        addOneChannelToAddSec.setEnabled(true);
                        removeAllChannelsToAddSec.setEnabled(true);
                        removeOneChannelToAddSec.setEnabled(true);
                    } else {
                        addAllChannelsToAddSec.setEnabled(false);
                        addOneChannelToAddSec.setEnabled(false);
                        removeAllChannelsToAddSec.setEnabled(false);
                        removeOneChannelToAddSec.setEnabled(false);
                    }
                }

                if (evt.getSource() == custom) {
                    encTime.setEnabled(custom.isSelected());
                    decTime.setEnabled(custom.isSelected());
                    secOverhead.setEnabled(custom.isSelected());
                }
                if (evt.getSource() == selectTwoSensors) {
                    addChannelFromSecondSensor.setEnabled(selectTwoSensors.isSelected());
                    removeChannelFromSecondSensor.setEnabled(selectTwoSensors.isSelected());
                    moveUpChannelFromSecondSensor.setEnabled(selectTwoSensors.isSelected());
                    moveDownChannelFromSecondSensor.setEnabled(selectTwoSensors.isSelected());
                    listCompSelectSecondSensor.setEnabled(selectTwoSensors.isSelected());
                    listChannelsSecondSensor.setEnabled(selectTwoSensors.isSelected());

                    addChannelFromThirdSensor.setEnabled(selectTwoSensors.isSelected());
                    removeChannelFromThirdSensor.setEnabled(selectTwoSensors.isSelected());
                    moveUpChannelFromThirdSensor.setEnabled(selectTwoSensors.isSelected());
                    moveDownChannelFromThirdSensor.setEnabled(selectTwoSensors.isSelected());
                    listCompSelectThirdSensor.setEnabled(selectTwoSensors.isSelected());
                    listChannelsThirdSensor.setEnabled(selectTwoSensors.isSelected());
                }
                if (evt.getSource() == listCompSelectReceiver || evt.getSource() == listCompSelectMainSensor) {
                    int selectedReceiverIndex = listCompSelectReceiver.getSelectedIndex();
                    int selectedMainSensorIndex = listCompSelectMainSensor.getSelectedIndex();
                    listChannelsMainSensor.removeAllItems();
                    if (selectedMainSensorIndex >= 0 && selectedReceiverIndex >= 0 && selectedMainSensorIndex != selectedReceiverIndex) {
                        allChannelsMainSensor = getListChannelsBetweenTwoTasks(allTasksFullName.get(selectedMainSensorIndex), allTasksFullName.get(selectedReceiverIndex));
                        
                        for(String chName : allChannelsMainSensor) {
                            listChannelsMainSensor.addItem(chName);
                        }
                    }
                    listChannelsMainSensor.setSelectedItem(-1);
                    listChannelsMainSensor.addActionListener(this);
                    selectedChannelsMainSensor.removeAllElements();
                    listSelectedChannelsMainSensor.setListData(selectedChannelsMainSensor);
                }

                if (evt.getSource() == listCompSelectReceiver || evt.getSource() == listCompSelectSecondSensor) {
                    int selectedReceiverIndex = listCompSelectReceiver.getSelectedIndex();
                    int selectedSecondSensorIndex = listCompSelectSecondSensor.getSelectedIndex();
                    listChannelsSecondSensor.removeAllItems();
                    if (selectedReceiverIndex >= 0 && selectedSecondSensorIndex >= 0  && selectedSecondSensorIndex!=selectedReceiverIndex) {
                        allChannelsSecondSensor = getListChannelsBetweenTwoTasks(allTasksFullName.get(selectedSecondSensorIndex), allTasksFullName.get(selectedReceiverIndex));
                        
                        for(String chName : allChannelsSecondSensor) {
                            listChannelsSecondSensor.addItem(chName);
                        }
                    }
                    listChannelsSecondSensor.setSelectedItem(-1);
                    listChannelsSecondSensor.addActionListener(this);
                    selectedChannelsSecondSensor.removeAllElements();
                    listSelectedChannelsSecondSensor.setListData(selectedChannelsSecondSensor);
                }
                if (evt.getSource() == listCompSelectReceiver || evt.getSource() == listCompSelectThirdSensor) {
                    int selectedReceiverIndex = listCompSelectReceiver.getSelectedIndex();
                    int selectedThirdSensorIndex = listCompSelectThirdSensor.getSelectedIndex();
                    listChannelsThirdSensor.removeAllItems();
                    if (selectedReceiverIndex >= 0 && selectedThirdSensorIndex >= 0  && selectedThirdSensorIndex!=selectedReceiverIndex) {
                        allChannelsThirdSensor = getListChannelsBetweenTwoTasks(allTasksFullName.get(selectedThirdSensorIndex), allTasksFullName.get(selectedReceiverIndex));
                        
                        for(String chName : allChannelsThirdSensor) {
                            listChannelsThirdSensor.addItem(chName);
                        }
                    }
                    listChannelsThirdSensor.setSelectedItem(-1);
                    listChannelsThirdSensor.addActionListener(this);
                    selectedChannelsThirdSensor.removeAllElements();
                    listSelectedChannelsThirdSensor.setListData(selectedChannelsThirdSensor);
                }
        }
    }

    private void closeDialog() {
        if (this.pvoa != null) {
            this.pvoa.removeListener(this);
        }
        if (mode == STARTED) {
            stopProcess();
        }
        CODE_PATH = code1.getText();
        EXECUTE_PATH = exe2.getText();
        if (stateReachabilityAll.isSelected()) {
            REACHABILITY_OPTION = REACHABILITY_ALL;
        } else if (stateReachabilitySelected.isSelected()) {
            REACHABILITY_OPTION = REACHABILITY_SELECTED;
        } else {
            REACHABILITY_OPTION = REACHABILITY_NONE;
        }
        MSG_DUPLICATION = privateChannelDup.isSelected();
        PI_CALCULUS = typedLanguage.isSelected();
        try {
            LOOP_ITERATION = Integer.decode(loopLimit.getText());
        } catch (Exception e) {}

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

    private class ProVerifResultSection {
        String title;
        List<AvatarPragma> results;
        JList<AvatarPragma> jlist;

        ProVerifResultSection(String title, List<AvatarPragma> results) {
            this.title = title;
            this.results = results;
        }
    }

    @Override
    public void run() {
        TraceManager.addDev("Thread started");
        File testFile;
        Map<String, List<String>> selectedCpuTasks = new HashMap<String, java.util.List<String>>();
        int currentPosY = 0;
        int stepY = 10; 
        if (jp1.getSelectedIndex() == 2) {
            interpretersCompTimeDefault = interpretersCompTime.getText();
            voterCompTimeDefault = voterCompTime.getText();
            voterTimeOutDefault = voterTimeOut.getText();
            jta.removeAll();
            boolean findErr = false;
            if (listCompSelectReceiver.getSelectedIndex() < 0) {
                JLabel label = new JLabel("ERROR: Receiver not selected");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (listCompSelectMainSensor.getSelectedIndex() < 0) {
                JLabel label = new JLabel("ERROR: Main Sensor not selected");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (listCompSelectMainSensor.getSelectedItem() == listCompSelectReceiver.getSelectedItem()) {
                JLabel label = new JLabel("ERROR: Receiver and Main Sensor must be different");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectedChannelsMainSensor.size() == 0) {
                JLabel label = new JLabel("ERROR: No Channel of main sensor is selected");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectTwoSensors.isSelected() && listCompSelectSecondSensor.getSelectedIndex() < 0) {
                JLabel label = new JLabel("ERROR: Second Sensor not selected");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectTwoSensors.isSelected() && selectedChannelsSecondSensor.size() == 0) {
                JLabel label = new JLabel("ERROR: No Channel of second sensor is selected");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectTwoSensors.isSelected() && listCompSelectThirdSensor.getSelectedIndex() < 0) {
                JLabel label = new JLabel("ERROR: Third Sensor not selected");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectTwoSensors.isSelected() && selectedChannelsThirdSensor.size() == 0) {
                JLabel label = new JLabel("ERROR: No Channel of third sensor is selected");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectTwoSensors.isSelected() && (selectedChannelsThirdSensor.size() != selectedChannelsSecondSensor.size() || selectedChannelsThirdSensor.size() != selectedChannelsMainSensor.size() || selectedChannelsMainSensor.size() != selectedChannelsSecondSensor.size())) {
                JLabel label = new JLabel("ERROR: The number of selected channels is not the same for the 3 sensors");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectTwoSensors.isSelected() && listCompSelectMainSensor.getSelectedItem() == listCompSelectSecondSensor.getSelectedItem()) {
                JLabel label = new JLabel("ERROR: Main and second Sensors must be different");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectTwoSensors.isSelected() && listCompSelectSecondSensor.getSelectedItem() == listCompSelectThirdSensor.getSelectedItem()) {
                JLabel label = new JLabel("ERROR: Second and third Sensors must be different");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectTwoSensors.isSelected() && listCompSelectMainSensor.getSelectedItem() == listCompSelectThirdSensor.getSelectedItem()) {
                JLabel label = new JLabel("ERROR: Main and third Sensors must be different");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectTwoSensors.isSelected() && listCompSelectReceiver.getSelectedItem() == listCompSelectSecondSensor.getSelectedItem()) {
                JLabel label = new JLabel("ERROR: Receiver and second Sensor must be different");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (selectTwoSensors.isSelected() && listCompSelectReceiver.getSelectedItem() == listCompSelectThirdSensor.getSelectedItem()) {
                JLabel label = new JLabel("ERROR: Receiver and third Sensor must be different");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
                currentPosY += stepY;
                findErr = true;
            }
            if (!findErr) {
                selectedSensors.put(listCompSelectMainSensor.getSelectedItem().toString(), selectedChannelsMainSensor);
                if (selectTwoSensors.isSelected()) {
                    selectedSensors.put(listCompSelectSecondSensor.getSelectedItem().toString(), selectedChannelsSecondSensor);
                    selectedSensors.put(listCompSelectThirdSensor.getSelectedItem().toString(), selectedChannelsThirdSensor);
                }
                mgui.gtm.addPattern(mgui, selectedSensors, listCompSelectReceiver.getSelectedItem().toString(), interpretersCompTimeDefault, voterCompTimeDefault, voterTimeOutDefault);
                JLabel label = new JLabel("Pattern Generation Complete");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(currentPosY));
            }
            mode = NOT_STARTED;
        } else {
            try {
                if (jp1.getSelectedIndex() == 1) {
                    if (autoSec.isSelected()){
                        encCC = encTime.getText();
                        decCC = decTime.getText();
                        secOv = secOverhead.getText();
                        TMLMapping map;
                        if (addHSM.isSelected() && selectedTasks.size()>0) {
                            

                            for (String task : selectedTasks) {
                                String cpu = taskCpuMap.get(task);
                                if (selectedCpuTasks.containsKey(cpu)) {
                                    selectedCpuTasks.get(cpu).add(task);
                                } else {
                                    ArrayList<String> tasks = new ArrayList<String>();
                                    tasks.add(task);
                                    selectedCpuTasks.put(cpu, tasks);
                                }
                            }
                            //mgui.gtm.addHSM(mgui, selectedCpuTasks);
                        }
                        if (autoConf.isSelected() || autoWeakAuth.isSelected() || autoStrongAuth.isSelected()) {
                            /*if (custom.isSelected()) {
                                map = mgui.gtm.autoSecure(mgui, encCC, secOv, decCC, autoConf.isSelected(), autoWeakAuth.isSelected(),
                                        autoStrongAuth.isSelected(), selectedCpuTasks);
                            } else {
                                map = mgui.gtm.autoSecure(mgui, "100", "0", "100", autoConf.isSelected(),
                                        autoWeakAuth.isSelected(), autoStrongAuth.isSelected(), selectedCpuTasks);
                            }*/
                            mgui.gtm.autoSecure(mgui, encCC, secOv, decCC, selectedChannelsToAddSec, selectedCpuTasks);
                        }
                    } 
                    else if (autoMapKeys.isSelected()) {
                        mgui.gtm.autoMapKeys();
                    }
                    
                    jta.removeAll();
                    JLabel label = new JLabel("Security Generation Complete");
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    this.jta.add(label, this.createGbc(0));
                    mode = NOT_STARTED;
                }
                else {
                    testGo();
                    pathCode = code1.getText().trim();

                    SpecConfigTTool.checkAndCreateProverifDir(pathCode);

                    pathCode += "pvspec";
                    testFile = new File(pathCode);


                    File dir = testFile.getParentFile();

                    if (dir == null || !dir.exists()) {
                        mode = STOPPED;
                        setButtons();
                        throw new ProVerifVerificationException("Error: invalid file: " + pathCode);
                    }


                    if (testFile.exists()) {
                        // FIXME Raise error if modified since last
                        TraceManager.addDev("FILE EXISTS!!!");
                    }


                    if (!mgui.gtm.generateProVerifFromAVATAR(
                            pathCode,
                            stateReachabilityAll.isSelected() ? REACHABILITY_ALL :
                                    stateReachabilitySelected.isSelected() ? REACHABILITY_SELECTED : REACHABILITY_NONE,
                            typedLanguage.isSelected(),
                            privateChannelDup.isSelected(),
                            loopLimit.getText())
                            ) {
                        throw new ProVerifVerificationException("Could not generate proverif code");
                    }


                    String cmd = exe2.getText().trim();

                    if (this.typedLanguage.isSelected()) {
                        cmd += " -in pitype ";
                    } else {
                        cmd += " -in pi ";
                    }

                    cmd += pathCode;
                    TraceManager.addDev("Executing command:" + cmd);

                    testGo();

                    this.rshc = new RshClient(hostProVerif);
                    this.rshc.setCmd(cmd);
                    this.rshc.sendExecuteCommandRequest();
                    RshClientReader reader = this.rshc.getDataReaderFromProcess();

                    if (this.pvoa == null) {
                        this.pvoa = mgui.gtm.getProVerifOutputAnalyzer();
                        this.pvoa.addListener(this);
                    }
                    //try {
                        this.pvoa.analyzeOutput(reader, typedLanguage.isSelected());
                    /*} catch (Exception e) {
                        TraceManager.addDev("Traces could not be analyzed: " + e.getMessage());
                    }*/

                    mgui.modelBacktracingProVerif(pvoa);

                    if (drawAvatarDesign != null) {
                        if (drawAvatarDesign.isSelected()) {
                            mgui.drawAvatarSpecification(mgui.gtm.getAvatarSpecification());
                        }
                    }

                    mode = NOT_STARTED;

                }
            } catch (LauncherException | ProVerifVerificationException le) {
                JLabel label = new JLabel("Error: " + le.getMessage());
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(0));
                mode = STOPPED;
            } catch (InterruptedException ie) {
                mode = NOT_STARTED;
            } catch (FileException e) {
                System.err.println(e.getMessage() + " : Can't generate proverif file.");
            } catch (Exception e) {
                mode = STOPPED;
                TraceManager.addDev("General exception in ProVerif proof or trace generation: " + e.getMessage());
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

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger() && e.getComponent() instanceof JList) {
            JList<?> curList = (JList<?>) e.getComponent();
            int row = curList.locationToIndex(e.getPoint());
            curList.clearSelection();
            curList.setSelectedIndex(row);
            Object o = curList.getModel().getElementAt(row);
            if (o instanceof AvatarPragma) {
                this.menuItem.pragma = (AvatarPragma) o;
                this.menuItem.result = this.results.get(this.menuItem.pragma);
                //   this.menuItem.setEnabled(this.adp != null && this.menuItem.result.getTrace() != null);
                this.menuItem.setEnabled(this.menuItem.result.getTrace() != null);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // TODO: unselect the other lists
    }

    @Override
    public void proVerifOutputChanged() {

        //TraceManager.addDev("Proverif output changed: removingAll");

        JLabel label;
        this.jta.removeAll();

        if (pvoa.getErrors().size() != 0) {
            int y = 0;

            label = new JLabel("Errors found in the generated code:");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.jta.add(label, this.createGbc(y++));
            label = new JLabel("----------------");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.jta.add(label, this.createGbc(y++));
            this.jta.add(Box.createRigidArea(new Dimension(0, 5)), this.createGbc(y++));
            for (String error : pvoa.getErrors()) {
                label = new JLabel(error);
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(y++));
            }
        } else {
            LinkedList<AvatarPragma> reachableEvents = new LinkedList<>();
            LinkedList<AvatarPragma> nonReachableEvents = new LinkedList<>();
            LinkedList<AvatarPragma> secretTerms = new LinkedList<>();
            LinkedList<AvatarPragma> nonSecretTerms = new LinkedList<>();
            LinkedList<AvatarPragma> satisfiedStrongAuth = new LinkedList<>();
            LinkedList<AvatarPragma> satisfiedWeakAuth = new LinkedList<>();
            LinkedList<AvatarPragma> nonSatisfiedAuth = new LinkedList<>();
            LinkedList<AvatarPragma> nonProved = new LinkedList<>();

            this.results = this.pvoa.getResults();
            
            if (this.results.keySet().size() == 0) {
                label = new JLabel("ERROR: no properties to prove");
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label, this.createGbc(0));
            }
            
            for (AvatarPragma pragma : this.results.keySet()) {
                if (pragma instanceof AvatarPragmaReachability) {
                    ProVerifQueryResult r = this.results.get(pragma);
                    if (r.isProved()) {
                        if (r.isSatisfied())
                            reachableEvents.add(pragma);
                        else
                            nonReachableEvents.add(pragma);
                    } else
                        nonProved.add(pragma);
                } else if (pragma instanceof AvatarPragmaSecret) {
                    ProVerifQueryResult r = this.results.get(pragma);
                    if (r.isProved()) {
                        if (r.isSatisfied())
                            secretTerms.add(pragma);
                        else
                            nonSecretTerms.add(pragma);
                    } else
                        nonProved.add(pragma);
                } else if (pragma instanceof AvatarPragmaAuthenticity) {
                    ProVerifQueryAuthResult r = (ProVerifQueryAuthResult) this.results.get(pragma);
                    if (!r.isWeakProved()) {
                        nonProved.add(pragma);
                    } else {
                        if (!r.isProved())
                            nonProved.add(pragma);
                        if (r.isProved() && r.isSatisfied())
                            satisfiedStrongAuth.add(pragma);
                        else if (r.isWeakSatisfied())
                            satisfiedWeakAuth.add(pragma);
                        else
                            nonSatisfiedAuth.add(pragma);
                    }
                }
            }

            Collection<ProVerifResultSection> sectionsList = new LinkedList<>();
            Collections.sort(reachableEvents);
            Collections.sort(nonReachableEvents);
            Collections.sort(secretTerms);
            Collections.sort(nonSecretTerms);
            Collections.sort(satisfiedStrongAuth);
            Collections.sort(satisfiedWeakAuth);
            Collections.sort(nonSatisfiedAuth);
            Collections.sort(nonProved);
            sectionsList.add(new ProVerifResultSection("Reachable states:", reachableEvents));
            sectionsList.add(new ProVerifResultSection("Non reachable states:", nonReachableEvents));
            sectionsList.add(new ProVerifResultSection("Confidential Data:", secretTerms));
            sectionsList.add(new ProVerifResultSection("Non confidential Data:", nonSecretTerms));
            sectionsList.add(new ProVerifResultSection("Satisfied Strong Authenticity:", satisfiedStrongAuth));
            sectionsList.add(new ProVerifResultSection("Satisfied Weak Authenticity:", satisfiedWeakAuth));
            sectionsList.add(new ProVerifResultSection("Non Satisfied Authenticity:", nonSatisfiedAuth));
            sectionsList.add(new ProVerifResultSection("Not Proved Queries:", nonProved));

            int y = 0;

            for (ProVerifResultSection section : sectionsList) {
                if (!section.results.isEmpty()) {
                    label = new JLabel(section.title);
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    this.jta.add(label, this.createGbc(y++));
                    this.jta.add(Box.createRigidArea(new Dimension(0, 5)), this.createGbc(y++));
                    section.jlist = new JList<>(section.results.toArray(new AvatarPragma[0]));
                    section.jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    section.jlist.addMouseListener(this);
                    section.jlist.setAlignmentX(Component.LEFT_ALIGNMENT);
                    this.jta.add(section.jlist, this.createGbc(y++));
                    this.jta.add(Box.createRigidArea(new Dimension(0, 10)), this.createGbc(y++));
                }
            }
        }

        this.repaint();
        this.revalidate();
    }
}
