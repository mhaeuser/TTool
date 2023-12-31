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

import avatartranslator.*;
import avatartranslator.modelchecker.AvatarModelChecker;
import avatartranslator.modelchecker.CounterexampleQueryReport;
import avatartranslator.modelchecker.SafetyProperty;
import avatartranslator.modelchecker.SpecificationActionLoop;
import avatartranslator.modelchecker.SpecificationReachability;
import avatartranslator.modelchecker.SpecificationPropertyPhase;
import help.HelpEntry;
import help.HelpManager;
import myutil.*;
import org.apache.commons.io.FilenameUtils;
import ui.util.IconManager;
import ui.MainGUI;
import ui.TGComponent;
import ui.avatarbd.AvatarBDSafetyPragma;
import graph.RG;
import graph.AUTGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class JDialogAvatarModelChecker
 * Dialog for managing the model checking of avatar specifications
 * Creation: 1/06/2016
 *
 * @author Ludovic APVRILLE
 * @version 1.1 1/06/2016
 */
public class JDialogAvatarModelChecker extends javax.swing.JFrame implements ActionListener, Runnable, MasterProcessInterface {
    private final static String[] INFOS = {"Not started", "Running", "Stopped by user", "Finished"};
    private final static Color[] COLORS = {Color.darkGray, Color.magenta, Color.red, Color.blue};
    private final static String[] WORD_BITS = {"32 bits", "16 bits", "8 bits"};
    private final static String[] SEARCH_TYPE = {"BFS", "DFS"};
    private final static String RG_NAME = "rgavatar$.aut";



    public final static int REACHABILITY_ALL = 1;
    public final static int REACHABILITY_SELECTED = 2;
    public final static int REACHABILITY_NONE = 3;

    public final static int LIVENESS_ALL = 4;
    public final static int LIVENESS_SELECTED = 5;
    public final static int LIVENESS_NONE = 6;


    protected static String graphDir;
    protected static boolean graphSelected = false;
    protected static String graphDirDot;
    protected static boolean graphSelectedDot = false;
    protected static boolean advancedInfoDeadlock = false;
    protected static boolean ignoreEmptyTransitionsSelected = true;
    protected static boolean ignoreConcurrenceBetweenInternalActionsSelected = true;
    protected static boolean ignoreInternalStatesSelected = true;
    protected static boolean generateDesignSelected = false;
    protected static boolean generateDependencyGraphSelected = false;
    protected static boolean generateDependencyGraphSelectedNoID = false;
    protected static boolean generateCompactDependencyGraphSelected = false;
    protected static boolean generateCompactDependencyGraphSelectedNoID = false;
    protected static boolean generateDependencyGraphEltSelected = false;
    protected static boolean generateDependencyGraphEltSelectedNoID = false;
    protected static int reachabilitySelected = REACHABILITY_NONE;
    protected static int livenessSelected = LIVENESS_NONE;
    protected static boolean safetySelected = false;
    protected static boolean checkNoDeadSelected = false;
    protected static boolean checkReinitSelected = false;
    protected static boolean checkActionLoopSelected = false;
    protected static boolean limitStatesSelected = false;
    protected static boolean generateCountertraceSelected = false;
    protected static boolean generateCountertraceAUTSelected = false;
    protected static String countertracePath;
    protected static String stateLimitValue;
    protected static boolean limitTimeSelected = false;
    protected static String timeLimitValue;
    protected static int wordRepresentationSelected = 1;
    protected static int searchTypeSelected = 0;
    protected static String maxNbOfThreads = "";

    protected MainGUI mgui;

    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    private int mode;

    protected final static int NO_GRAPH = 1;
    protected final static int GRAPH_OK = 2;

    private int graphMode;
    private String graphAUT;

    private AvatarSpecification spec;

    private avatartranslator.modelchecker.AvatarModelChecker amc;
    private ModelCheckerMonitor mcm;
    private Date startDate, endDate;
    private Date previousDate;
    private int previousNbOfStates;

    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    protected JButton show;
    protected JButton display;
    protected JComboBox wordRepresentationBox;
    protected JComboBox searchTypeBox;

    //protected JRadioButton exe, exeint;
    //protected ButtonGroup exegroup;
    //protected JLabel gen, comp;
    //protected JTextField code1, code2, unitcycle, compiler1, exe1, exe2, exe3, exe2int, loopLimit;

    protected JRadioButton noReachability, reachabilityCheckable, reachabilityAllStates;
    protected ButtonGroup reachabilities;
    protected JRadioButton noLiveness, livenessCheckable, livenessAllStates;
    protected ButtonGroup liveness;
    protected boolean showLiveness;
    protected JCheckBox stateLimit;
    protected JTextField stateLimitField;
    protected JCheckBox timeLimit;
    protected JTextField timeLimitField;
    protected JCheckBox noDeadlocks;
    protected JCheckBox reinit;
    protected JCheckBox actionLoop;
    protected JCheckBox safety;
    protected JCheckBox countertrace;
    protected JCheckBox countertraceAUT;
    protected JTextField countertraceField;
    protected JButton checkUncheckAllPragmas;
    protected java.util.List<JCheckBox> customChecks;
    protected JTextField maxNbOfThreadsText;


    protected JCheckBox saveGraphAUT, saveGraphDot, advancedInfoDeadlockBox, ignoreEmptyTransitions, ignoreInternalStates,
            ignoreConcurrenceBetweenInternalActions, generateDesign, generateDependencyGraph, generateDependencyGraphNoID,
            generateCompactDependencyGraph, generateCompactDependencyGraphNoID,
            generateDependencyGraphElt, generateDependencyGraphEltNOID;
    protected JButton graphDirButton, graphPathDotButton;
    protected JTextField graphPath, graphPathDot;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;

    // Information
    protected JLabel nbOfStates, nbOfLinks, nbOfPendingStates, elapsedTime, nbOfStatesPerSecond, nbOfDeadlocks,
            nbOfReachabilities, nbOfReachabilitiesNotFound, info;


    private Thread t;
    private boolean go = false;
    //  private boolean hasError = false;
    private java.util.Timer timer;
    //protected boolean startProcess = false;
    protected Map<String, Integer> verifMap;

    private HashSet<TGComponent> hasDependencyGraph;
    private HashMap<AvatarBlock, HashSet<AvatarStateElement>> mapOfDeadlockStates;

    // Help
    //protected JPopupMenu helpPopup;
    protected JMenu help;
    protected JMenuBar menuBar;


    /*
     * Creates new form
     */
    public JDialogAvatarModelChecker(Frame f, MainGUI _mgui, String title, AvatarSpecification _spec, String _graphDir,
                                     boolean _showLiveness) {
        super(title);

        mgui = _mgui;
        spec = _spec;

        if (graphDir == null) {
            graphDir = _graphDir + File.separator + RG_NAME;
        }
        if (graphDirDot == null) {
            graphDirDot = _graphDir + File.separator + RG_NAME;
        }
        
        countertracePath = "trace$.txt";
        stateLimitValue = "100";
        timeLimitValue =  "5000";

        //showLiveness = _showLiveness;
        showLiveness = true;
        customChecks = new LinkedList<JCheckBox>();
        
        initComponents();
        myInitComponents();
        pack();
        
        verifMap = new HashMap<String, Integer>();

        hasDependencyGraph = new HashSet<>();

	/*if ((mgui != null) && (spec != null)) {
        mgui.drawAvatarSpecification(spec);
	    }*/

        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    protected void myInitComponents() {
        mode = NOT_STARTED;
        graphMode = NO_GRAPH;
        setButtons();
    }

    @SuppressWarnings("unchecked")
    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());


        menuBar = new JMenuBar();
        menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        help = new JMenu("?");
        menuBar.add(help);
        setJMenuBar(menuBar);

        help.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                help();
            }
        });


        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Issue #41 Ordering of tabbed panes 
        jp1 = GraphicLib.createTabbedPane();//new JTabbedPane();

        JPanel jpopt = new JPanel();
        GridBagLayout gridbagopt = new GridBagLayout();
        GridBagConstraints copt = new GridBagConstraints();
        jpopt.setLayout(gridbagopt);
        jpopt.setBorder(new javax.swing.border.TitledBorder("Options"));

        copt.gridwidth = 1;
        copt.gridheight = 1;
        copt.weighty = 1.0;
        copt.weightx = 1.0;
        copt.fill = GridBagConstraints.HORIZONTAL;
        copt.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Graph generation options"));

        c01.gridwidth = 1;
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.fill = GridBagConstraints.HORIZONTAL;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row

        if (TraceManager.devPolicy == TraceManager.TO_CONSOLE) {
            generateDesign = new JCheckBox("[For testing purpose only] Generate Design", generateDesignSelected);
            generateDesign.addActionListener(this);
            jp01.add(generateDesign, c01);
        }

        if (TraceManager.devPolicy == TraceManager.TO_CONSOLE) {
            generateDependencyGraph = new JCheckBox("[For testing purpose only] Generate dependency graph", generateDependencyGraphSelected);
            generateDependencyGraph.addActionListener(this);
            jp01.add(generateDependencyGraph, c01);

            generateCompactDependencyGraph = new JCheckBox("[For testing purpose only] Generate compact dependency graph",
                    generateCompactDependencyGraphSelected);
            generateCompactDependencyGraph.addActionListener(this);
            jp01.add(generateCompactDependencyGraph, c01);
        }

        if (TraceManager.devPolicy == TraceManager.TO_CONSOLE) {
            generateDependencyGraphNoID = new JCheckBox("[For testing purpose only] Generate dependency graph with no ID",
                    generateDependencyGraphSelectedNoID);
            generateDependencyGraphNoID.addActionListener(this);
            jp01.add(generateDependencyGraphNoID, c01);

            generateCompactDependencyGraphNoID = new JCheckBox("[For testing purpose only] Generate compact dependency graph with no ID",
                    generateCompactDependencyGraphSelectedNoID);
            generateCompactDependencyGraphNoID.addActionListener(this);
            jp01.add(generateCompactDependencyGraphNoID, c01);
        }

        if (TraceManager.devPolicy == TraceManager.TO_CONSOLE) {
            generateDependencyGraphElt = new JCheckBox("[For testing purpose only] Generate dependency graph for Reachability / liveness",
                    generateDependencyGraphEltSelected);
            generateDependencyGraphElt.addActionListener(this);
            jp01.add(generateDependencyGraphElt, c01);
        }

        if (TraceManager.devPolicy == TraceManager.TO_CONSOLE) {
            generateDependencyGraphEltNOID = new JCheckBox("[For testing purpose only] Generate dependency graph for Reachability / liveness (No ID)",
                    generateDependencyGraphEltSelectedNoID);
            generateDependencyGraphEltNOID.addActionListener(this);
            jp01.add(generateDependencyGraphEltNOID, c01);
        }

        c01.gridwidth = 1;

        ignoreEmptyTransitions = new JCheckBox("Do not display empty transitions as internal actions", ignoreEmptyTransitionsSelected);
        ignoreEmptyTransitions.addActionListener(this);
        jp01.add(ignoreEmptyTransitions, c01);
        
        c01.anchor = GridBagConstraints.EAST;
        c01.fill = GridBagConstraints.NONE;
        jp01.add(new JLabel("Search type: "), c01);
        
        c01.anchor = GridBagConstraints.WEST;
        c01.fill = GridBagConstraints.HORIZONTAL;
        searchTypeBox = new JComboBox(SEARCH_TYPE);
        searchTypeBox.setSelectedIndex(searchTypeSelected);
        searchTypeBox.addActionListener(this);
        jp01.add(searchTypeBox, c01);
        
        c01.anchor = GridBagConstraints.EAST;
        c01.fill = GridBagConstraints.NONE;
        jp01.add(new JLabel("Word size: "), c01);
        
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.anchor = GridBagConstraints.WEST;
        c01.fill = GridBagConstraints.HORIZONTAL;

        wordRepresentationBox = new JComboBox(WORD_BITS);
        wordRepresentationBox.setSelectedIndex(wordRepresentationSelected);
        wordRepresentationBox.addActionListener(this);
        jp01.add(wordRepresentationBox, c01);

        //TraceManager.addDev("hello");

        c01.gridwidth = 1;
        jp01.add(new JLabel("Max nb of threads:"), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER;
        maxNbOfThreadsText = new JTextField(maxNbOfThreads);
        jp01.add(maxNbOfThreadsText, c01);


        ignoreConcurrenceBetweenInternalActions = new JCheckBox("Ignore concurrency between internal actions", ignoreConcurrenceBetweenInternalActionsSelected);
        ignoreConcurrenceBetweenInternalActions.addActionListener(this);
        jp01.add(ignoreConcurrenceBetweenInternalActions, c01);

        ignoreInternalStates = new JCheckBox("Ignore states between internal actions", ignoreInternalStatesSelected);
        ignoreInternalStates.addActionListener(this);
        jp01.add(ignoreInternalStates, c01);
        

        //Limitations
        c01.gridwidth = 1;
        stateLimit = new JCheckBox("Limit number of states in RG:", limitStatesSelected);
        stateLimit.addActionListener(this);
        jp01.add(stateLimit, c01);
        c01.gridwidth = GridBagConstraints.REMAINDER;
        stateLimitField = new JTextField(stateLimitValue);
        jp01.add(stateLimitField, c01);
        c01.gridwidth = 1;
        timeLimit = new JCheckBox("Time constraint for RG generation (ms):", limitTimeSelected);
        timeLimit.addActionListener(this);
        jp01.add(timeLimit, c01);
        c01.gridwidth = GridBagConstraints.REMAINDER;
        timeLimitField = new JTextField(timeLimitValue);
        jp01.add(timeLimitField, c01);



        JPanel jpbasic = new JPanel();
        GridBagLayout gridbagbasic = new GridBagLayout();
        GridBagConstraints cbasic = new GridBagConstraints();
        jpbasic.setLayout(gridbagbasic);
        jpbasic.setBorder(new javax.swing.border.TitledBorder("Basic properties"));

        cbasic.gridwidth = 1;
        cbasic.gridheight = 1;
        cbasic.weighty = 1.0;
        cbasic.weightx = 1.0;
        cbasic.fill = GridBagConstraints.HORIZONTAL;
        cbasic.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        
        // Deadlocks
        cbasic.gridwidth = 1;
        noDeadlocks = new JCheckBox("No deadlocks?", checkNoDeadSelected);
        noDeadlocks.addActionListener(this);
        jpbasic.add(noDeadlocks, cbasic);
        
        
        // Reinit
        reinit = new JCheckBox("Reinitialization?", checkReinitSelected);
        reinit.addActionListener(this);
        jpbasic.add(reinit, cbasic);
        
        // Internal action loop
        cbasic.gridwidth = GridBagConstraints.REMAINDER;
        actionLoop = new JCheckBox("No internal action loops?", checkActionLoopSelected);
        actionLoop.addActionListener(this);
        jpbasic.add(actionLoop, cbasic);

        
        // Reachability
        cbasic.gridwidth = 1;
        jpbasic.add(new JLabel("Reachability:"), cbasic);
        reachabilities = new ButtonGroup();

        noReachability = new JRadioButton("None");
        noReachability.addActionListener(this);
        jpbasic.add(noReachability, cbasic);
        reachabilities.add(noReachability);

        reachabilityCheckable = new JRadioButton("Selected states");
        reachabilityCheckable.addActionListener(this);
        jpbasic.add(reachabilityCheckable, cbasic);
        reachabilities.add(reachabilityCheckable);

        cbasic.gridwidth = GridBagConstraints.REMAINDER;
        reachabilityAllStates = new JRadioButton("All states");
        reachabilityAllStates.addActionListener(this);
        jpbasic.add(reachabilityAllStates, cbasic);
        reachabilities.add(reachabilityAllStates);
        
        noReachability.setSelected(reachabilitySelected == REACHABILITY_NONE);
        reachabilityCheckable.setSelected(reachabilitySelected == REACHABILITY_SELECTED);
        reachabilityAllStates.setSelected(reachabilitySelected == REACHABILITY_ALL);
        
        
        // Liveness
        cbasic.gridwidth = 1;
        jpbasic.add(new JLabel("Liveness:"), cbasic);
        liveness = new ButtonGroup();

        noLiveness = new JRadioButton("None");
        noLiveness.addActionListener(this);
        if (showLiveness) {
            jpbasic.add(noLiveness, cbasic);
        }
        liveness.add(noLiveness);

        livenessCheckable = new JRadioButton("Selected states");
        livenessCheckable.addActionListener(this);
        if (showLiveness) {
            jpbasic.add(livenessCheckable, cbasic);
        }
        liveness.add(livenessCheckable);

        cbasic.gridwidth = GridBagConstraints.REMAINDER;
        livenessAllStates = new JRadioButton("All states");
        livenessAllStates.addActionListener(this);
        if (showLiveness) {
            jpbasic.add(livenessAllStates, cbasic);
        }
        liveness.add(livenessAllStates);

        noLiveness.setSelected(livenessSelected == LIVENESS_NONE);
        livenessCheckable.setSelected(livenessSelected == LIVENESS_SELECTED);
        livenessAllStates.setSelected(livenessSelected == LIVENESS_ALL);
        
        
        JPanel jpadvanced = new JPanel();
        GridBagLayout gridbagadvanced = new GridBagLayout();
        GridBagConstraints cadvanced = new GridBagConstraints();
        jpadvanced.setLayout(gridbagadvanced);
        jpadvanced.setBorder(new javax.swing.border.TitledBorder("Advanced properties"));
        
        cadvanced.anchor = GridBagConstraints.WEST;
        cadvanced.gridwidth = 1;
        cadvanced.gridheight = 1;
        cadvanced.weighty = 1.0;
        cadvanced.weightx = 1.0;
        cadvanced.fill = GridBagConstraints.HORIZONTAL; 
        cadvanced.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        //Safety pragmas
        cadvanced.gridwidth = 1;
        safety = new JCheckBox("Safety pragmas", safetySelected);
        safety.addActionListener(this);
        jpadvanced.add(safety, cadvanced);
        
        cadvanced.gridwidth = GridBagConstraints.REMAINDER;
        checkUncheckAllPragmas = new JButton("Check / uncheck all");
        checkUncheckAllPragmas.addActionListener(this);
        
        if (spec.getSafetyPragmas() == null || spec.getSafetyPragmas().isEmpty()) {
            safety.setEnabled(false);
            safety.setSelected(false);
            //checkUncheckAllPragmas.setEnabled(false);
        } else {
            cadvanced.anchor = GridBagConstraints.EAST;
            cadvanced.fill = GridBagConstraints.NONE; 
            cadvanced.gridwidth = GridBagConstraints.REMAINDER; //end row
            jpadvanced.add(checkUncheckAllPragmas, cadvanced);
            cadvanced.gridwidth = GridBagConstraints.REMAINDER;

            JPanel jpadvancedQ = new JPanel();
            GridBagConstraints cadvancedQ = new GridBagConstraints();
            GridBagLayout gridbagadvancedQ = new GridBagLayout();
            cadvancedQ.anchor = GridBagConstraints.WEST;
            cadvancedQ.gridheight = 1;
            cadvancedQ.weighty = 1.0;
            cadvancedQ.weightx = 1.0;
            jpadvancedQ.setLayout(gridbagadvancedQ);
            cadvancedQ.fill = GridBagConstraints.BOTH;


            for (String s : spec.getSafetyPragmas()) {
                cadvancedQ.gridwidth = GridBagConstraints.RELATIVE;
                JLabel space = new JLabel("   ");
                cadvancedQ.weightx = 0.0;
                jpadvancedQ.add(space, cadvancedQ);
                cadvancedQ.gridwidth = GridBagConstraints.REMAINDER; //end row
                JCheckBox cqb = new JCheckBox(s);
                cqb.addActionListener(this);
                cadvancedQ.weightx = 1.0;
                cqb.setSelected(true);
                jpadvancedQ.add(cqb, cadvancedQ);
                customChecks.add(cqb);
            }

            JScrollPane jsp = new JScrollPane(jpadvancedQ, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            jsp.setPreferredSize(new Dimension(500, 120));
            cadvanced.gridheight = 10;
            cadvanced.anchor = GridBagConstraints.WEST;
            cadvanced.fill = GridBagConstraints.HORIZONTAL; 
            cadvanced.gridwidth = GridBagConstraints.REMAINDER; //end row
            jpadvanced.add(jsp, cadvanced);
        }
        cadvanced.gridwidth = GridBagConstraints.REMAINDER;
        
        //Countertrace
        cadvanced.gridwidth = 1;
        countertraceAUT = new JCheckBox("Generate trace (graph format)", generateCountertraceAUTSelected);
        countertraceAUT.addActionListener(this);
        jpadvanced.add(countertraceAUT, cadvanced);
        countertrace = new JCheckBox("Generate trace (TXT format)", generateCountertraceSelected);
        countertrace.addActionListener(this);
        jpadvanced.add(countertrace, cadvanced);
        cadvanced.gridwidth = GridBagConstraints.REMAINDER;
        countertraceField = new JTextField(countertracePath);
        jpadvanced.add(countertraceField, cadvanced);


        jpopt.add(jp01, c01);
        jpopt.add(jpbasic, cbasic);
        jpopt.add(jpadvanced, cadvanced);
        
        
        // RG
        copt.gridwidth = GridBagConstraints.REMAINDER; //end row
        saveGraphAUT = new JCheckBox("Reachability Graph Generation", graphSelected);
        saveGraphAUT.addActionListener(this);
        //saveGraphAUT.addSelectionListener(this);
        jpopt.add(saveGraphAUT, copt);
        copt.gridwidth = 1;
        graphPath = new JTextField(graphDir);
        jpopt.add(graphPath, copt);
        copt.gridwidth = GridBagConstraints.REMAINDER; //end row
        graphDirButton = new JButton("Select another directory", IconManager.imgic22);
        graphDirButton.addActionListener(this);
        jpopt.add(graphDirButton, copt);

        advancedInfoDeadlockBox = new JCheckBox("Backtrace deadlocks to model", advancedInfoDeadlock);
        jpopt.add(advancedInfoDeadlockBox, copt);

        saveGraphDot = new JCheckBox("Save RG in dotty:", graphSelectedDot);
        saveGraphDot.addActionListener(this);
        jpopt.add(saveGraphDot, copt);
        copt.gridwidth = 1;
        graphPathDot = new JTextField(graphDirDot);
        jpopt.add(graphPathDot, copt);
        copt.gridwidth = GridBagConstraints.REMAINDER; //end row
        graphPathDotButton = new JButton("Select another directory", IconManager.imgic22);
        graphPathDotButton.addActionListener(this);
        jpopt.add(graphPathDotButton, copt);
        
        c.add(jpopt, BorderLayout.NORTH);


        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to start the model checker\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.CENTER);

        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);
        show = new JButton("RG analysis", IconManager.imgic28);
        display = new JButton("Show", IconManager.imgic28);

        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(120, 30));
        show.setPreferredSize(new Dimension(150, 30));
        display.setPreferredSize(new Dimension(100, 30));

        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);
        show.addActionListener(this);
        display.addActionListener(this);

        // Information
        JPanel jplow = new JPanel(new BorderLayout());

        JPanel jpinfo = new JPanel();
        GridBagLayout gridbag02 = new GridBagLayout();
        GridBagConstraints c02 = new GridBagConstraints();
        jpinfo.setLayout(gridbag02);
        jpinfo.setBorder(new javax.swing.border.TitledBorder("Graph information"));
        jplow.add(jpinfo, BorderLayout.NORTH);
        c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.fill = GridBagConstraints.HORIZONTAL;
        //c02.gridwidth = 1;
        //jpinfo.add(new JLabel(""), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        info = new JLabel();
        info.setFont(new Font("Serif", Font.BOLD, 16));
        updateInfo();
        jpinfo.add(info, c02);
        jpinfo.add(new JLabel(" "), c02);
        // nb of states, nb of links, nb of pending states, elapsed time, nbOfStatesPerSeconds


        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Nb of states:"), c02);
        //c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfStates = new JLabel("-");
        jpinfo.add(nbOfStates, c02);


        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Nb of transitions:"), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfLinks = new JLabel("-");
        jpinfo.add(nbOfLinks, c02);

        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Reachability found:"), c02);
        //c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfReachabilities = new JLabel("-");
        jpinfo.add(nbOfReachabilities, c02);

        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Reachability not found:"), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfReachabilitiesNotFound = new JLabel("-");
        jpinfo.add(nbOfReachabilitiesNotFound, c02);


        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Nb of deadlock states:"), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfDeadlocks = new JLabel("-");
        jpinfo.add(nbOfDeadlocks, c02);

        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Nb of pending states:"), c02);
        //c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfPendingStates = new JLabel("-");
        jpinfo.add(nbOfPendingStates, c02);

        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Nb of states/seconds:"), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfStatesPerSecond = new JLabel("-");
        jpinfo.add(nbOfStatesPerSecond, c02);


        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Elapsed timed:"), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        elapsedTime = new JLabel("-");
        jpinfo.add(elapsedTime, c02);


        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);
        jp2.add(show);
        jp2.add(display);
        jplow.add(jp2, BorderLayout.SOUTH);

        c.add(jplow, BorderLayout.SOUTH);

    }


    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        if (command.equals("Start")) {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        } else if (evt.getSource() == checkUncheckAllPragmas) {
            checkUncheckAllPragmas();
        } else if (evt.getSource() == show) {
            showGraph();
        } else if (evt.getSource() == display) {
            displayGraph();
        } else if (evt.getSource() == saveGraphAUT) {
            setButtons();
        } else if (evt.getSource() == saveGraphDot) {
            setButtons();
        } else if (evt.getSource() == ignoreEmptyTransitions) {
            setButtons();
        } else if (evt.getSource() == ignoreConcurrenceBetweenInternalActions) {
            setButtons();
        } else if (evt.getSource() == ignoreInternalStates) {
            setButtons();
        } else if (evt.getSource() == graphDirButton) {
            selectDirectoryDir(graphPath);
        } else if (evt.getSource() == graphPathDotButton) {
            selectDirectoryDir(graphPathDot);
        } else {
            setButtons();
        }
    }

    public void selectDirectoryDir(JTextField field) {
        JFileChooser jfc = new JFileChooser(FilenameUtils.getPath(field.getText()));
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);

        int returnVal = jfc.showDialog(this, "Select directory");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File directory = jfc.getSelectedFile();
        field.setText(directory + File.separator + RG_NAME);
    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
        maxNbOfThreads = maxNbOfThreadsText.getText();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public void showGraph() {
        if (graphAUT != null) {
            mgui.showAUTFromString("Last RG", graphAUT);
        }
    }

    public void displayGraph() {
        // Make this in a different thread
        // And authorize the show only for a small nb of states ...

        if (graphAUT != null) {
            AUTGraph rg = new AUTGraph();
            rg.buildGraph(graphAUT);
            rg.display(!MainGUI.experimentalOn);
        }
    }

    public synchronized void stopProcess() {
        if (amc != null) {
            amc.stopModelChecking();
        }
        mode = STOPPED;
        setButtons();
        go = false;
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        updateValues();

    }

    public synchronized void startProcess() {
        if (mode == STARTED) {
            return;
        }
        t = new Thread(this);
        mode = STARTED;
        graphMode = NO_GRAPH;
        setButtons();
        go = true;
        t.start();
    }

    private void testGo() throws InterruptedException {
        if (go == false) {
            TraceManager.addDev("Interrupted by user");
            throw new InterruptedException("Stopped by user");
        }
    }

    public void run() {
        //       String cmd;
        //     String list, data;
        //   int cycle = 0;

        //   hasError = false;

        int nbOfThreads = Integer.MAX_VALUE;
        try {
            nbOfThreads = Integer.decode(maxNbOfThreadsText.getText());
        }  catch(Exception e) {

        }

        TraceManager.addDev("Model checker started");
        long timeBeg = System.currentTimeMillis();

        //   File testFile;
        try {
            reinitValues();
            jta.append("Starting the model checker\n");

            amc = new AvatarModelChecker(spec);

            if (nbOfThreads < Integer.MAX_VALUE) {
                TraceManager.addDev("Setting the nb of thrads to:" + nbOfThreads);
                amc.setMaxNbOfThreads(nbOfThreads);
            }

            if (generateDesignSelected) {
                TraceManager.addDev("Drawing non modified avatar spec");
                if ((mgui != null) && (spec != null)) {
                    mgui.drawAvatarSpecification(amc.getInitialSpec());
                }
            }

            if (generateDependencyGraphSelected) {
                TraceManager.addDev("Generating dependency graph");
                AvatarDependencyGraph adg = spec.makeDependencyGraph();
                RG rg = new RG("Dependency Graph");
                rg.graph = adg.getGraph();
                rg.nbOfStates = rg.graph.getNbOfStates();
                rg.nbOfTransitions = rg.graph.getNbOfTransitions();
                mgui.addRG(rg);
            }

            if (generateCompactDependencyGraphSelected) {
                TraceManager.addDev("Generating compact dependency graph");
                AvatarCompactDependencyGraph adg = spec.makeCompactDependencyGraph();
                RG rg = new RG("Compact Dependency Graph");
                rg.graph = adg.getGraph();
                rg.graph.referenceObject = adg;
                rg.nbOfStates = rg.graph.getNbOfStates();
                rg.nbOfTransitions = rg.graph.getNbOfTransitions();
                mgui.addRG(rg);
            }

            if (generateDependencyGraphSelectedNoID) {
                TraceManager.addDev("Generating dependency graph with no ID");
                AvatarDependencyGraph adg = spec.makeDependencyGraph(false);
                RG rg = new RG("Dependency Graph no ID");
                rg.graph = adg.getGraph();
                rg.nbOfStates = rg.graph.getNbOfStates();
                rg.nbOfTransitions = rg.graph.getNbOfTransitions();
                mgui.addRG(rg);
            }

            if (generateCompactDependencyGraphSelectedNoID) {
                TraceManager.addDev("Generating compacr dependency graph with no ID");
                AvatarCompactDependencyGraph adg = spec.makeCompactDependencyGraph(false);
                RG rg = new RG("Compact Dependency Graph no ID");
                rg.graph = adg.getGraph();
                rg.nbOfStates = rg.graph.getNbOfStates();
                rg.nbOfTransitions = rg.graph.getNbOfTransitions();
                mgui.addRG(rg);
            }

            endDate = null;
            previousNbOfStates = 0;
            startDate = new Date();
            mcm = new ModelCheckerMonitor(this);
            timer = new java.util.Timer(true);
            timer.scheduleAtFixedRate(mcm, 0, 500);

            // Setting options
            amc.setCompressionFactor(wordRepresentationSelected << 1);
            amc.setSearchType(searchTypeSelected);
            amc.setIgnoreEmptyTransitions(ignoreEmptyTransitionsSelected);
            amc.setIgnoreConcurrenceBetweenInternalActions(ignoreConcurrenceBetweenInternalActionsSelected);
            amc.setIgnoreInternalStates(ignoreInternalStatesSelected);
            amc.setCheckNoDeadlocks(checkNoDeadSelected);
            amc.setReinitAnalysis(checkReinitSelected);
            amc.setInternalActionLoopAnalysis(checkActionLoopSelected);
            amc.setCounterExampleTrace(generateCountertraceSelected, generateCountertraceAUTSelected);

            // Reachability
            int res;
            if (reachabilitySelected == REACHABILITY_SELECTED) {
                mgui.resetReachability();
                res = amc.setReachabilityOfSelected();
                jta.append("Reachability of " + res + " selected elements activated\n");

                for (SpecificationReachability sr : amc.getReachabilities()) {
                    handleReachability(sr.ref1, sr.result);
                    if (sr.ref2 != sr.ref1) {
                        handleReachability(sr.ref2, sr.result);
                    }
                }
            }

            if (reachabilitySelected == REACHABILITY_ALL) {
                mgui.resetReachability();
                res = amc.setReachabilityOfAllStates();
                jta.append("Reachability of " + res + " states activated\n");
                for (SpecificationReachability sr : amc.getReachabilities()) {
                    handleReachability(sr.ref1, sr.result);
                    if (sr.ref2 != sr.ref1) {
                        handleReachability(sr.ref2, sr.result);
                    }
                }
            }

            // RG?
            if (graphSelected || graphSelectedDot) {
                amc.setComputeRG(true);
                jta.append("Computation of Reachability Graph activated\n");
            }
            
            if (livenessSelected == LIVENESS_SELECTED) {
                mgui.resetLiveness();
                res = amc.setLivenessOfSelected();
                jta.append("Liveness of " + res + " states activated\n");
                
                for (SafetyProperty sp : amc.getLivenesses()) {
                    handleLiveness(sp.getState(), sp.getPhase());
                }
            }
            
            if (livenessSelected == LIVENESS_ALL) {
                mgui.resetLiveness();
                res = amc.setLivenessOfAllStates();
                jta.append("Liveness of " + res + " selected elements activated\n");
                
                for (SafetyProperty sp : amc.getLivenesses()) {
                    handleLiveness(sp.getState(), sp.getPhase());
                }
            }
            
            if (safetySelected) {
                //res = amc.setSafetyAnalysis();
                res = 0;

                for(JCheckBox cc: customChecks) {
                    if (cc.isSelected()) {
                        if (amc.addSafety(cc.getText(), spec.getSafetyPragmasRefs().get(cc.getText()))) {
                            res++;
                        }
                    }
                }
                jta.append("Analysis of " + res + " safety pragmas activated\n");
                
                handleSafety(amc.getSafeties());
            }
            
            // Limitations
            if (stateLimit.isSelected()) {
            	amc.setStateLimit(true);
				try{
					Long stateLimitLong = Long.parseLong(stateLimitField.getText());
					if (stateLimitLong <= 0) {
						jta.append("State Limit field is not valid, insert a positive number\n");
						go = false;
					}
					amc.setStateLimitValue(stateLimitLong.longValue());
				} catch (NumberFormatException e) {
					jta.append("State Limit field is not valid\n");
					go = false;
				}
            }
            if (timeLimit.isSelected()) {
                amc.setTimeLimit(true);
                try{
                    Long timeLimitLong = Long.parseLong(timeLimitField.getText());
                    if (timeLimitLong <= 0) {
                        jta.append("State Limit field is not valid, insert a positive number\n");
                        go = false;
                    }
                    amc.setTimeLimitValue(timeLimitLong.longValue());
                } catch (NumberFormatException e) {
                    jta.append("Time Limit field is not valid\n");
                    go = false;
                }
            }

            if (advancedInfoDeadlockBox.isSelected()) {
                amc.setFreeIntermediateStateCoding(false);
            }

            // Starting model checking
            testGo();

            if (livenessSelected == LIVENESS_NONE && safetySelected == false && checkNoDeadSelected == false && checkActionLoopSelected == false) {
                amc.startModelChecking();
            } else {
                amc.startModelCheckingProperties();
            }
            
            TraceManager.addDev("Model checking done");

            if (generateDesignSelected) {
                TraceManager.addDev("Drawing modified avatar spec");
                if ((mgui != null) && (spec != null)) {
                    mgui.drawAvatarSpecification(amc.getReworkedAvatarSpecification());
                }
            }



            //TraceManager.addDev("RG:" + amc.statesToString() + "\n\n");

            /*if (generateDesignSelected) {
                TraceManager.addDev("Drawing modified avatar spec");
                AvatarSpecification reworkedSpec = amc.getReworkedAvatarSpecification();
                if ((mgui != null) && (reworkedSpec != null)) {
                    mgui.drawAvatarSpecification(reworkedSpec);
                }
            }*/

            timer.cancel();
            endDate = new Date();
            updateValues();
            jta.append("\n\nModel checking done\n");
            jta.append("Nb of states:" + amc.getNbOfStates() + "\n");
            jta.append("Nb of links:" + amc.getNbOfLinks() + "\n");
            
            if (checkNoDeadSelected) {
                jta.append("\nNo deadlocks?\n" + "-> " + amc.deadlockToString() + "\n");
            }
            
            if (checkReinitSelected) {
                jta.append("\nReinitialization?\n" + "-> " + amc.reinitToString() + "\n");
            }
            
            if (checkActionLoopSelected) {
                boolean result = amc.getInternalActionLoopsResult();
                String s;
                if (result) {
                    s = "property is satisfied";
                } else {
                    s = "property is NOT satisfied";
                }            
                jta.append("\nNo internal action loops?\n" + "-> " + s + "\n");
                if (!result) {
                    ArrayList<SpecificationActionLoop> al = amc.getInternalActionLoops();
                    jta.append("Internal action loops:\n");
                    for (SpecificationActionLoop sal : al) {
                        if (sal.getResult()) {
                            jta.append(sal.toString());
                        }
                    }
                    jta.append("\n");
                }
            }

            if ((reachabilitySelected == REACHABILITY_SELECTED) || (reachabilitySelected == REACHABILITY_ALL)) {
                jta.append("\nReachabilities found:\n");
                jta.append(amc.reachabilityToString());

                // Back annotation on diagrams
                for (SpecificationReachability sr : amc.getReachabilities()) {
                    //TraceManager.addDev("Handing reachability of " + sr);
                    handleReachability(sr.ref1, sr.result);
                    if (sr.ref2 != sr.ref1) {
                        handleReachability(sr.ref2, sr.result);
                    }
                }
            }
            
            if (livenessSelected != LIVENESS_NONE) {
                jta.append("\nLiveness Analysis:\n");
                jta.append(amc.livenessToString());
                
                for (SafetyProperty sp : amc.getLivenesses()) {
                    handleLiveness(sp.getState(), sp.getPhase());
                }
            }
            
            if (safetySelected) {
                jta.append("\nSafety Analysis:\n");
                jta.append(amc.safetyToString());
                handleSafety(amc.getSafeties());
            }

            //TraceManager.addDev(amc.toString());
            //TraceManager.addDev(amc.toString());
            DateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
            Date date = new Date();
            String dateAndTime = dateFormat.format(date);
            
            if (generateCountertraceSelected || generateCountertraceAUTSelected) {
                
                String file;
                if (countertraceField.getText().indexOf("$") != -1) {
                    file = Conversion.replaceAllChar(countertraceField.getText(), '$', dateAndTime);
                } else {
                    file = countertraceField.getText();
                }
                if (generateCountertraceSelected) {
                    String trace = amc.getCounterTrace();
                    try {
                        File f = new File(file);
                        FileUtils.saveFile(file, trace);
                        jta.append("\nCounterexample trace saved in " + file + "\n");
                    } catch (Exception e) {
                        jta.append("\nCounterexample trace could not be saved in " + file + "\n");
                    }
                }
                if (generateCountertraceAUTSelected) {
                    List<CounterexampleQueryReport> autTraces = amc.getAUTTraces();
                    if (autTraces != null) {
                        int i = 0;
                        String autfile = FileUtils.removeFileExtension(file);
                        for (CounterexampleQueryReport tr : autTraces) {
                            String filename = autfile + "_" + i + ".aut";
                            try {
                                RG rg = new RG(file);
                                rg.data = tr.getReport();
                                rg.fileName = filename;
                                rg.nbOfStates = tr.getNbOfStates();
                                rg.nbOfTransitions = tr.getNbOfTransitions();
                                rg.name = tr.getQuery() + "_" + dateAndTime;
                                mgui.addRG(rg);
                                //File f = new File(filename);
                                FileUtils.saveFile(filename, tr.getReport());
                                jta.append("Counterexample graph trace " + tr.getQuery() + " saved in " + filename + "\n");
                            } catch (Exception e) {
                                jta.append("Counterexample graph trace "+ tr.getQuery() + " could not be saved in " + filename + "\n");
                            }
                            i++;
                        }
                    }
                }
            }

            long timeEnd = System.currentTimeMillis();
            TraceManager.addDev("Overall time: " + (timeEnd - timeBeg) + " ms");

            if (saveGraphAUT.isSelected()) {
                boolean success = false;
                try {
                    graphAUT = amc.toAUT();
                    success = true;
                } catch (OutOfMemoryError e) {
                    jta.append("Out of memory: graph is **too** large to be saved in AUT format\n");
                }

                if (success) {
                    graphMode = GRAPH_OK;
                    //TraceManager.addDev("graph AUT=\n" + graph);

                    String autfile;
                    if (graphPath.getText().indexOf("$") != -1) {
                        autfile = Conversion.replaceAllChar(graphPath.getText(), '$', dateAndTime);
                    } else {
                        autfile = graphPath.getText();
                    }

                    try {
                        RG rg = new RG(autfile);
                        rg.data = graphAUT;
                        rg.fileName = autfile;
                        rg.nbOfStates = amc.getNbOfStates();
                        rg.nbOfTransitions = amc.getNbOfLinks();
                        File f = new File(autfile);
                        rg.name = f.getName();
                        mgui.addRG(rg);
                        FileUtils.saveFile(autfile, graphAUT);
                        jta.append("\nGraph saved in " + autfile + "\n");
                    } catch (Exception e) {
                        jta.append("\nGraph could not be saved in " + autfile + "\n");
                    }
                }
            }

            if (advancedInfoDeadlockBox.isSelected()) {
                TraceManager.addDev("Deadlocks:");
                boolean success = false;
                HashMap<AvatarBlock, HashSet<AvatarStateElement>> mapOfDeadlockStates = new HashMap<>();
                try {
                    mapOfDeadlockStates = amc.getMapOfDeadlockStates();
                    success = true;
                    TraceManager.addDev("Got the map");
                } catch (OutOfMemoryError e) {
                    jta.append("Out of memory: computation of deadlock failed\n");
                }

                if (success) {

                    // Print them
                    // Identify all blocks and their states, and annotate them
                    for(AvatarBlock ab: mapOfDeadlockStates.keySet()) {
                        TraceManager.addDev("Deadlock states for block " + ab.getName() + ":");
                        for(AvatarStateElement ase: mapOfDeadlockStates.get(ab)) {
                            TraceManager.addDev("\t- state \"" + ase.getName() + "\"");
                            if (ase.getReferenceObject() != null) {
                                if (ase.getReferenceObject() instanceof TGComponent) {
                                    TGComponent tgc = (TGComponent) (ase.getReferenceObject());
                                    tgc.addTag("Deadlock (in at least one execution path)");
                                }
                            }
                        }
                    }
                }
            }

            if (saveGraphDot.isSelected()) {
                String dotfile;
                if (graphPathDot.getText().indexOf("$") != -1) {
                    dotfile = Conversion.replaceAllChar(graphPathDot.getText(), '$', dateAndTime);
                } else {
                    dotfile = graphPathDot.getText();
                }
                try {
                    String graph = amc.toDOT();
                    //TraceManager.addDev("graph AUT=\n" + graph);
                    FileUtils.saveFile(dotfile, graph);
                    jta.append("\nGraph saved in " + dotfile + "\n");
                } catch (Exception e) {
                    jta.append("\nGraph could not be saved in " + dotfile + "\n");
                }
            }

        } catch (InterruptedException ie) {
            jta.append("Interrupted\n");
        }
        
        amc = null;



        jta.append("\n\nReady to process next command\n");

        checkMode();
        setButtons();

        //
    }

    protected void handleReachability(Object _o, SpecificationPropertyPhase _res) {
        if (_o instanceof AvatarStateMachineElement) {
            Object o = ((AvatarStateMachineElement) _o).getReferenceObject();
            if (o instanceof TGComponent) {
                TGComponent tgc = (TGComponent) (o);

                handleDependencyGraph(_o, tgc);

                //TraceManager.addDev("Reachability of tgc=" + tgc + " value=" + tgc.getValue() + " class=" + tgc.getClass());
                switch (_res) {
                    case NOTCOMPUTED:
                        tgc.setReachability(TGComponent.ACCESSIBILITY_UNKNOWN);
                        break;
                    case SATISFIED:
                        tgc.setReachability(TGComponent.ACCESSIBILITY_OK);
                        break;
                    case NONSATISFIED:
                        tgc.setReachability(TGComponent.ACCESSIBILITY_KO);
                        tgc.setLiveness(TGComponent.ACCESSIBILITY_KO);
                        break;
                }
                tgc.getTDiagramPanel().repaint();
            }
        }
    }

    protected void handleDependencyGraph(Object _o, TGComponent _tgc) {
        if (hasDependencyGraph.contains(_tgc)) {
            return;
        }
        hasDependencyGraph.add(_tgc);
        if (generateDependencyGraphEltSelected) {
            //TraceManager.addDev("Generating dependency graph for component:" + _tgc.toString());
            AvatarSpecification specNew = spec.advancedClone();
            
            //long time1 = System.currentTimeMillis();
            AvatarDependencyGraph adg = specNew.makeDependencyGraph();
            long time1 = System.currentTimeMillis();
            ArrayList<AvatarElement> elts = new ArrayList<>();
            elts.add((AvatarElement)_o);
            AvatarDependencyGraph clonedG = adg.reduceGraphBefore(elts);


            RG rg = new RG("Dependency Graph of " + _tgc.toString() + "/" + _tgc.getAVATARID() + ".");
            rg.graph = clonedG.getGraph();
            rg.nbOfStates = rg.graph.getNbOfStates();
            rg.nbOfTransitions = rg.graph.getNbOfTransitions();
            mgui.addRG(rg);

            // Computing reduced specification

            specNew.reduceFromDependencyGraph(clonedG);
            long time2 = System.currentTimeMillis();

            TraceManager.addDev("Time to generate reduced spec:" + (time2-time1));

            //TraceManager.addDev("New spec: " + specNew);

            mgui.drawAvatarSpecification(specNew);

            // Add reduced dependency graph

        }

        if (generateDependencyGraphEltSelectedNoID) {
            TraceManager.addDev("Generating dependency graph for component:" + _tgc.toString());
            AvatarSpecification specNew = spec.advancedClone();

            long time1 = System.currentTimeMillis();
            AvatarDependencyGraph adg = specNew.makeDependencyGraph(false);

            ArrayList<AvatarElement> elts = new ArrayList<>();
            elts.add((AvatarElement)_o);
            AvatarDependencyGraph clonedG = adg.reduceGraphBefore(elts);

            //TraceManager.addDev("Adding reduced Dependency graph for " + _tgc.toString());
            RG rg = new RG("Dependency Graph of " + _tgc.toString() + "/" + _tgc.getAVATARID() + ".");
            rg.graph = clonedG.getGraph();
            rg.nbOfStates = rg.graph.getNbOfStates();
            rg.nbOfTransitions = rg.graph.getNbOfTransitions();
            mgui.addRG(rg);

            // Computing reduced specification
            specNew.reduceFromDependencyGraph(clonedG);
            long time2 = System.currentTimeMillis();
            TraceManager.addDev("Time to generate reduced spec:" + (time2-time1));
            mgui.drawAvatarSpecification(specNew);

        }
    }
    
    protected void handleLiveness(Object _o, SpecificationPropertyPhase _res) {
        if (_o instanceof AvatarStateMachineElement) {
            Object o = ((AvatarStateMachineElement) _o).getReferenceObject();
            if (o instanceof TGComponent) {
                TGComponent tgc = (TGComponent) (o);

                handleDependencyGraph(_o, tgc);

                //TraceManager.addDev("Reachability of tgc=" + tgc + " value=" + tgc.getValue() + " class=" + tgc.getClass());
                switch (_res) {
                    case NOTCOMPUTED:
                        tgc.setLiveness(TGComponent.ACCESSIBILITY_UNKNOWN);
                        break;
                    case SATISFIED:
                        tgc.setReachability(TGComponent.ACCESSIBILITY_OK);
                        tgc.setLiveness(TGComponent.ACCESSIBILITY_OK);
                        break;
                    case NONSATISFIED:
                        tgc.setLiveness(TGComponent.ACCESSIBILITY_KO);
                        break;
                }
                tgc.getTDiagramPanel().repaint();
            }
        }
    }
    
    protected void handleSafety(ArrayList<SafetyProperty> safeties) {

        TraceManager.addDev("Handling safety");

        int status;
        
        if (safeties == null) {
            return;
        }

        for (SafetyProperty sp : safeties) {
            if (sp.getPhase() == SpecificationPropertyPhase.SATISFIED) {
                status = AvatarBDSafetyPragma.PROVED_TRUE;
            } else if (sp.getPhase() == SpecificationPropertyPhase.NONSATISFIED) {
                status = AvatarBDSafetyPragma.PROVED_FALSE;
            } else {
                status = AvatarBDSafetyPragma.PROVED_ERROR;
            }
            //TraceManager.addDev("Putting in map: " + sp.getRefProperty() + " with status: " + status);
            verifMap.put(sp.getRefProperty(), status);
        }
        mgui.modelBacktracingUPPAAL(verifMap);
    }

    protected void checkMode() {
        mode = NOT_STARTED;
    }

    protected void setButtons() {
        graphSelected = saveGraphAUT.isSelected();
        graphPath.setEnabled(saveGraphAUT.isSelected());
        graphDirButton.setEnabled(saveGraphAUT.isSelected());

        advancedInfoDeadlock = advancedInfoDeadlockBox.isSelected();
        advancedInfoDeadlockBox.setEnabled(saveGraphAUT.isSelected());

        graphSelectedDot = saveGraphDot.isSelected();
        saveGraphDot.setEnabled(saveGraphAUT.isSelected());
        graphPathDotButton.setEnabled(saveGraphDot.isSelected() && saveGraphAUT.isSelected());
        graphPathDot.setEnabled(saveGraphDot.isSelected() && saveGraphAUT.isSelected());
        if (generateDesign != null) {
            generateDesignSelected = generateDesign.isSelected();
        }
        if (generateDependencyGraph != null) {
            generateDependencyGraphSelected = generateDependencyGraph.isSelected();
        }
        if (generateCompactDependencyGraph != null) {
            generateCompactDependencyGraphSelected = generateCompactDependencyGraph.isSelected();
        }
        if (generateDependencyGraphNoID != null) {
            generateDependencyGraphSelectedNoID = generateDependencyGraphNoID.isSelected();
        }
        if (generateCompactDependencyGraphNoID != null) {
            generateCompactDependencyGraphSelectedNoID = generateCompactDependencyGraphNoID.isSelected();
        }
        if (generateDependencyGraphElt != null) {
            generateDependencyGraphEltSelected = generateDependencyGraphElt.isSelected();
        }
        if (generateDependencyGraphEltNOID != null) {
            generateDependencyGraphEltSelectedNoID = generateDependencyGraphEltNOID.isSelected();
        }
        ignoreEmptyTransitionsSelected = ignoreEmptyTransitions.isSelected();
        ignoreConcurrenceBetweenInternalActionsSelected = ignoreConcurrenceBetweenInternalActions.isSelected();
        ignoreInternalStatesSelected = ignoreInternalStates.isSelected();
        checkNoDeadSelected = noDeadlocks.isSelected();
        checkReinitSelected = reinit.isSelected();
        checkActionLoopSelected = actionLoop.isSelected();
        wordRepresentationSelected = wordRepresentationBox.getSelectedIndex();
        searchTypeSelected = searchTypeBox.getSelectedIndex();

        if (noReachability.isSelected()) {
            reachabilitySelected = REACHABILITY_NONE;
        } else if (reachabilityCheckable.isSelected()) {
            reachabilitySelected = REACHABILITY_SELECTED;
        } else {
            reachabilitySelected = REACHABILITY_ALL;
        }
        
        if (noLiveness.isSelected()) {
            livenessSelected = LIVENESS_NONE;
        } else if (livenessCheckable.isSelected()) {
            livenessSelected = LIVENESS_SELECTED;
        } else {
            livenessSelected = LIVENESS_ALL;
        }
        
        safetySelected = safety.isSelected();
        
        if (!customChecks.isEmpty()) {
            for(JCheckBox cb: customChecks) {
                cb.setEnabled(safetySelected);
            }
        }
        
        countertrace.setEnabled(safety.isSelected() || noDeadlocks.isSelected() || actionLoop.isSelected());
        countertraceAUT.setEnabled(safety.isSelected() || noDeadlocks.isSelected() || actionLoop.isSelected());
        countertraceField.setEnabled(countertrace.isSelected() || countertraceAUT.isSelected());
        generateCountertraceSelected = countertrace.isSelected();
        generateCountertraceAUTSelected = countertraceAUT.isSelected();

        
        stateLimitField.setEnabled(stateLimit.isSelected());
        limitStatesSelected = stateLimit.isSelected();
        timeLimitField.setEnabled(timeLimit.isSelected());
        limitTimeSelected = timeLimit.isSelected();

        switch (mode) {
            case NOT_STARTED:
                if ((reachabilitySelected == REACHABILITY_SELECTED) || (reachabilitySelected == REACHABILITY_ALL) || (livenessSelected == LIVENESS_SELECTED) || (livenessSelected == LIVENESS_ALL) || checkNoDeadSelected || checkReinitSelected || checkActionLoopSelected || graphSelected || graphSelectedDot) {
                    start.setEnabled(true);
                } else {
                    if (safetySelected) {
                        boolean sel = false;
                        for(JCheckBox cb: customChecks) {
                            if (cb.isSelected()) {
                                sel = true;
                                break;
                            }
                        }
                        start.setEnabled(sel);
                    } else {
                        start.setEnabled(false);
                    }
                }
                stop.setEnabled(false);
                close.setEnabled(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                break;
            case STOPPED:
            default:
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
        }

        show.setEnabled(graphMode == GRAPH_OK);
        display.setEnabled(graphMode == GRAPH_OK);
    }

    public boolean hasToContinue() {
        return (go == true);
    }

    public void appendOut(String s) {
        jta.append(s);
    }

    @Override
    public void setError() {
//        hasError = true;
    }

    public void updateValues() {
        //TraceManager.addDev("Updating values...");
        try {
            if (amc != null) {
                int nbOfStatess = amc.getNbOfStates();
                nbOfStates.setText("" + nbOfStatess);
                nbOfLinks.setText("" + amc.getNbOfLinks());

                // Reachability and deadlocks
                int nb = amc.getNbOfReachabilities();
                if (nb == -1) {
                    //nbOfReachabilities.setText("-");
                } else {
                    nbOfReachabilities.setText("" + nb);
                }
                nb = amc.getNbOfRemainingReachabilities();
                nbOfReachabilitiesNotFound.setText("" + nb);


                nbOfDeadlocks.setText("" + amc.getNbOfDeadlocks());


                nbOfPendingStates.setText("" + amc.getNbOfPendingStates());
                Date d;
                previousDate = new Date();
                if (endDate != null) {
                    d = endDate;
                } else {
                    d = previousDate;
                }
                long duration = d.getTime() - startDate.getTime();
                long durationMn = TimeUnit.MILLISECONDS.toMinutes(duration);
                long durationSec = TimeUnit.MILLISECONDS.toSeconds(duration) - durationMn * 60;
                long durationMs = duration - 1000 * (durationSec - durationMn * 60);

                //TraceManager.addDev("mn=" + durationMn + " sec=" + durationSec + " ms=" + durationMs + " raw=" + duration);

                String t = String.format("%02d min %02d sec %03d msec", durationMn, durationSec, durationMs);

//                long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
//                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                //long diffInMs = TimeUnit.MILLISECONDS.toMilliseconds(duration);
                elapsedTime.setText(t);
                long diff = 0;
                if (endDate != null) {
                    diff = nbOfStatess;
                } else {
                    diff = nbOfStatess - previousNbOfStates;
                }
                previousNbOfStates = nbOfStatess;
                //if (diff == 0) {


                nbOfStatesPerSecond.setText("" + (int)(1000.0 * diff / duration));

                updateInfo();
                //}
            }
        } catch (Exception e) {
        }
    }
    

    private void checkUncheckAllPragmas() {
        if (customChecks != null) {
            int nb = 0;
            for(JCheckBox cb: customChecks) {
                nb = cb.isSelected() ? nb + 1 : nb ;
            }
            boolean check = (nb * 2) < customChecks.size();
            for(JCheckBox cb: customChecks) {
                cb.setSelected(check);
            }
            setButtons();
        }
    }

    public void reinitValues() {
        nbOfStates.setText("-");
        nbOfLinks.setText("-");
        nbOfReachabilities.setText("-");
        nbOfReachabilitiesNotFound.setText("-");
        nbOfDeadlocks.setText("-");
        nbOfPendingStates.setText("-");
        nbOfStatesPerSecond.setText("-");
        elapsedTime.setText("-");
    }

    public int getStateIndex() {
        if (amc == null) {
            return 0;
        }


        if ((endDate == null) && (go == true)) {
            return 1;
        }

        return amc.hasBeenStoppedBeforeCompletion() ? 2 : 3;

    }

    public void updateInfo() {
        info.setForeground(COLORS[getStateIndex()]);
        info.setText(INFOS[getStateIndex()]);
    }

    private class ModelCheckerMonitor extends TimerTask {
        private JDialogAvatarModelChecker jdamc;

        public ModelCheckerMonitor(JDialogAvatarModelChecker _jdamc) {
            jdamc = _jdamc;
        }

        public void run() {
            jdamc.updateValues();
        }

    }

    public void help() {
        if (mgui == null) {
            TraceManager.addDev("Null mgui");
        }

        HelpManager hm = mgui.getHelpManager();
        HelpEntry he = hm.getHelpEntryWithHTMLFile("reachabilitygraph.html");
        mgui.openHelpFrame(he);
    }
}
